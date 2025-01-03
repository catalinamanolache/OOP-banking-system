package org.poo.instances;

import org.poo.accounts.Account;
import org.poo.bankManager.CurrencyConverter;
import org.poo.transactions.Transaction;

public class Plan {
    public enum PlanType {
        STUDENT, STANDARD, SILVER, GOLD
    }

    private Plan() {
    }

    public static double getCommission(final PlanType planType, final double amount,
                                       final String currency) {
        if (planType.equals(PlanType.STANDARD)) {
            return amount * 0.2 / 100;
        } else if (planType.equals(PlanType.SILVER)) {
            double amountConverted = CurrencyConverter.convert(currency, "RON", amount);
            if (amountConverted >= 500) {
                return amount * 0.1 / 100;
            }
        }
        return 0;
    }

    public static double getPlanFee(final PlanType fromPlanType, final PlanType toPlanType) {
        if (fromPlanType.equals(PlanType.STUDENT) || fromPlanType.equals(PlanType.STANDARD)) {
            if (toPlanType.equals(PlanType.SILVER)) {
                return 100;
            } else if (toPlanType.equals(PlanType.GOLD)) {
                return 350;
            }
        } else {
            if (toPlanType.equals(PlanType.GOLD)) {
                return 250;
            }
        }
        return -1;
    }

    public static void checkIfCanUpgrade(User user) {
        // TODO: Utilizatorul nu trebuie să plăteasca neapărat fee-ul pentru upgrade de la silver
        //  la gold întrucât se va face upgrade automat dacă userul face 5 plăți de cel putin
        //  300RON fiecare. !!! adica in payOnline
        int validTransactionsCount = 0;
        for (Account userAccount : user.getAccounts()) {
            for (Transaction transaction : userAccount.getTransactions()) {
                if (transaction.getTransactionType().equals("payOnline")) {
                    double amount = transaction.getAmount();
                    double amountConverted = CurrencyConverter.convert(userAccount.getCurrency(),
                            "RON", amount);

                    if (amountConverted >= 300) {
                        validTransactionsCount++;
                    }

                    if (validTransactionsCount >= 5) {
                        user.setPlanType(PlanType.GOLD);
                        break;
                    }
                }
            }
        }
    }
}
