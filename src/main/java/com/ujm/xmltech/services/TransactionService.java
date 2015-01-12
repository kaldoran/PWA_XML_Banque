package com.ujm.xmltech.services;

import com.ujm.xmltech.entity.Transaction;

public interface TransactionService {

	void createTransaction(Transaction transaction);
        
        Transaction findTransactionByMandatID(String mandat_id);
	
}
