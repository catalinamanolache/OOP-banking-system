package org.poo.commands.accountOperations;

import org.poo.accounts.Account;
import org.poo.accounts.BusinessAccount;
import org.poo.instances.CommandData;
import org.poo.commands.Command;
import org.poo.bankManager.Bank;
import org.poo.instances.User;

public class AddFunds implements Command {
    private CommandData command;
    private Bank bank;

    public AddFunds(final CommandData commandData, final Bank bank) {
        this.command = commandData;
        this.bank = bank;
    }

    /**
     * Execute the add funds command.
     */
    @Override
    public void execute() {
        String iban = this.command.getAccount();
        double amount = this.command.getAmount();
        String email = this.command.getEmail();

        // deposit the amount in the account
        Account account = this.bank.getAccountByIban(iban);

        if (account == null) {
            return;
        }

        User user = this.bank.getUserByEmail(email);

        if (account.getAccountType().equals(Account.AccountType.BUSINESS)) {
            BusinessAccount businessAccount = (BusinessAccount) account;
            if (!businessAccount.isUserAssociatedWithBusiness(user)) {
                System.out.println("add funds user " + user.getEmail() + " is not associated with business " + account.getIban() + " timestamp " + command.getTimestamp());
                return;
            }
        }

        if (!account.verifyMoneyTransaction(user, amount, null)) {
//            System.out.println("cant deposit " + amount + " in " + iban + " by " + user.getEmail() + " timestamp: " + this.command.getTimestamp());
            return;
        }

        account.deposit(amount);
        account.handleMoneyTransactions(user, amount, null);
//        if (command.getTimestamp() >= 531 && command.getTimestamp() <= 600) {
//            System.out.println("deposit " + amount + " in " + iban + " by " + user.getEmail() + " timestamp: " + this.command.getTimestamp() + " balance" + account.getBalance());
//        }
    }
    }
