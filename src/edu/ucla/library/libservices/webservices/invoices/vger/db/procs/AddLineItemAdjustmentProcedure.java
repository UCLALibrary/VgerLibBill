package edu.ucla.library.libservices.webservices.invoices.vger.db.procs;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.object.StoredProcedure;
import edu.ucla.library.libservices.webservices.invoices.vger.db.source.DataSourceFactory;
import edu.ucla.library.libservices.invoicing.webservices.adjustments.beans.LineItemAdjustment;

import java.sql.Types;

import java.util.HashMap;
import java.util.Map;
import org.springframework.jdbc.core.SqlParameter;
import java.util.Properties;

public class AddLineItemAdjustmentProcedure
  extends StoredProcedure
{
  private DataSource ds;
  private LineItemAdjustment data;
  private Properties props;

  public AddLineItemAdjustmentProcedure( JdbcTemplate jdbcTemplate,
                                         String string )
  {
    super( jdbcTemplate, string );
  }

  public AddLineItemAdjustmentProcedure( DataSource dataSource,
                                         String string )
  {
    super( dataSource, string );
  }

  public AddLineItemAdjustmentProcedure()
  {
    super();
  }

  public void setData( LineItemAdjustment data )
  {
    this.data = data;
  }

  private void makeConnection()
  {
    ds = DataSourceFactory.createBillSource(getProps());
  }

  public void addAdjustment()
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
    //setSql( "invoice_owner.insert_line_item_adjustment" );
    setSql( "insert_line_item_adjustment" );
    declareParameter( new SqlParameter( "p_invoice_number",
                                        Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_line_number", Types.INTEGER ) );
    declareParameter( new SqlParameter( "p_user_name", Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_amount",
                                        Types.NUMERIC ) );
    declareParameter( new SqlParameter( "p_adjustment_type", Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_adjustment_reason", Types.VARCHAR ) );
    compile();
  }

  private Map execute()
  {
    Map input;
    Map out;

    out = null;
    input = new HashMap();

    input.put( "p_invoice_number", data.getInvoiceNumber() );
    input.put( "p_line_number", data.getLineNumber() );
    input.put( "p_user_name", data.getCreatedBy() );
    input.put( "p_amount", data.getAmount() );
    input.put( "p_adjustment_type", data.getAdjustmentType() );
    input.put( "p_adjustment_reason", data.getAdjustmentReason() );

    out = execute( input );

    return out;
  }

  public void setProps( Properties props )
  {
    this.props = props;
  }

  private Properties getProps()
  {
    return props;
  }
}
