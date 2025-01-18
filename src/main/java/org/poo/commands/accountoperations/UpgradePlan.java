package org.poo.commands.accountoperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.accounts.Account;
import org.poo.bankmanager.Bank;
import org.poo.bankmanager.CurrencyConverter;
import org.poo.commands.Command;
import org.poo.transactions.Transaction;
import org.poo.instances.Plan;
import org.poo.instances.User;
import org.poo.instances.CommandData;
import org.poo.instances.jsonexceptions.JSONException;

public class UpgradePlan implements Command {
    private CommandData command;
    private Bank bank;
    private ArrayNode output;

    public UpgradePlan(final CommandData command, final Bank bank, final ArrayNode output) {
        this.command = command;
        this.bank = bank;
        this.output = output;
    }

    /**
     * Executes the upgradePlan command.
     * @throws JSONException if the account is not found
     */
    @Override
    public void execute() throws JSONException {
        String newPlanTypeString = this.command.getNewPlanType();
        String iban = this.command.getAccount();
        int timestamp = this.command.getTimestamp();

        // get the account by iban
        Account account = this.bank.getAccountByIban(iban);

        // if the account is not found, print an error
        if (account == null) {
            throw new JSONException(this.command, "accountNotFound", this.output);
        }

        // get the account's owner
        User user = account.getOwner();

        // get the old plan type and the new plan type that the user wants to upgrade to
        Plan.PlanType oldPlanType = user.getPlanType();
        Plan.PlanType newPlanType = Plan.PlanType.valueOf(newPlanTypeString.toUpperCase());

        // if the user tries to downgrade the plan, create an error transaction
        if (Plan.checkIfDowngrade(oldPlanType, newPlanType)) {
            Transaction transaction;
            transaction = new Transaction.TransactionBuilder(timestamp,
                    "You cannot downgrade your plan.", this.command.getCommand())
                    .error("downgrade")
                    .build();
            account.addTransaction(transaction);
            return;
        }

        // if the user already has the new plan type, create an error transaction
        if (oldPlanType.equals(newPlanType)) {
            Transaction transaction;
            transaction = new Transaction.TransactionBuilder(timestamp,
                    "The user already has the " + newPlanType.name().toLowerCase()
                            + " plan.", this.command.getCommand())
                    .error("already has the plan")
                    .build();
            account.addTransaction(transaction);
            return;
        }

        // get the fee for upgrading the plan and convert it to the account's currency
        double fee = Plan.getPlanFee(oldPlanType, newPlanType);
        double convertedFee = CurrencyConverter.convert("RON", account.getCurrency(),
                fee);

        // if the user doesn't have enough funds to upgrade, create an error transaction
        if (convertedFee > account.getBalance()) {
            Transaction transaction;
            transaction = new Transaction.TransactionBuilder(timestamp,
                    "Insufficient funds", this.command.getCommand())
                    .error("insufficient funds")
                    .build();
            account.addTransaction(transaction);
            return;
        }

        // withdraw the fee from the account and upgrade the user's plan
        account.withdraw(convertedFee);
        user.setPlanType(newPlanType);

        // create a successful transaction for the upgrade
        Transaction transaction;
        transaction = new Transaction.TransactionBuilder(timestamp,
                "Upgrade plan", this.command.getCommand())
                .account(iban)
                .newPlanType(newPlanTypeString)
                .build();
        account.addTransaction(transaction);
    }
}
