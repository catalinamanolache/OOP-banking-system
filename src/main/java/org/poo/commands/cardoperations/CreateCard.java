package org.poo.commands.cardoperations;

import org.poo.accounts.Account;
import org.poo.cards.Card;
import org.poo.commands.Command;
import org.poo.transactions.Transaction;
import org.poo.instances.User;
import org.poo.instances.CommandData;
import org.poo.bankmanager.Bank;

public class CreateCard implements Command {
    private CommandData command;
    private Bank bank;

    public CreateCard(final CommandData commandData, final Bank bank) {
        this.command = commandData;
        this.bank = bank;
    }

    /**
     * Execute the create card command.
     */
    @Override
    public void execute() {
        String iban = this.command.getAccount();
        String email = this.command.getEmail();
        int timestamp = this.command.getTimestamp();

        // get the owner of the email
        User owner = this.bank.getUserByEmail(email);

        // get the account by iban
        Account account = this.bank.getAccountByIban(iban);

        if (account != null) {
            // create a new card
            Card newCard = new Card(account, owner);
            account.addCard(newCard);

            // create a successful transaction
            Transaction transaction;
            transaction = new Transaction.TransactionBuilder(timestamp,
                    "New card created", this.command.getCommand())
                    .card(newCard.getCardNumber())
                    .account(iban)
                    .cardHolder(owner.getEmail())
                    .build();
            account.addTransaction(transaction);
        } else {
            // if the user is not the owner of the account, create an error transaction
            Transaction transaction;
            transaction = new Transaction.TransactionBuilder(timestamp,
                    "User not owner of account", this.command.getCommand())
                    .build();
            account.addTransaction(transaction);
        }
    }
}
