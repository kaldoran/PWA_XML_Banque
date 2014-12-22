package com.ujm.xmltech;

import com.ujm.xmltech.utils.BankSimulationConstants;
import java.io.File;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {

  //TP scheduler
public void launch() {
    File input = retrieveFileToProcess();
    if (input != null) {
      String[] springConfig = { "spring/batch/jobs/jobs.xml" };
      ApplicationContext context = new ClassPathXmlApplicationContext(springConfig);
      JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
      Job job = (Job) context.getBean("integratePain008File");
      try {
        JobExecution execution = jobLauncher.run(job, new JobParametersBuilder().addString("inputFile",input.getName()).toJobParameters());
        System.out.println("Exit Status : " + execution.getStatus());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

private File retrieveFileToProcess() {
        System.out.println("Recherche de fichier.");
        File repIn = new File(BankSimulationConstants.IN_DIRECTORY);
        File[] requetes = repIn.listFiles();

        if (requetes.length > 0) {
            System.out.println("Traitement du fichier :" + requetes[0]);
            return requetes[0];
        }

        return null;
  }

/*
public void main() { 
    JAXB Pain008Reader reader = new Pain008Reader(); 
    Pain008Writer writer = new Pain008Writer(); 
    Pain008Checker checker = new Pain008Checker(); 
    try { 
        checker.checkFile(); 
        Object item = reader.read(); 
        writer.write(item); 
    } catch (Exception e) { e.printStackTrace(); }
    service.createTransaction(); 
    new App().run(); 
} 

private void run() { 
   TimerTask task = new MyScheduler();
   Timer timer = new Timer();
   //launch the job after 1 second the first time and then every 10 second 
    timer.schedule(task, 1000, 10000); 
}

public class MyScheduler extends TimerTask {
    @Override public void run() { 
        String[] springConfig = { "spring/batch/jobs/jobs.xml" }; 
        ApplicationContext context = new ClassPathXmlApplicationContext(springConfig); 
        JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher"); 
        Job job = (Job) context.getBean("integratePain008File"); 
        //launch job 
        try { 
            JobExecution execution = jobLauncher.run(job, new JobParameters());
            System.out.println("Exit Status : " + execution.getStatus()); 
        } catch (Exception e) {
            e.printStackTrace(); 
        } System.out.println("Done");
    }
}*/
  
}
