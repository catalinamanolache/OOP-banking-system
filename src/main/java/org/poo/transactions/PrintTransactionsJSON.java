package org.poo.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;

final class FormatTransactions {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private FormatTransactions() {
    }

    /**
     * Creates a JSON node for the addAccount transaction.
     * @param transaction transaction to be added
     * @return JSON node
     */
    public static ObjectNode addAccountTransaction(final Transaction transaction) {
        ObjectNode transactionNode = OBJECT_MAPPER.createObjectNode();

        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());

        return transactionNode;
    }

    /**
     * Creates a JSON node for the sendMoney transaction.
     * @param transaction transaction to be added
     * @return JSON node
     */
    public static ObjectNode sendMoneyTransaction(final Transaction transaction) {
        ObjectNode transactionNode = OBJECT_MAPPER.createObjectNode();

        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());

        if (!transaction.getDescription().equals("Insufficient funds")) {
            transactionNode.put("senderIBAN", transaction.getSenderIBAN());
            transactionNode.put("receiverIBAN", transaction.getReceiverIBAN());
            String amount = String.format("%.2f", transaction.getAmount());
            Double newAmount = Double.parseDouble(amount);
            transactionNode.put("amount", newAmount + " "
                    + transaction.getCurrency());
            transactionNode.put("transferType", transaction.getTransferType());
        }

        return transactionNode;
    }

    /**
     * Creates a JSON node for the createCard transaction.
     * @param transaction transaction to be added
     * @return JSON node
     */
    public static ObjectNode createCardTransaction(final Transaction transaction) {
        ObjectNode transactionNode = OBJECT_MAPPER.createObjectNode();

        transactionNode.put("account", transaction.getAccount());
        transactionNode.put("card", transaction.getCard());
        transactionNode.put("cardHolder", transaction.getCardHolder());
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());

        return transactionNode;
    }

    /**
     * Creates a JSON node for the deleteCard transaction.
     * @param transaction transaction to be added
     * @return JSON node
     */
    public static ObjectNode deleteCardTransaction(final Transaction transaction) {
        ObjectNode transactionNode = OBJECT_MAPPER.createObjectNode();

        transactionNode.put("account", transaction.getAccount());
        transactionNode.put("card", transaction.getCard());
        transactionNode.put("cardHolder", transaction.getCardHolder());
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());

        return transactionNode;
    }

    /**
     * Creates a JSON node for the payOnline transaction.
     * @param transaction transaction to be added
     * @return JSON node
     */
    public static ObjectNode payOnlineTransaction(final Transaction transaction) {
        ObjectNode transactionNode = OBJECT_MAPPER.createObjectNode();

        if (transaction.getAmount() != 0.0 && transaction.getCommerciant() != null) {
            String formattedAmount = String.format("%.2f", transaction.getAmount());
            Double newFormattedAmount = Double.parseDouble(formattedAmount);
            transactionNode.put("amount", newFormattedAmount);

            transactionNode.put("commerciant", transaction.getCommerciant());
        }
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());

        return transactionNode;
    }

    /**
     * Creates a JSON node for the checkCardStatus transaction.
     * @param transaction transaction to be added
     * @return JSON node
     */
    public static ObjectNode checkCardStatusTransaction(final Transaction transaction) {
        ObjectNode transactionNode = OBJECT_MAPPER.createObjectNode();

        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());

        return transactionNode;
    }

    /**
     * Creates a JSON node for the splitPayment transaction.
     * @param transaction transaction to be added
     * @return JSON node
     */
    public static ObjectNode splitPaymentTransaction(final Transaction transaction) {
        ObjectNode transactionNode = OBJECT_MAPPER.createObjectNode();

        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("currency", transaction.getCurrency());
        transactionNode.put("splitPaymentType", transaction.getSplitPaymentType());


        ArrayNode accountsArray = OBJECT_MAPPER.createArrayNode();

        for (String account : transaction.getInvolvedAccounts()) {
            accountsArray.add(account);
        }
        transactionNode.set("involvedAccounts", accountsArray);

        ArrayNode amountForUsersArray = OBJECT_MAPPER.createArrayNode();
        if (transaction.getSplitPaymentType().equals("custom")) {
            for (Double amount : transaction.getAmountForUsers()) {
                amountForUsersArray.add(amount);
            }
            transactionNode.set("amountForUsers", amountForUsersArray);
        }

        if (transaction.getSplitPaymentType().equals("equal")) {
            String formattedAmount = String.format("%.2f", transaction.getAmount());
            Double newFormattedAmount = Double.parseDouble(formattedAmount);
            transactionNode.put("amount", newFormattedAmount);
        }

        if (transaction.getError() != null) {
            transactionNode.put("error", transaction.getError());
        }

        return transactionNode;
    }

    /**
     * Creates a JSON node for the spendingsReport transaction.
     * @param transaction transaction to be added
     * @return JSON node
     */
    public static ObjectNode spendingsReportTransaction(final Transaction transaction) {
        ObjectNode transactionNode = OBJECT_MAPPER.createObjectNode();

        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("amount", transaction.getAmount());
        transactionNode.put("commerciant", transaction.getCommerciant());

        return transactionNode;
    }

    /**
     * Creates a JSON node for the deleteAccountFailed transaction.
     * @param transaction transaction to be added
     * @return JSON node
     */
    public static ObjectNode deleteAccountFailedTransaction(final Transaction transaction) {
        ObjectNode transactionNode = OBJECT_MAPPER.createObjectNode();

        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());

        return transactionNode;
    }

    /**
     * Creates a JSON node for the changeInterestRate transaction.
     * @param transaction transaction to be added
     * @return JSON node
     */
    public static ObjectNode changeInterestRateTransaction(final Transaction transaction) {
        ObjectNode transactionNode = OBJECT_MAPPER.createObjectNode();

        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());

        return transactionNode;
    }

    /**
     * Creates a JSON node for the withdrawSavings transaction.
     * @param transaction transaction to be added
     * @return JSON node
     */
    public static ObjectNode withdrawSavingsTransaction(final Transaction transaction) {
        ObjectNode transactionNode = OBJECT_MAPPER.createObjectNode();

        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());

        if (transaction.getError() == null) {
            transactionNode.put("amount", transaction.getAmount());
            transactionNode.put("classicAccountIBAN",
                    transaction.getClassicAccountIBAN());
            transactionNode.put("savingsAccountIBAN",
                    transaction.getSavingsAccountIBAN());
        }
        return transactionNode;
    }

    public static ObjectNode upgradePlanTransaction(final Transaction transaction) {
        ObjectNode transactionNode = OBJECT_MAPPER.createObjectNode();

        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());

        if (transaction.getError() == null) {
            transactionNode.put("accountIBAN", transaction.getAccount());
            transactionNode.put("newPlanType", transaction.getNewPlanType());
        }
        return transactionNode;
    }

    public static ObjectNode cashWithdrawalTransaction(final Transaction transaction) {
        ObjectNode transactionNode = OBJECT_MAPPER.createObjectNode();

        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());

        if (transaction.getError() == null) {
            transactionNode.put("amount", transaction.getAmount());
        }
        return transactionNode;
    }

    public static ObjectNode addInterestTransaction(final Transaction transaction) {
        ObjectNode transactionNode = OBJECT_MAPPER.createObjectNode();

        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());

        String formattedAmount = String.format("%.2f", transaction.getAmount());
        Double newFormattedAmount = Double.parseDouble(formattedAmount);
        transactionNode.put("amount", newFormattedAmount);

        transactionNode.put("currency", transaction.getCurrency());
        return transactionNode;
    }
}

public final class PrintTransactionsJSON {
    private PrintTransactionsJSON(final Transaction transaction) {
    }

    /**
     * Print transactions in JSON format.
     * @param transactions list of transactions
     * @param output output array
     */
    public static void printTransactions(final List<Transaction> transactions,
                                         final ArrayNode output) {
        for (Transaction transaction : transactions) {
            ObjectNode transactionNode = null;
            String transactionType = transaction.getTransactionType();

            if (transactionType.equals("addAccount")) {
                transactionNode = FormatTransactions.addAccountTransaction(transaction);
            } else if (transactionType.equals("sendMoney")) {
                transactionNode = FormatTransactions.sendMoneyTransaction(transaction);
            } else if (transactionType.equals("createCard")
                    || transactionType.equals("createOneTimeCard")) {
                transactionNode = FormatTransactions.createCardTransaction(transaction);
            } else if (transactionType.equals("payOnline")) {
                transactionNode = FormatTransactions.payOnlineTransaction(transaction);
            } else if (transactionType.equals("deleteCard")) {
                transactionNode = FormatTransactions.deleteCardTransaction(transaction);
            } else if (transactionType.equals("checkCardStatus")) {
                transactionNode = FormatTransactions.checkCardStatusTransaction(transaction);
            } else if (transactionType.equals("splitPayment")) {
                transactionNode = FormatTransactions.splitPaymentTransaction(transaction);
            } else if (transactionType.equals("spendingsReport")) {
                transactionNode = FormatTransactions.spendingsReportTransaction(transaction);
            } else if (transactionType.equals("deleteAccount")
                    && transaction.getError().equals("failed to delete account")) {
                transactionNode = FormatTransactions.deleteAccountFailedTransaction(transaction);
            } else if (transactionType.equals("changeInterestRate")) {
                transactionNode = FormatTransactions.changeInterestRateTransaction(transaction);
            } else if (transactionType.equals("withdrawSavings")) {
                transactionNode = FormatTransactions.withdrawSavingsTransaction(transaction);
            } else if (transactionType.equals("upgradePlan")) {
                transactionNode = FormatTransactions.upgradePlanTransaction(transaction);
            } else if (transactionType.equals("cashWithdrawal")) {
                transactionNode = FormatTransactions.cashWithdrawalTransaction(transaction);
            } else if (transactionType.equals("addInterest")) {
                transactionNode = FormatTransactions.addInterestTransaction(transaction);
            }

            output.add(transactionNode);
        }
    }

    /**
     * Print the transactions report in JSON format.
     * @param transactions list of transactions
     * @param output output array
     */
    public static void printReport(final List<Transaction> transactions,
                                   final ArrayNode output, final int startTimestamp,
                                   final int endTimestamp) {
        List<Transaction> transactionsRange = new ArrayList<>();

        // get all transactions in the given range
        for (Transaction transaction : transactions) {
            if (transaction.getTimestamp() >= startTimestamp
                    && transaction.getTimestamp() <= endTimestamp) {
                transactionsRange.add(transaction);
            }
        }

        // print the transactions in JSON format
        printTransactions(transactionsRange, output);

    }

    /**
     * Print the spendings report in JSON format.
     * @param transactions list of transactions
     * @param transactionArray transaction ArrayNode for the output
     * @param commerciantArray commerciant ArrayNode for the output
     * @param startTimestamp start timestamp for the report
     * @param endTimestamp end timestamp for the report
     */
    public static void printSpendingsReport(final List<Transaction> transactions,
                                            final ArrayNode transactionArray,
                                            final ArrayNode commerciantArray,
                                            final int startTimestamp, final int endTimestamp) {
        ArrayList<Transaction> transactionsRange = new ArrayList<>();
        Map<String, Double> totalSpent = new HashMap<>();

        // get all the succesful payOnline transactions in the given range
        for (Transaction transaction : transactions) {
            if (transaction.getTimestamp() >= startTimestamp
                    && transaction.getTimestamp() <= endTimestamp
                    && transaction.getTransactionType().equals("payOnline")
                    && !transaction.getDescription().equals("Insufficient funds")) {
                transactionsRange.add(transaction);

                // get the total amount spent at each commerciant and put it in a map
                if (!totalSpent.containsKey(transaction.getCommerciant())) {
                    totalSpent.put(transaction.getCommerciant(), transaction.getAmount());
                } else {
                    double currTotal = totalSpent.get(transaction.getCommerciant());
                    double toAdd = transaction.getAmount();
                    totalSpent.put(transaction.getCommerciant(), currTotal + toAdd);
                }
            }
        }

        // print the transactions in JSON format
        printTransactions(transactionsRange, transactionArray);

        // sort the map ascending by the commerciant names
        Map<String, Double> sortedTotalSpent = new TreeMap<>(totalSpent);

        // print the total amount spent at each commerciant in JSON format
        for (Map.Entry<String, Double> commerciantData : sortedTotalSpent.entrySet()) {
            String commerciant = commerciantData.getKey();
            Double total = commerciantData.getValue();

            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode commerciantNode = objectMapper.createObjectNode();

            commerciantNode.put("commerciant", commerciant);
            commerciantNode.put("total", total);

            commerciantArray.add(commerciantNode);
        }
    }
}
