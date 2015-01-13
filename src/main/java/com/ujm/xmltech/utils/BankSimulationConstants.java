package com.ujm.xmltech.utils;

public class BankSimulationConstants {

  /**
   * Directory where are files to process
   */
  public final static String IN_DIRECTORY = "/media/Public/In/";
  /**
   * Directory where are reports
   */
  public final static String OUT_DIRECTORY = "/media/Public/Out/";
  /**
   * Directory where are files under process
   */
  public final static String WORK_DIRECTORY = "/media/Public/Work/";
  /**
   * Directory where are files already processed
   */
  public final static String ARCHIVE_DIRECTORY = "/media/Public/Archive/";
  
  /**
   * Directory where file go when reject
   */
  public final static String REJECT_DIRECTORY = "/media/Public/Reject/";
    
  public final static String XSD_DIRECTORY = "/media/Public/Xsd/";
  
  /**
   * must contain only 4 upper case letters. Real example : BNPP
   */
  public final String MY_BANK_IDENTIFIER = "BKNK";

  /**
   * persistence unit name in the spring configuration
   */
  public static final String PERSISTENCE_UNIT = "bank-unit";
  /**
   * name of the transaction manager
   */
  public static final String TRANSACTION_MANAGER = "bankTransactionManager";

}
