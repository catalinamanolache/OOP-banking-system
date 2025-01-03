package org.poo.accounts;

import org.poo.bankManager.CurrencyConverter;
import org.poo.instances.Commerciant;
import org.poo.instances.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// TODO: Numai ownerul poate schimba limita pentru plăți și depus bani,
//  să adauge noi asociați, să seteze balanța minimă și să șteargă contul.
public class BusinessAccount extends Account {
    public enum UserType {
        MANAGER, EMPLOYEE
    }

    private Map<UserType, List<User>> userMap;
    private Map<User, Map<Commerciant, Double>> totalSpentBusiness;
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
        this.spendingLimit = CurrencyConverter.convert("RON", currency, 500);
        this.depositLimit = CurrencyConverter.convert("RON", currency, 500);

        List<User> users = this.userMap.get(UserType.MANAGER);
        users.add(owner);
    }

    @Override
    public void handleTransactions(final User user, final double amount, final Commerciant commerciant) {
        if (amount < 0 && -amount > this.spendingLimit) {
            System.out.println("amount exceeds spending limit");
            return;
        }

        if (amount > 0 && amount > this.depositLimit) {
            System.out.println("amount exceeds deposit limit");
            return;
        }

        if (amount < 0) {
            // add the amount to the total spent of the user
            if (this.totalSpentBusiness.containsKey(user)) {
                Map<Commerciant, Double> spentMap = this.totalSpentBusiness.get(user);
                if (spentMap.containsKey(commerciant)) {
                    spentMap.put(commerciant, spentMap.get(commerciant) + amount);
                } else {
                    spentMap.put(commerciant, amount);
                }
            } else {
                Map<Commerciant, Double> spentMap = new HashMap<>();
                spentMap.put(commerciant, amount);
                this.totalSpentBusiness.put(user, spentMap);
            }
        } else {
            // add the amount to the total deposited of the user
            if (this.totalDeposited.containsKey(user)) {
                this.totalDeposited.put(user, this.totalDeposited.get(user) + amount);
            } else {
                this.totalDeposited.put(user, amount);
            }
        }
    }

    public void addBusinessAssociate(final User user, final UserType type) {
        List<User> users = this.userMap.get(type);
        users.add(user);
    }

    public double getTotalDepositedByUser(final User user) {
        if (!this.totalDeposited.containsKey(user)) {
            return 0;
        }

        return this.totalDeposited.get(user);
    }

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

    public double getTotalSpentAtCommerciant(final Commerciant commerciant) {
        double totalSpent = 0;
        for (Map.Entry<User, Map<Commerciant, Double>> entry : this.totalSpentBusiness.entrySet()) {
            if (entry.getValue().containsKey(commerciant)) {
                totalSpent += entry.getValue().get(commerciant);
            }
        }
        return totalSpent;
    }

    public UserType getUserType(final User user) {
        for (Map.Entry<UserType, List<User>> entry : this.userMap.entrySet()) {
            if (entry.getValue().contains(user)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Map<Commerciant, Double> getTotalSpentAtCommerciantsMap() {
        Map<Commerciant, Double> totalSpent = new HashMap<>();
        for (Map.Entry<User, Map<Commerciant, Double>> entry : this.totalSpentBusiness.entrySet()) {
            for (Map.Entry<Commerciant, Double> spentEntry : entry.getValue().entrySet()) {
                if (totalSpent.containsKey(spentEntry.getKey())) {
                    double newAmount = totalSpent.get(spentEntry.getKey()) + spentEntry.getValue();
                    totalSpent.put(spentEntry.getKey(), newAmount);
                } else {
                    totalSpent.put(spentEntry.getKey(), spentEntry.getValue());
                }
            }
        }
        return totalSpent;
    }

    public List<User> getUsersWhoSpentAtCommerciant(final Commerciant commerciant) {
        List<User> users = new ArrayList<>();
        for (Map.Entry<User, Map<Commerciant, Double>> entry : this.totalSpentBusiness.entrySet()) {
            if (entry.getValue().containsKey(commerciant)) {
                users.add(entry.getKey());
            }
        }
        return users;
    }


    public Map<UserType, List<User>> getUserMap() {
        return this.userMap;
    }

    public void setUserMap(Map<UserType, List<User>> userMap) {
        this.userMap = userMap;
    }

    public double getSpendingLimit() {
        return spendingLimit;
    }

    public void setSpendingLimit(double spendingLimit) {
        this.spendingLimit = spendingLimit;
    }

    public double getDepositLimit() {
        return depositLimit;
    }

    public void setDepositLimit(double depositLimit) {
        this.depositLimit = depositLimit;
    }

    public Map<User, Map<Commerciant, Double>> getTotalSpentBusiness() {
        return totalSpentBusiness;
    }

    public void setTotalSpentBusiness(Map<User, Map<Commerciant, Double>> totalSpentBusiness) {
        this.totalSpentBusiness = totalSpentBusiness;
    }

    public Map<User, Double> getTotalDeposited() {
        return totalDeposited;
    }

    public void setTotalDeposited(Map<User, Double> totalDeposited) {
        this.totalDeposited = totalDeposited;
    }
}
