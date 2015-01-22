/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ujm.xmltech.tasklet;

import com.ujm.xmltech.adapter.JaxbDateAdapter;
import com.ujm.xmltech.entity.Pain008File;
import com.ujm.xmltech.entity.Transaction;
import com.ujm.xmltech.utils.BankSimulationConstants;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

/**
 *
 * @author kevin
 */
public class Pain008Writer implements Tasklet {
    private static long id_file = 1000;
    //public static HashMap<String, Object> hashMap_cstmrDrctDbtInitn;
    @Override
    public RepeatStatus execute(StepContribution sc, ChunkContext cc) throws Exception {
        //hashMap_cstmrDrctDbtInitn = new HashMap<String, Object>();
        if (ReadDataBase.hashMap_transactionsSortedByBanks != null) {
            for (String key : ReadDataBase.hashMap_transactionsSortedByBanks.keySet()) {
                CustomerDirectDebitInitiationV02 pain008 = fillInPain008(ReadDataBase.hashMap_transactionsSortedByBanks.get(key));
                //hashMap_cstmrDrctDbtInitn.put(key, pain008);
                if (pain008 != null) {
                    write(pain008, key);
                }
                
            }
        }
        /*for (String key : hashMap_cstmrDrctDbtInitn.keySet()) {
            if(hashMap_cstmrDrctDbtInitn != null) {
                Object o = hashMap_cstmrDrctDbtInitn.get(key);
                if(o != null) {
                    write(o, key);
                }
                
            }
        }*/
        System.out.println("fin Pain008Writer");
        return RepeatStatus.FINISHED;
    }
    
    public void write(Object item, String bank) {
        //Added a random in order to have a different file each time
        File file = null;
        if (bank.equals("BKNK"))
        {
            file = new File(BankSimulationConstants.BKNK_DIRECTORY + "pain008" +bank+ Math.random() + ".xml");
        } else if (bank.equals("UDEV")) {
            file = new File(BankSimulationConstants.UDEV_DIRECTORY + "pain008" +bank+ Math.random() + ".xml");
        } else {
            file = new File(BankSimulationConstants.REJECT_DIRECTORY + "pain008" +bank+ Math.random() + ".xml");
        }
        
        OutputStream out;
        try {
            out = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(out);
            JAXBContext ctx = JAXBContext.newInstance(item.getClass());
            Marshaller marshaller = ctx.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty("jaxb.fragment", Boolean.TRUE);
            //writer file header
            String documentBase = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.008.001.02\">\n";
            writer.write(documentBase);
            //write header item
            writer.write(getXMLFragment(item, "CstmrDrctDbtInitn", marshaller) + "\n");
            //write footer
            String documentEnd = "\n</Document>";
            writer.write(documentEnd);
            writer.close();
            out.close();    
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Transform an object into xml string
     *
     * @param object
     * @param name
     * @param marshaller
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private String getXMLFragment(Object object, String name, Marshaller marshaller) {
        StringWriter writer = new StringWriter();
        try {
            marshaller.marshal(new JAXBElement(new QName("", name, ""), object.getClass(), object), writer);
        } catch (JAXBException e) {
            return null;
        }
        String originFragment = writer.toString();
        String fragment = originFragment.replaceAll("<" + name + ".*>", "<" + name + ">").replaceAll("<ns2:", "<").replaceAll("</ns2:", "</");
        fragment = fragment.replaceAll("&quot;", "\"").replaceAll("&apos;", "\'");

        return fragment;
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
        groupHeader39.setMsgId(generatePain008FileId());
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
            InstdAmt.setCcy(t.getCCy());
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
    
    private String generatePain008FileId () {
        
        return "bknk" + id_file++ + (int)(Math.random()*100);
    }
}
