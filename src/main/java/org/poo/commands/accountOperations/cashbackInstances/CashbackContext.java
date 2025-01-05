package org.poo.commands.accountOperations.cashbackInstances;

import org.poo.accounts.Account;
import org.poo.instances.Commerciant;
import org.poo.instances.User;

import java.util.Map;

public class CashbackContext {
    private final CashbackStrategy strategy;

    public CashbackContext(String strategy) {
        switch (strategy) {
            case "nrOfTransactions":
                this.strategy = new NrOfTransactions();
                break;
            case "spendingThreshold":
                this.strategy = new SpendingThreshold();
                break;
            default:
                this.strategy = null;
                break;
        }
    }

    public void calculateFutureCashback(Account account, User user, Commerciant commerciant) {
        double futureCashback = this.strategy.calculateCashback(account, user, commerciant);
        if (futureCashback > 0) {
            account.setSpendingThresholdCashback(futureCashback);
        }
    }

    public void useDiscountCashback(User user, Account account, Commerciant commerciant, double amount) {
        for (Map.Entry<Commerciant.CommerciantType, Double> entry : account.getNrOfTransactionsCashback().entrySet()) {
//            System.out.println(entry.getKey() + " " + entry.getValue());
            if (entry.getKey().equals(Commerciant.CommerciantType.valueOf(commerciant.getType()))) {
                double cashback = entry.getValue();
                account.getNrOfTransactionsCashback().remove(entry.getKey());
                System.out.println("will get discount for " +commerciant.getType() + " balance before " + account.getBalance());
                account.deposit(amount * cashback);
                System.out.println("Deposited " + amount * cashback + account.getCurrency() + " user " + user.getEmail());
                System.out.println("Balance after and before commision " + account.getBalance());
                break;
            }
        }
    }

    public void useCashback(User user, Account account, Commerciant commerciant, double amount) {
//        for (Map.Entry<Commerciant.CommerciantType, Double> entry : user.getNrOfTransactionsCashback().entrySet()) {
//            System.out.println(entry.getKey() + " " + entry.getValue());
//            if (entry.getKey().equals(Commerciant.CommerciantType.valueOf(commerciant.getType()))) {
//                double cashback = entry.getValue();
//                user.getNrOfTransactionsCashback().remove(entry.getKey());
////                System.out.println("balance before " + account.getBalance());
//                account.deposit(amount * cashback);
//                System.out.println("Deposited " + amount * cashback + account.getCurrency() + " user " + user.getEmail());
////                System.out.println("Balance after " + account.getBalance());
//
//                return;
//            }
//        }

        double cashback = account.getSpendingThresholdCashback();
        if (cashback > 0) {
            account.setSpendingThresholdCashback(0);
            System.out.println("discount spending treshold balance before " + account.getBalance());
            account.deposit(amount * cashback);
            System.out.println("Deposited " + amount * cashback + account.getCurrency() + " user " + user.getEmail());
            System.out.println("Balance after and before commision " + account.getBalance());
        }
    }
}
