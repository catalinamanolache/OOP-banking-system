package org.poo.instances;

import org.poo.accounts.Account;

import java.util.Map;

public class NrOfTransactions implements CashbackStrategy {
    @Override
    public double calculateCashback(Account account, User user, Commerciant commerciant) {
        double cashback = 0;
//        System.out.println("FutureCashback strategy: Number of transactions");

        Map<Commerciant.CommerciantType, Double> cashbackMap =
                account.getNrOfTransactionsCashback();

        if (!cashbackMap.containsKey(commerciant.getType())) {
            return -1;
        }

        int nrOfTransactions = account.getNrOfTransactions().get(commerciant);

        // TODO: Odată ce un cashback a fost primit, nu contează de la care comerciant, nu se va mai primi a2a oară.
        if (nrOfTransactions == 2) {
            cashbackMap.put(Commerciant.CommerciantType.Food, 0.02);
        } else if (nrOfTransactions == 5) {
            cashbackMap.put(Commerciant.CommerciantType.Food, 0.02);
            cashbackMap.put(Commerciant.CommerciantType.Clothes, 0.05);
        } else if (nrOfTransactions == 10) {
            cashbackMap.put(Commerciant.CommerciantType.Food, 0.02);
            cashbackMap.put(Commerciant.CommerciantType.Clothes, 0.05);
            cashbackMap.put(Commerciant.CommerciantType.Tech, 0.1);
        }
        System.out.println(user.getEmail() + " nr of transactions: " + nrOfTransactions + " at " + commerciant.getCommerciant());
//        System.out.println(" | future cashback: " + cashbackMap.get(Commerciant.CommerciantType.Food));
        return cashback;
    }
}
