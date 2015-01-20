package com.ujm.xmltech.services;

import com.ujm.xmltech.entity.Pain008File;
import com.ujm.xmltech.entity.Transaction;
import java.util.List;

public interface TransactionService {

	void createTransaction(Pain008File transaction);
        
        Transaction findTransactionByMandatID(String mandat_id);
        
        Transaction findTransactionByMsgId(String mandat_id);
        
        List<Transaction> findTransactionByDone();
	
}
