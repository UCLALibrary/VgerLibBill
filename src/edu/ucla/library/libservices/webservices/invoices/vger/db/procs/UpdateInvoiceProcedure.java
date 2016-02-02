package edu.ucla.library.libservices.webservices.invoices.vger.db.procs;

import edu.ucla.library.libservices.webservices.invoices.vger.db.source.DataSourceFactory;

import java.sql.Types;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

public class UpdateInvoiceProcedure
  extends StoredProcedure
{
  private DataSource ds;
  private Properties props;
  private String invoiceNumber;
  private String status;
  private String whoBy;

  public UpdateInvoiceProcedure( JdbcTemplate jdbcTemplate, String string )
  {
    super( jdbcTemplate, string );
  }

  public UpdateInvoiceProcedure( DataSource dataSource, String string )
  {
    super( dataSource, string );
  }

  public UpdateInvoiceProcedure()
  {
    super();
  }

  public void setProps( Properties props )
  {
    this.props = props;
  }

  public void setInvoiceNumber( String invoiceNumber )
  {
    this.invoiceNumber = invoiceNumber;
  }

  public void setStatus( String status )
  {
    this.status = status;
  }

  public void setWhoBy( String whoBy )
  {
    this.whoBy = whoBy;
  }

  private void makeConnection()
  {
    ds = DataSourceFactory.createBillSource();
  }

  public void updateInvoice()
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
    //setSql( "invoice_owner.update_invoice" );
    setSql( "update_invoice" );
    declareParameter( new SqlParameter( "p_invoice_number",
                                        Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_status", Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_user_name", Types.VARCHAR ) );
    compile();
  }

  private Map execute()
  {
    Map input;
    Map out;

    out = null;
    input = new HashMap();

    input.put( "p_invoice_number", invoiceNumber );
    input.put( "p_status", status );
    input.put( "p_user_name", whoBy );

    out = execute( input );

    return out;
  }
}
