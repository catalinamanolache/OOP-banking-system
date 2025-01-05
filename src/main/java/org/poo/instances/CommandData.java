package org.poo.instances;

import org.poo.fileio.CommandInput;

import java.util.List;


public final class CommandData {
    private String command;
    private String email;
    private String account;
    private String newPlanType;
    private String role;
    private String currency;
    private String target;
    private String description;
    private String cardNumber;
    private String commerciant;
    private String receiver;
    private String alias;
    private String accountType;
    private String splitPaymentType;
    private String type;
    private String location;
    private int timestamp;
    private int startTimestamp;
    private int endTimestamp;
    private double interestRate;
    private double spendingLimit;
    private double depositLimit;
    private double amount;
    private double minBalance;
    private List<String> accounts;
    private List<Double> amountForUsers;

    public CommandData(final CommandInput commandInput) {
        this.command = commandInput.getCommand();
        this.email = commandInput.getEmail();
        this.account = commandInput.getAccount();
        this.currency = commandInput.getCurrency();
        this.amount = commandInput.getAmount();
        this.minBalance = commandInput.getMinBalance();
        this.target = commandInput.getTarget();
        this.description = commandInput.getDescription();
        this.cardNumber = commandInput.getCardNumber();
        this.commerciant = commandInput.getCommerciant();
        this.timestamp = commandInput.getTimestamp();
        this.startTimestamp = commandInput.getStartTimestamp();
        this.endTimestamp = commandInput.getEndTimestamp();
        this.receiver = commandInput.getReceiver();
        this.alias = commandInput.getAlias();
        this.accountType = commandInput.getAccountType();
        this.interestRate = commandInput.getInterestRate();
        this.accounts = commandInput.getAccounts();
        this.newPlanType = commandInput.getNewPlanType();
        this.role = commandInput.getRole();
        this.splitPaymentType = commandInput.getSplitPaymentType();
        this.type = commandInput.getType();
        this.location = commandInput.getLocation();
        this.spendingLimit = commandInput.getSpendingLimit();
        this.depositLimit = commandInput.getDepositLimit();
        this.amountForUsers = commandInput.getAmountForUsers();
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(final String command) {
        this.command = command;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(final String account) {
        this.account = account;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(final double amount) {
        this.amount = amount;
    }

    public double getMinBalance() {
        return minBalance;
    }

    public void setMinBalance(final double minBalance) {
        this.minBalance = minBalance;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(final String target) {
        this.target = target;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(final String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCommerciant() {
        return commerciant;
    }

    public void setCommerciant(final String commerciant) {
        this.commerciant = commerciant;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final int timestamp) {
        this.timestamp = timestamp;
    }

    public int getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(final int startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public int getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(final int endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(final String receiver) {
        this.receiver = receiver;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(final String alias) {
        this.alias = alias;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(final String accountType) {
        this.accountType = accountType;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(final double interestRate) {
        this.interestRate = interestRate;
    }

    public List<String> getAccounts() {
        return accounts;
    }

    public void setAccounts(final List<String> accounts) {
        this.accounts = accounts;
    }

    public String getNewPlanType() {
        return newPlanType;
    }

    public void setNewPlanType(final String newPlanType) {
        this.newPlanType = newPlanType;
    }

    public String getRole() {
        return role;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    public String getSplitPaymentType() {
        return splitPaymentType;
    }

    public void setSplitPaymentType(final String splitPaymentType) {
        this.splitPaymentType = splitPaymentType;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public double getSpendingLimit() {
        return spendingLimit;
    }

    public void setSpendingLimit(final double spendingLimit) {
        this.spendingLimit = spendingLimit;
    }

    public double getDepositLimit() {
        return depositLimit;
    }

    public void setDepositLimit(final double depositLimit) {
        this.depositLimit = depositLimit;
    }

    public List<Double> getAmountForUsers() {
        return amountForUsers;
    }

    public void setAmountForUsers(final List<Double> amountForUsers) {
        this.amountForUsers = amountForUsers;
    }
}
