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

public class ChangeInterestRate implements Command {
    private CommandData command;
    private Bank bank;
    private ArrayNode output;

    public ChangeInterestRate(final CommandData command, final Bank bank, final ArrayNode output) {
        this.command = command;
        this.bank = bank;
        this.output = output;
    }

    /**
     * Execute the changeInterestRate command.
     */
    @Override
    public void execute() {
        String iban = this.command.getAccount();
        int timestamp = this.command.getTimestamp();
        double interestRate = this.command.getInterestRate();

        // get the account by iban
        Account account = this.bank.getAccountByIban(iban);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();
        result.put("command", this.command.getCommand());

        // if the account is not a savings account, print an error
        if (account.getAccountType().equals("classic")) {
            ObjectNode outputNode = objectMapper.createObjectNode();
            outputNode.put("timestamp", timestamp);
            outputNode.put("description", "This is not a savings account");
            result.set("output", outputNode);
            result.put("timestamp", timestamp);
            this.output.add(result);
            return;
        }

        // change the interest rate of the account
        SavingsAccount savingsAccount = (SavingsAccount) account;
        savingsAccount.setInterestRate(interestRate);

        // add a successful transaction to the account
        Transaction transaction;
        transaction = new Transaction.TransactionBuilder(timestamp,
                "Interest rate of the account changed to " + interestRate,
                this.command.getCommand())
                .build();
        account.addTransaction(transaction);
    }
}
