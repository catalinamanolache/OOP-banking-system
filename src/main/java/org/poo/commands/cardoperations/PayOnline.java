package org.poo.commands.cardoperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.accounts.Account;
import org.poo.accounts.BusinessAccount;
import org.poo.cards.Card;
import org.poo.commands.Command;
import org.poo.instances.cashbackinstances.CashbackContext;
import org.poo.transactions.Transaction;
import org.poo.bankmanager.Bank;
import org.poo.bankmanager.CurrencyConverter;
import org.poo.instances.CommandData;
import org.poo.instances.Plan;
import org.poo.instances.User;
import org.poo.instances.Commerciant;
import org.poo.instances.jsonexceptions.JSONException;

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
     * @throws JSONException if the card is not found
     */
    @Override
    public void execute() throws JSONException {
        String cardNumber = this.command.getCardNumber();
        double amount = this.command.getAmount();
        String currency = this.command.getCurrency();
        String description = this.command.getDescription();
        String commerciantString = this.command.getCommerciant();
        String email = this.command.getEmail();
        int timestamp = this.command.getTimestamp();

        // you can't pay a non-existent amount
        if (amount <= 0) {
            return;
        }

        // get the user, card and account
        User user = this.bank.getUserByEmail(email);
        Card card = this.bank.getCardByCardNumber(cardNumber);
        Account account = this.bank.getAccountByCardNumber(cardNumber);

        if (account != null) {
            if (account.getAccountType().equals(Account.AccountType.BUSINESS)) {
                // for business accounts, check if the user is associated with the business, else
                // the card is not found
                BusinessAccount businessAccount = (BusinessAccount) account;
                if (!businessAccount.isUserAssociatedWithBusiness(user)) {
                    card = null;
                }
            } else if (!account.getOwner().getEmail().equals(email)) {
                // for personal accounts, check if the user is the owner of the account, else
                // the card is not found
                card = null;
            }
        }

        // if the card is not found, print an error message
        if (card == null) {
            throw new JSONException(this.command, "cardNotFound", this.output);
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

        // get the user's plan
        Plan.PlanType userPlan = user.getPlanType();

        // for business accounts, get the business owner's plan
        if (account.getAccountType().equals(Account.AccountType.BUSINESS)) {
            userPlan = account.getOwner().getPlanType();
        }

        // take the commission depending on the user's plan
        double commission = Plan.getCommission(userPlan, amountConverted,
                account.getCurrency());

        // get the commerciant
        Commerciant commerciant = this.bank.getCommerciantByName(commerciantString);

        // check if the account has enough money and pay
        boolean canPay = account.verifyTransaction(user, -amountConverted);

        // if the account has enough money, the user is allowed to make the transaction and the
        // commerciant is valid, pay the amount given
        if (card.getParentAccount().getBalance() >= amountConverted + commission && canPay
                && commerciant != null) {
            // create a cashback context for the current commerciant
            CashbackContext cashbackContext =
                    new CashbackContext(commerciant.getCashbackStrategy());

            // pay the amount and the commission
            card.pay(amountConverted + commission);

            // handle the transaction for business accounts
            account.handleMoneyTransactions(user, -amountConverted, commerciant);

            // handle the cashback process
            cashbackContext.handleCashbackTransaction(user, account, commerciant, amount,
                    amountConverted, currency);

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
            if (Plan.checkIfCanUpgrade(user)) {
                // add an upgrade transaction to the account
                transaction = new Transaction.TransactionBuilder(timestamp,
                        "Upgrade plan", "upgradePlan")
                        .newPlanType(Plan.PlanType.GOLD.toString().toLowerCase())
                        .account(account.getIban())
                        .build();
                account.addTransaction(transaction);
            }
        } else {
            // if the account doesn't have enough money, add an error transaction
            transaction = new Transaction.TransactionBuilder(timestamp,
                    "Insufficient funds", this.command.getCommand())
                    .build();
            account.addTransaction(transaction);
        }
    }
}
