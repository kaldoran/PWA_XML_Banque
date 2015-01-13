/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ujm.xmltech.tasklet;

import com.ujm.xmltech.entity.Transaction;
import com.ujm.xmltech.services.TransactionService;
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
public class ReadDataBase implements Tasklet{
    
    @Autowired
    TransactionService service;
    
    @Override
    public RepeatStatus execute(StepContribution sc, ChunkContext cc) throws Exception {
        List<Transaction> t = null;
        t = service.findTransactionByDone();
        
        if(t!=null) {
            for(Transaction trs : t) {
                System.out.println("No Empty results " + trs.getEndToEndId());
            }
            
        }
        return RepeatStatus.FINISHED;
    }
    
    
    
}
