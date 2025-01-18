package org.poo.instances;

import org.poo.accounts.Account;
import org.poo.fileio.UserInput;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

final class AgeCalculator {
    private AgeCalculator() {
    }

    /**
     * Compute the age of a user based on their birthdate.
     * @param birthDateString the birthdate of the user
     * @return the age of the user
     */
    public static int computeAge(final String birthDateString) {
        LocalDate presentDate = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthDate = LocalDate.parse(birthDateString, formatter);

        return Period.between(birthDate, presentDate).getYears();
    }
}

public final class User {
    private String firstName;
    private String lastName;
    private String email;
    private String birthDate;
    private String occupation;
    private int age;

    private List<Account> accounts;
    private Plan.PlanType planType;

    public User(final UserInput user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.birthDate = user.getBirthDate();
        this.occupation = user.getOccupation();
        this.accounts = new ArrayList<>();

        // compute his age based on the current date
        this.age = AgeCalculator.computeAge(this.birthDate);

        // for students, the starting plan is the student plan and for others, the standard plan
        if (this.occupation.equals("student")) {
            this.planType = Plan.PlanType.STUDENT;
        } else {
            this.planType = Plan.PlanType.STANDARD;
        }
    }

    /**
     * Add an account to the user's accounts.
     * @param account the account to add
     */
    public void addAccount(final Account account) {
        accounts.add(account);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(final String birthDate) {
        this.birthDate = birthDate;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(final String occupation) {
        this.occupation = occupation;
    }

    public int getAge() {
        return age;
    }

    public void setAge(final int age) {
        this.age = age;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(final List<Account> accounts) {
        this.accounts = accounts;
    }

    public Plan.PlanType getPlanType() {
        return planType;
    }

    public void setPlanType(final Plan.PlanType planType) {
        this.planType = planType;
    }
}
