package org.poo.commands.accountoperations.savingsoperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.accounts.Account;
import org.poo.accounts.SavingsAccount;
import org.poo.instances.CommandData;
import org.poo.commands.Command;
import org.poo.bankmanager.Bank;
import org.poo.instances.jsonexceptions.JSONException;
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
     * @throws JSONException if the account is not a savings account
     */
    @Override
    public void execute() throws JSONException {
        String iban = this.command.getAccount();
        int timestamp = this.command.getTimestamp();

        Account account = this.bank.getAccountByIban(iban);

        if (!account.getAccountType().equals(Account.AccountType.SAVINGS)) {
            throw new JSONException(this.command, "notSavingsAccount", this.output);
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
