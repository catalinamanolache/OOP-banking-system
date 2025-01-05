package org.poo.commands.accountOperations.savingsAccountOperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.accounts.Account;
import org.poo.bankManager.Bank;
import org.poo.bankManager.CurrencyConverter;
import org.poo.commands.Command;
import org.poo.instances.CommandData;
import org.poo.instances.User;
import org.poo.transactions.Transaction;

public class WithdrawSavings implements Command {
    private CommandData command;
    private Bank bank;
    private ArrayNode output;

    public WithdrawSavings(final CommandData command, final Bank bank, final ArrayNode output) {
        this.command = command;
        this.bank = bank;
        this.output = output;
    }

    @Override
    public void execute() {
        String iban = this.command.getAccount();
        double amount = this.command.getAmount();
        String currency = this.command.getCurrency();
        int timestamp = this.command.getTimestamp();

        Account account = this.bank.getAccountByIban(iban);

        if (account == null) {
            // TODO: transaction or output “Account not found.”
//            Transaction transaction;
//            transaction = new Transaction.TransactionBuilder(timestamp,
//                    "Account not found.", this.command.getCommand())
//                    .build();
            // TODO: add transaction???
            System.out.println("account not found");
            return;
        }

        if (!account.getAccountType().equals(Account.AccountType.SAVINGS)) {
            // TODO: transaction or output "“Account is not of type savings.”
            Transaction transaction;
            transaction = new Transaction.TransactionBuilder(timestamp,
                    "Account is not of type savings", this.command.getCommand())
                    .error("not savings account")
                    .build();
            account.addTransaction(transaction);
            System.out.println("account is not of type savings");
            return;
        }

        User user = account.getOwner();

        if (user.getAge() < 21) {
            // TODO: transaction or output "You don't have the minimum age required."
            Transaction transaction;
            transaction = new Transaction.TransactionBuilder(timestamp,
                    "You don't have the minimum age required.",
                    this.command.getCommand())
                    .error("minimum age")
                    .build();
            account.addTransaction(transaction);
            System.out.println("minimum age error " + account.getIban() + " " + user.getEmail() + " " + user.getAge());
            return;
        }

        Account toDepositAccount = null;

        for (Account userAccount : user.getAccounts()) {
            if (userAccount.getAccountType().equals(Account.AccountType.CLASSIC)
                    && userAccount.getCurrency().equals(currency)) {
                toDepositAccount = userAccount;
                break;
            }
        }

        if (toDepositAccount == null) {
            // TODO: transaction or output "You don't have a classic account."
            Transaction transaction;
            transaction = new Transaction.TransactionBuilder(timestamp,
                    "You do not have a classic account.",
                    this.command.getCommand())
                    .error("classic account not found")
                    .build();
            account.addTransaction(transaction);
            System.out.println("classic account not found " + account.getIban() + " " + user.getEmail());
            return;
        }

        double amountToDeposit = CurrencyConverter.convert(currency,
                toDepositAccount.getCurrency(), amount);

        // TODO: check with minBalance?
        if (account.getBalance() < amountToDeposit) {
            // TODO: transaction or output "Insufficient funds."
            Transaction transaction;
            transaction = new Transaction.TransactionBuilder(timestamp,
                    "Insufficient funds",
                    this.command.getCommand())
                    .error("insufficient funds")
                    .build();
            account.addTransaction(transaction);
            System.out.println("Insufficient funds.");
            return;
        }

        account.withdraw(amount);
        toDepositAccount.deposit(amountToDeposit);

        // TODO: transaction or output "Savings withdrawal."
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
        System.out.println("Savings withdrawal.");
//        System.out.println("Did not implement WithdrawSavings");
    }
}
