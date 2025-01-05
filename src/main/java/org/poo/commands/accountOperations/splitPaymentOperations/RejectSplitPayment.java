package org.poo.commands.accountOperations.splitPaymentOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.bankManager.Bank;
import org.poo.bankManager.SplitPaymentContext;
import org.poo.commands.Command;
import org.poo.instances.CommandData;
import org.poo.instances.User;
import org.poo.transactions.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RejectSplitPayment implements Command {
    private CommandData command;
    private Bank bank;
    private SplitPaymentContext context;
    private ArrayNode output;

    public RejectSplitPayment(final CommandData command, final Bank bank, final ArrayNode output) {
        this.command = command;
        this.bank = bank;
        this.output = output;
    }

    @Override
    public void execute() {
        String email = this.command.getEmail();
        int timestamp = this.command.getTimestamp();
        String type = this.command.getSplitPaymentType();
        Map<Integer, SplitPaymentContext> splitPaymentContextMap =
                new TreeMap<>(this.bank.getSplitPaymentContextMap());

//        for (Map.Entry<Integer, SplitPaymentContext> contextEntry : splitPaymentContextMap.entrySet()) {
//            Map<String, List<String>> participantsMap = contextEntry.getValue().getParticipantsMap();
//            SplitPaymentContext.SplitPaymentType splitPaymentType =
//                    SplitPaymentContext.SplitPaymentType.valueOf(type.toUpperCase());
//            for (Map.Entry<String, List<String>> participantEntry : participantsMap.entrySet()) {
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
            return;
        }

        if (this.context == null) {
            System.out.println("context is null in reject split payment timestamp " + timestamp);
            return;
        }

        if (!this.context.isRefused()) {
            this.context.setRefused(true);
            this.context.setRefusedBy(email);
            System.out.println(" participant " + email + " rejected the payment " + type + " timestamp " + timestamp + "  for timestamp originated at " + this.context.getStartedTimestamp());

            List<String> participants = new ArrayList<>(this.context.getParticipantsIbanList());
            int accountsNumber = participants.size();
            String currency = this.context.getCurrency();
            double amount = this.context.getAmount();
            List<Double> amountForUsers = new ArrayList<>(this.context.getAmountForUsers());
            String refusedBy = this.context.getRefusedBy();
            int startedTimestamp = this.context.getStartedTimestamp();

            for (int i = 0; i < accountsNumber; i++) {
                Account accountInvolved = this.bank.getAccountByIban(participants.get(i));

                if (accountInvolved == null) {
                    System.out.println("account at position " + i + " is null in reject split payment");
                    continue;
                }
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
                        .error("One user rejected the payment.")
                        .build();
//                System.out.println("reject transaction added to account " + accountInvolved.getIban());
                accountInvolved.addTransaction(transaction);
            }

            this.bank.getSplitPaymentContextMap().remove(this.context.getStartedTimestamp());
        }
    }
}
