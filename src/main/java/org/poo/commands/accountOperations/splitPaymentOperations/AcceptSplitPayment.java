package org.poo.commands.accountOperations.splitPaymentOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.TreeMap;

public class AcceptSplitPayment implements Command {
    private CommandData command;
    private Bank bank;
    private SplitPaymentContext context;
    private ArrayNode output;

    public AcceptSplitPayment(final CommandData command, final Bank bank, final ArrayNode output) {
        this.command = command;
        this.bank = bank;
        this.output = output;
    }

    @Override
    public void execute() {
//        System.out.println("Executing command " + this.command.getCommand() + " timestamp " + this.command.getTimestamp());
        String email = this.command.getEmail();
        int timestamp = this.command.getTimestamp();
        String type = this.command.getSplitPaymentType();
        Map<Integer, SplitPaymentContext> splitPaymentContextMap =
                new TreeMap<>(this.bank.getSplitPaymentContextMap());

//        for (Map.Entry<Integer, SplitPaymentContext> contextEntry : splitPaymentContextMap.entrySet()) {
//            Map<String, List<String>> participantsMap = contextEntry.getValue().getParticipantsMap();
//            for (Map.Entry<String, List<String>> participantEntry : participantsMap.entrySet()) {
//                SplitPaymentContext.SplitPaymentType splitPaymentType =
//                        SplitPaymentContext.SplitPaymentType.valueOf(type.toUpperCase());
//                if (participantEntry.getKey().equals(email)
//                        && contextEntry.getValue().getType().equals(splitPaymentType)) {
//                    this.context = contextEntry.getValue();
//                    break;
//                }
//            }
//        }
        for (Map.Entry<Integer, SplitPaymentContext> contextEntry : splitPaymentContextMap.entrySet()) {
            SplitPaymentContext context = contextEntry.getValue();
            if (context.getParticipantsMap().containsKey(email)
                    && context.getType().toString().equalsIgnoreCase(type)) {
                this.context = context;
                break;
            }
        }

        User user = this.bank.getUserByEmail(email);

        if (user == null) {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode resultNode = objectMapper.createObjectNode();
            resultNode.put("command", this.command.getCommand());

            ObjectNode outputNode = objectMapper.createObjectNode();
            outputNode.put("description", "User not found");
            outputNode.put("timestamp", timestamp);
            resultNode.put("timestamp", timestamp);
            resultNode.set("output", outputNode);
            this.output.add(resultNode);
            System.out.println("User not found in accept split payment timestamp " + timestamp);
            return;
        }

        if (this.context == null) {
            System.out.println("context is null in accept split payment timestamp " + timestamp);
            return;
        }

        if (!this.context.isRefused()) {
            this.context.acceptParticipant(email);
//            System.out.println(" participants " + this.context.getParticipantsIbanList());
            System.out.print(" participant " + email + " accepted the payment " + type + " timestamp " + timestamp + " for payment originated at timestamp " + this.context.getStartedTimestamp());
            System.out.print(" participants left to accept " + this.context.getParticipantsLeftToAccept() + "\n");
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

                // check if all accounts have enough funds for the split payment
                boolean failed = false;
                String accountFailed = null;

                // remove the current split payment context from the bank
                this.bank.getSplitPaymentContextMap().remove(this.context.getStartedTimestamp());

                for (int i = 0; i < accountsNumber; i++) {
                    Account account = this.bank.getAccountByIban(participants.get(i));
                    if (account == null) {
                        System.out.println("account at position " + i + " is null in printing split payment");
                        // TODO : “One of the accounts is invalid.” →
                        //  cand unul dintre conturile date in lista de conturi pentru split este invalid
                        continue;
                    }
                    double amountConverted = CurrencyConverter.convert(currency, account.getCurrency(),
                            amountForUsers.get(i));
                    System.out.println("user " + account.getOwner().getEmail() +  " with account " + account.getIban() +" balance in currency " + currency + " is " +
                            CurrencyConverter.convert(account.getCurrency(), currency, account.getBalance()) + " balance in account currency " + account.getBalance() + " " + account.getCurrency() + " has to pay " + amountConverted + " " + currency);
//            System.out.print("account " + accountIbans.get(i) + " " + " owner " + account.getOwner().getEmail() + " ");
                }
                System.out.println();

                for (int i = 0; i < accountsNumber; i++) {
                    Account account = this.bank.getAccountByIban(participants.get(i));
                    if (account == null) {
                        System.out.println("account at position " + i + " is null in checking split payment");
                        continue;
                    }

                    // convert the amount to the account's currency
                    double amountConverted = CurrencyConverter.convert(currency, account.getCurrency(),
                            amountForUsers.get(i));


                    // check if the account has enough funds and get the first account that failed
                    if (account.getBalance() < amountConverted) {
                        failed = true;
                        accountFailed = participants.get(i);
                        break;
                    }
                }

                // if the payment failed, add the failed transaction to each account
                if (failed) {
                    System.out.println("failed split payment at timestamp " + timestamp + " because of account " + accountFailed);
                    for (int i = 0; i < accountsNumber; i++) {
                        Account accountInvolved = this.bank.getAccountByIban(participants.get(i));
                        if (accountInvolved == null) {
                            System.out.println("account at position " + i + " is null in printing failed transaction");
                            // TODO : “One of the accounts is invalid.” →
                            //  cand unul dintre conturile date in lista de conturi pentru split este invalid
                            continue;
                        }
                        String formattedAmount = String.format("%.2f", amount);

                        Transaction transaction;
                        transaction = new Transaction.TransactionBuilder(this.context.getStartedTimestamp(),
                                "Split payment of " + formattedAmount + " " + currency,
                                "splitPayment")
                                .currency(currency)
                                .amount(amountForUsers.get(i))
                                .involvedAccounts(participants)
                                .splitPaymentType(this.context.getType().toString().toLowerCase())
                                .amountForUsers(amountForUsers)
                                .error("Account " + accountFailed
                                        + " has insufficient funds for a split payment.")
                                .build();
                        accountInvolved.addTransaction(transaction);
                    }
                    return;
                }

                // withdraw the amount from each account and add a successful transaction
                for (int i = 0; i < accountsNumber; i++) {
                    Account account = this.bank.getAccountByIban(participants.get(i));

                    if (account == null) {
                        System.out.println("account at position " + i + " is null in printing successful transaction");
                        continue;
                    }

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
                    account.addTransaction(transaction);
                }
            }
        }
    }
}
