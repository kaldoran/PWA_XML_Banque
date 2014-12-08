package com.ujm.xmltech.tasklet;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.xml.sax.SAXException;

import com.ujm.xmltech.utils.BankSimulationConstants;

public class Pain008Checker implements Tasklet {

  public boolean checkFile(String fileName) {
    try {
      File fileToValidate = new File(BankSimulationConstants.WORK_DIRECTORY + fileName);
      File xsdFile = new File(BankSimulationConstants.XSD_DIRECTORY + "pain.008.001.02.xsd");
      SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema schema = factory.newSchema(new StreamSource(xsdFile));
      Validator validator = schema.newValidator();
      validator.validate(new StreamSource(fileToValidate));
    } catch (SAXException e) {
      e.printStackTrace();
      return false;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    
    return true;
  }

  @Override
  public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
    if(checkFile((String) arg1.getStepContext().getJobParameters().get("inputFile"))) {
        System.out.println("file checked : true in ["   + BankSimulationConstants.ARCHIVE_DIRECTORY 
                                                        + (String) arg1.getStepContext().getJobParameters().get("inputFile")+ "]" );
    } else {
    System.out.println("file checked : false ! in ["    + BankSimulationConstants.ARCHIVE_DIRECTORY 
                                                        + (String) arg1.getStepContext().getJobParameters().get("inputFile")+ "]" );
    }
    return RepeatStatus.FINISHED;
  }

}
