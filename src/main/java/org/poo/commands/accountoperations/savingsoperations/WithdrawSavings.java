package org.poo.commands.accountoperations.savingsoperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.accounts.Account;
import org.poo.bankmanager.Bank;
import org.poo.bankmanager.CurrencyConverter;
import org.poo.commands.Command;
import org.poo.instances.CommandData;
import org.poo.instances.User;
import org.poo.transactions.Transaction;

import static org.poo.instances.Constants.SAVINGS_AGE_LIMIT;

public class WithdrawSavings implements Command {
    private CommandData command;
    private Bank bank;
    private ArrayNode output;

    public WithdrawSavings(final CommandData command, final Bank bank, final ArrayNode output) {
        this.command = command;
        this.bank = bank;
        this.output = output;
    }

    /**
     * Executes the withdrawSavings command.
     */
    @Override
    public void execute() {
        String iban = this.command.getAccount();
        double amount = this.command.getAmount();
        String currency = this.command.getCurrency();
        int timestamp = this.command.getTimestamp();

        Account account = this.bank.getAccountByIban(iban);

        if (account == null) {
            return;
        }

        // if the account is not a savings account, create an error transaction
        if (!account.getAccountType().equals(Account.AccountType.SAVINGS)) {
            Transaction transaction;
            transaction = new Transaction.TransactionBuilder(timestamp,
                    "Account is not of type savings", this.command.getCommand())
                    .error("not savings account")
                    .build();
            account.addTransaction(transaction);
            return;
        }

        User user = account.getOwner();

        // if the user is not of age, create an error transaction
        if (user.getAge() < SAVINGS_AGE_LIMIT) {
            Transaction transaction;
            transaction = new Transaction.TransactionBuilder(timestamp,
                    "You don't have the minimum age required.",
                    this.command.getCommand())
                    .error("minimum age")
                    .build();
            account.addTransaction(transaction);
            return;
        }

        Account toDepositAccount = null;

        // get the first classic account of the user in the given currency
        for (Account userAccount : user.getAccounts()) {
            if (userAccount.getAccountType().equals(Account.AccountType.CLASSIC)
                    && userAccount.getCurrency().equals(currency)) {
                toDepositAccount = userAccount;
                break;
            }
        }

        // if the user does not have such account, create an error transaction
        if (toDepositAccount == null) {
            Transaction transaction;
            transaction = new Transaction.TransactionBuilder(timestamp,
                    "You do not have a classic account.",
                    this.command.getCommand())
                    .error("classic account not found")
                    .build();
            account.addTransaction(transaction);
            return;
        }

        // convert the amount to deposit in the classic account to its currency
        double amountToDeposit = CurrencyConverter.convert(currency,
                toDepositAccount.getCurrency(), amount);

        // if the user doesn't have enough funds in the savings account, create an error transaction
        if (account.getBalance() < amountToDeposit) {
            Transaction transaction;
            transaction = new Transaction.TransactionBuilder(timestamp,
                    "Insufficient funds",
                    this.command.getCommand())
                    .error("insufficient funds")
                    .build();
            account.addTransaction(transaction);
            return;
        }

        // withdraw the amount from the savings account and deposit it in the classic account
        account.withdraw(amount);
        toDepositAccount.deposit(amountToDeposit);

        // create a successful transaction and add it to both accounts
        Transaction transaction;
        transaction = new Transaction.TransactionBuilder(timestamp,
                "Savings withdrawal",
                this.command.getCommand())
                .classicAccountIBAN(toDepositAccount.getIban())
                .savingsAccountIBAN(account.getIban())
                .amount(amount)
                .build();

        account.addTransaction(transaction);
        toDepositAccount.addTransaction(transaction);
    }
}
