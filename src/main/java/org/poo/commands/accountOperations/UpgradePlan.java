package org.poo.commands.accountOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.bankManager.Bank;
import org.poo.bankManager.CurrencyConverter;
import org.poo.commands.Command;
import org.poo.instances.CommandData;
import org.poo.instances.Plan;
import org.poo.instances.User;
import org.poo.transactions.Transaction;

public class UpgradePlan implements Command {
    private CommandData command;
    private Bank bank;
    private ArrayNode output;

    public UpgradePlan(final CommandData command, final Bank bank, final ArrayNode output) {
        this.command = command;
        this.bank = bank;
        this.output = output;
    }

    @Override
    public void execute() {
        String newPlanTypeString = this.command.getNewPlanType();
        String iban = this.command.getAccount();
        int timestamp = this.command.getTimestamp();

        Account account = this.bank.getAccountByIban(iban);
        if (account == null) {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode objectNode = objectMapper.createObjectNode();

            objectNode.put("command", this.command.getCommand());

            ObjectNode outputNode = objectMapper.createObjectNode();
            outputNode.put("timestamp", timestamp);
            outputNode.put("description", "Account not found");

            objectNode.set("output", outputNode);

            objectNode.put("timestamp", timestamp);

            this.output.add(objectNode);
            return;
        }
        User user = account.getOwner();

        Plan.PlanType oldPlanType = user.getPlanType();
        Plan.PlanType newPlanType = Plan.PlanType.valueOf(newPlanTypeString.toUpperCase());

        if (checkIfDowngrade(oldPlanType, newPlanType)) {
            // TODO: “You cannot downgrade your plan.”
            Transaction transaction;
            transaction = new Transaction.TransactionBuilder(timestamp,
                    "You cannot downgrade your plan.", this.command.getCommand())
                    .error("downgrade")
                    .build();
            account.addTransaction(transaction);
            System.out.println("You cannot downgrade your plan");
            return;
        }

        if (oldPlanType.equals(newPlanType)) {
            // TODO: “The user already has the ${newPlanType} plan.”
            Transaction transaction;
            transaction = new Transaction.TransactionBuilder(timestamp,
                    "The user already has the " + newPlanType.name().toLowerCase()
                            + " plan.", this.command.getCommand())
                    .error("already has the plan")
                    .build();
            account.addTransaction(transaction);
            System.out.println("The user already has the " + newPlanType.name().toLowerCase() + " plan");
            return;
        }

        double fee = Plan.getPlanFee(oldPlanType, newPlanType);
        double convertedFee = CurrencyConverter.convert("RON", account.getCurrency(),
                fee);

        // TODO: check with minBalance?
        if (convertedFee > account.getBalance()) {
            // TODO: “Insufficient funds.”
            Transaction transaction;
            transaction = new Transaction.TransactionBuilder(timestamp,
                    "Insufficient funds", this.command.getCommand())
                    .error("insufficient funds")
                    .build();
            account.addTransaction(transaction);
            System.out.println("Insufficient funds in upgrade plan");
            return;
        }

        account.withdraw(convertedFee);
        user.setPlanType(newPlanType);
        Transaction transaction;
        transaction = new Transaction.TransactionBuilder(timestamp,
                "Upgrade plan", this.command.getCommand())
                .account(iban)
                .newPlanType(newPlanTypeString)
                .build();
        account.addTransaction(transaction);
        // TODO: Utilizatorul nu trebuie să plăteasca neapărat fee-ul pentru upgrade de la silver
        //  la gold întrucât se va face upgrade automat dacă userul face 5 plăți de cel putin
        //  300RON fiecare. !!! adica in payOnline
        System.out.println("Upgrade plan");

    }
    // TODO: student to standard??
    private boolean checkIfDowngrade(Plan.PlanType currentPlan, Plan.PlanType newPlan) {
        return currentPlan.compareTo(newPlan) > 0;
    }
}
