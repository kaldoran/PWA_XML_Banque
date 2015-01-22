package com.ujm.xmltech.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ujm.xmltech.dao.TransactionDao;
import com.ujm.xmltech.entity.Pain008File;
import com.ujm.xmltech.entity.Transaction;
import com.ujm.xmltech.services.TransactionService;
import java.util.List;

@Service("TransactionService")
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionDao dao;

    @Override
    public void createTransaction(Pain008File transaction) {
        dao.createTransaction(transaction);
    }

    @Override
    public Transaction findTransactionByMandatID(String mandat_id) {
        return dao.findTransactionByMandatID(mandat_id);
    }

    @Override
    public Transaction findTransactionByMsgId(String mandat_id) {
        return dao.findTransactionByMsgId(mandat_id);
    }

    @Override
    public List<Transaction> findTransactionByDone() {
        return dao.findTransactionByDone();
    }

    @Override
    public Pain008File findPain008FileById(long id) {
        return dao.findPain008FileById(id);
    }

    @Override
    public void updateProceced() {
        dao.updateProceced();
    }
}
