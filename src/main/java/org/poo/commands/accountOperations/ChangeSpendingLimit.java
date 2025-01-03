package org.poo.commands.accountOperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.accounts.Account;
import org.poo.accounts.BusinessAccount;
import org.poo.bankManager.Bank;
import org.poo.bankManager.CurrencyConverter;
import org.poo.commands.Command;
import org.poo.instances.CommandData;
import org.poo.instances.User;

public class ChangeSpendingLimit implements Command {
    private CommandData command;
    private Bank bank;
    private ArrayNode output;

    public ChangeSpendingLimit(final CommandData command, final Bank bank, final ArrayNode output) {
        this.command = command;
        this.bank = bank;
        this.output = output;
    }

    @Override
    public void execute() {
        String email = this.command.getEmail();
        String iban = this.command.getAccount();
        double spendingLimit = this.command.getSpendingLimit();
        int timestamp = this.command.getTimestamp();

        Account account = this.bank.getAccountByIban(iban);

        if (account == null) {
            return;
        }

        User user = this.bank.getUserByEmail(email);

        if (user == null) {
            return;
        }

        if (!account.getAccountType().equals(Account.AccountType.BUSINESS)) {
            System.out.println("not a business account in change spending limit.");
            return;
        }

        if (!account.getOwner().getEmail().equals(email)) {
            // TODO: You are not authorized to make this transaction.
            System.out.println("You are not authorized to make this transaction in change spending limit.");
            return;
        }

        BusinessAccount businessAccount = (BusinessAccount) account;
//        double convertedSpendingLimit = CurrencyConverter.convert("RON",
//                businessAccount.getCurrency(), spendingLimit);
        businessAccount.setSpendingLimit(spendingLimit);
    }
}
