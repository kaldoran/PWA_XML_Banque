package com.ujm.xmltech.entity;

import com.ujm.xmltech.utils.BankSimulationConstants;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;

@Entity
public class Transaction implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8315057757268890401L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long amount;

    private String endToEndId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getEndToEndId() {
        return endToEndId;
    }

    public void setEndToEndId(String endToEndId) {
        this.endToEndId = endToEndId;
    }

}
