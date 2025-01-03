package org.poo.bankManager;

import org.poo.accounts.Account;
import org.poo.instances.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplitPaymentContext {
    public enum SplitPaymentType {
        EQUAL, CUSTOM
    }

    // map of participants with their email as key and iban as value
    private Map<String, String> participantsMap;
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
        this.participantsMap = new HashMap<>();
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
            Account account = this.bank.getAccountByIban(iban);
            this.participantsMap.put(account.getOwner().getEmail(), iban);
        }

        this.refused = false;
        this.refusedBy = null;
        this.participantsLeftToAccept = participants.size();
        this.type = SplitPaymentType.valueOf(type.toUpperCase());
        this.currency = currency;
        this.amount = amount;
        this.startedTimestamp = timestamp;

        if (this.type == SplitPaymentType.CUSTOM) {
            this.amountForUsers = new ArrayList<>(amountForUsers);
        } else {
            this.amountForUsers = new ArrayList<>();
            for (int i = 0; i < participants.size(); i++) {
                this.amountForUsers.add(amount / participants.size());
            }
        }
    }

    public void acceptParticipant(final String email) {
        if (this.participantsMap.containsKey(email)) {
            this.participantsLeftToAccept--;
        }
    }

    public Map<String, String> getParticipantsMap() {
        return participantsMap;
    }

    public void setParticipantsMap(Map<String, String> participantsMap) {
        this.participantsMap = participantsMap;
    }

    public List<String> getParticipantsIbanList() {
        return participantsIbanList;
    }

    public void setParticipantsIbanList(List<String> participantsIbanList) {
        this.participantsIbanList = participantsIbanList;
    }

    public int getStartedTimestamp() {
        return startedTimestamp;
    }

    public void setStartedTimestamp(int startedTimestamp) {
        this.startedTimestamp = startedTimestamp;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public boolean isRefused() {
        return refused;
    }

    public void setRefused(boolean refused) {
        this.refused = refused;
    }

    public String getRefusedBy() {
        return refusedBy;
    }

    public void setRefusedBy(String refusedBy) {
        this.refusedBy = refusedBy;
    }

    public int getParticipantsLeftToAccept() {
        return participantsLeftToAccept;
    }

    public void setParticipantsLeftToAccept(int participantsLeftToAccept) {
        this.participantsLeftToAccept = participantsLeftToAccept;
    }

    public SplitPaymentType getType() {
        return type;
    }

    public void setType(SplitPaymentType type) {
        this.type = type;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public List<Double> getAmountForUsers() {
        return amountForUsers;
    }

    public void setAmountForUsers(List<Double> amountForUsers) {
        this.amountForUsers = amountForUsers;
    }
}