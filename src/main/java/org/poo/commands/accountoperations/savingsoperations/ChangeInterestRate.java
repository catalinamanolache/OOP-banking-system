package org.poo.commands.accountoperations.savingsoperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.accounts.Account;
import org.poo.accounts.SavingsAccount;
import org.poo.instances.CommandData;
import org.poo.commands.Command;
import org.poo.bankmanager.Bank;
import org.poo.instances.jsonexceptions.JSONException;
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
     * @throws JSONException if the account is not a savings account
     */
    @Override
    public void execute() throws JSONException {
        String iban = this.command.getAccount();
        int timestamp = this.command.getTimestamp();
        double interestRate = this.command.getInterestRate();

        // get the account by iban
        Account account = this.bank.getAccountByIban(iban);

        // if the account is not a savings account, print an error
        if (!account.getAccountType().equals(Account.AccountType.SAVINGS)) {
            throw new JSONException(this.command, "notSavingsAccount", this.output);
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
