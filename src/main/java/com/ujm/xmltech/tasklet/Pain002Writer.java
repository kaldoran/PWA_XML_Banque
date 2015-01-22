package com.ujm.xmltech.tasklet;

import com.ujm.xmltech.adapter.JaxbDateAdapter;
import com.ujm.xmltech.entity.Transaction;
import static com.ujm.xmltech.tasklet.Pain008Checker.isValide;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ujm.xmltech.utils.BankSimulationConstants;
import iso.std.iso._20022.tech.xsd.pain_002_001.CustomerPaymentStatusReportV03;
import iso.std.iso._20022.tech.xsd.pain_002_001.GroupHeader36;
import iso.std.iso._20022.tech.xsd.pain_002_001.OriginalGroupInformation20;
import iso.std.iso._20022.tech.xsd.pain_002_001.OriginalPaymentInformation1;
import iso.std.iso._20022.tech.xsd.pain_002_001.PaymentTransactionInformation25;
import iso.std.iso._20022.tech.xsd.pain_002_001.StatusReason6Choice;
import iso.std.iso._20022.tech.xsd.pain_002_001.StatusReasonInformation8;
import iso.std.iso._20022.tech.xsd.pain_002_001.TransactionIndividualStatus3Code;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;

public class Pain002Writer implements Tasklet {
    private static int id_file = 1000;
    @Override
    public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
        if (Pain008Reader.hashMap_transactions != null) {
            for (String test : Pain008Reader.hashMap_transactions.keySet()) {
                if(!(test.equals("PERSIST"))) {
                    if( !(Pain008Reader.hashMap_transactions.get(test).isEmpty()) ) {
                        CustomerPaymentStatusReportV03 cstmrPmtStsRpt = fillInPain002(Pain008Reader.hashMap_transactions.get(test), test);
                        if(cstmrPmtStsRpt != null) {
                            write(cstmrPmtStsRpt);   
                        }
                    }

                }
            }
        }
        
        
        System.out.println("fin writeReport");
        return RepeatStatus.FINISHED;
    }

    public void write(Object item) {
        //Added a random in order to have a different file each time
        File file = new File(BankSimulationConstants.OUT_DIRECTORY + "report" + Math.random() + ".xml");
        OutputStream out;
        try {
            out = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(out);
            JAXBContext ctx = JAXBContext.newInstance(item.getClass());
            Marshaller marshaller = ctx.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty("jaxb.fragment", Boolean.TRUE);
            //writer file header
            String documentBase = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.002.001.03\">\n";
            writer.write(documentBase);
            //write header item
            writer.write(getXMLFragment(item, "CstmrPmtStsRpt", marshaller) + "\n");
            //write footer
            String documentEnd = "\n</Document>";
            writer.write(documentEnd);
            writer.close();
            out.close();    
            
            checkFile(file);
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

    private CustomerPaymentStatusReportV03 fillInPain002(ArrayList<Transaction> transactions, String test) {
        CustomerPaymentStatusReportV03 cstmrPmtStsRpt = null;
        XMLGregorianCalendar xmlGregorianCalendar = null;
        GroupHeader36 grpHdr = null;
        OriginalGroupInformation20 orgnlGrpInfAndSts = null;
        
        OriginalPaymentInformation1 orgnlPmtInfAndSts = null;
        //OrgnlPmtInfId
        PaymentTransactionInformation25 txInfAndSts = null;
        TransactionIndividualStatus3Code txSts = null;
        StatusReasonInformation8 stsRsnInf = null;
        StatusReason6Choice rsn = null;
        JaxbDateAdapter dateAdapter = new JaxbDateAdapter();
        
        try {
            GregorianCalendar calendar = new GregorianCalendar();
            xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateAdapter.marshalv2(calendar.getTime()));
        } catch (DatatypeConfigurationException ex) {
            Logger.getLogger(ReadDataBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ReadDataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        grpHdr = new GroupHeader36();
        grpHdr.setMsgId(generatePain002FileId());
        grpHdr.setCreDtTm(xmlGregorianCalendar);
        
        orgnlGrpInfAndSts = new OriginalGroupInformation20();
        orgnlGrpInfAndSts.setOrgnlMsgId("xxxxx.xxxxxx.xxxx");
        orgnlGrpInfAndSts.setOrgnlMsgNmId("PAIN.008.001.02");
        
        for(Transaction t : transactions) {
            rsn = new StatusReason6Choice();
            rsn.setCd(test);

            stsRsnInf = new StatusReasonInformation8();
            stsRsnInf.setRsn(rsn);
            
            txInfAndSts = new PaymentTransactionInformation25();
            txInfAndSts.getStsRsnInf().add(stsRsnInf);
            txInfAndSts.setOrgnlEndToEndId(t.getEndToEndId());
            txInfAndSts.setTxSts(TransactionIndividualStatus3Code.RJCT);
           
        }
        orgnlPmtInfAndSts = new OriginalPaymentInformation1();
        orgnlPmtInfAndSts.setOrgnlPmtInfId("FRXXXXXZZZZZXXXXX-xxxx");
        orgnlPmtInfAndSts.getTxInfAndSts().add(txInfAndSts);
        
        cstmrPmtStsRpt = new CustomerPaymentStatusReportV03();
        cstmrPmtStsRpt.setGrpHdr(grpHdr);
        cstmrPmtStsRpt.setOrgnlGrpInfAndSts(orgnlGrpInfAndSts);
        cstmrPmtStsRpt.getOrgnlPmtInfAndSts().add(orgnlPmtInfAndSts);
        

        return cstmrPmtStsRpt;
    }
    
    private String generatePain002FileId () {
        
        return "bknk" + id_file++ + (int)(Math.random()*1000);
    }
    
    private boolean checkFile(File fileToValidate) {
        isValide = true;
        try {
            //File fileToValidate = new File(BankSimulationConstants.WORK_DIRECTORY + fileName);
            File xsdFile = new File(BankSimulationConstants.XSD_DIRECTORY + "pain.002.001.03.xsd");
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(xsdFile));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(BankSimulationConstants.OUT_DIRECTORY+"pain00200103Partial.xml")));
        } catch (SAXException e) {
            //e.printStackTrace();
            System.out.println("Pain002 is not valide");
            isValide = false;
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            isValide = false;

            return false;
        }
         return true;
    }
}
