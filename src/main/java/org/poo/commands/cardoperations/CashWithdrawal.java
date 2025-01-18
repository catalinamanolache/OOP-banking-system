package org.poo.commands.cardoperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.accounts.Account;
import org.poo.bankmanager.Bank;
import org.poo.bankmanager.CurrencyConverter;
import org.poo.cards.Card;
import org.poo.commands.Command;
import org.poo.transactions.Transaction;
import org.poo.instances.CommandData;
import org.poo.instances.Plan;
import org.poo.instances.User;
import org.poo.instances.jsonexceptions.JSONException;

public class CashWithdrawal implements Command {
    private CommandData command;
    private Bank bank;
    private ArrayNode output;

    public CashWithdrawal(final CommandData command, final Bank bank, final ArrayNode output) {
        this.command = command;
        this.bank = bank;
        this.output = output;
    }

    /**
     * Executes the cashWithdrawal command.
     * @throws JSONException if the user or card are not found or the card does not
     * belong to the user
     */
    @Override
    public void execute() throws JSONException {
        String cardNumber = this.command.getCardNumber();
        double amount = this.command.getAmount();
        String email = this.command.getEmail();
        String location = this.command.getLocation();
        int timestamp = this.command.getTimestamp();

        User user = this.bank.getUserByEmail(email);

        // if the user is not found, print an error
        if (user == null) {
            throw new JSONException(this.command, "userNotFound", this.output);
        }

        // get the account and card by card number
        Account account = this.bank.getAccountByCardNumber(cardNumber);
        Card card = this.bank.getCardByCardNumber(cardNumber);

        // if the account is not found or the card does not belong to the user, print an error
        if (card == null || !card.getOwner().equals(user)) {
            throw new JSONException(this.command, "cardNotFound", this.output);
        }

        // convert the amount to the account's currency, since we only withdraw RON
        double amountConverted = CurrencyConverter.convert("RON", account.getCurrency(),
                amount);

        // get the user's plan
        Plan.PlanType userPlan = user.getPlanType();
        if (account.getAccountType().equals(Account.AccountType.BUSINESS)) {
            userPlan = account.getOwner().getPlanType();
        }

        // take the commission depending on the user's plan
        double commission = Plan.getCommission(userPlan, amountConverted,
                account.getCurrency());

        // if the user doesn't have enough funds, print an error
        if (amountConverted + commission > account.getBalance()) {
            Transaction transaction;
            transaction = new Transaction.TransactionBuilder(timestamp,
                    "Insufficient funds", this.command.getCommand())
                    .error("Insufficient funds")
                    .build();
            account.addTransaction(transaction);
            return;
        }

        // withdraw the amount and the commission and add a transaction
        account.withdraw(amountConverted + commission);

        Transaction transaction;
        transaction = new Transaction.TransactionBuilder(timestamp,
                "Cash withdrawal of " + amount, this.command.getCommand())
                .amount(amount)
                .build();
        account.addTransaction(transaction);
    }
}
