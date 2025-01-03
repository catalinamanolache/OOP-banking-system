package org.poo.commands.accountOperations;

import org.poo.accounts.Account;
import org.poo.instances.CommandData;
import org.poo.commands.Command;
import org.poo.bankManager.Bank;

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

        // deposit the amount in the account
        Account account = this.bank.getAccountByIban(iban);

        if (account == null) {
            return;
        }
        account.deposit(amount);
        }
    }
