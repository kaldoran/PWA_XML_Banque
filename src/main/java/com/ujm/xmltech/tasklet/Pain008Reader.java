package com.ujm.xmltech.tasklet;

import com.ujm.xmltech.entity.Transaction;
import com.ujm.xmltech.services.TransactionService;
import iso.std.iso._20022.tech.xsd.pain_008_001.Document;
import iso.std.iso._20022.tech.xsd.pain_008_001.GroupHeader39;
import iso.std.iso._20022.tech.xsd.pain_008_001.ObjectFactory;
import iso.std.iso._20022.tech.xsd.pain_008_001.PaymentInstructionInformation4;

import java.io.File;
import java.io.FileReader;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.repeat.RepeatStatus;

import com.ujm.xmltech.utils.BankSimulationConstants;
import com.ujm.xmltech.utils.Banks;
import iso.std.iso._20022.tech.xsd.pain_008_001.DirectDebitTransactionInformation9;
import java.security.Timestamp;
import java.util.Date;
import java.util.GregorianCalendar;
import org.springframework.beans.factory.annotation.Autowired;

public class Pain008Reader implements Tasklet {
    
    @Autowired
    private TransactionService service;
    private Date date_today = new Date();
    
    @Override
    public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
        Object o = read((String) arg1.getStepContext().getJobParameters().get("inputFile"));
        /*if(o !=null) {
            System.out.println("file readed : true in ["   + BankSimulationConstants.ARCHIVE_DIRECTORY 
                                                        + (String) arg1.getStepContext().getJobParameters().get("inputFile")+ "]" );
            arg1.getStepContext().getStepExecution().getExecutionContext().put("it", o);
        } else {
            System.out.println("file checked : false ! in ["    + BankSimulationConstants.ARCHIVE_DIRECTORY 
                                                        + (String) arg1.getStepContext().getJobParameters().get("inputFile")+ "]" );
        }*/
        
        return RepeatStatus.FINISHED;
    }

    @SuppressWarnings("rawtypes")
    public Object read(String fileName) throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        JAXBContext jc;
        try {
            jc = JAXBContext.newInstance(ObjectFactory.class);
            Unmarshaller u = jc.createUnmarshaller();
            File f = new File(BankSimulationConstants.WORK_DIRECTORY + fileName);
            FileReader fileReader = new FileReader(f);
            JAXBElement element = (JAXBElement) u.unmarshal(fileReader);
            Document document = (Document) element.getValue();
            GroupHeader39 header = document.getCstmrDrctDbtInitn().getGrpHdr();
            System.out.println(header.getMsgId());
            Iterator<PaymentInstructionInformation4> it = document.getCstmrDrctDbtInitn().getPmtInf().iterator();
            while (it.hasNext()) {
                PaymentInstructionInformation4 transaction = it.next();
                Transaction t = null;
                Iterator<DirectDebitTransactionInformation9> it2 = transaction.getDrctDbtTxInf().iterator();
                while (it2.hasNext()) {
                    t = new Transaction();
                    DirectDebitTransactionInformation9 directDebitTransactionInformation = it2.next();
                    t.setAmount(directDebitTransactionInformation.getInstdAmt().getValue().longValue());
                    t.setEndToEndId(directDebitTransactionInformation.getPmtId().getEndToEndId()); 
                    t.setMandatID(directDebitTransactionInformation.getDrctDbtTx().getMndtRltdInf().getMndtId());
                    t.setDateOfSignature(directDebitTransactionInformation.getDrctDbtTx().getMndtRltdInf().getDtOfSgntr().toGregorianCalendar().getTime().toString());
                    t.setIBAN_debitor(directDebitTransactionInformation.getDbtrAcct().getId().getIBAN());
                    t.setBIC_debitor(directDebitTransactionInformation.getDbtrAgt().getFinInstnId().getBIC());
                    t.setIBAN_creditor(transaction.getCdtrAcct().getId().getIBAN());
                    t.setBIC_creditor(transaction.getCdtrAgt().getFinInstnId().getBIC());
//System.out.println("SeqTp : " + directDebitTransactionInformation.getPmtTpInf().getSeqTp().value());
                    /** do pain008Processor step*/
                    pain008Processor(   t, 
                                        directDebitTransactionInformation.getInstdAmt().getCcy() , 
                                        directDebitTransactionInformation.getDbtrAgt().getFinInstnId().getBIC(),
                                        transaction.getReqdColltnDt().toGregorianCalendar().getTime(),
                                        directDebitTransactionInformation.getDrctDbtTx().getMndtRltdInf().getDtOfSgntr().toGregorianCalendar());
    
    //service.createTransaction(directDebitTransactionInformation.getInstdAmt().getValue().longValue(), directDebitTransactionInformation.getPmtId().getEndToEndId());
                }

                System.out.println(transaction.getPmtInfId());
            }
            return document.getCstmrDrctDbtInitn();
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RepeatStatus.FINISHED;
    }
    
    public boolean pain008Processor( Transaction transaction, String Ccy, String BIC, Date dateOfTransaction, GregorianCalendar dateOfSignature) {
        int diffMonth,diffYears,diffDays;
        boolean existes = false;
        String bank = BIC.substring(0, 4);
        GregorianCalendar grgrnCldr = new GregorianCalendar();
        Date today = grgrnCldr.getTime();
        //System.out.println(today.toString());
        System.out.println("aujourd'hui  : " + grgrnCldr.get(GregorianCalendar.DAY_OF_MONTH)); 
        //Il n'est pas autorisé de créer une transaction dans la passé (RJC004)
        //
        //
        System.out.println("today" + today.toString() + " < Date date of Transaction : " + dateOfTransaction.toString() + " compare " + today.compareTo(dateOfTransaction));
        if(today.after(dateOfTransaction)) {
            System.out.println("rejeted : date of transaction is outdated");
            return false;
        }
        
        //Il n'est pas autorisé de créer une transaction dans plus de 13 mois (RJC005)
        //
        //
        diffYears = today.getYear() - dateOfSignature.getTime().getYear();
        if(diffYears > 0) {
            diffMonth = (diffYears*12-dateOfSignature.getTime().getMonth()) + today.getMonth();
            if(diffMonth > 13) {
                System.out.println("rejected date of transaction outdates 13 months");
                return false;
            }
            
            if(diffMonth == 13) {
                System.out.println("day date of signature : " + dateOfSignature.get(GregorianCalendar.DAY_OF_MONTH) + " today " + grgrnCldr.get(GregorianCalendar.DAY_OF_MONTH));
                diffDays = dateOfSignature.get(GregorianCalendar.DAY_OF_MONTH) - grgrnCldr.get(GregorianCalendar.DAY_OF_MONTH);
                
                if(diffDays < 0) {
                    System.out.println("rejected date of transaction outdates 13 months");
                    return false;
                }
            }
        }
        
        /** if debit account belongs to existed banks */
        for ( Banks bankExistes : Banks.values() ) {
            System.out.println("bankExistes : " + bankExistes.toString() + " " + bankExistes.name());
            if ( bank.equals(bankExistes.toString()) ) {
                existes = true;
                System.out.println(" existes TRUE !!");
                break;
            }
        }
        
        if ( !existes ) {
            return false;
        }
        
        /**if amount's money is different than euro*/
        if ( !Ccy.equals("EUR") ) {
            return false;
        }
        
        /** if transaction's amount is less than 1 euro*/
        if ( transaction.getAmount() < 1 ) {
            System.out.println("rejected amount less than 1");
            return false; 
        }
        
        /** if transaction's amount is  greater than 10 000 euros */
        if ( transaction.getAmount() > 10000) {
            System.out.println("rejected amount less than 10 000");
            return false;
        }

        service.createTransaction(transaction);
        return true;
    }
}     