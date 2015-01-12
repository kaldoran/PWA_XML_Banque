package com.ujm.xmltech.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ujm.xmltech.dao.TransactionDao;
import com.ujm.xmltech.entity.Transaction;
import com.ujm.xmltech.services.TransactionService;

@Service("TransactionService")
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionDao dao;

    @Override
    public void createTransaction(Transaction transaction) {
        dao.createTransaction(transaction);
    }

    @Override
    public Transaction findTransactionByMandatID(String mandat_id) {
        return dao.findTransactionByMandatID(mandat_id);
    }
}
