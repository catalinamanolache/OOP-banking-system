package org.poo.commands.cardOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.cards.Card;
import org.poo.instances.CommandData;
import org.poo.commands.Command;
import org.poo.bankManager.Bank;
import org.poo.transactions.Transaction;

public class CheckCardStatus implements Command {
    private CommandData command;
    private Bank bank;
    private ArrayNode output;

    public CheckCardStatus(final CommandData command, final Bank bank, final ArrayNode output) {
        this.command = command;
        this.bank = bank;
        this.output = output;
    }

    /**
     * Executes the checkCardStatus command.
     */
    @Override
    public void execute() {
        String cardNumber = this.command.getCardNumber();
        int timestamp = this.command.getTimestamp();

        // get the account and card by card number
        Account account = this.bank.getAccountByCardNumber(cardNumber);
        Card card = this.bank.getCardByCardNumber(cardNumber);

        if (account == null || card == null) {
            //if the account or card are not found, print an error
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode result = objectMapper.createObjectNode();
            result.put("command", this.command.getCommand());

            ObjectNode error = objectMapper.createObjectNode();
            error.put("timestamp", timestamp);
            error.put("description", "Card not found");

            result.set("output", error);
            result.put("timestamp", timestamp);
            this.output.add(result);
            return;
        }

        // if the account has reached the minimum balance, print a message and freeze the card
        if (account.getBalance() <= account.getMinBalance()) {
            Transaction transaction;
            transaction = new Transaction.TransactionBuilder(timestamp,
                    "You have reached the minimum amount of funds, the card will be frozen",
                    this.command.getCommand())
                    .build();
            account.addTransaction(transaction);
            card.setIsFrozen(true);
        }
    }
}
