package org.poo.commands.accountOperations.cashbackInstances;

import org.poo.accounts.Account;
import org.poo.instances.Commerciant;
import org.poo.instances.Plan;
import org.poo.instances.User;

import java.util.Map;

public class SpendingThreshold implements CashbackStrategy {
    @Override
    public void calculateCashback(Account account, User user, Commerciant commerciant) {
//        System.out.println("FutureCashback strategy: Spending threshold");
        Map<Commerciant, Double> totalSpentMap = account.getTotalSpent();
        Map<Commerciant, Double> cashbackMap = account.getSpendingThresholdCashback();

        // can comment next line?
//        if (!totalSpentMap.containsKey(commerciant)) {
//            return;
//        }

        double totalSpent = 0;
        for (Map.Entry<Commerciant, Double> entry : totalSpentMap.entrySet()) {
            if (entry.getKey().getCashbackStrategy().equals("spendingThreshold")) {
                totalSpent += entry.getValue();
            }
        }

        Plan.PlanType planType = user.getPlanType();
        if (!account.getOwner().equals(user)) {
            planType = account.getOwner().getPlanType();
        }
//        System.out.println("user plan type " + user.getPlanType() + " owner plan type " + account.getOwner().getPlanType());

        for (Map.Entry<Commerciant, Double> entry : cashbackMap.entrySet()) {
            System.out.println("cashbackMap " + entry.getKey().getCommerciant() + " " + entry.getValue());
        }
        
//        double totalSpent = totalSpentMap.get(commerciant);
        double cashback = 0;

        System.out.println(user.getEmail() + " totalSpent: " + totalSpent);
        if (totalSpent >= 100 && totalSpent < 300) {
            if (planType.equals(Plan.PlanType.STANDARD) || planType.equals(Plan.PlanType.STUDENT)) {
                cashback = 0.001;
            } else if (planType.equals(Plan.PlanType.SILVER)) {
                cashback = 0.003;
            } else {
                cashback = 0.005;
            }
        } else if (totalSpent >= 300 && totalSpent < 500) {
            if (planType.equals(Plan.PlanType.STANDARD) || planType.equals(Plan.PlanType.STUDENT)) {
                cashback = 0.002;
            } else if (planType.equals(Plan.PlanType.SILVER)) {
                cashback = 0.004;
            } else {
                cashback = 0.0055;
            }
        } else if (totalSpent >= 500) {
            if (planType.equals(Plan.PlanType.STANDARD) || planType.equals(Plan.PlanType.STUDENT)) {
                cashback = 0.0025;
            } else if (planType.equals(Plan.PlanType.SILVER)) {
                cashback = 0.005;
            } else {
                cashback = 0.007;
            }
        }

//        for (Map.Entry<Commerciant, Double> entry : cashbackMap.entrySet()) {
//            cashbackMap.put(entry.getKey(), cashback);
//        }
        cashbackMap.put(commerciant, cashback);
        account.setSpendingThresholdTotal(cashback);
        System.out.println("spendingThreshold future cashback: " + cashback + " at " + commerciant.getCommerciant());
    }
}
