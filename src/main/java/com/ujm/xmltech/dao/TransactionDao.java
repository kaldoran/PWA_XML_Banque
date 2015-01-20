package com.ujm.xmltech.dao;

import com.ujm.xmltech.entity.Pain008File;
import com.ujm.xmltech.entity.Transaction;
import java.util.List;

public interface TransactionDao {

  void createTransaction(Pain008File transaction);

  Transaction findTransactionById(long id);
  
  Transaction findTransactionByMandatID(String mandat_id);
  
  Transaction findTransactionByMsgId(String mandat_id);
  
  List<Transaction> findTransactionByDone();

}
