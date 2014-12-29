package com.ujm.xmltech.utils;

public class BankSimulationConstants {

   /**
   * Directory where are files to process
   */
  public final static String IN_DIRECTORY = "/home/bascool/Documents/XML/FichierTPXML/in/";
  /**
   * Directory where are reports
   */
  public final static String OUT_DIRECTORY = "/home/bascool/Documents/XML/FichierTPXML/out/";
  /**
   * Directory where are files under process
   */
  public final static String WORK_DIRECTORY = "/home/bascool/Documents/XML/FichierTPXML/work/";
  /**
   * Directory where are files already processed
   */
  public final static String ARCHIVE_DIRECTORY = "/home/bascool/Documents/XML/FichierTPXML/archive/";
  
  /**
   * Directory where file go when reject
   */
  public final static String REJECT_DIRECTORY = "/home/bascool/Documents/XML/FichierTPXML/reject/";
    
  public final static String XSD_DIRECTORY = "/home/bascool/Documents/PWA_XML_Banque/src/main/resources/xsd/";
  
  

  /**
   * must contain only 4 upper case letters. Real example : BNPP
   */
  public final String MY_BANK_IDENTIFIER = "";

  /**
   * persistence unit name in the spring configuration
   */
  public static final String PERSISTENCE_UNIT = "bank-unit";
  /**
   * name of the transaction manager
   */
  public static final String TRANSACTION_MANAGER = "bankTransactionManager";

}
