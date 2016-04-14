package edu.ucla.library.libservices.webservices.invoices.vger.main;

import edu.ucla.library.libservices.invoicing.webservices.adjustments.beans.LineItemAdjustment;
import edu.ucla.library.libservices.webservices.invoices.vger.beans.PatronRefund;
import edu.ucla.library.libservices.webservices.invoices.vger.client.RefundClient;
import edu.ucla.library.libservices.webservices.invoices.vger.generators.PatronRefundGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;

public class RefundProcessor
{
  private static List<PatronRefund> refunds;
  private static final SimpleDateFormat SIMPLE =
    new SimpleDateFormat( "MM/dd/yy" );

  public RefundProcessor()
  {
    super();
  }

  public static void main( String[] args )
  {
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

    refunds = generator.getPatrons();
  }

  private static void addRefunds()
  {
    for ( PatronRefund theRefund: refunds )
    {
      System.out.println( "working with refund on " +
                          theRefund.getInvoiceNumber() + " on line " +
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
      theClient.insertRefund();
    }
  }

  private static void updateNotes()
  {
    BufferedWriter writer;
    try
    {
      writer =
          new BufferedWriter( new FileWriter( new File( "C:\\Temp\\libbill\\update_libbill_refunds.sql" ) ) );
      for ( PatronRefund theRefund: refunds )
      {
        StringBuffer buffer;
        buffer =
            new StringBuffer( "UPDATE ucladb.fine_fee_transactions SET trans_note = '" );
        buffer.append( SIMPLE.format( new Date() ) ).append( " Credited in LibBill           " );
        buffer.append( theRefund.getInvoiceNumber() ).append( " Line " );
        buffer.append( theRefund.getLineNumber() ).append( "' WHERE fine_fee_trans_id = " );
        buffer.append( theRefund.getVgerTransID() ).append( ";" );
        System.out.println( "add query to file: " + buffer );
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
}
/*
 * generate list of refunds
 * for zach refund do:
 *  insert refund in libbill
 *  update refund note in voyager
 * done
 */