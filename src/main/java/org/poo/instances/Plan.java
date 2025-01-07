package org.poo.instances;

import org.poo.accounts.Account;
import org.poo.bankManager.CurrencyConverter;
import org.poo.transactions.Transaction;

import static org.poo.instances.Constants.SILVER_THRESHOLD;
import static org.poo.instances.Constants.SILVER_COMMISSION;
import static org.poo.instances.Constants.STANDARD_COMMISSION;
import static org.poo.instances.Constants.STANDARD_TO_GOLD_FEE;
import static org.poo.instances.Constants.STANDARD_TO_SILVER_FEE;
import static org.poo.instances.Constants.SILVER_TO_GOLD_FEE;
import static org.poo.instances.Constants.AUTO_UPGRADE_THRESHOLD;
import static org.poo.instances.Constants.AUTO_UPGRADE_NUMBER;

public final class Plan {
    public enum PlanType {
        STUDENT, STANDARD, SILVER, GOLD
    }

    private Plan() {
    }

    /**
     * Calculates the commission for a transaction based on the plan type of the user.
     * @param planType the plan type of the user
     * @param amount the amount of the transaction
     * @param currency the currency of the transaction
     * @return the commission for the transaction
     */
    public static double getCommission(final PlanType planType, final double amount,
                                       final String currency) {
        if (planType.equals(PlanType.STANDARD)) {
            return amount * STANDARD_COMMISSION;
        } else if (planType.equals(PlanType.SILVER)) {
            // check if the amount is greater than the threshold in RON
            double amountConverted = CurrencyConverter.convert(currency, "RON", amount);
            if (amountConverted >= SILVER_THRESHOLD) {
                return amount * SILVER_COMMISSION;
            }
        }

        // for STUDENT and GOLD plans
        return 0;
    }

    /**
     * Returns the fee for upgrading from a plan type to another.
     * @param fromPlanType the plan type to upgrade from
     * @param toPlanType the plan type to upgrade to
     * @return the fee for upgrading from the fromPlanType to the toPlanType
     */
    public static double getPlanFee(final PlanType fromPlanType, final PlanType toPlanType) {
        if (fromPlanType.equals(PlanType.STUDENT) || fromPlanType.equals(PlanType.STANDARD)) {
            if (toPlanType.equals(PlanType.SILVER)) {
                return STANDARD_TO_SILVER_FEE;
            } else if (toPlanType.equals(PlanType.GOLD)) {
                return STANDARD_TO_GOLD_FEE;
            }
        } else {
            if (toPlanType.equals(PlanType.GOLD)) {
                return SILVER_TO_GOLD_FEE;
            }
        }
        return -1;
    }

    /**
     * Checks if a user tries to downgrade his plan.
     * @param currentPlan the current plan of the user
     * @param newPlan the new plan the user wants to upgrade to
     * @return true if the user tries to downgrade, false otherwise
     */
    public static boolean checkIfDowngrade(Plan.PlanType currentPlan, Plan.PlanType newPlan) {
        return currentPlan.compareTo(newPlan) > 0;
    }

    /**
     * Checks if a user can upgrade automatically from silver to gold
     * based on the transactions made.
     * @param user the user to check if can upgrade
     */
    public static void checkIfCanUpgrade(final User user) {
        int validTransactionsCount = 0;

        if (!user.getPlanType().equals(PlanType.SILVER)) {
            return;
        }

        // count the number of transactions that are greater than 300 RON
        for (Account userAccount : user.getAccounts()) {
            for (Transaction transaction : userAccount.getTransactions()) {
                if (transaction.getTransactionType().equals("payOnline")
                        || transaction.getTransactionType().equals("sendMoney")) {
                    double amount = transaction.getAmount();
                    double amountConverted = CurrencyConverter.convert(userAccount.getCurrency(),
                            "RON", amount);

                    if (amountConverted >= AUTO_UPGRADE_THRESHOLD) {
                        validTransactionsCount++;
                    }

                    // if the user has at least 5 transactions greater than 300 RON, he can upgrade
                    if (validTransactionsCount >= AUTO_UPGRADE_NUMBER) {
                        user.setPlanType(PlanType.GOLD);
                        break;
                    }
                }
            }
        }
    }
}
