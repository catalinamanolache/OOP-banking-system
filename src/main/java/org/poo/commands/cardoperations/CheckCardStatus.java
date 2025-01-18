package org.poo.commands.cardoperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.accounts.Account;
import org.poo.cards.Card;
import org.poo.instances.CommandData;
import org.poo.commands.Command;
import org.poo.bankmanager.Bank;
import org.poo.instances.jsonexceptions.JSONException;
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
     * @throws JSONException if the card is not found
     */
    @Override
    public void execute() throws JSONException {
        String cardNumber = this.command.getCardNumber();
        int timestamp = this.command.getTimestamp();

        // get the account and card by card number
        Account account = this.bank.getAccountByCardNumber(cardNumber);
        Card card = this.bank.getCardByCardNumber(cardNumber);

        // if the account or card are not found, print an error
        if (account == null || card == null) {
            throw new JSONException(this.command, "cardNotFound", this.output);
        }

        // if the account has reached the minimum balance, print a message and freeze the card
        if (account.getBalance() <= account.getMinBalance()) {
            Transaction transaction;
            transaction = new Transaction.TransactionBuilder(timestamp,
                    "You have reached the minimum amount of funds, "
                           + "the card will be frozen", this.command.getCommand())
                    .build();
            account.addTransaction(transaction);
            card.setIsFrozen(true);
        }
    }
}
