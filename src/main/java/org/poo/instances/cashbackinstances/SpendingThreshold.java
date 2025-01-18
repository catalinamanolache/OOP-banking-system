package org.poo.instances.cashbackinstances;

import org.poo.accounts.Account;
import org.poo.instances.Commerciant;
import org.poo.instances.Plan;
import org.poo.instances.User;

import java.util.Map;

import static org.poo.instances.Constants.SPENDING_THRESHOLD_FIRST_THRESHOLD;
import static org.poo.instances.Constants.SPENDING_THRESHOLD_SECOND_THRESHOLD;
import static org.poo.instances.Constants.SPENDING_THRESHOLD_THIRD_THRESHOLD;
import static org.poo.instances.Constants.STANDARD_CASHBACK_100;
import static org.poo.instances.Constants.STANDARD_CASHBACK_300;
import static org.poo.instances.Constants.STANDARD_CASHBACK_500;
import static org.poo.instances.Constants.SILVER_CASHBACK_100;
import static org.poo.instances.Constants.SILVER_CASHBACK_300;
import static org.poo.instances.Constants.SILVER_CASHBACK_500;
import static org.poo.instances.Constants.GOLD_CASHBACK_100;
import static org.poo.instances.Constants.GOLD_CASHBACK_300;
import static org.poo.instances.Constants.GOLD_CASHBACK_500;

public class SpendingThreshold implements CashbackStrategy {

    /**
     * Calculates the spending threshold cashback for a user, taking into consideration the total
     * spent for all commerciants that have the spendingThreshold strategy and the plan type
     * of the user.
     * @param account the account
     * @param user the user
     * @param commerciant the commerciant at which the user is paying
     */
    @Override
    public void calculateCashback(final Account account, final User user,
                                  final Commerciant commerciant) {
        Map<Commerciant, Double> totalSpentMap = account.getTotalSpent();
        Map<Commerciant, Double> cashbackMap = account.getSpendingThresholdCashback();

        // compute the total spent for all commerciants that have the spendingThreshold strategy
        double totalSpent = 0;
        for (Map.Entry<Commerciant, Double> entry : totalSpentMap.entrySet()) {
            if (entry.getKey().getCashbackStrategy().equals("spendingThreshold")) {
                totalSpent += entry.getValue();
            }
        }

        // get the plan type of the user that made the transaction
        Plan.PlanType planType = user.getPlanType();
        if (!account.getOwner().equals(user)) {
            planType = account.getOwner().getPlanType();
        }

        // compute the cashback based on the total spent of the user and the plan type
        double cashback = 0;
        if (totalSpent >= SPENDING_THRESHOLD_FIRST_THRESHOLD
                && totalSpent < SPENDING_THRESHOLD_SECOND_THRESHOLD) {
            if (planType.equals(Plan.PlanType.STANDARD) || planType.equals(Plan.PlanType.STUDENT)) {
                cashback = STANDARD_CASHBACK_100;
            } else if (planType.equals(Plan.PlanType.SILVER)) {
                cashback = SILVER_CASHBACK_100;
            } else {
                cashback = GOLD_CASHBACK_100;
            }
        } else if (totalSpent >= SPENDING_THRESHOLD_SECOND_THRESHOLD
                && totalSpent < SPENDING_THRESHOLD_THIRD_THRESHOLD) {
            if (planType.equals(Plan.PlanType.STANDARD) || planType.equals(Plan.PlanType.STUDENT)) {
                cashback = STANDARD_CASHBACK_300;
            } else if (planType.equals(Plan.PlanType.SILVER)) {
                cashback = SILVER_CASHBACK_300;
            } else {
                cashback = GOLD_CASHBACK_300;
            }
        } else if (totalSpent >= SPENDING_THRESHOLD_THIRD_THRESHOLD) {
            if (planType.equals(Plan.PlanType.STANDARD) || planType.equals(Plan.PlanType.STUDENT)) {
                cashback = STANDARD_CASHBACK_500;
            } else if (planType.equals(Plan.PlanType.SILVER)) {
                cashback = SILVER_CASHBACK_500;
            } else {
                cashback = GOLD_CASHBACK_500;
            }
        }
        cashbackMap.put(commerciant, cashback);
    }
}
