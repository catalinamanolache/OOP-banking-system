package org.poo.commands.accountoperations;

import org.poo.accounts.Account;
import org.poo.accounts.BusinessAccount;
import org.poo.instances.CommandData;
import org.poo.commands.Command;
import org.poo.bankmanager.Bank;
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

        // get the account and the user
        Account account = this.bank.getAccountByIban(iban);

        if (account == null) {
            return;
        }

        User user = this.bank.getUserByEmail(email);

        // for business accounts, the user must be associated with the business
        if (account.getAccountType().equals(Account.AccountType.BUSINESS)) {
            BusinessAccount businessAccount = (BusinessAccount) account;
            if (!businessAccount.isUserAssociatedWithBusiness(user)) {
                return;
            }
        }

        // if the user is not allowed to deposit this amount, return
        if (!account.verifyTransaction(user, amount)) {
            return;
        }

        // deposit the amount in the account and handle the transaction for business accounts
        account.deposit(amount);
        account.handleMoneyTransactions(user, amount, null);
        }
    }
