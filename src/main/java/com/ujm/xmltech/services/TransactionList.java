/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ujm.xmltech.services;

import com.ujm.xmltech.entity.Transaction;
import com.ujm.xmltech.utils.BankSimulationConstants;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 * @author bascool
 */

@Stateless
public class TransactionList {
    @PersistenceContext(unitName = BankSimulationConstants.PERSISTENCE_UNIT)
    EntityManager em;
    
    
    PagingAndSortingRepository<Transaction, String> list;
    
    public void initList(int limit){
        
        String query = "SELECT * FROM Transaction ORDER BY amount";
        
        if(limit > 0){
            query += " LIMIT "+limit;
        }
        
        List<Transaction> it = em.createQuery(query,Transaction.class).getResultList();
        
        list.save(it);
    }

}
