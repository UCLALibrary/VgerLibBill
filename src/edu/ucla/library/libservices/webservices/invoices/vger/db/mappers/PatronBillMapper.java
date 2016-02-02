package edu.ucla.library.libservices.webservices.invoices.vger.db.mappers;

import edu.ucla.library.libservices.webservices.invoices.vger.beans.PatronBill;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class PatronBillMapper
  implements RowMapper
{
  public PatronBillMapper()
  {
    super();
  }

  public Object mapRow( ResultSet rs, int i )
    throws SQLException
  {
    PatronBill bean;

    bean = new PatronBill();
    bean.setAuthor( rs.getString( "author" ) );
    bean.setFineFeeBalance( rs.getInt( "fine_fee_balance" ) / 100 );
    bean.setFineFeeID( rs.getInt( "fine_fee_id" ) );
    bean.setFineFeeType( rs.getInt( "fine_fee_type" ) );
    bean.setItemBarcode( rs.getString( "item_barcode" ) );
    bean.setItemID( rs.getInt( "item_id" ) );
    bean.setLocationCode( rs.getString( "location_code" ) );
    bean.setNormalizedCallNo( rs.getString( "normalized_call_no" ) );
    bean.setPatronID( rs.getInt( "patron_id" ) );
    bean.setTitle( rs.getString( "title" ) );

    return bean;
  }
}
