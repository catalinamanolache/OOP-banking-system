package org.poo.commands.cardoperations;

import org.poo.accounts.Account;
import org.poo.cards.Card;
import org.poo.commands.Command;
import org.poo.transactions.Transaction;
import org.poo.instances.User;
import org.poo.instances.CommandData;
import org.poo.bankmanager.Bank;

public class DeleteCard implements Command {
    private CommandData command;
    private Bank bank;

    public DeleteCard(final CommandData commandData, final Bank bank) {
        this.command = commandData;
        this.bank = bank;
    }

    /**
     *
     * Executes the deleteCard command.
     */
    @Override
    public void execute() {
        String email = this.command.getEmail();
        String cardNumber = this.command.getCardNumber();
        int timestamp = this.command.getTimestamp();

        // get the user who tries to delete the card and the card to delete
        User user = this.bank.getUserByEmail(email);
        Card cardToDelete = this.bank.getCardByCardNumber(cardNumber);

        if (cardToDelete == null || user == null) {
            return;
        }

        // get the parent account of the card
        Account account = cardToDelete.getParentAccount();

        // get the iban of the parent account
        String iban = cardToDelete.getParentAccount().getIban();

        // create a transaction for the deletion of the card
        Transaction transaction;
        transaction = new Transaction.TransactionBuilder(timestamp,
                "The card has been destroyed", this.command.getCommand())
                .card(cardNumber)
                .cardHolder(user.getEmail())
                .card(cardNumber)
                .account(iban)
                .build();
        account.addTransaction(transaction);

        Account.AccountType accountType = account.getAccountType();

        // remove the card from the account if the balance is 0 and the account is not classic
        if (account.getBalance() <= 0 && !accountType.equals(Account.AccountType.CLASSIC)) {
            account.getCards().remove(cardToDelete);
        }
    }
}
