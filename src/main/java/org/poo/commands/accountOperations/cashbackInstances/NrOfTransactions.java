package org.poo.commands.accountOperations.cashbackInstances;

import org.poo.accounts.Account;
import org.poo.instances.Commerciant;
import org.poo.instances.User;

import java.util.Map;

public class NrOfTransactions implements CashbackStrategy {
    @Override
    public double calculateCashback(Account account, User user, Commerciant commerciant) {
        double cashback = 0;
//        System.out.println("FutureCashback strategy: Number of transactions");

        Map<Commerciant.CommerciantType, Double> cashbackMap =
                account.getNrOfTransactionsCashback();

//        if (!cashbackMap.containsKey(Commerciant.CommerciantType.valueOf(commerciant.getType()))) {
//            return -1;
//        }

        int nrOfTransactions = account.getNrOfTransactions().get(commerciant);

        // TODO: Odată ce un cashback a fost primit, nu contează de la care comerciant, nu se va mai primi a2a oară.
        if (nrOfTransactions == 2) {
            cashbackMap.put(Commerciant.CommerciantType.Food, 0.002);
            System.out.println("will get cashback for food");
        } else if (nrOfTransactions == 5) {
            cashbackMap.put(Commerciant.CommerciantType.Clothes, 0.005);
            System.out.println("will get cashback for clothes");
        } else if (nrOfTransactions == 10) {
            cashbackMap.put(Commerciant.CommerciantType.Tech, 0.01);
            System.out.println("will get cashback for tech");
        }
        System.out.println(user.getEmail() + " nr of transactions: " + nrOfTransactions + " at " + commerciant.getCommerciant());
//        System.out.println(" | future cashback: " + cashbackMap.get(Commerciant.CommerciantType.Food));
        return cashback;
    }
}
