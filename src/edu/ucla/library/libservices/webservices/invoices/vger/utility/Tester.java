package edu.ucla.library.libservices.webservices.invoices.vger.utility;

import edu.ucla.library.libservices.invoicing.webservices.invoices.beans.InsertHeaderBean;
import edu.ucla.library.libservices.invoicing.webservices.invoices.beans.LineItemBean;
import edu.ucla.library.libservices.invoicing.webservices.invoices.beans.LineItemNote;
import edu.ucla.library.libservices.webservices.invoices.vger.beans.PatronBill;

import edu.ucla.library.libservices.webservices.invoices.vger.client.HeaderClient;
import edu.ucla.library.libservices.webservices.invoices.vger.client.LineItemClient;
import edu.ucla.library.libservices.webservices.invoices.vger.client.LineNoteClient;
import edu.ucla.library.libservices.webservices.invoices.vger.client.StatusClient;
import edu.ucla.library.libservices.webservices.invoices.vger.generators.PatronBillGenerator;

import java.util.Date;
import java.util.List;

import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import edu.ucla.library.libservices.invoicing.utiltiy.testing.ContentTests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.text.SimpleDateFormat;

public class Tester
{
  private static final int REPLACEMENT = 2;
  //private static final int PROCESSING = 3;

  private static Map<Integer, Vector<PatronBill>> billsByPatron;
  private static Vector<PatronBill> lawBills;
  private static Vector<PatronBill> otherLibBills;
  private static Vector<PatronBill> lawReplacementBills;
  private static Vector<PatronBill> otherLibReplacementBills;
  private static Vector<PatronBill> lawProcessingBills;
  private static Vector<PatronBill> otherLibProcessingBills;
  private static List<PatronBill> allBills;
  private static BufferedWriter writer;
  private static final SimpleDateFormat SIMPLE =
    new SimpleDateFormat( "MM/dd/yy" );

  public Tester()
  {
    super();
  }

  public static void main( String[] args )
  {
    prepWriter();
    getAllBills();
    groupBillsByPatron();
    for ( Integer patronID: billsByPatron.keySet() )
    {
      groupBillsByLib( patronID );
      groupLawBillsByType();
      groupOtherLibBillsByType();
      //reportBills();
      if ( lawReplacementBills.size() > 0 )
      {
        generateInvoice( patronID, lawReplacementBills, "LW" );
        writeInvoice( lawReplacementBills );
      }
      if ( otherLibReplacementBills.size() > 0 )
      {
        generateInvoice( patronID, otherLibReplacementBills, "CS" );
        writeInvoice( otherLibReplacementBills );
      }
      if ( lawProcessingBills.size() > 0 )
      {
        generateInvoice( patronID, lawProcessingBills, "LW" );
        writeInvoice( lawProcessingBills );
      }
      if ( otherLibProcessingBills.size() > 0 )
      {
        generateInvoice( patronID, otherLibProcessingBills, "CS" );
        writeInvoice( otherLibProcessingBills );
      }
    }
    finishWriter();
  }

  private static void getAllBills()
  {
    PatronBillGenerator generator;

    generator = new PatronBillGenerator();
    generator.setDbName( "" );

    allBills = generator.getPatrons();
  }

  private static void groupBillsByPatron()
  {
    billsByPatron = new TreeMap<Integer, Vector<PatronBill>>();
    for ( PatronBill thePatron: allBills )
    {
      if ( billsByPatron.containsKey( thePatron.getPatronID() ) )
        billsByPatron.get( thePatron.getPatronID() ).add( thePatron );
      else
      {
        Vector<PatronBill> temp;
        temp = new Vector<PatronBill>();
        temp.add( thePatron );
        billsByPatron.put( thePatron.getPatronID(), temp );
      }
    }
  }

  private static void groupBillsByLib( Integer patronID )
  {
    lawBills = new Vector<PatronBill>();
    otherLibBills = new Vector<PatronBill>();
    for ( PatronBill theBill: billsByPatron.get( patronID ) )
    {
      if ( theBill.getLocationCode().startsWith( "lw" ) )
        lawBills.add( theBill );
      else
        otherLibBills.add( theBill );
    }
  }

  private static void groupLawBillsByType()
  {
    lawReplacementBills = new Vector<PatronBill>();
    lawProcessingBills = new Vector<PatronBill>();
    for ( PatronBill theBill: lawBills )
    {
      System.out.println( "Law: fee/fine type = " +
                          theBill.getFineFeeType() );
      if ( theBill.getFineFeeType() == REPLACEMENT )
      {
        System.out.println( "\tadding to replacements" );
        lawReplacementBills.add( theBill );
      }
      else
      {
        System.out.println( "\tadding to processing" );
        lawProcessingBills.add( theBill );
      }
    }
  }

  private static void groupOtherLibBillsByType()
  {
    otherLibReplacementBills = new Vector<PatronBill>();
    otherLibProcessingBills = new Vector<PatronBill>();
    for ( PatronBill theBill: otherLibBills )
    {
      System.out.println( "Not Law: fee/fine type = " +
                          theBill.getFineFeeType() );
      if ( theBill.getFineFeeType() == REPLACEMENT )
      {
        System.out.println( "\tadding to replacements" );
        otherLibReplacementBills.add( theBill );
      }
      else
      {
        System.out.println( "\tadding to processing" );
        otherLibProcessingBills.add( theBill );
      }
    }
  }

  private static void reportBills()
  {
    for ( PatronBill lawReplace: lawReplacementBills )
      System.out.println( "law replacement: " + lawReplace );
    for ( PatronBill otherLibReplace: otherLibReplacementBills )
      System.out.println( "other unit replacement: " + otherLibReplace );
    for ( PatronBill lawProcessing: lawProcessingBills )
      System.out.println( "law processing: " + lawProcessing );
    for ( PatronBill otherLibProcessing: otherLibProcessingBills )
      System.out.println( "other unit processing: " + otherLibProcessing );
  }

  private static void generateInvoice( int patronID,
                                       Vector<PatronBill> bills,
                                       String unit )
  {
    String invoiceNo;
    int lineNumber = 0;

    invoiceNo = createHeader( //bills,
          patronID, unit );
    if ( invoiceNo != null && invoiceNo.length() > 0 )
    {
      System.out.println( "new invoice number: " + invoiceNo );
      for ( PatronBill theBill: bills )
      {
        lineNumber = addLineItem( theBill, invoiceNo, lineNumber );
        theBill.setTransNote( constructNote( invoiceNo ) );
      }
      System.out.println( "line count = " + lineNumber );
      setStatus( invoiceNo );
    }
    else
    {
      /*mailError( " Problem creating invoice header", "",
                 concatReqIDs( files.toArray() ) );*/
    }
  }

  private static int addLineItem( PatronBill bill, String invoiceNo,
                                  int lineNumber )
  {
    LineItemClient theClient;
    LineItemBean bean;

    theClient = new LineItemClient();
    theClient.setProps( null );

    bean = new LineItemBean();
    if ( bill.getFineFeeType() == 2 ) //custom
    {
      bean.setBranchServiceID( 254 );
      bean.setUnitPrice( bill.getFineFeeBalance() );
    }
    else //fixed
    {
      bean.setBranchServiceID( 255 );
    }
    bean.setCreatedBy( "vger_user" );
    bean.setCreatedDate( new Date() );
    bean.setInvoiceNumber( invoiceNo );
    bean.setQuantity( 1 );

    theClient.setTheLine( bean );
    theClient.insertLine();
    lineNumber += 1;
    addLineNote( invoiceNo, lineNumber, bill );

    return lineNumber;
  }

  private static String createHeader( int patronID, String unit )
  {
    String invoice;
    InsertHeaderBean insertHeader;
    HeaderClient theClient;

    invoice = "";

    insertHeader = new InsertHeaderBean();
    insertHeader.setBranchCode( unit );
    insertHeader.setCreatedBy( "vger_user" );
    insertHeader.setInvoiceDate( new Date() );
    insertHeader.setOnPremises( "N" );
    insertHeader.setPatronID( patronID );
    insertHeader.setStatus( "Pending" );

    theClient = new HeaderClient();
    theClient.setProps( null );
    theClient.setTheHeader( insertHeader );

    invoice = theClient.insertHeader();

    System.out.println( "invoice # = " + invoice );
    return invoice;
  }

  private static void addLineNote( String invoiceNo, int lineNumber,
                                   PatronBill bill )
  {
    LineItemNote theNote;
    LineNoteClient theClient;
    StringBuffer note;

    note = new StringBuffer( "Title: " );
    note.append( !ContentTests.isEmpty( bill.getTitle() ) ?
                 bill.getTitle().trim(): "N/A" );
    note.append( "; \tAuthor: " ).append( !ContentTests.isEmpty( bill.getAuthor() ) ?
                                          bill.getAuthor().trim(): "N/A" );
    note.append( "; \tBarcode: " ).append( !ContentTests.isEmpty( bill.getItemBarcode() ) ?
                                           bill.getItemBarcode().trim():
                                           "N/A" );
    note.append( "; \tCall number: " ).append( !ContentTests.isEmpty( bill.getNormalizedCallNo() ) ?
                                               bill.getNormalizedCallNo().trim():
                                               "N/A" );

    theNote = new LineItemNote();
    theNote.setCreatedBy( "vger_user" );
    theNote.setCreatedDate( new Date() );
    theNote.setInternal( false );
    theNote.setInvoiceNumber( invoiceNo );
    theNote.setLineNumber( lineNumber );
    theNote.setNote( note.toString() );

    theClient = new LineNoteClient();
    theClient.setProps( null );
    theClient.setTheNote( theNote );
    theClient.insertNote();
  }

  private static void setStatus( String invoiceNo )
  {
    StatusClient theClient;

    theClient = new StatusClient();
    theClient.setInvoiceNumber( invoiceNo );
    theClient.setProps( null );
    theClient.setWhoBy( "vger_user" );

    theClient.updateStatus();

  }

  private static void prepWriter()
  {
    try
    {
      writer =
          new BufferedWriter( new FileWriter( new File( "C:\\Temp\\libbill\\post_libbill_trans" ) ) );
    }
    catch ( IOException ioe )
    {
      ioe.printStackTrace();
      System.exit( 1 );
    }
  }

  private static void writeInvoice( Vector<PatronBill> bills )
  {
    for ( PatronBill theBill: bills )
    {
      try
      {
        writer.write( theBill.getPatronID() + "\t" );
        writer.write( theBill.getFineFeeBalance() + "\t" );
        writer.write( "7\t0\t" );
        writer.write( theBill.getFineFeeID() + "\t" );
        writer.write( theBill.getTransNote() );
        writer.newLine();
      }
      catch ( IOException ioe )
      {
        ioe.printStackTrace();
      }
    }
  }

  private static String constructNote( String invoiceNo )
  {
    StringBuffer buffer;

    buffer = new StringBuffer( SIMPLE.format( new Date() ) );
    buffer.append( " " ).append( "Invoiced in LibBill           " );
    buffer.append( invoiceNo );

    return buffer.toString();
  }

  private static void finishWriter()
  {
    try
    {
      writer.flush();
      writer.close();
    }
    catch ( IOException ioe )
    {
      ioe.printStackTrace();
    }
  }
}
