package org.example.service.impl

import org.example.enums.TransactionTypeEnum

import org.example.entity.Account
import org.example.entity.Transaction
import org.example.entity.Customer
import org.example.properties.RewardAppProperties
import org.example.properties.Threshold
import org.example.service.AccountService
import org.example.service.CustomerService
import org.example.service.TransactionService
import org.springframework.dao.InvalidDataAccessApiUsageException
import spock.lang.Specification

import java.sql.Timestamp
import java.time.LocalDateTime

class RewardServiceTest extends Specification {
    def static PROPERTIES = new RewardAppProperties(3,
            List.of(new Threshold(BigDecimal.valueOf(50), 1),
                    new Threshold(BigDecimal.valueOf(100), 2)))

    def static USER1 = new Customer(1L, "John", "Wick")
    def static USER2 = new Customer(2L, "Howard", "Stark")

    def static ACCOUNT1 = new Account(1, USER1, 20.0, Timestamp.valueOf(LocalDateTime.now()))
    def static ACCOUNT2 = new Account(2, USER2, 580.0, Timestamp.valueOf(LocalDateTime.now()))

    def static TRANSACTION1 = new Transaction(1, ACCOUNT1, 20.00,
            TransactionTypeEnum.A, Timestamp.valueOf(LocalDateTime.now()))
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

    def "CountReward check"() {
        given:
        def transactionService = Mock(TransactionService)
        def accountService = Mock(AccountService)
        def customerService = Mock(CustomerService)
        transactionService.getLastTransactions(userId) >> transactions
        def rewardService = new RewardServiceImpl(PROPERTIES, accountService, customerService, transactionService)

        when:
        def count = rewardService.countReward(userId)

        then:
        count == expected

        where:
        userId | transactions                       | expected
        1      | List.of(TRANSACTION1)              | 0.0
        2      | List.of(TRANSACTION3, TRANSACTION4,
                TRANSACTION5, TRANSACTION6,
                TRANSACTION7)                       | 270.0
    }

    def "CountReward exception check"() {
        given:
        def transactionService = Mock(TransactionService)
        def accountService = Mock(AccountService)
        def customerService = Mock(CustomerService)
        transactionService.getLastTransactions(3) >> {throw new NoSuchElementException("Account belonging to user Clark Kent not found")}
        transactionService.getLastTransactions(null) >> {throw new InvalidDataAccessApiUsageException("The given id must not be null!; nested exception is java.lang.IllegalArgumentException: The given id must not be null!")}
        def rewardService = new RewardServiceImpl(PROPERTIES, accountService, customerService, transactionService)

        when:
        rewardService.countReward(userId)

        then:
        thrown(exception)

        where:
        userId | exception
        3      | NoSuchElementException
        null   | InvalidDataAccessApiUsageException
    }
}
