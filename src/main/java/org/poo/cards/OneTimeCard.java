package org.poo.cards;

import org.poo.accounts.Account;
import org.poo.instances.User;
import org.poo.transactions.Transaction;
import org.poo.utils.Utils;

public class OneTimeCard extends Card {
    public OneTimeCard(final Account parentAccount, final User owner) {
        super(parentAccount, owner);
    }

    /**
     * Handle transactions for the one time card: once the card is used,
     * it is destroyed and a new one is created and this is reflected in the account's transactions.
     * @param timestamp the timestamp of the transaction
     * @param command the command of the transaction
     * @param iban the iban of the account
     * @param oldCardNumber the old card number
     * @param newCardNumber the new card number
     * @param account the account
     * @param owner the email of the account's owner
     */
    @Override
    public void handleTransactions(final int timestamp, final String command, final String iban,
                                   final String oldCardNumber, final String newCardNumber,
                                   final Account account, final String owner) {
        // create a transaction for the old card
        Transaction transaction1;
        transaction1 = new Transaction.TransactionBuilder(timestamp, "The card has been destroyed",
                "deleteCard")
                .account(iban)
                .card(oldCardNumber)
                .cardHolder(owner)
                .build();
        account.addTransaction(transaction1);

        // create a transaction for the new card
        Transaction transaction2;
        transaction2 = new Transaction.TransactionBuilder(timestamp, "New card created",
                "createCard")
                .account(iban)
                .card(newCardNumber)
                .cardHolder(owner)
                .build();
        account.addTransaction(transaction2);
    }

    /**
     * Pay with the card by withdrawing the amount from the parent account and then generating a new
     * card.
     * @param amount the amount to pay
     */
    @Override
    public void pay(final double amount) {
        // pay with the card
        super.pay(amount);
        this.setCardNumber(Utils.generateCardNumber());
    }
}
