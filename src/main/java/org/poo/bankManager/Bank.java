package org.poo.bankManager;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.accounts.Account;
import org.poo.cards.Card;
import org.poo.instances.Commerciant;
import org.poo.instances.ExchangeRate;
import org.poo.instances.User;
import org.poo.commandManager.ProccessCommands;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.UserInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Bank {
    private ArrayList<User> users;
    private ArrayList<ExchangeRate> exchangeRates;
    private ArrayList<Commerciant> commerciants;
    private Map<String, String> aliasMap;
    private Map<Integer, SplitPaymentContext> splitPaymentContextMap;

    public Bank(final UserInput[] userInputs, final ExchangeInput[] exchangeInputs,
                final CommerciantInput[] commerciantInputs) {
        this.users = new ArrayList<>();
        this.exchangeRates = new ArrayList<>();
        this.commerciants = new ArrayList<>();
        this.aliasMap = new HashMap<>();
        this.splitPaymentContextMap = new HashMap<>();

        for (UserInput user : userInputs) {
            this.users.add(new User(user));
        }

        for (ExchangeInput exchange : exchangeInputs) {
            this.exchangeRates.add(new ExchangeRate(exchange));
        }

        if (commerciantInputs != null) {
            for (CommerciantInput commerciant : commerciantInputs) {
                this.commerciants.add(new Commerciant(commerciant));
            }
        }
    }


    /**
     * Set the bank's exchange rates.
     */
    public void setCurrencyExchange() {
        CurrencyConverter.setBank(this.exchangeRates);
    }

    /**
     * Process the commands from the input array.
     * @param commandInputs the commands
     * @param output the output array
     */
    public void processCommands(final CommandInput[] commandInputs, final ArrayNode output) {
        ProccessCommands.processCommands(commandInputs, this, output);
    }

    /**
     * Gets the commerciant with the given IBAN.
     * @param iban the IBAN
     * @return the commerciant with the given IBAN
     */
    public Commerciant getCommerciantByIban(final String iban) {
        for (Commerciant commerciant : this.commerciants) {
            if (commerciant.getAccount().equals(iban)) {
                return commerciant;
            }
        }

        return null;
    }

    /**
     * Get the commerciant with the given name.
     * @param name the name
     * @return the commerciant with the given name
     */
    public Commerciant getCommerciantByName(final String name) {
        for (Commerciant commerciant : this.commerciants) {
            // the given commerciant can be its name or its id, so we check both
            if (commerciant.getCommerciant().equals(name)
                    || (name.matches("-?\\d+(\\.\\d+)?")
                    && commerciant.getId() == Integer.parseInt(name))) {
                return commerciant;
            }
        }
        return null;
    }

    /**
     * Get the account that has the card with the given card number.
     * @param cardNumber the card number
     * @return the account that has the card with the given card number
     */
    public Account getAccountByCardNumber(final String cardNumber) {
        for (User user : this.users) {
            for (Account account : user.getAccounts()) {
                for (Card card : account.getCards()) {
                    if (card.getCardNumber().equals(cardNumber)) {
                        return account;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Get the card with the given card number.
     * @param cardNumber the card number
     * @return the card with the given card number
     */
    public Card getCardByCardNumber(final String cardNumber) {
        for (User user : this.users) {
            for (Account account : user.getAccounts()) {
                for (Card card : account.getCards()) {
                    if (card.getCardNumber().equals(cardNumber)) {
                        return card;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Get the account with the given IBAN.
     * @param iban the IBAN
     * @return the account with the given IBAN
     */
    public Account getAccountByIban(final String iban) {
        for (User user : this.users) {
            for (Account account : user.getAccounts()) {
                if (account.getIban().equals(iban)) {
                    return account;
                }
            }
        }

        return null;
    }

    /**
     * Get the user with the given email.
     * @param email the email
     * @return the user with the given email
     */
    public User getUserByEmail(final String email) {
        for (User user : this.users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }

        return null;
    }

    /**
     * Creates a split payment context based on the given parameters.
     * @param participants the participants in the split payment
     * @param currency the currency of the split payment
     * @param amountForUsers the amount each user has to pay
     * @param amount the total amount
     * @param type the type of the split payment
     * @param timestamp the timestamp when the split payment was initiated
     */
    public void createSplitPaymentContext(final List<String> participants, final String currency,
                                          final List<Double> amountForUsers, final double amount,
                                          final String type, final int timestamp) {
        SplitPaymentContext context = new SplitPaymentContext(participants, currency,
                amountForUsers, amount, type, timestamp, this);
        this.splitPaymentContextMap.put(timestamp, context);
    }

    public Map<String, String> getAliasMap() {
        return aliasMap;
    }

    public void setAliasMap(final Map<String, String> aliasMap) {
        this.aliasMap = aliasMap;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<ExchangeRate> getExchangeRates() {
        return exchangeRates;
    }

    public ArrayList<Commerciant> getCommerciants() {
        return commerciants;
    }

    public void setUsers(final ArrayList<User> users) {
        this.users = users;
    }

    public void setExchangeRates(final ArrayList<ExchangeRate> exchangeRates) {
        this.exchangeRates = exchangeRates;
    }

    public void setCommerciants(final ArrayList<Commerciant> commerciants) {
        this.commerciants = commerciants;
    }

    public Map<Integer, SplitPaymentContext> getSplitPaymentContextMap() {
        return splitPaymentContextMap;
    }

    public void setSplitPaymentContextMap(final Map<Integer, SplitPaymentContext> map) {
        this.splitPaymentContextMap = map;
    }
}
