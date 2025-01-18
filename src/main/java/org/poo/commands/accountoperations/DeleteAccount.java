package org.poo.commands.accountoperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.instances.jsonexceptions.JSONException;
import org.poo.instances.jsonexceptions.PrintJSONMessages;
import org.poo.instances.User;
import org.poo.instances.CommandData;
import org.poo.commands.Command;
import org.poo.bankmanager.Bank;
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
     * @throws JSONException if the account is not found or has funds when trying to delete
     */
    @Override
    public void execute() throws JSONException {
        String email = this.command.getEmail();
        String iban = this.command.getAccount();
        int timestamp = this.command.getTimestamp();

        // get the owner of the account and the account to delete
        User owner = this.bank.getUserByEmail(email);
        Account toDelete = this.bank.getAccountByIban(iban);

       // if this is a business account and you are not the owner of the account, return
        if (toDelete != null && toDelete.getAccountType().equals(Account.AccountType.BUSINESS)
                && !toDelete.getOwner().getEmail().equals(email)) {
            return;
        }

        // if the account exists and has no funds, delete it
        if (toDelete != null && toDelete.getBalance() == 0) {
            ObjectNode result = PrintJSONMessages.successDeleteAccount(this.command);
            this.output.add(result);

            toDelete.getCards().clear();
            owner.getAccounts().remove(toDelete);
        } else {

            // if the account exists but has funds, add an error transaction
            if (toDelete != null) {
                Transaction transaction;
                transaction = new Transaction.TransactionBuilder(timestamp,
                        "Account couldn't be deleted - there are funds remaining",
                        this.command.getCommand())
                        .error("failed to delete account")
                        .build();
                toDelete.addTransaction(transaction);
            }

            // if the account doesn't exist or has funds, return an error
            throw new JSONException(this.command, "failedDeleteAccount", this.output);
        }
    }

}
