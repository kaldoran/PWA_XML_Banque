/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ujm.xmltech.tasklet;

import com.ujm.xmltech.adapter.JaxbDateAdapter;
import com.ujm.xmltech.entity.Pain008File;
import com.ujm.xmltech.entity.Transaction;
import com.ujm.xmltech.services.TransactionService;
import com.ujm.xmltech.utils.Banks;
import iso.std.iso._20022.tech.xsd.pain_008_001.AccountIdentification4Choice;
import iso.std.iso._20022.tech.xsd.pain_008_001.ActiveOrHistoricCurrencyAndAmount;
import iso.std.iso._20022.tech.xsd.pain_008_001.BranchAndFinancialInstitutionIdentification4;
import iso.std.iso._20022.tech.xsd.pain_008_001.CashAccount16;

import iso.std.iso._20022.tech.xsd.pain_008_001.ContactDetails2;
import iso.std.iso._20022.tech.xsd.pain_008_001.CustomerDirectDebitInitiationV02;
import iso.std.iso._20022.tech.xsd.pain_008_001.DirectDebitTransaction6;
import iso.std.iso._20022.tech.xsd.pain_008_001.DirectDebitTransactionInformation9;
import iso.std.iso._20022.tech.xsd.pain_008_001.FinancialInstitutionIdentification7;
import iso.std.iso._20022.tech.xsd.pain_008_001.GroupHeader39;
import iso.std.iso._20022.tech.xsd.pain_008_001.MandateRelatedInformation6;
import iso.std.iso._20022.tech.xsd.pain_008_001.PartyIdentification32;
import iso.std.iso._20022.tech.xsd.pain_008_001.PaymentIdentification1;
import iso.std.iso._20022.tech.xsd.pain_008_001.PaymentInstructionInformation4;
import iso.std.iso._20022.tech.xsd.pain_008_001.PaymentMethod2Code;
import iso.std.iso._20022.tech.xsd.pain_008_001.PaymentTypeInformation20;
import iso.std.iso._20022.tech.xsd.pain_008_001.PostalAddress6;
import iso.std.iso._20022.tech.xsd.pain_008_001.SequenceType1Code;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
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

    public static HashMap<String, Object> hashMap_cstmrDrctDbtInitn;

    @Override
    public RepeatStatus execute(StepContribution sc, ChunkContext cc) throws Exception {
        Pain008File file = null;
        List<Transaction> listTransaction = null;
        HashMap<String, ArrayList<Transaction>> hashMap_transactionsSortedByBanks = null;
        hashMap_cstmrDrctDbtInitn = new HashMap<String, Object>();
        
        listTransaction = service.findTransactionByDone();

        hashMap_transactionsSortedByBanks = sortByBank(listTransaction);

        if (hashMap_transactionsSortedByBanks != null) {
            for (String key : hashMap_transactionsSortedByBanks.keySet()) {
                CustomerDirectDebitInitiationV02 pain008 = fillInPain008(hashMap_transactionsSortedByBanks.get(key));
                hashMap_cstmrDrctDbtInitn.put(key, pain008);
            }
        }
        

        return RepeatStatus.FINISHED;
    }

    private CustomerDirectDebitInitiationV02 fillInPain008(ArrayList<Transaction> transactions) {
        CustomerDirectDebitInitiationV02 cstmrDrctDbtInitn = null;
        PaymentInstructionInformation4 paymentInstructionInformation4 = null;
        XMLGregorianCalendar xmlGregorianCalendar = null;
        GroupHeader39 groupHeader39 = null;
        PartyIdentification32 partyIdentification32 = null;
        PartyIdentification32 cdtr = null;
        PostalAddress6 postalAddress6 = null;
        ContactDetails2 contactDetails2 = null;
        DirectDebitTransactionInformation9 drctDbtTxInf = null;
        PaymentIdentification1 paymentIdentification1 = null;
        PaymentTypeInformation20 paymentTypeInformation20 = null;
        ActiveOrHistoricCurrencyAndAmount InstdAmt = null;
        DirectDebitTransaction6 drctDbtTx = null;
        MandateRelatedInformation6 mandateRelatedInformation6 = null;
        BranchAndFinancialInstitutionIdentification4 dbtrAgt = null;
        BranchAndFinancialInstitutionIdentification4 cdtrAgt = null;
        FinancialInstitutionIdentification7 finInstnId = null;
        FinancialInstitutionIdentification7 cdtr_finInstnId = null;
        PartyIdentification32 dbtr = null;
        CashAccount16 cashAccount16 = null;
        CashAccount16 cdtrAccount = null;
        AccountIdentification4Choice accountIdentification4Choice = null;
        AccountIdentification4Choice cdtrAccount_id = null;
        Pain008File file = null;
        long sum = 0 ;
        JaxbDateAdapter dateAdapter = new JaxbDateAdapter();

        if (transactions.isEmpty()) {
            System.out.println("No item in this list transaction !");
            return null;
        }

        file = transactions.get(0).getFile();

        try {
            GregorianCalendar calendar = new GregorianCalendar();
            xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateAdapter.marshalv2(calendar.getTime()));
        } catch (DatatypeConfigurationException ex) {
            Logger.getLogger(ReadDataBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ReadDataBase.class.getName()).log(Level.SEVERE, null, ex);
        }

        //creation of postal address to complete partyIdentification32
        //
        //
        postalAddress6 = new PostalAddress6();
        postalAddress6.setStrtNm(file.getStreetHeader());
        postalAddress6.setTwnNm(file.getTownHeader());
        postalAddress6.setCtry(file.getCountry());

        //creation of contactDetail to complete partyIdentification32
        //
        //
        contactDetails2 = new ContactDetails2();
        contactDetails2.setNm(file.getNameHeader());
        contactDetails2.setEmailAdr(file.getEmail());

        //creation of partyIdentification32 to complete groupHeader39
        //
        //
        partyIdentification32 = new PartyIdentification32();
        partyIdentification32.setNm(file.getNameHeader());
        partyIdentification32.setPstlAdr(postalAddress6);
        partyIdentification32.setCtctDtls(contactDetails2);

        //creation of groupHeader39 to complete cstmrDrctDbtInitn
        //
        //
        for (int i = 0 ; i < transactions.size(); i++) {
            sum += transactions.get(i).getAmount();
        }
        groupHeader39 = new GroupHeader39();
        groupHeader39.setMsgId(file.getMsgId());
        groupHeader39.setCreDtTm(xmlGregorianCalendar);
        groupHeader39.setNbOfTxs(String.valueOf(transactions.size()));
        groupHeader39.setCtrlSum(BigDecimal.valueOf(sum));
        groupHeader39.setInitgPty(partyIdentification32);
        
        paymentInstructionInformation4 = new PaymentInstructionInformation4();
        paymentInstructionInformation4.setPmtInfId("1");
        paymentInstructionInformation4.setPmtMtd(PaymentMethod2Code.DD);
        
        
        for (Transaction t : transactions) {
            cdtr = new PartyIdentification32();
            cdtr.setNm(t.getCreditor_name());
            
            cdtrAccount_id = new AccountIdentification4Choice();
            cdtrAccount_id.setIBAN(t.getIBAN_creditor());
            
            cdtrAccount = new CashAccount16();
            cdtrAccount.setId(cdtrAccount_id);
            
            cdtr_finInstnId = new FinancialInstitutionIdentification7();
            cdtr_finInstnId.setBIC(t.getBIC_creditor());
            
            cdtrAgt = new BranchAndFinancialInstitutionIdentification4();
            cdtrAgt.setFinInstnId(cdtr_finInstnId);
            
            
            try {
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(t.getReqdColltnDt());
                xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateAdapter.marshal(calendar.getTime()));
            } catch (DatatypeConfigurationException ex) {
                Logger.getLogger(ReadDataBase.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(ReadDataBase.class.getName()).log(Level.SEVERE, null, ex);
            }
            paymentInstructionInformation4.setReqdColltnDt(xmlGregorianCalendar);
            paymentInstructionInformation4.setCdtr(dbtr);
            paymentInstructionInformation4.setCdtrAcct(cdtrAccount);
            paymentInstructionInformation4.setCdtrAgt(cdtrAgt);
            
            
            
            drctDbtTxInf = new DirectDebitTransactionInformation9(); 
            //EndToEndId
            paymentIdentification1 = new PaymentIdentification1();
            paymentIdentification1.setEndToEndId(t.getEndToEndId());

            //SeqTp
            paymentTypeInformation20 = new PaymentTypeInformation20();
            if (t.getSeqTp().equals("FRST")) {
                paymentTypeInformation20.setSeqTp(SequenceType1Code.FRST);
            } else if (t.getSeqTp().equals("RCUR")) {
                paymentTypeInformation20.setSeqTp(SequenceType1Code.RCUR);
            } else {
                System.out.println("SeqTp différent de FRST ou RCUR ");
            }

            //Ccy="EUR" & Amount
            InstdAmt = new ActiveOrHistoricCurrencyAndAmount();
            InstdAmt.setCcy("EUR");
            InstdAmt.setValue(BigDecimal.valueOf(t.getAmount()));

            //MndtId & Date Of Signature
            mandateRelatedInformation6 = new MandateRelatedInformation6();
            try {
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(t.getDtOfSgntr());
                xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateAdapter.marshal(calendar.getTime()));
            } catch (DatatypeConfigurationException ex) {
                Logger.getLogger(ReadDataBase.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(ReadDataBase.class.getName()).log(Level.SEVERE, null, ex);
            }
            mandateRelatedInformation6.setMndtId(t.getMndtId());
            mandateRelatedInformation6.setDtOfSgntr(xmlGregorianCalendar);
            
            drctDbtTx = new DirectDebitTransaction6();
            drctDbtTx.setMndtRltdInf(mandateRelatedInformation6);

            //BIC Debitor
            
            finInstnId = new FinancialInstitutionIdentification7();
            finInstnId.setBIC(t.getBIC_debitor());
            
            
            dbtrAgt = new BranchAndFinancialInstitutionIdentification4();
            dbtrAgt.setFinInstnId(finInstnId);

            //name Debitor
            dbtr = new PartyIdentification32();
            dbtr.setNm(t.getDebitor_name());

            //IBAN Debitor
            accountIdentification4Choice = new AccountIdentification4Choice();
            accountIdentification4Choice.setIBAN(t.getIBAN_debitor());
            cashAccount16 = new CashAccount16();
            cashAccount16.setId(accountIdentification4Choice);
            

            drctDbtTxInf.setPmtId(paymentIdentification1);
            drctDbtTxInf.setPmtTpInf(paymentTypeInformation20);
            drctDbtTxInf.setInstdAmt(InstdAmt);
            drctDbtTxInf.setDrctDbtTx(drctDbtTx);
            drctDbtTxInf.setDbtrAgt(dbtrAgt);
            drctDbtTxInf.setDbtr(dbtr);
            drctDbtTxInf.setDbtrAcct(cashAccount16);
            
            paymentInstructionInformation4.getDrctDbtTxInf().add(drctDbtTxInf);
        }
        
        cstmrDrctDbtInitn = new CustomerDirectDebitInitiationV02();
        cstmrDrctDbtInitn.setGrpHdr(groupHeader39);
        cstmrDrctDbtInitn.getPmtInf().add(paymentInstructionInformation4);

        return cstmrDrctDbtInitn;
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
