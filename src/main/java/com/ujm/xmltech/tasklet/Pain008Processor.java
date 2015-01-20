package com.ujm.xmltech.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class Pain008Processor implements Tasklet {

  /*@Autowired
  private TransactionService service;*/

  @Override
  public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
    /*Iterator<PaymentInstructionInformation4> it = 
        (Iterator<PaymentInstructionInformation4>) arg1.getStepContext().getStepExecution()
                                                                        .getJobExecution()
                                                                        .getExecutionContext()
                                                                        .get("it");
    while(it.hasNext()) {
        System.out.println(it.next().getCdtr().getNm());
    }*/
    //service.createTransaction();
    System.out.println("transaction created");
    return RepeatStatus.FINISHED;
  }

}
