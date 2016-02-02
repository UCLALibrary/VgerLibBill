package edu.ucla.library.libservices.webservices.invoices.vger.db.procs;

import edu.ucla.library.libservices.webservices.invoices.vger.db.source.DataSourceFactory;
import edu.ucla.library.libservices.invoicing.webservices.invoices.beans.LineItemBean;

import java.sql.Types;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

public class AddLineItemProcedure
  extends StoredProcedure
{
  private DataSource ds;
  private Properties props;
  private LineItemBean data;


  public AddLineItemProcedure( JdbcTemplate jdbcTemplate, String string )
  {
    super( jdbcTemplate, string );
  }

  public AddLineItemProcedure( DataSource dataSource, String string )
  {
    super( dataSource, string );
  }

  public AddLineItemProcedure()
  {
    super();
  }

  public void setProps( Properties props )
  {
    this.props = props;
  }

  public void setData( LineItemBean data )
  {
    this.data = data;
  }

  private void makeConnection()
  {
    ds = DataSourceFactory.createBillSource();
  }

  public void addLineItem()
  {
    Map results;

    makeConnection();
    prepProc();
    results = execute();
  }

  private void prepProc()
  {
    setDataSource( ds );
    setFunction( false );
    //setSql( "invoice_owner.insert_line_item" );
    setSql( "insert_line_item" );
    declareParameter( new SqlParameter( "p_invoice_number",
                                        Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_location_service_id",
                                        Types.NUMERIC ) );
    declareParameter( new SqlParameter( "p_user_name", Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_quantity", Types.NUMERIC ) );
    if ( data.getUnitPrice() != 0D )
      declareParameter( new SqlParameter( "p_unit_price", Types.NUMERIC ) );
    compile();
  }

  private Map execute()
  {
    Map input;
    Map out;

    out = null;
    input = new HashMap();

    input.put( "p_invoice_number", data.getInvoiceNumber() );
    input.put( "p_location_service_id", data.getBranchServiceID() );
    input.put( "p_user_name", data.getCreatedBy() );
    input.put( "p_quantity", data.getQuantity() );
    if ( data.getUnitPrice() != 0D )
      input.put( "p_unit_price", data.getUnitPrice() );

    out = execute( input );

    return out;
  }
}
