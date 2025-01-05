package org.poo.transactions;

import java.util.List;

public final class Transaction {
    private int timestamp;
    private String description;
    private String senderIBAN;
    private String receiverIBAN;
    private double amount;
    private String currency;
    private String transferType;
    private String card;
    private String cardHolder;
    private String account;
    private String commerciant;
    private List<String> involvedAccounts;
    private String error;
    private String transactionType;
    private String classicAccountIBAN;
    private String savingsAccountIBAN;
    private String newPlanType;
    private List<Double> amountForUsers;
    private String splitPaymentType;

    private Transaction(final TransactionBuilder builder) {
        this.timestamp = builder.timestamp;
        this.description = builder.description;
        this.senderIBAN = builder.senderIBAN;
        this.receiverIBAN = builder.receiverIBAN;
        this.amount = builder.amount;
        this.currency = builder.currency;
        this.transferType = builder.transferType;
        this.card = builder.card;
        this.cardHolder = builder.cardHolder;
        this.account = builder.account;
        this.commerciant = builder.commerciant;
        this.involvedAccounts = builder.involvedAccounts;
        this.error = builder.error;
        this.transactionType = builder.transactionType;
        this.classicAccountIBAN = builder.classicAccountIBAN;
        this.savingsAccountIBAN = builder.savingsAccountIBAN;
        this.newPlanType = builder.newPlanType;
        this.amountForUsers = builder.amountForUsers;
        this.splitPaymentType = builder.splitPaymentType;
    }

    public static final class TransactionBuilder {
        private int timestamp;
        private String description;
        private String senderIBAN;
        private String receiverIBAN;
        private double amount;
        private String currency;
        private String transferType;
        private String card;
        private String cardHolder;
        private String account;
        private String commerciant;
        private List<String> involvedAccounts;
        private String error;
        private String transactionType;
        private String classicAccountIBAN;
        private String savingsAccountIBAN;
        private String newPlanType;
        private List<Double> amountForUsers;
        private String splitPaymentType;

        public TransactionBuilder(final int timestamp, final String description,
                                  final String transactionType) {
            this.timestamp = timestamp;
            this.description = description;
            this.transactionType = transactionType;
        }

        /**
         * Builds a transaction with the given sender IBAN.
         * @param sender the IBAN of the sender
         * @return the transaction builder
         */
        public TransactionBuilder senderIBAN(final String sender) {
            this.senderIBAN = sender;
            return this;
        }

        /**
         * Builds a transaction with the given receiver IBAN.
         * @param receiver the IBAN of the receiver
         * @return the transaction builder
         */
        public TransactionBuilder receiverIBAN(final String receiver) {
            this.receiverIBAN = receiver;
            return this;
        }

        /**
         * Builds a transaction with the given amountSum.
         * @param amountSum the amountSum of the transaction
         * @return the transaction builder
         */
        public TransactionBuilder amount(final double amountSum) {
            this.amount = amountSum;
            return this;
        }

        /**
         * Builds a transaction with the given currencySum.
         * @param currencySum the currencySum of the transaction
         * @return the transaction builder
         */
        public TransactionBuilder currency(final String currencySum) {
            this.currency = currencySum;
            return this;
        }

        /**
         * Builds a transaction with the given transferTypeString.
         * @param transferTypeString the transferTypeString of the transaction
         * @return the transaction builder
         */
        public TransactionBuilder transferType(final String transferTypeString) {
            this.transferType = transferTypeString;
            return this;
        }

        /**
         * Builds a transaction with the given cardNumber.
         * @param cardNumber the cardNumber of the transaction
         * @return the transaction builder
         */
        public TransactionBuilder card(final String cardNumber) {
            this.card = cardNumber;
            return this;
        }

        /**
         * Builds a transaction with the given cardHolderName.
         * @param cardHolderName the cardHolderName of the transaction
         * @return the transaction builder
         */
        public TransactionBuilder cardHolder(final String cardHolderName) {
            this.cardHolder = cardHolderName;
            return this;
        }

        /**
         * Builds a transaction with the given accountIban.
         * @param accountIban the accountIban of the transaction
         * @return the transaction builder
         */
        public TransactionBuilder account(final String accountIban) {
            this.account = accountIban;
            return this;
        }

        /**
         * Builds a transaction with the given commerciantName.
         * @param commerciantName the commerciantName of the transaction
         * @return the transaction builder
         */
        public TransactionBuilder commerciant(final String commerciantName) {
            this.commerciant = commerciantName;
            return this;
        }

        /**
         * Builds a transaction with the given involvedAccountsList.
         * @param involvedAccountsList the involvedAccountsList of the transaction
         * @return the transaction builder
         */
        public TransactionBuilder involvedAccounts(final List<String> involvedAccountsList) {
            this.involvedAccounts = involvedAccountsList;
            return this;
        }

        /**
         * Builds a transaction with the given errorString.
         * @param errorString the errorString of the transaction
         * @return the transaction builder
         */
        public TransactionBuilder error(final String errorString) {
            this.error = errorString;
            return this;
        }

        /**
         * Builds a transaction with the given classicAccountIBAN.
         * @param classicAccountIBANString the classicAccountIBAN of the transaction
         * @return the transaction builder
         */
        public TransactionBuilder classicAccountIBAN(final String classicAccountIBANString) {
            this.classicAccountIBAN = classicAccountIBANString;
            return this;
        }

        /**
         * Builds a transaction with the given savingsAccountIBAN.
         * @param savingsAccountIBANString the savingsAccountIBAN of the transaction
         * @return the transaction builder
         */
        public TransactionBuilder savingsAccountIBAN(final String savingsAccountIBANString) {
            this.savingsAccountIBAN = savingsAccountIBANString;
            return this;
        }

        /**
         * Builds a transaction with the given newPlanType.
         * @param newPlanTypeString the newPlanType of the transaction
         * @return the transaction builder
         */
        public TransactionBuilder newPlanType(final String newPlanTypeString) {
            this.newPlanType = newPlanTypeString;
            return this;
        }

        /**
         * Builds a transaction with the given amountForUsersList.
         * @param amountForUsersList the amountForUsersList of the transaction
         * @return the transaction builder
         */
        public TransactionBuilder amountForUsers(final List<Double> amountForUsersList) {
            this.amountForUsers = amountForUsersList;
            return this;
        }

        /**
         * Builds a transaction with the given splitPaymentType.
         * @param splitPaymentTypeString the splitPaymentType of the transaction
         * @return the transaction builder
         */
        public TransactionBuilder splitPaymentType(final String splitPaymentTypeString) {
            this.splitPaymentType = splitPaymentTypeString;
            return this;
        }

        /**
         * Builds the transaction.
         * @return the transaction
         */
        public Transaction build() {
            return new Transaction(this);
        }
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final int timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getSenderIBAN() {
        return senderIBAN;
    }

    public void setSenderIBAN(final String senderIBAN) {
        this.senderIBAN = senderIBAN;
    }

    public String getReceiverIBAN() {
        return receiverIBAN;
    }

    public void setReceiverIBAN(final String receiverIBAN) {
        this.receiverIBAN = receiverIBAN;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(final double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(final String transferType) {
        this.transferType = transferType;
    }

    public String getCard() {
        return card;
    }

    public void setCard(final String card) {
        this.card = card;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(final String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(final String account) {
        this.account = account;
    }

    public String getCommerciant() {
        return commerciant;
    }

    public void setCommerciant(final String commerciant) {
        this.commerciant = commerciant;
    }

    public List<String> getInvolvedAccounts() {
        return involvedAccounts;
    }

    public void setInvolvedAccounts(final List<String> involvedAccounts) {
        this.involvedAccounts = involvedAccounts;
    }

    public String getError() {
        return error;
    }

    public void setError(final String error) {
        this.error = error;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(final String transactionType) {
        this.transactionType = transactionType;
    }

    public String getClassicAccountIBAN() {
        return classicAccountIBAN;
    }

    public void setClassicAccountIBAN(final String classicAccountIBAN) {
        this.classicAccountIBAN = classicAccountIBAN;
    }

    public String getSavingsAccountIBAN() {
        return savingsAccountIBAN;
    }

    public void setSavingsAccountIBAN(final String savingsAccountIBAN) {
        this.savingsAccountIBAN = savingsAccountIBAN;
    }

    public String getNewPlanType() {
        return newPlanType;
    }

    public void setNewPlanType(final String newPlanType) {
        this.newPlanType = newPlanType;
    }

    public List<Double> getAmountForUsers() {
        return amountForUsers;
    }

    public void setAmountForUsers(final List<Double> amountForUsers) {
        this.amountForUsers = amountForUsers;
    }

    public String getSplitPaymentType() {
        return splitPaymentType;
    }

    public void setSplitPaymentType(final String splitPaymentType) {
        this.splitPaymentType = splitPaymentType;
    }
}
