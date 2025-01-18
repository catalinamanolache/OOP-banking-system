# J. POO Morgan Chase & Co. - Stage 2

### Name: Manolache Maria-Catalina, Group: 323CA

## Description
- Implemented a simplified banking system that allows users to create accounts,
  deposit money, transfer money between accounts, create transactions, view
  their account balance etc.
- The system is implemented using multiple design patterns such as **Factory,
  Command, Builder and Strategy** to manage the different possible commands and
  types of objects.

## Solution Components
### Bank
- **Role**: Stores all the user's information, reading the input data, creating
  the user list, exchange rates list, setting up the bank's currency exchange
  mechanism, the alias hashmap, the split payment context hashmap and executing
  the commands.
- The class also contains methods for getting a commerciant by its name or iban,
  an account by a given card number or iban, a card object by a given card number
  and a user object by its email, as well as getters and setters for each field.
- A split payment context is also created for each transaction that is a split
  payment, containing the transaction's information and the list of the users
  that are involved in the split payment.
- **Relationships**: Interacts with `User`, `ExchangeRates`, `Commerciant`,
  `CurrencyConverter` and `ProcessCommands` to manage the bank's operations.

### User
- **Role**: Represents a user of the bank, containing a list of accounts and
  its personal information such as name, email, occupation, birthdate and his
  account's plan type. His age is calculated based on his birthdate and the
  current date, using the class `AgeCalculator`.
- **Relationships**: Interacts with `Account`, `Plan` and `AgeCalculator` to 
  manage the user's accounts and information.

### ExchangeRate, Commerciant and CommandData
- **Role**: The classes hold information on the exchange rates between different
  currencies and the rate for the bank's transactions, on the commerciants and
  on the different commands.
- **Relationships**: The classes interact with `CurrencyConverter` to manage the
  exchange rates, `Bank` to manage the commerciants and with the command classes
  to get the command's information.

### Account, ClassicAccount, SavingsAccount, BusinessAccount and AccountFactory
- **Role**: The classes represent an account in the bank, containing the
  account's balance, iban, currency etc. and lists of the account's cards
  and transactions.
- All classes inherit Account, SavingsAccount and BusinessAccount having
  additional fields and methods for their specific account type. Account
  also has methods for updating the total spent and the number of transactions
  made at a given commerciant and for handling money transactions.
- AccountFactory is a **Factory** class that creates new accounts based on the
  account type.
- **Relationships**: Interacts with `Card`, `Transaction`, `User`, `Commerciant`
  to manage the account's operations.

### Card and OneTimeCard
- **Role**: The classes represent a card associated with an account, containing
  the card's number, parent account and the card's state.
- OneTimeCard inherits Card and is a card that can be used only once (its card
  number is regenerated after each payment).
- The classes contain a method for handling the transactions after payment,
  which is only implemented for OneTimeCard.
- **Relationships**: Interacts with `Account` to store the card's parent
  account.

### CurrencyConverter
- **Role**: Represents the currency exchange mechanism of the bank, containing
  the exchange rates in a graph and a method for converting money between
  different currencies.
- The conversion is realised using a graph of the exchange rates and currencies
  and the DFS algorithm.
- **Relationships**: Interacts with `ExchangeRates` to manage the exchange rates.

### Transaction, TransactionBuilder and PrintTransactionsJSON
- **Role**: Creates a transaction object which contains different information
  based on the transaction type.
- I used the **Builder** pattern because each transaction has three default fields
  (timestamp, description and type) and the other fields are optional.
- PrintTransactionsJSON is a class that prints each transaction in JSON format
  and also contains methods for printing reports and spendings reports in JSON
  format.
- **Relationships**: PrintTransactionsJSON interacts with `Transaction` to print
  the transactions in JSON format.

### Command classes (AddAccount, CreateCard, PrintUsers etc.)
- **Role**: The classes represent the commands that can be executed by the bank,
  each command having a method for executing it.
- Each command is created with the **Command and Factory** patterns (using
  the `CommandFactory` class).
- Each command implements a Command interface and contains the execute method
  which does all the operations needed for the command.
- The command's errors are printed to the JSON ArrayNode by throwing a
  `JSONExceptions` exception that is caught in the `CommandInvoker` class and
  printed through the `PrintJSONMessages` class.
- **Relationships**: Interacts with `Bank`, `CommandData` and `ArrayNode` to
  execute and print the command.

### Cashback implementation (CashbackContext, CashbackStrategy and its subclasses)
- **Role**: The classes represent the cashback mechanism of the bank, creating
  a cashback context for each transaction that can get cashback and using the
  **Strategy** pattern to create different cashback methods.
- The `CashbackContext` class contains methods for creating and applying the
  cashback strategy, based on the commerciant's strategy.
- `NrOfTransactions` and `SpendingThreshold` are the two cashback strategies
  implemented, each overriding the interface's method for calculating the
  cashback amount.

## Usage of the given `fileio` classes
- I used the `fileio` classes to populate my arrays with the given UserInput,
  ExchangeInput and CommerciantInput data. I used an CommandInput array to
  create my CommandData objects which are used for executing the commands.