package org.poo.commands.accountOperations.businessAccountOperations;

import org.poo.accounts.Account;
import org.poo.accounts.BusinessAccount;
import org.poo.bankManager.Bank;
import org.poo.commands.Command;
import org.poo.instances.CommandData;
import org.poo.instances.User;

public class AddNewBusinessAssociate implements Command {
    private CommandData command;
    private Bank bank;

    public AddNewBusinessAssociate(final CommandData command, final Bank bank) {
        this.command = command;
        this.bank = bank;
    }

    /**
     * Executes the addNewBusinessAssociate command.
     */
    @Override
    public void execute() {
        String iban = this.command.getAccount();
        String email = this.command.getEmail();
        String role = this.command.getRole().toUpperCase();
        int timestamp = this.command.getTimestamp();

        Account account = this.bank.getAccountByIban(iban);

        if (account == null) {
            return;
        }

        if (!account.getAccountType().equals(Account.AccountType.BUSINESS)) {
            System.out.println("not a business account in add new business associate.");
            return;
        }

        User user = this.bank.getUserByEmail(email);

        // add the user as a business associate with the given role
        BusinessAccount businessAccount = (BusinessAccount) account;
        businessAccount.addBusinessAssociate(user, BusinessAccount.UserType.valueOf(role));
    }

}
