package com.ujm.xmltech.tasklet;

import com.ujm.xmltech.adapter.JaxbDateAdapter;
import com.ujm.xmltech.entity.Pain008File;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class Pain008Reader implements Tasklet {

    @Autowired
    private TransactionService service;
    public static HashMap<String, ArrayList<Transaction>> hashMap_transactions;

    @Override
    public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
        if (Pain008Checker.isValide) {
            System.out.println("inputFile : " + (String) arg1.getStepContext().getJobParameters().get("inputFile"));
            Object o = read((String) arg1.getStepContext().getJobParameters().get("inputFile"));
        }

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
            Pain008File file = null;
            System.out.println(header.getMsgId());

            if (checkMsgId(header)) {

                if (checkSumChecker(header, document.getCstmrDrctDbtInitn().getPmtInf().iterator())) {
                    file = new Pain008File();
                    file.setMsgId(header.getMsgId());
                    file.setNameHeader(header.getInitgPty().getNm());
                    file.setStreetHeader(header.getInitgPty().getPstlAdr().getStrtNm());
                    file.setTownHeader(header.getInitgPty().getPstlAdr().getTwnNm());
                    file.setCountry(header.getInitgPty().getPstlAdr().getCtry());
                    file.setEmail(header.getInitgPty().getCtctDtls().getEmailAdr());
                    
                    Iterator<PaymentInstructionInformation4> it = document.getCstmrDrctDbtInitn().getPmtInf().iterator();
                    List<Transaction> list_transaction = new ArrayList<Transaction>();
                    while (it.hasNext()) {
                        PaymentInstructionInformation4 transaction = it.next();

                        Iterator<DirectDebitTransactionInformation9> it2 = transaction.getDrctDbtTxInf().iterator();

                        while (it2.hasNext()) {
                            Transaction t = new Transaction();

                            DirectDebitTransactionInformation9 directDebitTransactionInformation = it2.next();

                            t.setAmount(directDebitTransactionInformation.getInstdAmt().getValue().longValue());

                            t.setEndToEndId(directDebitTransactionInformation.getPmtId().getEndToEndId());

                            t.setMndtId(directDebitTransactionInformation.getDrctDbtTx().getMndtRltdInf().getMndtId());

                            t.setDtOfSgntr(directDebitTransactionInformation.getDrctDbtTx().getMndtRltdInf().getDtOfSgntr().toGregorianCalendar().getTime());
                            
                            t.setReqdColltnDt(transaction.getReqdColltnDt().toGregorianCalendar().getTime());
                            
                            t.setIBAN_debitor(directDebitTransactionInformation.getDbtrAcct().getId().getIBAN());

                            t.setBIC_debitor(directDebitTransactionInformation.getDbtrAgt().getFinInstnId().getBIC());

                            t.setIBAN_creditor(transaction.getCdtrAcct().getId().getIBAN());

                            t.setBIC_creditor(transaction.getCdtrAgt().getFinInstnId().getBIC());

                            t.setSeqTp(directDebitTransactionInformation.getPmtTpInf().getSeqTp().value());

                            t.setFile(file);
                            
                            t.setCreditor_name(transaction.getCdtr().getNm());
                            
                            t.setDebitor_name(directDebitTransactionInformation.getDbtr().getNm());
                            
                            t.setCCy(directDebitTransactionInformation.getInstdAmt().getCcy());
                            list_transaction.add(t);
                          
                        }
                    }

                    // do pain008Processor step
                    //
                    //
                    hashMap_transactions = pain008Processor(list_transaction);

                    file.setTransaction(hashMap_transactions.get("PERSIST"));
                    service.createTransaction(file);
                }
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RepeatStatus.FINISHED;
    }

    public HashMap<String, ArrayList<Transaction>> pain008Processor(List<Transaction> list_transaction) {
        GregorianCalendar grgrnCldr = new GregorianCalendar();
        Date today = grgrnCldr.getTime();
        int diffMonth, diffYears, diffDays;
        HashMap<String, ArrayList<Transaction>> hashMap_transactions = new HashMap<String, ArrayList<Transaction>>();
        
        hashMap_transactions.put("PERSIST", new ArrayList<Transaction>());
        hashMap_transactions.put("RJC000", new ArrayList<Transaction>());
        hashMap_transactions.put("RJC001", new ArrayList<Transaction>());
        hashMap_transactions.put("RJC002", new ArrayList<Transaction>());
        hashMap_transactions.put("RJC003", new ArrayList<Transaction>());
        hashMap_transactions.put("RJC004", new ArrayList<Transaction>());
        hashMap_transactions.put("RJC006", new ArrayList<Transaction>());
        hashMap_transactions.put("RJC007", new ArrayList<Transaction>());
        hashMap_transactions.put("RJC008", new ArrayList<Transaction>());
        

        for ( Transaction transaction : list_transaction) {
            boolean existes = false;
            long diffD = ((transaction.getDtOfSgntr().getTime() - today.getTime()) / (24 * 60 * 60 * 1000));
            System.out.println("diff D : " + diffD);
            
            //if debit account belongs to existed banks  (RJC000)
            //
            //
            for (Banks bankExistes : Banks.values()) {
                if (transaction.getBIC_debitor().substring(0, 4).equals(bankExistes.toString())) {
                    existes = true;
                    break;
                }
            }

            if (!existes) {
                System.out.println("rejected inexisted bank");
                hashMap_transactions.get("RJC000").add(transaction);
            }

            // if transaction's amount is less than 1 euro (RJC001)
            //
            //
            if (transaction.getAmount() < 1) {
                System.out.println("rejected amount less than 1");
                hashMap_transactions.get("RJC001").add(transaction);
            }

            // if transaction's amount is  greater than 10 000 euros (RJC002)
            //
            //
            if (transaction.getAmount() > 10000) {
                System.out.println("rejected amount less than 10 000");
                hashMap_transactions.get("RJC002").add(transaction);
            }

            //if amount's money is different than euro (RJC003)
            //
            //
            if (!transaction.getCCy().equals("EUR")) {
                System.out.println("");
                hashMap_transactions.get("RJC003").add(transaction);
            }

            //no transaction approved in past(RJC004)
            //
            // 
            if (today.after(transaction.getReqdColltnDt())) {
                System.out.println("rejeted : date of transaction is outdated");
                hashMap_transactions.get("RJC004").add(transaction);
            }

            //no transaction approved in more than 13 months (RJC005)
            //
            //
            diffYears = today.getYear() - transaction.getDtOfSgntr().getYear();
            if (diffYears > 0) {
                diffMonth = (diffYears * 12 - transaction.getDtOfSgntr().getMonth()) + today.getMonth();
                if (diffMonth > 13) {
                    System.out.println("rejected date of transaction outdates 13 months");
                    hashMap_transactions.get("RJC005").add(transaction);
                }

                if (diffMonth == 13) {
                    GregorianCalendar tmp = new GregorianCalendar();
                    tmp.setTime(transaction.getDtOfSgntr());
                    System.out.println("day date of signature : " + tmp.get(GregorianCalendar.DAY_OF_MONTH) + " today " + grgrnCldr.get(GregorianCalendar.DAY_OF_MONTH));
                    diffDays = tmp.get(GregorianCalendar.DAY_OF_MONTH) - grgrnCldr.get(GregorianCalendar.DAY_OF_MONTH);

                    if (diffDays < 0) {
                        System.out.println("rejected date of transaction outdates 13 months");
                        hashMap_transactions.get("RJC005").add(transaction);
                    }
                }
            }

            //If the sequence type (tag <SeqTp>) is RCUR and that the date is in less than 2 days
            //you must reject the transaction (RJC006)
            //
            if (transaction.getSeqTp().equals("RCUR") && diffD < 2) {
                System.out.println("rejected : " + today.toString() + " datePrelevement : " + transaction.getDtOfSgntr().toString() + " seqTpType = " + transaction.getSeqTp());
                hashMap_transactions.get("RJC006").add(transaction);
            }

            //If the sequence type (tag <SeqTp>) is FRST and the date is in less than 5 days
            //you must reject the transaction (RJC007)
            //
            if (transaction.getSeqTp().equals("FRST") && diffD < 5) {
                System.out.println("rejected FRST Or diff < 5");
                hashMap_transactions.get("RJC007").add(transaction);
            }

            //(RJC008)
            //
            //
            if (transaction.getSeqTp().equals("RCUR")) {
                Transaction result = null;
                result = service.findTransactionByMandatID(transaction.getMndtId());

                if (result == null) {
                    System.out.println("inexisted mandat for these transactions");
                    hashMap_transactions.get("RJC000").add(transaction);
                }
            }
            
            hashMap_transactions.get("PERSIST").add(transaction);
        }
        //Else, Do persistance
        //
        //
        return hashMap_transactions;
    }

    /**
     * Verify if checksum is valide
     *
     * @param header
     * @param it
     * @return
     */
    private boolean checkSumChecker(GroupHeader39 header, Iterator<PaymentInstructionInformation4> it) {
        int checkSum_nb_transaction = Integer.valueOf(header.getNbOfTxs());
        int checkSum_ttl_amount = header.getCtrlSum().intValue();
        int i_nb_transaction = 0;
        int i_ttl_amount = 0;
        while (it.hasNext()) {
            PaymentInstructionInformation4 transaction = it.next();
            for (DirectDebitTransactionInformation9 tr : transaction.getDrctDbtTxInf()) {
                i_nb_transaction++;
                i_ttl_amount += tr.getInstdAmt().getValue().intValue();
            }
        }
        if (checkSum_nb_transaction != i_nb_transaction || checkSum_ttl_amount != i_ttl_amount) {
            System.out.println("rejected invalidate checksum.");
            return false;
        }
        return true;
    }

    private boolean checkMsgId(GroupHeader39 header) {

        Transaction foundMsgId = service.findTransactionByMsgId(header.getMsgId().toString());
        if (foundMsgId == null) {
            return true;
        }
        System.out.println("rejected : existed file in database");
        return false;
    }
}
