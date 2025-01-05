package org.poo.commands.accountOperations.cashbackInstances;

import org.poo.accounts.Account;
import org.poo.instances.Commerciant;
import org.poo.instances.Plan;
import org.poo.instances.User;

import java.util.Map;

public class SpendingThreshold implements CashbackStrategy {
    @Override
    public double calculateCashback(Account account, User user, Commerciant commerciant) {
        double cashback = 0;

//        System.out.println("FutureCashback strategy: Spending threshold");
        Map<Commerciant, Double> totalSpentMap = account.getTotalSpent();

        if (!totalSpentMap.containsKey(commerciant)) {
            return -1;
        }

        double totalSpent = totalSpentMap.get(commerciant);

        System.out.print(user.getEmail() + " totalSpent: " + totalSpent + " at " + commerciant.getCommerciant());
        if (totalSpent >= 100 && totalSpent < 300) {
            if (user.getPlanType().equals(Plan.PlanType.STANDARD) || user.getPlanType().equals(Plan.PlanType.STUDENT)) {
                cashback = 0.1 / 100;
            } else if (user.getPlanType().equals(Plan.PlanType.SILVER)) {
                cashback = 0.3 / 100;
            } else {
                cashback = 0.5 / 100;
            }
        } else if (totalSpent >= 300 && totalSpent < 500) {
            if (user.getPlanType().equals(Plan.PlanType.STANDARD) || user.getPlanType().equals(Plan.PlanType.STUDENT)) {
                cashback = 0.2 / 100;
            } else if (user.getPlanType().equals(Plan.PlanType.SILVER)) {
                cashback = 0.4 / 100;
            } else {
                cashback = 0.55 / 100;
            }
        } else if (totalSpent >= 500) {
            if (user.getPlanType().equals(Plan.PlanType.STANDARD) || user.getPlanType().equals(Plan.PlanType.STUDENT)) {
                cashback = 0.25 / 100;
            } else if (user.getPlanType().equals(Plan.PlanType.SILVER)) {
                cashback = 0.5 / 100;
            } else {
                cashback = 0.7 / 100;
            }
        }
        System.out.print(" | future cashback: " + cashback + "\n");
        return cashback;
    }
}
