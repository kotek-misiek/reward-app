package org.example.service.impl

import org.example.entity.Account
import org.example.entity.Transaction
import org.example.entity.Customer
import org.example.enums.TransactionTypeEnum
import org.example.exceptions.TransactionNotFoundException
import org.example.exceptions.UnacceptableAmountException
import org.example.properties.RewardAppProperties
import org.example.properties.Threshold
import org.example.repository.AccountRepository
import org.example.repository.TransactionRepository
import org.example.repository.CustomerRepository
import spock.lang.Specification

import java.sql.Timestamp
import java.time.LocalDateTime

class TransactionServiceTest extends Specification {
    def static NO_CLARK = "Account belonging to user Clark Kent not found"
    def static NO_USER = "User with ID = 4 not found"
    def static NO_AMOUNT = "Unacceptable amount = 0.0"
    def static NO_TRANSACTION = "No such of transaction"
    def static NO_TRANSACTION_2 = "Transaction #2 not found"


    def static PROPERTIES = new RewardAppProperties(3,
            List.of(new Threshold(BigDecimal.valueOf(50), 1),
                    new Threshold(BigDecimal.valueOf(100), 2)))

    def static USER1 = new Customer(1L, "John", "Wick")
    def static USER2 = new Customer(2L, "Howard", "Stark")
    def static USER3 = new Customer(3L, "Clark", "Kent")

    def static ACCOUNT0 = new Account(0, USER1, 20.0, Timestamp.valueOf(LocalDateTime.now()))
    def static ACCOUNT1 = new Account(1, USER1, 20.0, Timestamp.valueOf(LocalDateTime.now()))
    def static ACCOUNT2 = new Account(2, USER2, 580.0, Timestamp.valueOf(LocalDateTime.now()))

    def static TRANSACTION1 = new Transaction(1, ACCOUNT1, 20.00,
            TransactionTypeEnum.A, Timestamp.valueOf(LocalDateTime.now()))
    def static TRANSACTION2 = new Transaction(2, ACCOUNT2, 110.00,
            TransactionTypeEnum.A, Timestamp.valueOf(LocalDateTime.now().minusMonths(4)))
    def static TRANSACTION3 = new Transaction(3, ACCOUNT2, 120.00,
            TransactionTypeEnum.A, Timestamp.valueOf(LocalDateTime.now().minusMonths(2)))
    def static TRANSACTION4 = new Transaction(4, ACCOUNT2, 50.00,
            TransactionTypeEnum.A, Timestamp.valueOf(LocalDateTime.now().minusDays(4)))
    def static TRANSACTION5 = new Transaction(5, ACCOUNT2, 70.00,
            TransactionTypeEnum.A, Timestamp.valueOf(LocalDateTime.now().minusDays(3)))
    def static TRANSACTION6 = new Transaction(6, ACCOUNT2, 100.00,
            TransactionTypeEnum.A, Timestamp.valueOf(LocalDateTime.now().minusDays(2)))
    def static TRANSACTION7 = new Transaction(7, ACCOUNT2, 130.00,
            TransactionTypeEnum.A, Timestamp.valueOf(LocalDateTime.now()))

    def userRepository = Mock(CustomerRepository)
    def accountRepository = Mock(AccountRepository)
    def transactionRepository = Mock(TransactionRepository)

    def "getAllTransactions whole"() {
        given:
        transactionRepository.findAll() >> List.of(TRANSACTION1, TRANSACTION2, TRANSACTION3)
        def transactionService = new TransactionServiceImpl(userRepository, accountRepository, transactionRepository, PROPERTIES)

        when:
        def res = transactionService.getAllTransactions()

        then:
        res == List.of(TRANSACTION1, TRANSACTION2, TRANSACTION3)
    }

    def "getAllTransactions OK"() {
        given:
        userRepository.findById(1L) >> Optional.of(USER1)
        userRepository.findById(2L) >> Optional.of(USER2)
        accountRepository.findByCustomerId(1L) >> Optional.of(ACCOUNT1)
        accountRepository.findByCustomerId(2L) >> Optional.of(ACCOUNT2)
        transactionRepository.findByAccountId(1L) >> List.of(TRANSACTION1)
        transactionRepository.findByAccountId(2L) >> List.of(TRANSACTION2, TRANSACTION3, TRANSACTION4)

        def transactionService = new TransactionServiceImpl(userRepository, accountRepository, transactionRepository, PROPERTIES)

        when:
        def res = transactionService.getAllTransactions(userId)

        then:
        res == transactions

        where:
        userId | user  | account  | transactions
        1L     | USER1 | ACCOUNT1 | List.of(TRANSACTION1)
        2L     | USER2 | ACCOUNT2 | List.of(TRANSACTION2, TRANSACTION3, TRANSACTION4)
    }

    def "getLastTransactions OK"() {
        given:
        userRepository.findById(2L) >> Optional.of(USER2)
        accountRepository.findByCustomerId(2L) >> Optional.of(ACCOUNT2)
        transactionRepository.findByAccountIdAndUpdateTimeGreaterThan(2L, (Timestamp) _) >> List.of(TRANSACTION3, TRANSACTION4)

        def transactionService = new TransactionServiceImpl(userRepository, accountRepository, transactionRepository, PROPERTIES)

        when:
        def res = transactionService.getLastTransactions(2L)

        then:
        res == List.of(TRANSACTION3, TRANSACTION4)
    }

    def "getAllTransactions Exception"() {
        given:
        userRepository.findById(3L) >> Optional.of(USER3)
        userRepository.findById(4L) >> { throw new NoSuchElementException(NO_USER) }
        accountRepository.findByCustomerId(3L) >> { throw new NoSuchElementException(NO_CLARK) }

        def transactionService = new TransactionServiceImpl(userRepository, accountRepository, transactionRepository, PROPERTIES)

        when:
        transactionService.getAllTransactions(userId)

        then:
        def e = thrown(NoSuchElementException)
        e.getMessage() == message

        where:
        userId | message
        3L     | NO_CLARK
        4L     | NO_USER
    }

    def "addTransaction Incorrect amount"() {
        given:
        def transactionService = new TransactionServiceImpl(userRepository, accountRepository, transactionRepository, PROPERTIES)

        when:
        transactionService.addTransaction(1L, 0.0)

        then:
        def e = thrown(UnacceptableAmountException)
        e.getMessage() == NO_AMOUNT
    }

    def "addTransaction OK"() {
        given:
        userRepository.findById(2L) >> Optional.of(USER2)
        accountRepository.findByCustomerId(2L) >> Optional.of(ACCOUNT2)
        transactionRepository.save((Transaction) _) >> TRANSACTION7

        def transactionService = new TransactionServiceImpl(userRepository, accountRepository, transactionRepository, PROPERTIES)

        when:
        def res = transactionService.addTransaction(2L, 130.0)

        then:
        res == TRANSACTION7
    }

    def "updateLastTransaction Cannot find last ID"() {
        given:
        userRepository.findById(1L) >> Optional.of(USER1)
        userRepository.findById(2L) >> Optional.of(USER2)
        accountRepository.findByCustomerId(userId) >> Optional.of(account)
        transactionRepository.findLastId(0L) >> Optional.empty()
        transactionRepository.findLastId(2L) >> Optional.of(2L)
        transactionRepository.findById(2L) >> { throw new TransactionNotFoundException(2L) }
        def transactionService = new TransactionServiceImpl(userRepository, accountRepository, transactionRepository, PROPERTIES)

        when:
        transactionService.updateLastTransaction(userId, amount)

        then:
        def e = thrown(exception)
        e.getMessage() == message

        where:
        amount | userId | id   | account  | exception                    | message
        0.0    | 1L     | null | ACCOUNT1 | UnacceptableAmountException  | NO_AMOUNT
        20.0   | 1L     | null | ACCOUNT0 | TransactionNotFoundException | NO_TRANSACTION
        100.0  | 2L     | 2L   | ACCOUNT2 | TransactionNotFoundException | NO_TRANSACTION_2
    }

    def "updateLastTransaction OK"() {
        given:
        userRepository.findById(2L) >> Optional.of(USER2)
        accountRepository.findByCustomerId(2L) >> Optional.of(ACCOUNT2)
        transactionRepository.findLastId(2L) >> Optional.of(7L)
        transactionRepository.findById(7L) >> Optional.of(TRANSACTION7)
        transactionRepository.save((Transaction) _) >> TRANSACTION7

        def transactionService = new TransactionServiceImpl(userRepository, accountRepository, transactionRepository, PROPERTIES)

        when:
        def res = transactionService.updateLastTransaction(2L, 130.0)

        then:
        res == TRANSACTION7
    }

    def "deleteLastTransaction Exception"() {
        given:
        userRepository.findById(1L) >> Optional.of(USER1)
        accountRepository.findByCustomerId(1L) >> Optional.of(ACCOUNT1)
        transactionRepository.findLastId(1L) >> optLastId
        transactionRepository.findById(2L) >> Optional.empty()
        def transactionService = new TransactionServiceImpl(userRepository, accountRepository, transactionRepository, PROPERTIES)

        when:
        transactionService.deleteLastTransaction(1L)

        then:
        def e = thrown(TransactionNotFoundException)
        e.getMessage() == message

        where:
        optLastId        | message
        Optional.empty() | NO_TRANSACTION
        Optional.of(2L)  | NO_TRANSACTION_2
    }

    def "deleteLastTransaction OK"() {
        given:
        userRepository.findById(2L) >> Optional.of(USER2)
        accountRepository.findByCustomerId(2L) >> Optional.of(ACCOUNT2)
        transactionRepository.findLastId(2L) >> Optional.of(7L)
        transactionRepository.findById(7L) >> Optional.of(TRANSACTION7)

        def transactionService = new TransactionServiceImpl(userRepository, accountRepository, transactionRepository, PROPERTIES)

        when:
        transactionService.deleteLastTransaction(2L)

        then:
        1 * transactionRepository.deleteById(_)
    }
}
