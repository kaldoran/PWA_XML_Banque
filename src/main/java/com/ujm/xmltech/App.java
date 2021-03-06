package com.ujm.xmltech;

import com.ujm.xmltech.utils.BankSimulationConstants;
import java.io.File;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {

  //TP scheduler
    public void launch_IntegratePain008File() {
        File input = retrieveFileToProcess();
        if (input != null) {
            String[] springConfig = {"spring/batch/jobs/jobs.xml"};
            ApplicationContext context = new ClassPathXmlApplicationContext(springConfig);
            JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
            Job job = (Job) context.getBean("integratePain008File");
            try {
                JobExecution execution = jobLauncher.run(job, new JobParametersBuilder().addString("inputFile", input.getName()).toJobParameters());
                System.out.println("Exit Status : " + execution.getStatus());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (context != null) {
                    ((AbstractApplicationContext) context).close();
                }
            }
        }
    }

    public void launch_WritePain008File() {
            String[] springConfig = {"spring/batch/jobs/jobs.xml"};
            ApplicationContext context = new ClassPathXmlApplicationContext(springConfig);
            JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
            Job job = (Job) context.getBean("WritePain008File");
            try {
                JobExecution execution = jobLauncher.run(job, new JobParametersBuilder().toJobParameters());
                System.out.println("Exit Status : " + execution.getStatus());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (context != null) {
                    ((AbstractApplicationContext) context).close();
                }
            }
    }

    private File retrieveFileToProcess() {
        System.out.println("Recherche de fichier.");
        File repIn = new File(BankSimulationConstants.IN_DIRECTORY);
        File[] requetes = repIn.listFiles();

        for (int i = 0; i < requetes.length; ++i) {
            if (requetes[i].getName().endsWith(".xml")) {
                System.out.println("Traitement du fichier :" + requetes[i]);
                return requetes[i];
            } else {
                System.out.println("Reject File !");
                requetes[i].renameTo(new File(BankSimulationConstants.REJECT_DIRECTORY + requetes[i].getName()));
            }
        }

        return null;
    }

}
