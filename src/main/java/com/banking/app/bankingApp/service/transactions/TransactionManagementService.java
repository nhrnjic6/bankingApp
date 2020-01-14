package com.banking.app.bankingApp.service.transactions;

import com.banking.app.bankingApp.database.transactions.DBTransaction;
import com.banking.app.bankingApp.database.transactions.TransactionsDatabaseService;
import com.banking.app.bankingApp.request.transactions.CreateTransaction;
import com.banking.app.bankingApp.request.transactions.UpdateTransaction;
import com.banking.app.bankingApp.response.accounts.Account;
import com.banking.app.bankingApp.response.transactions.Transaction;
import com.banking.app.bankingApp.service.accounts.AccountManagementService;
import com.banking.app.bankingApp.service.balance.BalanceManagementService;

import java.util.ArrayList;
import java.util.List;

public class TransactionManagementService {
    private static final TransactionManagementService transactionManagementService = new TransactionManagementService();
    private TransactionsDatabaseService transactionsDatabaseService;
    private BalanceManagementService balanceManagementService;
    private AccountManagementService accountManagementService;

    public TransactionManagementService() {
        accountManagementService = AccountManagementService.getInstance();
        transactionsDatabaseService = TransactionsDatabaseService.getInstance();
        balanceManagementService = BalanceManagementService.getInstance();
    }

    public static TransactionManagementService getInstance() {
        return transactionManagementService;
    }

    public Transaction addTransaction(CreateTransaction createTransaction) {
        Account account = accountManagementService.getAccountById(createTransaction.getSourceId());
        if (account.getBalance() <= balanceManagementService.getBalance(createTransaction.getSourceId())) {
            DBTransaction dbTransaction = transactionsDatabaseService.addDBTransaction(createTransaction);
            Transaction transaction = new Transaction();
            transaction.setId(dbTransaction.getId());
            transaction.setAmount(dbTransaction.getAmount());
            transaction.setSourceId(dbTransaction.getSourceAccount().getId());
            transaction.setDestinationId(dbTransaction.getDestinationAccount().getId());
            transaction.setPurpose(dbTransaction.getPurpose());
            transaction.setCreatedAt(dbTransaction.getCreatedAt());
            return transaction;
        } else {

            throw new RuntimeException();
        }
    }

    public Transaction getTransactionById(String id) {
        Transaction transaction = new Transaction();
        DBTransaction dbTransaction = transactionsDatabaseService.getDBTransactionById(id);
        transaction.setId(dbTransaction.getId());
        transaction.setAmount(dbTransaction.getAmount());
        transaction.setPurpose(dbTransaction.getPurpose());
        transaction.setSourceId(dbTransaction.getSourceAccount().getId());
        transaction.setDestinationId(dbTransaction.getDestinationAccount().getId());
        transaction.setCreatedAt(dbTransaction.getCreatedAt());
        return transaction;
    }

    public List<Transaction> getAllTransactions(String id) {
        List<Transaction> transactions = new ArrayList<>();
        List<Transaction> transactions1 = new ArrayList<>();
        List<DBTransaction> dbTransactions = transactionsDatabaseService.getAllDBTransactionsWhereSource(id);
        List<DBTransaction> dbTransactions1 = transactionsDatabaseService.getAllDBTransactionsWhereDestination(id);
        for (int i = 0; i < dbTransactions.size(); i++) {
            Transaction transaction = new Transaction();
            transaction.setId(dbTransactions.get(i).getId());
            transaction.setAmount(dbTransactions.get(i).getAmount());
            transaction.setPurpose(dbTransactions.get(i).getPurpose());
            transaction.setSourceId(dbTransactions.get(i).getSourceAccount().getId());
            transaction.setDestinationId(dbTransactions.get(i).getDestinationAccount().getId());
            transaction.setCreatedAt(dbTransactions.get(i).getCreatedAt());
            transactions1.add(transaction);
        }
        for (int i = 0; i < dbTransactions.size(); i++) {
            Transaction transaction = new Transaction();
            transaction.setId(dbTransactions1.get(i).getId());
            transaction.setAmount(dbTransactions1.get(i).getAmount());
            transaction.setPurpose(dbTransactions1.get(i).getPurpose());
            transaction.setSourceId(dbTransactions1.get(i).getSourceAccount().getId());
            transaction.setDestinationId(dbTransactions1.get(i).getDestinationAccount().getId());
            transaction.setCreatedAt(dbTransactions1.get(i).getCreatedAt());
            transactions.add(transaction);
        }
        for (int i = 0; i < dbTransactions.size() + dbTransactions1.size(); i++) {
            transactions.add(transactions1.get(i));
        }
        return transactions;
    }

    public void updateTransaction(String id, UpdateTransaction updateTransaction) {
        Account account = accountManagementService.getAccountById(updateTransaction.getSourceId());
        if (account.getBalance() <= balanceManagementService.getBalance(updateTransaction.getSourceId())) {
            transactionsDatabaseService.updateDBTransaction(updateTransaction, id);
        } else {
            throw new IllegalStateException();
        }
    }

    public void deleteTransaction(String id) {
        transactionsDatabaseService.deleteDBTransaction(id);
    }
}
