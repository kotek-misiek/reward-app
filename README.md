# Reward - App - Task
##  Coding task (JAVA)
A retailer offers a rewards program to its customers, awarding points based on each recorded purchase.
A customer receives 2 points for every dollar spent over $100 in each transaction, plus 1 point for every dollar
spent over $50 in each transaction (e.g. a $120 purchase = 2x$20 + 1x$50 = 90 points).
Given a record of every transaction during a three-month period, calculate the reward points earned for each
customer per month and total.

Checklist
1. Goal of the assignment: showcase great craftsmanship skills in area of JAVA, services, testing,
building readable, maintainable, resilient systems. If you have personal project (comparable in
complexity) that you would like to present instead of the assignment –let us know
2. Key elements: tests, good coding practices, well thought-out design –if requirements change your code
should be easily fixable. Your assignment solution should not contain shortcuts that violate good coding
practices e.g. class having multiple responsibilities; magic numbers in code; methods, variables names
not giving any clue what they are for; etc.
3. Implement RestAPI’s for all CRUD operations – in this case – creating/updating transactions,
calculating and providing reward information for a User.
4. Time required: anywhere between 4-8h depending on proficiency. It’s best to focus on solving the
problem first with appropriate tests and benefit from features of recent versions of language and
frameworks (autogenerate boilerplate code) to complete the assignment quickly. Check “key elements”
that are evaluated.
5. Technical guidelines:
  * Used libraries should be up to date (for example: Junit5 not 4, Spring boot 2.6.* not 1.5.*)
  * Code should be clean, nice formatted, easy to read and understand
  * Code must compile and run, there should be an instruction in readme.md file how to run the application
  * Provide documentation of an application REST API
  * All errors should be handled with appropriate HTTP status codes (200, 400, 422, 500 etc.) and with human readable
messages.
  * Points calculation logic should be separated from infrastructure (REST, DB) and should be easy to test with unit tests
  * Please show the ability to write testable code. It is important.
  * Unit tests for points calculation logic should be written (please remember about corner cases)
  * Integration tests should not only check if application starts but also call REST endpoints and check at least if HTTP status
code is correct
  * Use of appropriate logging levels, framework
  * The solution must be checked into Github (provide a public Github url


# Reward - App - realization
## Short description
Reward-app (further 'application') contains two modules: 
1. **Reward** - the application itself
2. **Acceptance-tests** - acceptation tests sending HTTP requests to the running application.

Application saves its data in in-memory database **H2**. All the data saves only in memory and restore the initial database state after a restart of the application. The initial SQL commands are in the file '\Reward\src\main\resources\data.sql'.
## DB structure
There are three tables in a database:
1. **CUSTOMERS** - list of customers with columns **ID**, **FIRST_NAME** and **LAST_NAME**.
   The application hasn't any possibility to edit/add/remove customers. They are always three customers for all the time.
2. **ACCOUNTS** - list of accounts where the tables **ACCOUNT**/**CUSTOMERS** have a relation One-To-One (one account belongs to one customer and one customer has only one account).
   There are two accounts attached to two customers. The third customer is not attached to any account, it is "up in the air" to check some exceptions.
   Columns: **ID**, **CUSTOMER_ID**, **AMOUNT** (amount of money on the account) and **UPDATE_TIME** (last update of an account).
3. **TRANSACTIONS** - list of all transaction for all accounts. TRANSACTIONS/ACCOUNTS have a relation Many-To-One (many transactions to one account).
   Columns: **ID**, **ACCOUNT_ID**, **AMOUNT** (amount of a transaction), **T_TYPE** (transaction type: _A_ - added and _U_ - updated), **UPDATE_TIME** (time of the transaction).
## Configuration
Configuration parameters are in the file `application.yaml`
Main groups of parameters:
1. Server
2. Spring configuration (incl.database configuration)
3. Application parameters: `period-month` (3 months) and `thresholds` of _50_ (_1_ point) and _100_ (_2_ points). The configuration is created to add a possibility to add some additional thresholds if needs. The application code gives such of possibility. 
## Web interface
The interface gives possibility to perform all CRUD operations with the transaction and to count a reward for each customer. 
In a case of any data error (in a request or in a database) the application returns an error communicate in JSON format (with an appropriate HTTP status), anything like this below.
```
{
   "timestamp": "2023-09-14T00:30:25.2450216",
   "status": 400,
   "error": "Bad Request",
   "message": "customer with ID = -100 not found",
   "path": "/transactions/all/-100"
}
```
Such of format of exceptions are provided by the class `RewardExceptionHandler` proceeding all the type of exceptions generated by the application.

The endpoints are defined in two Controller classes: `RewardController` and `TransactionController`.
All endpoints (of both controllers) use _path variables_ as parameters.
1. `RewardController` 
   * `getReward` 
     * **GET** _URL_: `/reward/{customerId}` where `{customerId}` can be _1_ or _2_ in this application;
       If this `customerId` has another value or there has no value (`/reward`) the application return a JSON error message.
   * `getRewardTable`
   * **GET** _URL_: `/reward/table/{customerId}` where `{customerId}` can be _1_ or _2_ in this application;
     It will return a list od rates for this customer every month (first and last ones can be not full because the list of transactions contains only data since the same date 3 months ago, if the request is sent 15.10.2023 only transaction since 15.07.2023 will be proceeded)
   * **GET** _URL_: `/reward/table`;
     It will return a list od rates for this every customer in the database by the same way as above;
```
[
    {
        "name": "John Wick",
        "monthRate": [
            {
                "month": "SEPTEMBER",
                "points": 0
            },
            {
                "month": "TOTAL",
                "points": 0
            }
        ]
    },
    {
        "name": "Howard Stark",
        "monthRate": [
            {
                "month": "JULY",
                "points": 90.00
            },
            {
                "month": "AUGUST",
                "points": 0
            },
            {
                "month": "SEPTEMBER",
                "points": 180.00
            },
            {
                "month": "TOTAL",
                "points": 270.00
            }
        ]
    }
]
```

2. `TransactionController`
   * `getAllTransactions` 
     * **GET** _URL_: `/transactions/all`: returns all the transactions saved in a database;
     * **GET** _URL_: `/transactions/all/{customerId}`: returns all the transactions belonging to a customer with this `customerId`;
   * `getLastTransactions`
     * **GET** _URL_: `/transactions/{customerId}`: returns all the transactions belonging to a customer with this `customerId` not later than before 3 months (parameter `reward.period-months` in `application.yaml` defines this interval);
   * `addTransaction`
     * **POST** _URL_: `/transactions/{customerId}/{amount}`: add a transaction to an account of a customer with a given `customerId`. The request returns a transaction data in JSON format like below. An incorrect value of `customerId` and `amount` params invoke the exception message like above;
   * `updateLastTransaction`
     * **PUT** _URL_: `/transactions/{customerId}/{amount}`: update a **last** transaction (i.e with a maximal value of `updateTime`) of an account of a customer with a given `customerId`. The updated transaction has the field `transactionType` equals _U_ ("updated).  A request returns a transaction data in JSON format like below;
   * `deleteLastTransaction`
     * **DELETE** _URL_: `/transactions/{customerId}`: delete a **last** transaction (i.e with a maximal value of `updateTime`) of an account of a customer with a given `customerId`. The request returns a full list of transactions for this customer leaving after this operation (like in `getLastTransactions`); 

_This is a transaction data returning by some endpoints_ (some other return list of similar blocks):
```
         {
             "id": 8,
             "account": {
                 "id": 1,
                 "customer": {
                     "id": 1,
                     "firstName": "John",
                     "lastName": "Wick"
                 },
                 "amount": 120.00,
                 "updateTime": "2023-09-13T23:23:52.785+00:00"
             },
             "amount": 100.0,
             "transactionType": "A",
             "updateTime": "2023-09-13T23:23:58.860+00:00"
         }
```
Endpoints `updateLastTransaction` and `deleteLastTransaction` contains more than one operation on the DB so they have maximal transaction isolation level `SERIALIZABLE` to avoid any possible problems in possible multi-customer environnment. 
## Application structure
The application code is structured and divided of some functional packages: `controller` (controllers with endpoints), `entity` (DB entities), `enums` (one single enum defining `transactionType`), `exceptions` (self-made exceptions and exception handler), `properties` (structured properties classes), `repository` (JPA repositories) and `service` (interfaces and implementations of the endpoints functionalities).

## Testing
Unit tests are based on `Spock` (`Groovy`). They cover methods in services classes.
Acceptation tests are in the special module `Acceptance-tests`. The profile of an acceptance tests checking id disabled by default in `Maven`.

## Used technologies
* Java 17
* Lombok
* Spring boot
* JPA
* Spock (Groovy)
* In-memory SQL DB H2
* REST assured for acceptance tests

P.S. Maybe it was possible to create more advanced functionality or to optimise some weak points, but I had a strong deficit of time.

That's all from my side.