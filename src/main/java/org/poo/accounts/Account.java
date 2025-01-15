package org.poo.accounts;
import org.poo.bankManager.CurrencyConverter;
import org.poo.cards.Card;
import org.poo.instances.Commerciant;
import org.poo.instances.User;
import org.poo.transactions.Transaction;
import org.poo.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Account {
    public enum AccountType {
        CLASSIC, SAVINGS, BUSINESS
    }

    private double balance;
    private String currency;
    private String iban;
    private AccountType accountType;
    private double minBalance;

    private User owner;
    private List<Card> cards;
    private List<Transaction> transactions;

    private Map<Commerciant, Double> totalSpent;
    private Map<Commerciant, Integer> nrOfTransactions;
    private Map<Commerciant.CommerciantType, Double> nrOfTransactionsCashback;
    private Map<Commerciant, Double> spendingThresholdCashback;
    private double spendingThresholdTotal;

    public Account(final String currency, final AccountType accountType, final User owner) {
        this.balance = 0;
        this.currency = currency;
        this.iban = Utils.generateIBAN();
        this.cards = new ArrayList<>();
        this.accountType = accountType;
        this.minBalance = 0;
        this.owner = owner;
        this.transactions = new ArrayList<>();
        this.totalSpent = new HashMap<>();
        this.nrOfTransactions = new HashMap<>();
        this.nrOfTransactionsCashback = new HashMap<>();
        this.spendingThresholdCashback = new HashMap<>();
        this.spendingThresholdTotal = 0;
    }

    /**
     * Handle money transactions between the user and a commerciant.
     * @param user the user making the transaction
     * @param amount the amount of the transaction
     * @param commerciant the commerciant receiving the transaction
     * @return true if the transaction was successful, false otherwise
     */
    public boolean verifyMoneyTransaction(final User user, final double amount,
                                          final Commerciant commerciant) {
        return true;
    }

    public void handleMoneyTransactions(final User user, final double amount,
                                        final Commerciant commerciant) {}


    /**
     * Handle card transactions between the user and a card.
     * @param card the card used for the transaction
     * @param user the user making the transaction
     * @return true if the transaction was successful, false otherwise
     */
    public boolean handleCardTransactions(final Card card, final User user) {
        return true;
    }

    public boolean handleDeleteCard(final Card card, final User user) {
        return true;
    }

    /**
     * Update the total amount spent at a given commerciant. The total spent will be stored in RON.
     * @param commerciant the commerciant to update the total spent for
     * @param amount the amount to add to the total spent
     */
    public void updateTotalSpent(final Commerciant commerciant, final double amount,
                                 final String currencyString) {
        double amountConverted = CurrencyConverter.convert(currencyString, "RON", amount);
        if (this.totalSpent.containsKey(commerciant)) {
            double newAmount = this.totalSpent.get(commerciant) + amountConverted;
            this.totalSpent.put(commerciant, newAmount);
        } else {
            this.totalSpent.put(commerciant, amountConverted);
        }
//        System.out.println("total spent " + this.totalSpent.get(commerciant));
    }

    /**
     * Update the number of transactions made at a given commerciant.
     * @param commerciant the commerciant to update the number of transactions for
     */
    public void updateNrOfTransactions(final Commerciant commerciant) {
        if (this.nrOfTransactions.containsKey(commerciant)) {
            int newNumber = this.nrOfTransactions.get(commerciant) + 1;
            this.nrOfTransactions.put(commerciant, newNumber);
        } else {
            this.nrOfTransactions.put(commerciant, 1);
        }
    }

    /**
     * Deposit money into the account
     * @param amount the amount to be deposited
     */
    public void deposit(final double amount) {
        balance += amount;
    }

    /**
     * Withdraw money from the account
     * @param amount the amount to be withdrawn
     */
    public void withdraw(final double amount) {
        balance -= amount;
    }

    /**
     * Getter for transactions
     * @return the list of transactions
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Setter for transactions
     * @param transactions the list of transactions
     */
    public void setTransactions(final List<Transaction> transactions) {
        this.transactions = transactions;
    }

    /**
     * Add a transaction to the list of transactions
     * @param transaction the transaction to be added
     */
    public void addTransaction(final Transaction transaction) {
        transactions.add(transaction);
    }

    /**
     * Getter for owner
     * @return the owner of the account
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Setter for owner
     * @param owner the owner of the account
     */
    public void setOwner(final User owner) {
        this.owner = owner;
    }

    /**
     * Getter for minBalance
     * @return the minimum balance of the account
     */
    public double getMinBalance() {
        return minBalance;
    }

    /**
     * Setter for minBalance
     * @param minBalance the minimum balance of the account
     */
    public void setMinBalance(final double minBalance) {
        this.minBalance = minBalance;
    }

    /**
     * Getter for cards
     * @return the list of cards
     */
    public List<Card> getCards() {
        return cards;
    }

    /**
     * Add a card to the list of cards
     * @param card the card to be added
     */
    public void addCard(final Card card) {
        cards.add(card);
    }

    /**
     * Setter for cards
     * @param cards the list of cards
     */
    public void setCards(final List<Card> cards) {
        this.cards = cards;
    }

    /**
     * Getter for balance
     * @return the balance of the account
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Setter for balance
     * @param balance the balance of the account
     */
    public void setBalance(final double balance) {
        this.balance = balance;
    }

    /**
     * Getter for currency
     * @return the currency of the account
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Setter for currency
     * @param currency the currency of the account
     */
    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    /**
     * Getter for iban
     * @return the iban of the account
     */
    public String getIban() {
        return iban;
    }

    /**
     * Setter for iban
     * @param iban the iban of the account
     */
    public void setIban(final String iban) {
        this.iban = iban;
    }

    /**
     * Getter for accountType
     * @return the account type
     */
    public AccountType getAccountType() {
        return accountType;
    }

    /**
     * Setter for accountType
     * @param accountType the account type
     */
    public void setAccountType(final AccountType accountType) {
        this.accountType = accountType;
    }

    /**
     * Getter for totalSpent
     * @return the total spent at each commerciant
     */
    public Map<Commerciant, Double> getTotalSpent() {
        return totalSpent;
    }

    /**
     * Setter for totalSpent
     * @param totalSpent the total spent at each commerciant
     */
    public void setTotalSpent(final Map<Commerciant, Double> totalSpent) {
        this.totalSpent = totalSpent;
    }

    /**
     * Getter for nrOfTransactions
     * @return the number of transactions at each commerciant
     */
    public Map<Commerciant, Integer> getNrOfTransactions() {
        return nrOfTransactions;
    }

    /**
     * Setter for nrOfTransactions
     * @param nrOfTransactions the number of transactions at each commerciant
     */
    public void setNrOfTransactions(final Map<Commerciant, Integer> nrOfTransactions) {
        this.nrOfTransactions = nrOfTransactions;
    }

    /**
     * Getter for nrOfTransactionsCashback
     * @return the number of transactions cashback
     */
    public Map<Commerciant.CommerciantType, Double> getNrOfTransactionsCashback() {
        return nrOfTransactionsCashback;
    }

    /**
     * Setter for nrOfTransactionsCashback
     * @param map the number of transactions cashback
     */
    public void setNrOfTransactionsCashback(final Map<Commerciant.CommerciantType, Double> map) {
        this.nrOfTransactionsCashback = map;
    }

    /**
     * Getter for spendingThresholdCashback
     * @return the spending threshold cashback
     */
    public Map<Commerciant, Double> getSpendingThresholdCashback() {
        return spendingThresholdCashback;
    }

    /**
     * Setter for spendingThresholdCashback
     * @param map the spending threshold cashback
     */
    public void setSpendingThresholdCashback(final Map<Commerciant, Double> map) {
        this.spendingThresholdCashback = map;
    }

    public double getSpendingThresholdTotal() {
        return spendingThresholdTotal;
    }

    public void setSpendingThresholdTotal(double spendingThresholdTotal) {
        this.spendingThresholdTotal = spendingThresholdTotal;
    }
}
