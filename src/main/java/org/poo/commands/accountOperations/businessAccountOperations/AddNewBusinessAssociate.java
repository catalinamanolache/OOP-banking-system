package org.poo.commands.accountOperations.businessAccountOperations;

import org.poo.accounts.Account;
import org.poo.accounts.BusinessAccount;
import org.poo.bankManager.Bank;
import org.poo.commands.Command;
import org.poo.instances.CommandData;
import org.poo.instances.User;

import java.util.List;
import java.util.Map;

public class AddNewBusinessAssociate implements Command {
    private CommandData command;
    private Bank bank;

    public AddNewBusinessAssociate(final CommandData command, final Bank bank) {
        this.command = command;
        this.bank = bank;
    }

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

//        if (!account.getOwner().equals(email)) {
//            // TODO: You are not authorized to make this transaction.
//            System.out.println("You are not authorized to make this transaction in add new business associate.");
//            return;
//        }

        User user = this.bank.getUserByEmail(email);

        BusinessAccount businessAccount = (BusinessAccount) account;
        businessAccount.addBusinessAssociate(user, BusinessAccount.UserType.valueOf(role));

        for (Map.Entry< BusinessAccount.UserType, List<User>> entry : businessAccount.getUserMap().entrySet()) {
            System.out.print(entry.getKey() + " ");
            for (User u : entry.getValue()) {
                System.out.print(u.getEmail() + " ");
            }
        }
        System.out.println();
    }

}
