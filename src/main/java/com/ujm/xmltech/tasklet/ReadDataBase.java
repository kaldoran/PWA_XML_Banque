/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ujm.xmltech.tasklet;

import com.ujm.xmltech.entity.Pain008File;
import com.ujm.xmltech.entity.Transaction;
import com.ujm.xmltech.services.TransactionService;
import com.ujm.xmltech.utils.Banks;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author kevin
 */
public class ReadDataBase implements Tasklet {

    @Autowired
    TransactionService service;
    
    public static HashMap<String, ArrayList<Transaction>> hashMap_transactionsSortedByBanks = null;

    @Override
    public RepeatStatus execute(StepContribution sc, ChunkContext cc) throws Exception {
        Pain008File file = null;
        List<Transaction> listTransaction = null;
      
        listTransaction = service.findTransactionByDone();
        service.updateProceced();

        hashMap_transactionsSortedByBanks = sortByBank(listTransaction);

        return RepeatStatus.FINISHED;
    }

    private HashMap<String, ArrayList<Transaction>> sortByBank(List<Transaction> list) {
        HashMap<String, ArrayList<Transaction>> hashmap = null;

        if (list == null) {
            return null;
        }
        
        if (list.isEmpty()) {
            return null;
        }

        //HashMap initialization
        //
        //
        hashmap = new HashMap<String, ArrayList<Transaction>>();
        for (Banks bank : Banks.values()) {
            hashmap.put(bank.toString(), new ArrayList<Transaction>());
        }

        //Sort transactions and stock in hashMap
        //
        //
        for (Transaction item : list) {
            if (hashmap.containsKey(item.getBIC_debitor().substring(0, 4))) {
                hashmap.get(item.getBIC_debitor().substring(0, 4)).add(item);
                System.out.println("key : " + item.getBIC_debitor().substring(0, 4) + " + " + item.getEndToEndId());
            }
        }

        return hashmap;
    }
    
}
