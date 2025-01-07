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

    /**
     * Execute the rejectSplitPayment command.
     */
    @Override
    public void execute() {
        String email = this.command.getEmail();
        int timestamp = this.command.getTimestamp();
        String type = this.command.getSplitPaymentType();
        Map<Integer, SplitPaymentContext> splitPaymentContextMap =
                new TreeMap<>(this.bank.getSplitPaymentContextMap());

        // find the split payment context of the given type that the user is involved in
        for (Map.Entry<Integer, SplitPaymentContext> contextEntry
                : splitPaymentContextMap.entrySet()) {
            SplitPaymentContext currContext = contextEntry.getValue();
            if (currContext.getParticipantsMap().containsKey(email)
                    && currContext.getType().toString().equalsIgnoreCase(type)) {
                this.context = currContext;
                break;
            }
        }

        User user = this.bank.getUserByEmail(email);

        // if the user is not found, add an error message to the output
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
            return;
        }

        // only refuse the payment if it wasn't already refused
        if (!this.context.isRefused()) {
            this.context.setRefused(true);

            // get the split payment's details
            List<String> participants = new ArrayList<>(this.context.getParticipantsIbanList());
            int accountsNumber = participants.size();
            String currency = this.context.getCurrency();
            double amount = this.context.getAmount();
            List<Double> amountForUsers = new ArrayList<>(this.context.getAmountForUsers());
            int startedTimestamp = this.context.getStartedTimestamp();
            String splitType = this.context.getType().toString().toLowerCase();

            // add a failed transaction to each account involved in the split payment
            for (int i = 0; i < accountsNumber; i++) {
                Account accountInvolved = this.bank.getAccountByIban(participants.get(i));

                if (accountInvolved == null) {
                    continue;
                }

                String formattedAmount = String.format("%.2f", amount);

                Transaction transaction;
                transaction = new Transaction.TransactionBuilder(startedTimestamp,
                        "Split payment of " + formattedAmount + " " + currency,
                        "splitPayment")
                        .currency(currency)
                        .amountForUsers(amountForUsers)
                        .splitPaymentType(splitType)
                        .amount(amountForUsers.get(i))
                        .involvedAccounts(participants)
                        .error("One user rejected the payment.")
                        .build();
                accountInvolved.addTransaction(transaction);
            }

            // remove the current split payment context from the bank
            this.bank.getSplitPaymentContextMap().remove(startedTimestamp);
        }
    }
}
