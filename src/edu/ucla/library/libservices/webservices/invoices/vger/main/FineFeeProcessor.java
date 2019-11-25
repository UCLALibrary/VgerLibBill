package edu.ucla.library.libservices.webservices.invoices.vger.main;

import edu.ucla.library.libservices.invoicing.utiltiy.testing.ContentTests;
import edu.ucla.library.libservices.invoicing.webservices.invoices.beans.InsertHeaderBean;
import edu.ucla.library.libservices.invoicing.webservices.invoices.beans.LineItemBean;
import edu.ucla.library.libservices.invoicing.webservices.invoices.beans.LineItemNote;
import edu.ucla.library.libservices.webservices.invoices.vger.beans.PatronBill;
import edu.ucla.library.libservices.webservices.invoices.vger.client.HeaderClient;
import edu.ucla.library.libservices.webservices.invoices.vger.client.LineItemClient;
import edu.ucla.library.libservices.webservices.invoices.vger.client.LineNoteClient;
import edu.ucla.library.libservices.webservices.invoices.vger.client.PdfClient;
import edu.ucla.library.libservices.webservices.invoices.vger.client.StatusClient;
import edu.ucla.library.libservices.webservices.invoices.vger.generators.LineTypeGenerator;
import edu.ucla.library.libservices.webservices.invoices.vger.generators.PatronBillGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.log4j.Logger;

public class FineFeeProcessor
{
  private static final SimpleDateFormat SIMPLE = new SimpleDateFormat( "MM/dd/yy" );
  private static final String PERMALINK = "note.permalink";
  private static final String REPLACEMENT = "type.replace.general";
  private static final String LAW_REPLACE = "type.replace.law";
  private static final String PROCESSING = "type.process.general";
  private static final String OVERDUE = "type.overdue.general";
  private static final String GENERAL_LIB = "locale.general";
  //private static final String LAW_LIB = "locale.law";
  private static final String BILL_FILE = "file.bill";
  private static final int VGER_OVERDUE = 1;
  private static final int VGER_REPLACE = 2;
  private static final int VGER_PROCESS = 3;
  private static final Logger logger = Logger.getLogger( FineFeeProcessor.class );

  private static Map<Integer, Vector<PatronBill>> billsByPatron;
  private static Vector<PatronBill> lawBills;
  private static Vector<PatronBill> generalLibBills;
  private static List<PatronBill> allBills;
  private static BufferedWriter writer;
  private static Properties props;

  public FineFeeProcessor()
  {
    super();
  }

  public static void main( String[] args )
  {
    loadProperties( args[ 0 ] );

    prepWriter();
    getAllBills();
    groupBillsByPatron();
    for ( Integer patronID : billsByPatron.keySet() )
    {
      groupBillsByLib( patronID );
      if ( lawBills.size() > 0 )
      {
        //generateInvoice( patronID, lawBills, "LW" );
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
    generator.setProps( props );
    //generator.setDbName( "" );

    allBills = generator.getPatrons();
    for ( PatronBill theBill : allBills )
      System.out.println( theBill.toString() );
  }

  private static void groupBillsByPatron()
  {
    billsByPatron = new TreeMap<Integer, Vector<PatronBill>>();
    for ( PatronBill thePatron : allBills )
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
    for ( PatronBill theBill : billsByPatron.get( patronID ) )
    {
      if ( theBill.getLocationCode().startsWith( "lw" ) )
        lawBills.add( theBill );
      else
        generalLibBills.add( theBill );
    }
  }

  private static void generateInvoice( int patronID, Vector<PatronBill> bills, String unit )
  {
    String invoiceNo;
    int lineNumber;

    lineNumber = 0;
    invoiceNo = createHeader( patronID, unit );

    if ( invoiceNo != null && invoiceNo.length() > 0 )
    {
      logger.info( "new invoice number: " + invoiceNo );
      System.out.println( "new invoice number: " + invoiceNo );
      for ( PatronBill theBill : bills )
      {
        lineNumber = addLineItem( theBill, invoiceNo, lineNumber );
        theBill.setTransNote( constructNote( invoiceNo, lineNumber ) );
      }
      logger.info( "line count = " + lineNumber );
      setStatus( invoiceNo );
      mailInvoice( invoiceNo );
    }
    else
    {
      /*mailError( " Problem creating invoice header", "",
                 concatReqIDs( files.toArray() ) );*/
    }
  }
  private static void mailInvoice(String invoiceNo)
  {
    PdfClient client;
    client = new PdfClient();
    client.setInvoiceNo( invoiceNo );
    client.mailPdf();
  }

  private static int addLineItem( PatronBill bill, String invoiceNo, int lineNumber )
  {
    LineItemClient theClient;
    LineItemBean bean;

    theClient = new LineItemClient();
    theClient.setProps( null );

    bean = new LineItemBean();

    System.out.println( "\tworking with fine/fee record " + bill.getFineFeeID() );
    System.out.println( "\tfine fee type = " + bill.getFineFeeType() );
    switch ( bill.getFineFeeType() )
    {
      case VGER_OVERDUE:
        /*if ( bill.getLocationCode().startsWith( "lw" ) )
        {
          bean.setBranchServiceID( getLineType( props.getProperty( OVERDUE ), props.getProperty( LAW_LIB ) ) );
        }
        else
        {
          bean.setBranchServiceID( getLineType( props.getProperty( OVERDUE ), props.getProperty( GENERAL_LIB ) ) );
        }*/
        bean.setBranchServiceID( getLineType( props.getProperty( OVERDUE ), props.getProperty( GENERAL_LIB ) ) );
        break;
      case VGER_REPLACE:
        if ( bill.getLocationCode().startsWith( "lw" ) )
        {
          //bean.setBranchServiceID( getLineType( props.getProperty( LAW_REPLACE ), props.getProperty( LAW_LIB ) ) );
          bean.setBranchServiceID( getLineType( props.getProperty( LAW_REPLACE ), props.getProperty( GENERAL_LIB ) ) );
        }
        else
        {
          bean.setBranchServiceID( getLineType( props.getProperty( REPLACEMENT ), props.getProperty( GENERAL_LIB ) ) );
        }
        break;
      case VGER_PROCESS:
        /*if ( bill.getLocationCode().startsWith( "lw" ) )
        {
          bean.setBranchServiceID( getLineType( props.getProperty( PROCESSING ), props.getProperty( LAW_LIB ) ) );
        }
        else
        {
          bean.setBranchServiceID( getLineType( props.getProperty( PROCESSING ), props.getProperty( GENERAL_LIB ) ) );
        }*/
        bean.setBranchServiceID( getLineType( props.getProperty( PROCESSING ), props.getProperty( GENERAL_LIB ) ) );
        break;
      default:
        logger.info( "no match made for charge type " + bill.getFineFeeType() );
    }
    System.out.println( "\tsetting line type to " + bean.getBranchServiceID() );
    if ( ( bill.getFineFeeType() == VGER_OVERDUE ) ||
       ( bill.getFineFeeType() == VGER_REPLACE ) ) //custom price fines
    {
      System.out.println( "\t\tsetting line amount to " + bill.getFineFeeBalance() );
      bean.setUnitPrice( bill.getFineFeeBalance() / 100D );
    }
    bean.setCreatedBy( "vger_user" );
    bean.setCreatedDate( new Date() );
    bean.setInvoiceNumber( invoiceNo );
    bean.setQuantity( 1 );

    theClient.setTheLine( bean );
    theClient.setProps( props );
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
    theClient.setProps( props );
    theClient.setTheHeader( insertHeader );

    invoice = theClient.insertHeader();

    //System.out.println( "invoice # = " + invoice + " for patron " + patronID );
    return invoice;
  }

  private static void addLineNote( String invoiceNo, int lineNumber, PatronBill bill )
  {
    LineItemNote theNote;
    LineNoteClient theClient;
    Vector<String> notes;

    notes = new Vector<String>( 4 );
    notes.add( makeNote( "Title: ", bill.getTitle() ) );
    notes.add( makeNote( "Author: ", bill.getAuthor() ) );
    notes.add( makeNote( "Barcode: ", bill.getItemBarcode() ) );
    notes.add( makeNote( "Call number: ", bill.getNormalizedCallNo() ) );
    notes.add( makeNote( "Permalink: ", makeLink( bill.getBibID() ) ) );

    for ( String aNote : notes )
    {
      theNote = new LineItemNote();
      theNote.setCreatedBy( "vger_user" );
      theNote.setCreatedDate( new Date() );
      theNote.setInternal( false );
      theNote.setInvoiceNumber( invoiceNo );
      theNote.setLineNumber( lineNumber );
      theNote.setNote( aNote );

      theClient = new LineNoteClient();
      theClient.setProps( props );
      theClient.setTheNote( theNote );
      theClient.insertNote();
    }
  }

  private static void setStatus( String invoiceNo )
  {
    StatusClient theClient;

    theClient = new StatusClient();
    theClient.setInvoiceNumber( invoiceNo );
    theClient.setProps( props );
    theClient.setWhoBy( "vger_user" );

    theClient.updateStatus();
  }

  private static void prepWriter()
  {
    try
    {
      writer = new BufferedWriter( new FileWriter( new File( props.getProperty( BILL_FILE ) ) ) );
    }
    catch ( IOException ioe )
    {
      ioe.printStackTrace();
      System.exit( 1 );
    }
  }

  private static void writeInvoice( Vector<PatronBill> bills )
  {
    for ( PatronBill theBill : bills )
    {
      try
      {
        writer.write( theBill.getPatronID() + "\t" );
        writer.write( theBill.getFineFeeBalance() / 100 + "\t" );
        writer.write( "5\t0\t" );
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

  private static int getLineType( String chargeType, String locale )
  {
    //System.out.print( "in getLineType with type " + chargeType + "  and locale " + locale );

    LineTypeGenerator generator;

    generator = new LineTypeGenerator();
    generator.setServiceName( chargeType );
    generator.setLocation( locale );
    generator.setProps( props );
    return generator.getLineType();
  }

  private static String makeNote( String header, String value )
  {
    StringBuffer note;
    note = new StringBuffer( header );
    note.append( !ContentTests.isEmpty( value ) ? value.trim() : "N/A" );
    return note.toString();
  }

  private static String makeLink( int bibID )
  {
    return props.getProperty( PERMALINK ).concat( String.valueOf( bibID ) );
  }

  private static void loadProperties( String propFile )
  {
    props = new Properties();
    try
    {
      props.load( new FileInputStream( new File( propFile ) ) );
    }
    catch ( IOException ioe )
    {
      logger.fatal( "problem with props file" + ioe.getMessage() );
      System.exit( -1 );
    }
  }
}
