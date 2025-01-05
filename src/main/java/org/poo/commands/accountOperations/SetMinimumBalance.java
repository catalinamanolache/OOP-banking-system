package org.poo.commands.accountOperations;

import org.poo.accounts.Account;
import org.poo.instances.CommandData;
import org.poo.commands.Command;
import org.poo.bankManager.Bank;

public class SetMinimumBalance implements Command {
    private CommandData command;
    private Bank bank;

    public SetMinimumBalance(final CommandData commandData, final Bank bank) {
        this.command = commandData;
        this.bank = bank;
    }

    /**
     * Executes the setMinimumBalance command.
     */
    @Override
    public void execute() {
        String iban = this.command.getAccount();
        double amount = this.command.getAmount();

        Account account = this.bank.getAccountByIban(iban);

        if (account != null) {
            account.setMinBalance(amount);
        }


    }
}

