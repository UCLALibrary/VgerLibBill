package edu.ucla.library.libservices.webservices.invoices.vger.db.mappers;

import edu.ucla.library.libservices.webservices.invoices.vger.beans.PatronRefund;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class PatronRefundMapper
  implements RowMapper
{
  public PatronRefundMapper()
  {
    super();
  }

  public Object mapRow( ResultSet rs, int i )
    throws SQLException
  {
    PatronRefund bean;
    
    bean = new PatronRefund();
    bean.setAmount( rs.getInt( "trans_amount" ) );
    bean.setInvoiceNumber( rs.getString( "invoice_no" ) );
    bean.setLineNumber( rs.getInt( "line_no" ) );
    bean.setReason( rs.getString( "refund_note" ) );
    bean.setVgerTransID( rs.getInt( "fine_fee_trans_id" ) );
    
    return bean;
  }
}
