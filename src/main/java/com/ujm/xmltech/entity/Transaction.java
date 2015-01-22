package com.ujm.xmltech.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Transaction implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8315057757268890401L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String IBAN_debitor;

    private String BIC_debitor;

    private boolean proceced = false;

    private boolean transfer = false;

    private String IBAN_creditor;

    private String BIC_creditor;
    
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Pain008File file;

    private String endToEndId;

    private String seqTp;
    
    private String CCy;

    private long amount;

    private String mndtId;

    @Temporal(TemporalType.DATE)
    private Date dtOfSgntr;
    
    @Temporal(TemporalType.DATE)
    private Date ReqdColltnDt;
    
    private String pmtInf;
    
    private String creditor_name;
    
    private String debitor_name;
    
    private boolean done;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isProceced() {
        return proceced;
    }

    public void setProceced(boolean proceced) {
        this.proceced = proceced;
    }

    public boolean isTransfer() {
        return transfer;
    }

    public void setTransfer(boolean transfer) {
        this.transfer = transfer;
    }

    public Pain008File getFile() {
        return file;
    }

    public void setFile(Pain008File file) {
        this.file = file;
    }

    public String getEndToEndId() {
        return endToEndId;
    }

    public void setEndToEndId(String endToEndId) {
        this.endToEndId = endToEndId;
    }

    public String getSeqTp() {
        return seqTp;
    }

    public void setSeqTp(String seqTp) {
        this.seqTp = seqTp;
    }

    public String getCCy() {
        return CCy;
    }

    public void setCCy(String CCy) {
        this.CCy = CCy;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getMndtId() {
        return mndtId;
    }

    public void setMndtId(String mndtId) {
        this.mndtId = mndtId;
    }

    public Date getDtOfSgntr() {
        return dtOfSgntr;
    }

    public void setDtOfSgntr(Date dtOfSgntr) {
        this.dtOfSgntr = dtOfSgntr;
    }

    public Date getReqdColltnDt() {
        return ReqdColltnDt;
    }

    public void setReqdColltnDt(Date ReqdColltnDt) {
        this.ReqdColltnDt = ReqdColltnDt;
    }

    public String getIBAN_debitor() {
        return IBAN_debitor;
    }

    public void setIBAN_debitor(String IBAN_debitor) {
        this.IBAN_debitor = IBAN_debitor;
    }

    public String getBIC_debitor() {
        return BIC_debitor;
    }

    public void setBIC_debitor(String BIC_debitor) {
        this.BIC_debitor = BIC_debitor;
    }

    public String getIBAN_creditor() {
        return IBAN_creditor;
    }

    public void setIBAN_creditor(String IBAN_creditor) {
        this.IBAN_creditor = IBAN_creditor;
    }

    public String getBIC_creditor() {
        return BIC_creditor;
    }

    public void setBIC_creditor(String BIC_creditor) {
        this.BIC_creditor = BIC_creditor;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getPmtInf() {
        return pmtInf;
    }

    public void setPmtInf(String pmtInf) {
        this.pmtInf = pmtInf;
    }

    public String getCreditor_name() {
        return creditor_name;
    }

    public void setCreditor_name(String creditor_name) {
        this.creditor_name = creditor_name;
    }

    public String getDebitor_name() {
        return debitor_name;
    }

    public void setDebitor_name(String debitor_name) {
        this.debitor_name = debitor_name;
    }
    
}
