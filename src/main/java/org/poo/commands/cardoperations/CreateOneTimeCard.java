package org.poo.commands.cardoperations;

import org.poo.accounts.Account;
import org.poo.cards.OneTimeCard;
import org.poo.instances.User;
import org.poo.instances.CommandData;
import org.poo.commands.Command;
import org.poo.bankmanager.Bank;
import org.poo.transactions.Transaction;

public class CreateOneTimeCard implements Command {
    private CommandData command;
    private Bank bank;

    public CreateOneTimeCard(final CommandData commandData, final Bank bank) {
        this.command = commandData;
        this.bank = bank;
    }

    /**
     * Execute the create one time command.
     */
    @Override
    public void execute() {
        String iban = this.command.getAccount();
        String email = this.command.getEmail();
        int timestamp = this.command.getTimestamp();

        // check if the user exists
        User owner = this.bank.getUserByEmail(email);
        if (owner != null) {
            // check if the account exists
            Account account = this.bank.getAccountByIban(iban);
            if (account != null) {
                // create a new one-time card
                OneTimeCard newCard = new OneTimeCard(account, owner);
                account.addCard(newCard);

                // create a transaction for creating the new card
                Transaction transaction;
                transaction = new Transaction.TransactionBuilder(timestamp,
                        "New card created", this.command.getCommand())
                        .account(iban)
                        .card(newCard.getCardNumber())
                        .cardHolder(owner.getEmail())
                        .build();
                account.addTransaction(transaction);
            } else {
                // create a transaction for the case when the account does not exist
                Transaction transaction;
                transaction = new Transaction.TransactionBuilder(timestamp,
                        "User not owner of account", this.command.getCommand())
                        .build();
                account.addTransaction(transaction);
            }
        }
    }
}
