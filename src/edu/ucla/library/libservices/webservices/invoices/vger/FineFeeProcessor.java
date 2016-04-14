package edu.ucla.library.libservices.webservices.invoices.vger.main;

import edu.ucla.library.libservices.invoicing.utiltiy.testing.ContentTests;
import edu.ucla.library.libservices.invoicing.webservices.invoices.beans.InsertHeaderBean;
import edu.ucla.library.libservices.invoicing.webservices.invoices.beans.LineItemBean;
import edu.ucla.library.libservices.invoicing.webservices.invoices.beans.LineItemNote;
import edu.ucla.library.libservices.webservices.invoices.vger.beans.PatronBill;
import edu.ucla.library.libservices.webservices.invoices.vger.client.HeaderClient;
import edu.ucla.library.libservices.webservices.invoices.vger.client.LineItemClient;
import edu.ucla.library.libservices.webservices.invoices.vger.client.LineNoteClient;
import edu.ucla.library.libservices.webservices.invoices.vger.client.StatusClient;
import edu.ucla.library.libservices.webservices.invoices.vger.generators.LineTypeGenerator;
import edu.ucla.library.libservices.webservices.invoices.vger.generators.PatronBillGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.log4j.Logger;

public class FineFeeProcessor
{
  private static final SimpleDateFormat SIMPLE =
    new SimpleDateFormat( "MM/dd/yy" );
  private static final String GENERAL_REPLACE =
    "Replacement Fee - General Library";
  private static final String LAW_REPLACE = "Replacement Fee - Law";
  private static final String PROCESSING = "Processing Fee";
  private static final String OVERDUE = "Overdue Fine";
  private static final int VGER_OVERDUE = 1;
  private static final int VGER_REPLACE = 2;
  private static final int VGER_PROCESS = 3;
  private static final Logger logger =
    Logger.getLogger( FineFeeProcessor.class );

  private static Map<Integer, Vector<PatronBill>> billsByPatron;
  private static Vector<PatronBill> lawBills;
  private static Vector<PatronBill> generalLibBills;
  private static List<PatronBill> allBills;
  private static BufferedWriter writer;

  public FineFeeProcessor()
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
      if ( lawBills.size() > 0 )
      {
        generateInvoice( patronID, lawBills, "CS" );
        writeInvoice( lawBills );
      }
      if ( generalLibBills.size() > 0 )
      {
        generateInvoice( patronID, generalLibBills, "CS" );
        writeInvoice( generalLibBills );
      }
    }
    finishWriter();
  }

  private static void getAllBills()
  {
    PatronBillGenerator generator;

    generator = new PatronBillGenerator();
    //generator.setDbName( "" );

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
    generalLibBills = new Vector<PatronBill>();
    for ( PatronBill theBill: billsByPatron.get( patronID ) )
    {
      if ( theBill.getLocationCode().startsWith( "lw" ) )
        lawBills.add( theBill );
      else
        generalLibBills.add( theBill );
    }
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
        theBill.setTransNote( constructNote( invoiceNo, lineNumber ) );
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

    switch ( bill.getFineFeeType() )
    {
      case VGER_OVERDUE:
        bean.setBranchServiceID( getLineType( OVERDUE ) );
        break;
      case VGER_REPLACE:
        if ( bill.getLocationCode().startsWith( "lw" ) )
        {
          bean.setBranchServiceID( getLineType( LAW_REPLACE ) );
        }
        else
        {
          bean.setBranchServiceID( getLineType( GENERAL_REPLACE ) );
        }
        break;
      case VGER_PROCESS:
        bean.setBranchServiceID( getLineType( PROCESSING ) );
        break;
      default:
        logger.info( "no match made for charge type " + bill.getFineFeeType() );
    }
    if ( bill.getFineFeeType() == 2 ) //custom
    {
      bean.setUnitPrice( bill.getFineFeeBalance() );
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
        writer.write( "11\t0\t" );
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

  private static String constructNote( String invoiceNo, int lineNo )
  {
    StringBuffer buffer;

    buffer = new StringBuffer( SIMPLE.format( new Date() ) );
    buffer.append( " " ).append( "Invoiced in LibBill           " );
    buffer.append( invoiceNo );
    buffer.append( " " ).append( "Line " );
    buffer.append( lineNo );

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

  private static int getLineType( String chargeType )
  {
    LineTypeGenerator generator;
    
    generator = new LineTypeGenerator();
    generator.setServiceName( chargeType );
    return generator.getLineType();
  }
}
