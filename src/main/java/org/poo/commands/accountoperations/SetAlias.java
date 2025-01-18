package org.poo.commands.accountoperations;

import org.poo.instances.CommandData;
import org.poo.commands.Command;
import org.poo.bankmanager.Bank;

import java.util.Map;

public class SetAlias implements Command {
    private CommandData command;
    private Bank bank;

    public SetAlias(final CommandData command, final Bank bank) {
        this.command = command;
        this.bank = bank;
    }

    /**
     * Executes the setAlias command.
     */
    @Override
    public void execute() {
        String email = this.command.getEmail();
        String iban = this.command.getAccount();
        String alias = this.command.getAlias();
        int timestamp = this.command.getTimestamp();

        // put the alias in the aliasMap
        Map<String, String> aliasMap = this.bank.getAliasMap();
        aliasMap.put(alias, iban);
    }
}
