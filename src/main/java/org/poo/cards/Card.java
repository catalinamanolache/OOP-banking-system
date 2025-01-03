package org.poo.cards;

import org.poo.accounts.Account;
import org.poo.instances.User;
import org.poo.utils.Utils;

public class Card {
    private String cardNumber;
    private Account parentAccount;
    private boolean isFrozen;
    private User owner;

    public Card(final Account parentAccount, final User owner) {
        this.cardNumber = Utils.generateCardNumber();
        this.parentAccount = parentAccount;
        this.isFrozen = false;
        this.owner = owner;
    }

    /**
     * Check if the card is frozen.
     * @return true if the card is frozen, false otherwise
     */
    public boolean getIsFrozen() {
        return isFrozen;
    }

    /**
     * Freeze or unfreeze the card.
     * @param frozen true if the card is frozen, false otherwise
     */
    public void setIsFrozen(final boolean frozen) {
        this.isFrozen = frozen;
    }

    /**
     * Get the parent account of the card.
     * @return the parent account
     */
    public Account getParentAccount() {
        return parentAccount;
    }

    /**
     * Set the parent account of the card.
     * @param parentAccount the parent account
     */
    public void setParentAccount(final Account parentAccount) {
        this.parentAccount = parentAccount;
    }

    /**
     * Get the card number.
     * @return the card number
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Set the card number.
     * @param cardNumber the card number
     */
    public void setCardNumber(final String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    /**
     * Handle the transactions of the card.
     * @param timestamp the timestamp of the transaction
     * @param command the command of the transaction
     * @param iban the IBAN of the account
     * @param oldCardNumber the old card number
     * @param newCardNumber the new card number
     * @param account the account
     * @param owner the owner of the account
     */
    public void handleTransactions(final int timestamp, final String command, final String iban,
                                   final String oldCardNumber, final String newCardNumber,
                                   final Account account, final String owner) {
    }

    /**
     * Pay with the card by withdrawing the amount from the parent account.
     * @param amount the amount to pay
     */
    public void pay(final double amount) {
        // pay with the card
        this.parentAccount.withdraw(amount);
    }
}
