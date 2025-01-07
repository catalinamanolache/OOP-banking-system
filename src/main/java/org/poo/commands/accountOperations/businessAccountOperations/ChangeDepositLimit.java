package org.poo.commands.accountOperations.businessAccountOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.BusinessAccount;
import org.poo.bankManager.Bank;
import org.poo.commands.Command;
import org.poo.instances.CommandData;
import org.poo.instances.User;

public class ChangeDepositLimit implements Command {
    private CommandData command;
    private Bank bank;
    private ArrayNode output;

    public ChangeDepositLimit(final CommandData command, final Bank bank, final ArrayNode output) {
        this.command = command;
        this.bank = bank;
        this.output = output;
    }

    /**
     * Executes the changeDepositLimit command.
     */
    @Override
    public void execute() {
        String email = this.command.getEmail();
        String iban = this.command.getAccount();
        double depositLimit = this.command.getAmount();
        int timestamp = this.command.getTimestamp();

        Account account = this.bank.getAccountByIban(iban);

        if (account == null) {
            return;
        }

        User user = this.bank.getUserByEmail(email);

        if (user == null) {
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();

        // if the account is not a business account, print an error
        if (!account.getAccountType().equals(Account.AccountType.BUSINESS)) {
            System.out.println("not a business account in change deposit limit.");
            ObjectNode resultNode = objectMapper.createObjectNode();
            resultNode.put("command", "changeSpendingLimit");

            ObjectNode outputNode = objectMapper.createObjectNode();
            outputNode.put("description", "This is not a business account");
            outputNode.put("timestamp", timestamp);
            resultNode.set("output", outputNode);
            resultNode.put("timestamp", timestamp);
            this.output.add(resultNode);
            return;
        }

        // if the user is not the owner of the account, print an error
        if (!account.getOwner().getEmail().equals(email)) {
            ObjectNode resultNode = objectMapper.createObjectNode();
            resultNode.put("command", this.command.getCommand());

            ObjectNode outputNode = objectMapper.createObjectNode();

            outputNode.put("description",
                    "You must be owner in order to change deposit limit.");
            outputNode.put("timestamp", timestamp);
            resultNode.put("timestamp", timestamp);
            resultNode.set("output", outputNode);
            this.output.add(resultNode);
            System.out.println("user " + email + " is not owenr of account " + iban + " the owner is " + account.getOwner().getEmail());
            return;
        }

        // change the deposit limit of the account
        BusinessAccount businessAccount = (BusinessAccount) account;
        businessAccount.setDepositLimit(depositLimit);
        System.out.println("Deposit limit changed successfully to " + depositLimit + " timestamp: " + timestamp);
    }
}
