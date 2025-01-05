package org.poo.bankManager;

import org.poo.accounts.Account;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.HashMap;

public final class SplitPaymentContext {
    public enum SplitPaymentType {
        EQUAL, CUSTOM
    }

    // map of participants with their email as key and ibans involved as value
    private Map<String, List<String>> participantsMap;

    // list of participants ibans
    private List<String> participantsIbanList;

    private int participantsLeftToAccept;
    private boolean refused;
    private String refusedBy;
    private SplitPaymentType type;
    private String currency;
    private double amount;
    private List<Double> amountForUsers;
    private int startedTimestamp;
    private Bank bank;

    public SplitPaymentContext() {
        this.participantsMap = new TreeMap<>();
        this.participantsIbanList = new ArrayList<>();
        this.refused = false;
        this.refusedBy = null;
        this.participantsLeftToAccept = 0;
        this.type = null;
        this.currency = null;
        this.amount = 0;
        this.amountForUsers = new ArrayList<>();
        this.startedTimestamp = 0;
        this.bank = null;
    }

    public SplitPaymentContext(final List<String> participants, final String currency,
                               final List<Double> amountForUsers, final double amount,
                               final String type, final int timestamp, final Bank bank) {
        this.participantsIbanList = new ArrayList<>(participants);
        this.participantsMap = new HashMap<>();
        this.bank = bank;

        // put the participants in the map with their email as key and iban as value
        for (String iban : participants) {
            // get the account with the iban
            Account account = this.bank.getAccountByIban(iban);

            if (account == null) {
                continue;
            }

            // get all the ibans for the current user (a user can be involved in a split payment
            // with multiple accounts)
            List<String> keys = this.participantsMap.get(account.getOwner().getEmail());

            // create a new list if the user does not have any ibans involved yet
            if (keys == null) {
                keys = new ArrayList<>();
            }

            // add the iban to the list
            keys.add(iban);

            // put the list back in the map
            this.participantsMap.put(account.getOwner().getEmail(), keys);
        }

        this.refused = false;
        this.refusedBy = null;
        this.participantsLeftToAccept = participants.size();
        this.type = SplitPaymentType.valueOf(type.toUpperCase());
        this.currency = currency;
        this.amount = amount;
        this.startedTimestamp = timestamp;

        // if the split payment is custom, use the given amounts, otherwise split the total amount
        if (this.type == SplitPaymentType.CUSTOM) {
            this.amountForUsers = new ArrayList<>(amountForUsers);
        } else {
            this.amountForUsers = new ArrayList<>();
            for (int i = 0; i < participants.size(); i++) {
                this.amountForUsers.add(amount / participants.size());
            }
        }
    }

    /**
     * Handle the acceptance of a participant.
     * @param email the email of the participant
     */
    public void acceptParticipant(final String email) {
        if (this.participantsMap.containsKey(email)) {
            this.participantsLeftToAccept--;
        }
    }

    public Map<String, List<String>> getParticipantsMap() {
        return participantsMap;
    }

    public void setParticipantsMap(final Map<String, List<String>> participantsMap) {
        this.participantsMap = participantsMap;
    }

    public List<String> getParticipantsIbanList() {
        return participantsIbanList;
    }

    public void setParticipantsIbanList(final List<String> participantsIbanList) {
        this.participantsIbanList = participantsIbanList;
    }

    public int getStartedTimestamp() {
        return startedTimestamp;
    }

    public void setStartedTimestamp(final int startedTimestamp) {
        this.startedTimestamp = startedTimestamp;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(final Bank bank) {
        this.bank = bank;
    }

    public boolean isRefused() {
        return refused;
    }

    public void setRefused(final boolean refused) {
        this.refused = refused;
    }

    public String getRefusedBy() {
        return refusedBy;
    }

    public void setRefusedBy(final String refusedBy) {
        this.refusedBy = refusedBy;
    }

    public int getParticipantsLeftToAccept() {
        return participantsLeftToAccept;
    }

    public void setParticipantsLeftToAccept(final int participantsLeftToAccept) {
        this.participantsLeftToAccept = participantsLeftToAccept;
    }

    public SplitPaymentType getType() {
        return type;
    }

    public void setType(final SplitPaymentType type) {
        this.type = type;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(final double amount) {
        this.amount = amount;
    }

    public List<Double> getAmountForUsers() {
        return amountForUsers;
    }

    public void setAmountForUsers(final List<Double> amountForUsers) {
        this.amountForUsers = amountForUsers;
    }
}
