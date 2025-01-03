package org.poo.accounts;

import org.poo.instances.User;

class ClassicAccount extends Account {
    ClassicAccount(final String currency, final User owner) {
        super(currency, AccountType.CLASSIC, owner);
    }
}
