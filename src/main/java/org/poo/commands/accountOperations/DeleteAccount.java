package org.poo.commands.accountOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.instances.User;
import org.poo.instances.CommandData;
import org.poo.commands.Command;
import org.poo.bankManager.Bank;
import org.poo.transactions.Transaction;

public class DeleteAccount implements Command {
    private CommandData command;
    private Bank bank;
    private ArrayNode output;

    public DeleteAccount(final CommandData commandData, final Bank bank, final ArrayNode output) {
        this.command = commandData;
        this.bank = bank;
        this.output = output;
    }

    /**
     * Execute the deleteAccount command.
     */
    @Override
    public void execute() {
        String email = this.command.getEmail();
        String iban = this.command.getAccount();
        int timestamp = this.command.getTimestamp();

        // get the owner of the account and the account to delete
        User owner = this.bank.getUserByEmail(email);
        Account toDelete = this.bank.getAccountByIban(iban);

        System.out.println(email + " 's accounts:");
        for (Account account : owner.getAccounts()) {
            System.out.print(account.getIban() + " ");
        }
        System.out.println();
        if (toDelete != null && toDelete.getAccountType().equals(Account.AccountType.BUSINESS)
                && !toDelete.getOwner().getEmail().equals(email)) {
            // TODO: “You are not authorized to make this transaction.”
            System.out.println("You are not authorized to make this transaction in delete account");
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "deleteAccount");
        ObjectNode deleteNode = objectMapper.createObjectNode();

        if (toDelete != null && toDelete.getBalance() == 0) {
            // if the account exists and has no funds, delete it
            deleteNode.put("success", "Account deleted");
            toDelete.getCards().clear();
            owner.getAccounts().remove(toDelete);
        } else {
            // if the account doesn't exist or has funds, return an error
            deleteNode.put("error",
                    "Account couldn't be deleted - see org.poo.transactions for details");
            if (toDelete != null) {
                System.out.println("account is classic "+ toDelete.getAccountType().equals(Account.AccountType.CLASSIC));

                // if the account exists but has funds, add an error transaction
                Transaction transaction;
                transaction = new Transaction.TransactionBuilder(timestamp,
                        "Account couldn't be deleted - there are funds remaining",
                        this.command.getCommand())
                        .error("failed to delete account")
                        .build();
                toDelete.addTransaction(transaction);
            }
        }
        deleteNode.put("timestamp", timestamp);
        objectNode.set("output", deleteNode);
        objectNode.put("timestamp", timestamp);
        this.output.add(objectNode);
    }

}
