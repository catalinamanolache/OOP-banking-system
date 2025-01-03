package org.poo.commands.accountOperations;

import org.poo.accounts.Account;
import org.poo.accounts.AccountFactory;
import org.poo.commands.Command;
import org.poo.instances.Plan;
import org.poo.transactions.Transaction;
import org.poo.instances.User;
import org.poo.instances.CommandData;
import org.poo.bankManager.Bank;

public class AddAccount implements Command {
    private CommandData command;
    private Bank bank;

    public AddAccount(final CommandData command, final Bank bank) {
        this.command = command;
        this.bank = bank;
    }

    /**
     * Executes the addAccount command.
     */
    @Override
    public void execute() {
        String email = this.command.getEmail();
        String currency = this.command.getCurrency();
        String accountType = this.command.getAccountType();
        double interestRate = this.command.getInterestRate();
        int timestamp = this.command.getTimestamp();

        // add a new account to the user given by email
        User user = this.bank.getUserByEmail(email);

        user.addAccount(AccountFactory.createAccount(accountType, currency, interestRate,
                user));
        Account account = user.getAccounts().get(user.getAccounts().size() - 1);

        // add a successful transaction to the account
        Transaction transaction;
        transaction = new Transaction.TransactionBuilder(timestamp, "New account created",
                this.command.getCommand())
                .build();
        account.addTransaction(transaction);
    }
}
