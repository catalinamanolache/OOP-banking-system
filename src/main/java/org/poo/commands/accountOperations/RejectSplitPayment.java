package org.poo.commands.accountOperations;

import org.poo.accounts.Account;
import org.poo.bankManager.Bank;
import org.poo.bankManager.SplitPaymentContext;
import org.poo.commands.Command;
import org.poo.instances.CommandData;
import org.poo.transactions.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RejectSplitPayment implements Command {
    private CommandData command;
    private Bank bank;
    private SplitPaymentContext context;

    public RejectSplitPayment(final CommandData command, final Bank bank) {
        this.command = command;
        this.bank = bank;
    }

    @Override
    public void execute() {
        System.out.println("Executing command " + this.command.getCommand() + " timestamp " + this.command.getTimestamp());

        String email = this.command.getEmail();
        int timestamp = this.command.getTimestamp();
        Map<Integer, SplitPaymentContext> splitPaymentContextMap =
                this.bank.getSplitPaymentContextMap();

        for (Map.Entry<Integer, SplitPaymentContext> contextEntry : splitPaymentContextMap.entrySet()) {
            Map<String, String> participantsMap = contextEntry.getValue().getParticipantsMap();
            for (Map.Entry<String, String> participantEntry : participantsMap.entrySet()) {
                if (participantEntry.getKey().equals(email)) {
                    this.context = contextEntry.getValue();
                    break;
                }
            }
        }
        if (this.context == null) {
            return;
        }

        if (!this.context.isRefused()) {
            this.context.setRefused(true);
            this.context.setRefusedBy(email);
            System.out.println(" participant " + email + " rejected the payment");

            List<String> participants = new ArrayList<>(this.context.getParticipantsIbanList());
            int accountsNumber = participants.size();
            String currency = this.context.getCurrency();
            double amount = this.context.getAmount();
            List<Double> amountForUsers = new ArrayList<>(this.context.getAmountForUsers());
            String refusedBy = this.context.getRefusedBy();
            int startedTimestamp = this.context.getStartedTimestamp();

            for (int i = 0; i < accountsNumber; i++) {
                Account accountInvolved = this.bank.getAccountByIban(participants.get(i));
                String formattedAmount = String.format("%.2f", amount);

                Transaction transaction;
                transaction = new Transaction.TransactionBuilder(startedTimestamp,
                        "Split payment of " + formattedAmount + " " + currency,
                        "splitPayment")
                        .currency(currency)
                        .amountForUsers(amountForUsers)
                        .splitPaymentType(this.context.getType().toString().toLowerCase())
                        .amount(amountForUsers.get(i))
                        .involvedAccounts(participants)
                        .error("User "
                                + refusedBy + " rejected the payment.")
                        .build();
                System.out.println("reject transaction added to account " + accountInvolved.getIban());
                accountInvolved.addTransaction(transaction);
            }
        }
    }
}
