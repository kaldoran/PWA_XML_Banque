package com.ujm.xmltech.dao;

import com.ujm.xmltech.entity.Transaction;
import java.util.List;

public interface TransactionDao {

  void createTransaction(Transaction transaction);

  Transaction findTransactionById(long id);
  
  Transaction findTransactionByMandatID(String mandat_id);
  
  Transaction findTransactionByMsgId(String mandat_id);
  
  List<Transaction> findTransactionByDone();

}
