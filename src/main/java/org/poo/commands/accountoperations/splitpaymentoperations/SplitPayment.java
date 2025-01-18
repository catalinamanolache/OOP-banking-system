package org.poo.commands.accountoperations.splitpaymentoperations;

import org.poo.instances.CommandData;
import org.poo.commands.Command;
import org.poo.bankmanager.Bank;

import java.util.ArrayList;
import java.util.List;

public class SplitPayment implements Command {
    private CommandData command;
    private Bank bank;

    public SplitPayment(final CommandData command, final Bank bank) {
        this.command = command;
        this.bank = bank;
    }

    /**
     * Executes the splitPayment command.
     */
    @Override
    public void execute() {
        List<String> accountIbans = this.command.getAccounts();
        String currency = this.command.getCurrency();
        double amount = this.command.getAmount();
        int timestamp = this.command.getTimestamp();
        String type = this.command.getSplitPaymentType();
        List<Double> amountForUsers = this.command.getAmountForUsers();

        // if the amount for users is not given, split the amount equally
        int accountsNumber = accountIbans.size();
        if (amountForUsers == null) {
            amountForUsers = new ArrayList<>();
            for (int i = 0; i < accountsNumber; i++) {
                amountForUsers.add(amount / accountsNumber);
            }
        }

        // create a new split payment context with the given parameters
        this.bank.createSplitPaymentContext(accountIbans, currency, amountForUsers, amount, type,
                timestamp);
    }
}
