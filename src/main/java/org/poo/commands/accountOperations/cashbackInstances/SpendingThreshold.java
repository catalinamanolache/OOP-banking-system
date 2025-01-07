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

        Plan.PlanType planType = user.getPlanType();
        if (!account.getOwner().equals(user)) {
            planType = account.getOwner().getPlanType();
        }
        
        double totalSpent = totalSpentMap.get(commerciant);
        double cashback = 0;
//        System.out.print(user.getEmail() + " totalSpent: " + totalSpent + " at " + commerciant.getCommerciant());
        if (totalSpent >= 100 && totalSpent < 300) {
            if (planType.equals(Plan.PlanType.STANDARD) || planType.equals(Plan.PlanType.STUDENT)) {
                cashback = 0.1 / 100;
            } else if (planType.equals(Plan.PlanType.SILVER)) {
                cashback = 0.3 / 100;
            } else {
                cashback = 0.5 / 100;
            }
        } else if (totalSpent >= 300 && totalSpent < 500) {
            if (planType.equals(Plan.PlanType.STANDARD) || planType.equals(Plan.PlanType.STUDENT)) {
                cashback = 0.2 / 100;
            } else if (planType.equals(Plan.PlanType.SILVER)) {
                cashback = 0.4 / 100;
            } else {
                cashback = 0.55 / 100;
            }
        } else if (totalSpent >= 500) {
            if (planType.equals(Plan.PlanType.STANDARD) || planType.equals(Plan.PlanType.STUDENT)) {
                cashback = 0.25 / 100;
            } else if (planType.equals(Plan.PlanType.SILVER)) {
                cashback = 0.5 / 100;
            } else {
                cashback = 0.7 / 100;
            }
        }
        cashbackMap.put(commerciant, cashback);
//        System.out.print(" | future cashback: " + cashback + "\n");
    }
}
