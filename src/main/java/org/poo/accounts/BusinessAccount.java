package org.poo.accounts;

import org.poo.bankmanager.CurrencyConverter;
import org.poo.instances.Commerciant;
import org.poo.instances.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.poo.instances.Constants.INITIAL_BUSINESS_LIMIT;

public class BusinessAccount extends Account {
    public enum UserType {
        MANAGER, EMPLOYEE
    }

    // a map of the list of users associated with the business account, organized by their type
    private Map<UserType, List<User>> userMap;

    // a map of the total amount spent at each commerciant by each user
    private Map<User, Map<Commerciant, Double>> totalSpentBusiness;

    // a map of the number of transactions made at each commerciant by each user
    private Map<User, Map<Commerciant, Integer>> nrOfTransactionsBusiness;

    // a map of the total amount deposited by each user
    private Map<User, Double> totalDeposited;

    private double spendingLimit;
    private double depositLimit;

    public BusinessAccount(final String currency, final User owner) {
        super(currency, AccountType.BUSINESS, owner);
        this.userMap = new HashMap<>();
        this.userMap.put(UserType.MANAGER, new ArrayList<>());
        this.userMap.put(UserType.EMPLOYEE, new ArrayList<>());
        this.totalSpentBusiness = new HashMap<>();
        this.totalDeposited = new HashMap<>();
        this.nrOfTransactionsBusiness = new HashMap<>();

        // convert the initial limits to the currency of the account
        this.spendingLimit = CurrencyConverter.convert("RON", currency,
                INITIAL_BUSINESS_LIMIT);
        this.depositLimit = CurrencyConverter.convert("RON", currency,
                INITIAL_BUSINESS_LIMIT);
    }

    /**
     * Handles the depositing and spending of money for the business account, updating the total
     * deposited and spent amounts for each user.
     * @param user the user that is depositing or spending money
     * @param amount the amount of money that is being deposited or spent
     * @param commerciant the commerciant where the money is being spent
     */
    @Override
    public void handleMoneyTransactions(final User user, final double amount,
                                           final Commerciant commerciant) {

        double localAmount = amount;
        // if the amount is negative, the user is spending money
        if (localAmount < 0) {
            // make the amount positive
            localAmount = -localAmount;

            // add the amount to the total spent of the user, if the user has already spent money
            if (this.totalSpentBusiness.containsKey(user)) {
                Map<Commerciant, Double> spentMap = this.totalSpentBusiness.get(user);
                Map<Commerciant, Integer> nrOfTransactionsMap
                        = this.nrOfTransactionsBusiness.get(user);

                // if the user has already spent money at this commerciant, add the amount to it
                if (spentMap.containsKey(commerciant)) {
                    spentMap.put(commerciant, spentMap.get(commerciant) + localAmount);
                } else {
                    // create a new entry for the commerciant and add the amount to it
                    spentMap.put(commerciant, localAmount);
                }

                if (nrOfTransactionsMap.containsKey(commerciant)) {
                    nrOfTransactionsMap.put(commerciant, nrOfTransactionsMap.get(commerciant) + 1);
                } else {
                    nrOfTransactionsMap.put(commerciant, 1);
                }
            } else {
                // create a new map for the user and add the amount to it
                Map<Commerciant, Double> spentMap = new HashMap<>();
                spentMap.put(commerciant, localAmount);
                this.totalSpentBusiness.put(user, spentMap);

                Map<Commerciant, Integer> nrOfTransactionsMap = new HashMap<>();
                nrOfTransactionsMap.put(commerciant, 1);
                this.nrOfTransactionsBusiness.put(user, nrOfTransactionsMap);
            }
        } else {
            // add the amount to the total deposited of the user, if the user has already deposited
            if (this.totalDeposited.containsKey(user)) {
                this.totalDeposited.put(user, this.totalDeposited.get(user) + localAmount);
            } else {
                // create a new entry for the user and add the amount to it
                this.totalDeposited.put(user, localAmount);
            }
        }
    }

    /**
     * Verifies if the user can make the transaction, checking if the amount exceeds the spending
     * or depositing limit of the user, only for employees.
     * @param user the user making the transaction
     * @param amount the amount of the transaction
     * @return true if the user can make the transaction, false otherwise
     */
    @Override
    public boolean verifyTransaction(final User user, final double amount) {
        // if the user is an employee, check if the amount exceeds the spending/depositing limit
        if (this.userMap.get(UserType.EMPLOYEE).contains(user)) {
            if (amount < 0 && -amount > this.spendingLimit) {
                return false;
            } else if (amount > 0 && amount > this.depositLimit) {
                return false;
            }
        }

        // for other users, the transaction is always possible (they don't have a limit)
        return true;
    }

    /**
     * Checks if the user is associated with the business account (is a manager, employee or the
     * owner).
     * @param user the user to check
     * @return true if the user is associated with the business account, false otherwise
     */
    public boolean isUserAssociatedWithBusiness(final User user) {
        List<User> employees = this.userMap.get(UserType.EMPLOYEE);
        List<User> managers = this.userMap.get(UserType.MANAGER);
        return employees.contains(user) || managers.contains(user) || user.equals(this.getOwner());
    }

    /**
     * Adds a business associate to the business account in the corresponding list.
     * @param user the user to add
     * @param type the type of the user (manager or employee)
     */
    public void addBusinessAssociate(final User user, final UserType type) {
        // if the user is already associated with the business account, don't add it again
        if (this.getOwner().equals(user) || this.userMap.get(UserType.MANAGER).contains(user)
                || this.userMap.get(UserType.EMPLOYEE).contains(user)) {
            return;
        }

        // add the user to the corresponding list
        List<User> users = this.userMap.get(type);
        users.add(user);
    }

    /**
     * Gets the total amount of money deposited by a user.
     * @param user the user to get the total deposited amount for
     * @return the total amount of money deposited by the user
     */
    public double getTotalDepositedByUser(final User user) {
        if (!this.totalDeposited.containsKey(user)) {
            return 0;
        }

        return this.totalDeposited.get(user);
    }

    /**
     * Gets the total amount of money spent by a user.
     * @param user the user to get the total spent amount for
     * @return the total amount of money spent by the user
     */
    public double getTotalSpentByUser(final User user) {
        double totalSpent = 0;
        Map<Commerciant, Double> spentMap = this.totalSpentBusiness.get(user);
        if (spentMap == null) {
            return 0;
        }

        for (Map.Entry<Commerciant, Double> entry : spentMap.entrySet()) {
            totalSpent += entry.getValue();
        }
        return totalSpent;
    }

    /**
     * Gets the user type of a user.
     * @param user the user to get the type for
     * @return the user type of the user
     */
    public UserType getUserType(final User user) {
        for (Map.Entry<UserType, List<User>> entry : this.userMap.entrySet()) {
            if (entry.getValue().contains(user)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Creates a map with the total amount of money spent at each commerciant.
     * @return the map with the total amount of money spent at each commerciant
     */
    public Map<Commerciant, Double> getTotalSpentAtCommerciantsMap() {
        Map<Commerciant, Double> totalSpent = new HashMap<>();

        for (Map.Entry<User, Map<Commerciant, Double>> entry
                : this.totalSpentBusiness.entrySet()) {
            for (Map.Entry<Commerciant, Double> spentEntry : entry.getValue().entrySet()) {
                // if the commerciant is already in the map, add the amount to it
                if (totalSpent.containsKey(spentEntry.getKey())) {
                    double newAmount = totalSpent.get(spentEntry.getKey()) + spentEntry.getValue();
                    totalSpent.put(spentEntry.getKey(), newAmount);
                } else {
                    // create a new entry for the commerciant and add the amount to it
                    totalSpent.put(spentEntry.getKey(), spentEntry.getValue());
                }
            }
        }
        return totalSpent;
    }

    /**
     * Gets the users that have spent money at a commerciant.
     * @param commerciant the commerciant to get the users for
     * @return the users that have spent money at the commerciant
     */
    public List<User> getUsersWhoSpentAtCommerciant(final Commerciant commerciant) {
        List<User> users = new ArrayList<>();
        for (Map.Entry<User, Map<Commerciant, Double>> entry
                : this.totalSpentBusiness.entrySet()) {
            if (entry.getValue().containsKey(commerciant)) {
                users.add(entry.getKey());
            }
        }
        return users;
    }

    /**
     * Gets the total amount spent at a commerciant by a user.
     * @param commerciant the commerciant
     * @param user the user
     * @return the total amount spent at the commerciant by the user
     */
    public double getTotalSpentAtCommerciantByUser(final Commerciant commerciant, final User user) {
        if (this.totalSpentBusiness.containsKey(user)
                && this.totalSpentBusiness.get(user).containsKey(commerciant)) {
            return this.totalSpentBusiness.get(user).get(commerciant);
        }
        return 0;
    }

    /**
     * Gets the users of the business account.
     * @return the users of the business account
     */
    public Map<UserType, List<User>> getUserMap() {
        return this.userMap;
    }

    /**
     * Sets the users of the business account.
     * @param userMap the users of the business account
     */
    public void setUserMap(final Map<UserType, List<User>> userMap) {
        this.userMap = userMap;
    }

    /**
     * Gets the spending limit of the business account.
     * @return the spending limit of the business account
     */
    public double getSpendingLimit() {
        return spendingLimit;
    }

    /**
     * Sets the spending limit of the business account.
     * @param spendingLimit the spending limit of the business account
     */
    public void setSpendingLimit(final double spendingLimit) {
        this.spendingLimit = spendingLimit;
    }

    /**
     * Gets the deposit limit of the business account.
     * @return the deposit limit of the business account
     */
    public double getDepositLimit() {
        return depositLimit;
    }

    /**
     * Sets the deposit limit of the business account.
     * @param depositLimit the deposit limit of the business account
     */
    public void setDepositLimit(final double depositLimit) {
        this.depositLimit = depositLimit;
    }

    /**
     * Gets the amount spent at each commerciant for each user of the business account.
     * @return the amount spent at each commerciant for each user of the business account
     */
    public Map<User, Map<Commerciant, Double>> getTotalSpentBusiness() {
        return totalSpentBusiness;
    }

    /**
     * Sets the amount spent at each commerciant for each user of the business account.
     * @param totalSpent the amount spent at each commerciant for each user of the business
     *                          account
     */
    public void setTotalSpentBusiness(final Map<User, Map<Commerciant, Double>> totalSpent) {
        this.totalSpentBusiness = totalSpent;
    }

    /**
     * Gets the total amount of money deposited by each user of the business account.
     * @return the total amount of money deposited by each user of the business account
     */
    public Map<User, Double> getTotalDeposited() {
        return totalDeposited;
    }

    /**
     * Sets the total amount of money deposited by each user of the business account.
     * @param totalDeposited the total amount of money deposited by each user of the business
     *                       account
     */
    public void setTotalDeposited(final Map<User, Double> totalDeposited) {
        this.totalDeposited = totalDeposited;
    }

    /**
     * Gets the number of transactions made at each commerciant for each user of the business
     * account.
     * @return the number of transactions made at each commerciant for each user of the business
     * account
     */
    public Map<User, Map<Commerciant, Integer>> getNrOfTransactionsBusiness() {
        return nrOfTransactionsBusiness;
    }

    /**
     * Sets the number of transactions made at each commerciant for each user of the business
     * account.
     * @param map the number of transactions made at each commerciant for each
     *                                 user of the business account
     */
    public void setNrOfTransactionsBusiness(final Map<User, Map<Commerciant, Integer>> map) {
        this.nrOfTransactionsBusiness = map;
    }
}
