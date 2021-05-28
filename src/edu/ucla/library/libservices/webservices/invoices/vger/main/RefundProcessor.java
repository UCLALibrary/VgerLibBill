package edu.ucla.library.libservices.webservices.invoices.vger.main;

import edu.ucla.library.libservices.invoicing.webservices.adjustments.beans.LineItemAdjustment;
import edu.ucla.library.libservices.webservices.invoices.vger.beans.PatronRefund;
import edu.ucla.library.libservices.webservices.invoices.vger.client.RefundClient;
import edu.ucla.library.libservices.webservices.invoices.vger.generators.PatronRefundGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

public class RefundProcessor
{
  private static final String REFUND_FILE = "file.refund";
  private static List<PatronRefund> refunds;
  private static final SimpleDateFormat SIMPLE = new SimpleDateFormat( "MM/dd/yy" );
  private static final Logger logger = Logger.getLogger( FineFeeProcessor.class );

  private static Properties props;

  public RefundProcessor()
  {
    super();
  }

  public static void main( String[] args )
  {
    loadProperties( args[ 0 ] );

    getRefunds();
    if ( refunds.size() > 0 )
    {
      addRefunds();
      updateNotes();
    }
  }

  private static void getRefunds()
  {
    PatronRefundGenerator generator;

    generator = new PatronRefundGenerator();
    generator.setProps( props );

    refunds = generator.getPatrons();
  }

  private static void addRefunds()
  {
    for ( PatronRefund theRefund : refunds )
    {
      logger.info( "working with refund on " + theRefund.getInvoiceNumber() + " on line " +
                          theRefund.getLineNumber() );
      RefundClient theClient;
      LineItemAdjustment bean;

      theClient = new RefundClient();
      bean = new LineItemAdjustment();
      bean.setAdjustmentReason( theRefund.getReason() );
      bean.setAdjustmentType( "DISCOUNT" );
      bean.setAmount( ( theRefund.getAmount() / 100D ) * -1D );
      bean.setCreatedBy( "vger_user" );
      bean.setCreatedDate( new Date() );
      bean.setInvoiceNumber( theRefund.getInvoiceNumber().trim() );
      bean.setLineNumber( theRefund.getLineNumber() );
      theClient.setTheRefund( bean );
      theClient.setProps( getProps() );
      try
      {
        theClient.insertRefund();
      }
      catch ( Exception e )
      {
        System.err.println("problem with " + theRefund.getInvoiceNumber() + " on line " +
                          theRefund.getLineNumber() + " : " + e.getMessage());
        logger.error("problem with " + theRefund.getInvoiceNumber() + " on line " +
                          theRefund.getLineNumber() + " : " + e.getMessage());
      }
    }
  }

  private static void updateNotes()
  {
    BufferedWriter writer;
    try
    {
      writer = new BufferedWriter( new FileWriter( new File( props.getProperty( REFUND_FILE ) ) ) );
      for ( PatronRefund theRefund : refunds )
      {
        StringBuffer buffer;
        buffer = new StringBuffer( "UPDATE ucladb.fine_fee_transactions SET trans_note = '" );
        buffer.append( SIMPLE.format( new Date() ) ).append( " Credited in LibBill           " );
        buffer.append( theRefund.getInvoiceNumber() ).append( " Line " );
        buffer.append( theRefund.getLineNumber() ).append( "' WHERE fine_fee_trans_id = " );
        buffer.append( theRefund.getVgerTransID() ).append( ";" );
        //System.out.println( "add query to file: " + buffer );
        writer.write( buffer.toString() );
        writer.newLine();
      }
      writer.flush();
      writer.close();
    }
    catch ( IOException ioe )
    {
      ioe.printStackTrace();
    }
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

  public static void setProps( Properties props )
  {
    RefundProcessor.props = props;
  }

  private static Properties getProps()
  {
    return props;
  }
}
/*
 * generate list of refunds
 * for zach refund do:
 *  insert refund in libbill
 *  update refund note in voyager
 * done
 */