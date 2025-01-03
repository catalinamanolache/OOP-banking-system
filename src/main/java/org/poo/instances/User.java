package org.poo.instances;

import org.poo.accounts.Account;
import org.poo.bankManager.CurrencyConverter;
import org.poo.cards.Card;
import org.poo.fileio.UserInput;
import org.poo.transactions.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Map;

class AgeCalculator {
    private AgeCalculator() {
    }

    public static int computeAge(String birthDateString) {
        LocalDate presentDate = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthDate = LocalDate.parse(birthDateString, formatter);

        return Period.between(birthDate, presentDate).getYears();
    }
}

public final class User {
    private String firstName;
    private String lastName;
    private String email;
    private String birthDate;
    private String occupation;

    private List<Account> accounts;
    private int age;
    private Plan.PlanType planType;

//    private Map<Commerciant, Double> totalSpent;
//    private Map<Commerciant, Integer> nrOfTransactions;
//    private Map<Commerciant.CommerciantType, Double> nrOfTransactionsCashback;
//    private double spendingThresholdCashback;

    public User(final UserInput user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.birthDate = user.getBirthDate();
        this.occupation = user.getOccupation();
        this.accounts = new ArrayList<>();
        this.age = AgeCalculator.computeAge(this.birthDate);
        if (this.occupation.equals("student")) {
            this.planType = Plan.PlanType.STUDENT;
        } else {
            this.planType = Plan.PlanType.STANDARD;
        }
//        this.totalSpent = new HashMap<>();
//        this.nrOfTransactions = new HashMap<>();
//        this.nrOfTransactionsCashback = new HashMap<>();
//        this.spendingThresholdCashback = 0;
    }

//    /**
//     * Update the total amount spent at a given commerciant. The total spent will be stored in RON.
//     * @param commerciant the commerciant to update the total spent for
//     * @param amount the amount to add to the total spent
//     */
//    public void updateTotalSpent(Commerciant commerciant, double amount, String currency) {
//        double amountConverted = CurrencyConverter.convert(currency, "RON", amount);
//        if (this.totalSpent.containsKey(commerciant)) {
//            double newAmount = this.totalSpent.get(commerciant) + amountConverted;
//            this.totalSpent.put(commerciant, newAmount);
//        } else {
//            this.totalSpent.put(commerciant, amountConverted);
//        }
//    }
//
//    /**
//     * Update the number of transactions made at a given commerciant.
//     * @param commerciant the commerciant to update the number of transactions for
//     */
//    public void updateNrOfTransactions(Commerciant commerciant) {
//        if (this.nrOfTransactions.containsKey(commerciant)) {
//            int newNumber = this.nrOfTransactions.get(commerciant) + 1;
//            this.nrOfTransactions.put(commerciant, newNumber);
//        } else {
//            this.nrOfTransactions.put(commerciant, 1);
//        }
//    }

    /**
     * Add an account to the user's accounts.
     * @param account the account to add
     */
    public void addAccount(final Account account) {
        accounts.add(account);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(final List<Account> accounts) {
        this.accounts = accounts;
    }

    public Plan.PlanType getPlanType() {
        return planType;
    }

    public void setPlanType(Plan.PlanType planType) {
        this.planType = planType;
    }

//    public Map<Commerciant, Double> getTotalSpentBusiness() {
//        return totalSpent;
//    }
//
//    public void setTotalSpentBusiness(Map<Commerciant, Double> totalSpent) {
//        this.totalSpent = totalSpent;
//    }
//
//    public Map<Commerciant, Integer> getNrOfTransactions() {
//        return nrOfTransactions;
//    }
//
//    public void setNrOfTransactions(Map<Commerciant, Integer> nrOfTransactions) {
//        this.nrOfTransactions = nrOfTransactions;
//    }
//
//    public Map<Commerciant.CommerciantType, Double> getNrOfTransactionsCashback() {
//        return nrOfTransactionsCashback;
//    }
//
//    public void setNrOfTransactionsCashback(Map<Commerciant.CommerciantType, Double> nrOfTransactionsCashback) {
//        this.nrOfTransactionsCashback = nrOfTransactionsCashback;
//    }
//
//    public double getSpendingThresholdCashback() {
//        return spendingThresholdCashback;
//    }
//
//    public void setSpendingThresholdCashback(double spendingThresholdCashback) {
//        this.spendingThresholdCashback = spendingThresholdCashback;
//    }
}
