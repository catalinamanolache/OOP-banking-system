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

    /**
     * Execute the acceptSplitPayment command.
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

        // only accept the split payment if the user has not refused it yet
        if (!this.context.isRefused()) {
            // mark the participant as accepted
            this.context.acceptParticipant(email);

            // if all participants have accepted the split payment, execute it
            if (this.context.getParticipantsLeftToAccept() == 0) {
                this.context.setRefused(false);

                // get the split's payment details
                List<String> participants = new ArrayList<>(this.context.getParticipantsIbanList());
                int accountsNumber = participants.size();
                String currency = this.context.getCurrency();
                double amount = this.context.getAmount();
                List<Double> amountForUsers = new ArrayList<>(this.context.getAmountForUsers());
                int startedTimestamp = this.context.getStartedTimestamp();
                String paymentType = this.context.getType().toString().toLowerCase();

                // check if all accounts have enough funds for the split payment
                boolean failed = false;
                String accountFailed = null;

                // remove the current split payment context from the bank
                this.bank.getSplitPaymentContextMap().remove(this.context.getStartedTimestamp());

                // check if all accounts have enough funds for the split payment
                for (int i = 0; i < accountsNumber; i++) {
                    Account account = this.bank.getAccountByIban(participants.get(i));
                    if (account == null) {
                        continue;
                    }

                    // convert the amount to the account's currency
                    double amountConverted = CurrencyConverter.convert(currency,
                            account.getCurrency(), amountForUsers.get(i));

                    // check if the account has enough funds and get the first account that failed
                    if (account.getBalance() < amountConverted) {
                        failed = true;
                        accountFailed = participants.get(i);
                        break;
                    }
                }

                // if the payment failed, add the failed transaction to each account
                if (failed) {
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
                                .amount(amountForUsers.get(i))
                                .involvedAccounts(participants)
                                .splitPaymentType(paymentType)
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
                            .splitPaymentType(paymentType)
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
