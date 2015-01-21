/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ujm.xmltech.tasklet;

import static com.ujm.xmltech.tasklet.ReadDataBase.hashMap_cstmrDrctDbtInitn;
import com.ujm.xmltech.utils.BankSimulationConstants;
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

/**
 *
 * @author kevin
 */
public class Pain008Writer implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution sc, ChunkContext cc) throws Exception {
        for (String key : ReadDataBase.hashMap_cstmrDrctDbtInitn.keySet()) {
            if(hashMap_cstmrDrctDbtInitn != null) {
                Object o = hashMap_cstmrDrctDbtInitn.get(key);
                if(o != null) {
                    write(o, key);
                }
                
            }
        }
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
}
