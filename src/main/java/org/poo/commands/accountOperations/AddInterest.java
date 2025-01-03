package org.poo.commands.accountOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.SavingsAccount;
import org.poo.instances.CommandData;
import org.poo.commands.Command;
import org.poo.bankManager.Bank;
import org.poo.transactions.Transaction;

public class AddInterest implements Command {
    private CommandData command;
    private Bank bank;
    private ArrayNode output;

    public AddInterest(final CommandData command, final Bank bank, final ArrayNode output) {
        this.command = command;
        this.bank = bank;
        this.output = output;
    }

    /**
     * Executes the addInterest command.
     */
    @Override
    public void execute() {
        String iban = this.command.getAccount();
        int timestamp = this.command.getTimestamp();

        Account account = this.bank.getAccountByIban(iban);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();
        result.put("command", this.command.getCommand());

        if (account.getAccountType().equals("classic")) {
            ObjectNode outputNode = objectMapper.createObjectNode();
            outputNode.put("timestamp", timestamp);
            outputNode.put("description", "This is not a savings account");
            result.set("output", outputNode);
            result.put("timestamp", timestamp);
            this.output.add(result);
            return;
        }

        SavingsAccount savingsAccount = (SavingsAccount) account;
        double toAdd = account.getBalance() * savingsAccount.getInterestRate();
        account.deposit(toAdd);

        Transaction transaction;
        transaction = new Transaction.TransactionBuilder(timestamp,
                "Interest rate income", this.command.getCommand())
                .currency(account.getCurrency())
                .amount(toAdd)
                .build();
        account.addTransaction(transaction);
    }
}
