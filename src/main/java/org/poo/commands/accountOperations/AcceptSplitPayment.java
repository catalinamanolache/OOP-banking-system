package org.poo.commands.accountOperations;

import org.poo.accounts.Account;
import org.poo.bankManager.Bank;
import org.poo.bankManager.CurrencyConverter;
import org.poo.bankManager.SplitPaymentContext;
import org.poo.commands.Command;
import org.poo.instances.CommandData;
import org.poo.instances.User;
import org.poo.transactions.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AcceptSplitPayment implements Command {
    private CommandData command;
    private Bank bank;
    private SplitPaymentContext context;

    public AcceptSplitPayment(final CommandData command, Bank bank) {
        this.command = command;
        this.bank = bank;
//        System.out.println("accept split payment timestamptest07_simple_split_payment.json " + this.command.getTimestamp())
//        System.out.println("accept split payment command participants " + this.context.getParticipants());
    }

    @Override
    public void execute() {
        System.out.println("Executing command " + this.command.getCommand() + " timestamp " + this.command.getTimestamp());
        String email = this.command.getEmail();
        int timestamp = this.command.getTimestamp();
        Map<Integer, SplitPaymentContext> splitPaymentContextMap =
                this.bank.getSplitPaymentContextMap();

        System.out.println("map is " + splitPaymentContextMap);
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
            this.context.acceptParticipant(email);
//            System.out.println(" participants " + this.context.getParticipantsIbanList());
//            System.out.println(" participant " + email + " accepted the payment timestamp " + timestamp);
//            System.out.println(" participants left to accept " + this.context.getParticipantsLeftToAccept());
            if (this.context.getParticipantsLeftToAccept() == 0) {
//                System.out.println("all participants accepted the payment timestamp " + timestamp);
                this.context.setRefused(false);
                this.context.setRefusedBy(null);

                List<String> participants = new ArrayList<>(this.context.getParticipantsIbanList());
                int accountsNumber = participants.size();
//                System.out.println("participants number " + accountsNumber);
                String currency = this.context.getCurrency();
                double amount = this.context.getAmount();
                List<Double> amountForUsers = new ArrayList<>(this.context.getAmountForUsers());
                int startedTimestamp = this.context.getStartedTimestamp();
                // withdraw the amount from each account and add a successful transaction
                for (int i = 0; i < accountsNumber; i++) {
                    Account account = this.bank.getAccountByIban(participants.get(i));

                    // convert the amount to the account's currency
                    double amountConverted = CurrencyConverter.convert(currency,
                            account.getCurrency(), amountForUsers.get(i));

                    account.withdraw(amountConverted);

                    String formattedAmount = String.format("%.2f", amount);

                    Transaction transaction;
                    transaction = new Transaction.TransactionBuilder(startedTimestamp,
                            "Split payment of " + formattedAmount + " " + currency,
                            "splitPayment")
                            .currency(currency)
                            .splitPaymentType(this.context.getType().toString().toLowerCase())
                            .amount(amountForUsers.get(i))
                            .involvedAccounts(participants)
                            .amountForUsers(amountForUsers)
                            .build();
                    System.out.println("accept transaction added to account " + account.getIban());
                    account.addTransaction(transaction);
                }
            }
        }
    }
}
