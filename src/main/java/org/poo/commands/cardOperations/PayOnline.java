package org.poo.commands.cardOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.BusinessAccount;
import org.poo.cards.Card;
import org.poo.commands.Command;
import org.poo.commands.accountOperations.cashbackInstances.CashbackContext;
import org.poo.instances.CommandData;
import org.poo.transactions.Transaction;
import org.poo.bankManager.Bank;
import org.poo.bankManager.CurrencyConverter;
import org.poo.instances.Commerciant;
import org.poo.instances.Plan;
import org.poo.instances.User;

import java.util.Map;

public class PayOnline implements Command {
    private CommandData command;
    private Bank bank;
    private ArrayNode output;

    public PayOnline(final CommandData commandData, final Bank bank, final ArrayNode output) {
        this.command = commandData;
        this.bank = bank;
        this.output = output;
    }

    /**
     * Executes the payOnline command.
     */
    @Override
    public void execute() {
        String cardNumber = this.command.getCardNumber();
        double amount = this.command.getAmount();
        String currency = this.command.getCurrency();
        String description = this.command.getDescription();
        String commerciantString = this.command.getCommerciant();
        String email = this.command.getEmail();
        int timestamp = this.command.getTimestamp();

        if (amount == 0) {
            return;
        }

        // get the user, card and account
        User user = this.bank.getUserByEmail(email);
        Card card = this.bank.getCardByCardNumber(cardNumber);
        Account account = this.bank.getAccountByCardNumber(cardNumber);
//        System.out.println("timestamp " + timestamp + " new payOnline command " + user.getEmail());
//        System.out.println("commerciant string " + commerciantString);
//        System.out.println("card is " + card.getCardNumber() + " account is " + account.getIban() + " user who tries to pay " + email +" timestamp " + timestamp);
//        for (Card cardIter : account.getCards()) {
//            System.out.println("card " + cardIter.getCardNumber());
//        }

        if (account != null) {
            if (account.getAccountType().equals(Account.AccountType.BUSINESS)) {
                BusinessAccount businessAccount = (BusinessAccount) account;
                if (!businessAccount.isUserAssociatedWithBusiness(user)) {
                    card = null;
                }
            } else if (!account.getOwner().getEmail().equals(email)) {
                card = null;
            }
        }

        if (card == null) {
            // if the card is not found, print an error message
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode objectNode = objectMapper.createObjectNode();

            objectNode.put("command", this.command.getCommand());

            ObjectNode payNode = objectMapper.createObjectNode();
            payNode.put("timestamp", timestamp);
            payNode.put("description", "Card not found");

            objectNode.set("output", payNode);

            objectNode.put("timestamp", timestamp);

            this.output.add(objectNode);
            return;
        }

        Transaction transaction;
        if (card.getIsFrozen()) {
            // if the card is frozen, add an error transaction and return
            transaction = new Transaction.TransactionBuilder(timestamp,
                    "The card is frozen", this.command.getCommand())
                    .account(account.getIban())
                    .build();
            account.addTransaction(transaction);
            return;
        }

        // convert the amount to the account's currency
        double amountConverted = CurrencyConverter.convert(currency,
                card.getParentAccount().getCurrency(), amount);

        // take the commission depending on the user's plan
        double commission = Plan.getCommission(user.getPlanType(), amountConverted,
                account.getCurrency());

        Commerciant commerciant = this.bank.getCommerciantByName(commerciantString);

//            System.out.println("trying to pay amount " + amount + " amountConverted "
//                    + amountConverted + " commission " + commission + " balance" + card.getParentAccount().getBalance());
        // check if the account has enough money and pay
        boolean canPay = account.handleMoneyTransactions(user, -amountConverted, commerciant);

        if (card.getParentAccount().getBalance() >= amountConverted + commission && canPay
                && commerciant != null) {
            CashbackContext cashbackContext =
                    new CashbackContext(commerciant.getCashbackStrategy());

            card.pay(amountConverted + commission);

            System.out.println("payOnline balance before " + account.getBalance());
//                System.out.print(this.command.getCommand() + " | took commission " + commission + " for email " + email +
//                        " timestamp " + timestamp);
//                System.out.print(" | new balance " + account.getBalance() + "\n");


            // get the cashback discount benefit if the user reached the milestones for nrOfTransactions
            cashbackContext.useDiscount(user, account, commerciant, amountConverted);

            // update the total spent for the commerciant
            account.updateTotalSpent(commerciant, amount, currency);

            // update the number of transactions for the commerciant
            account.updateNrOfTransactions(commerciant);

            // calculate the future cashback for the sender
            cashbackContext.calculateCashback(account, user, commerciant);

            // get the cashback benefit for spending threshold
            cashbackContext.useCashback(account, commerciant, amountConverted);

            // add the successful transaction to the account
            transaction = new Transaction.TransactionBuilder(timestamp,
                    "Card payment", this.command.getCommand())
                    .amount(amountConverted)
                    .commerciant(commerciantString)
                    .build();
            account.addTransaction(transaction);

            // handle the transactions for a one time card
            card.handleTransactions(timestamp, this.command.getCommand(), account.getIban(),
                    cardNumber, card.getCardNumber(), account, email);

            // check if the user can upgrade its plan from silver to gold automatically
            if (user.getPlanType().equals(Plan.PlanType.SILVER)) {
                if(Plan.checkIfCanUpgrade(user)) {
                    // add an upgrade transaction to the account
                    transaction = new Transaction.TransactionBuilder(timestamp,
                            "Upgrade plan", "upgradePlan")
                            .newPlanType(Plan.PlanType.GOLD.toString().toLowerCase())
                            .account(account.getIban())
                            .build();
                    account.addTransaction(transaction);
                    System.out.println("user " + user.getEmail() + " upgraded from silver to gold");
//                    for (Map.Entry<Commerciant, Double> cashback : account.getSpendingThresholdCashback().entrySet()) {
//                        System.out.println("Commerciant " + cashback.getKey().getCommerciant() + " cashback percentage " + cashback.getValue());
//                        double totalSpent = account.getTotalSpent().get(cashback.getKey());
//                        if (totalSpent >= 100 && totalSpent < 300) {
//                            account.getSpendingThresholdCashback().put(cashback.getKey(), 0.005);
//                        } else if (totalSpent >= 300 && totalSpent < 500) {
//                            account.getSpendingThresholdCashback().put(cashback.getKey(), 0.0055);
//                        } else if (totalSpent >= 500) {
//                            account.getSpendingThresholdCashback().put(cashback.getKey(), 0.007);
//                        }
//                        System.out.println("Commerciant " + cashback.getKey().getCommerciant() + " new cashback percentage " + cashback.getValue());
//                    }
                }
            }

            System.out.println("timestamp " + timestamp + " Paid " + amount + " currency amount " + amountConverted + account.getCurrency() + " commision " + commission + " to " + commerciantString + " with card " +
                    cardNumber + " from account " + account.getIban()+ " with balance " + account.getBalance() + " and plan " + account.getOwner().getPlanType());

        } else {
            // if the account doesn't have enough money, add an error transaction
            transaction = new Transaction.TransactionBuilder(timestamp,
                    "Insufficient funds", this.command.getCommand())
                    .build();
            account.addTransaction(transaction);
        }
    }
}
