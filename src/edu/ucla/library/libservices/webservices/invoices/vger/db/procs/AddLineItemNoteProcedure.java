package edu.ucla.library.libservices.webservices.invoices.vger.db.procs;

import edu.ucla.library.libservices.invoicing.webservices.invoices.beans.LineItemNote;

import edu.ucla.library.libservices.webservices.invoices.vger.db.source.DataSourceFactory;

import java.sql.Types;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

public class AddLineItemNoteProcedure
  extends StoredProcedure
{
  private DataSource ds;
  private LineItemNote data;
  private Properties props;

  public AddLineItemNoteProcedure( JdbcTemplate jdbcTemplate,
                                   String string )
  {
    super( jdbcTemplate, string );
  }

  public AddLineItemNoteProcedure( DataSource dataSource, String string )
  {
    super( dataSource, string );
  }

  public AddLineItemNoteProcedure()
  {
    super();
  }

  public void setData( LineItemNote data )
  {
    this.data = data;
  }

  private LineItemNote getData()
  {
    return data;
  }

  private void makeConnection()
  {
    ds = DataSourceFactory.createBillSource();
  }

  public void setProps( Properties props )
  {
    this.props = props;
  }

  private Properties getProps()
  {
    return props;
  }

  public void addNote()
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
    setSql( "insert_line_item_note" );
    declareParameter( new SqlParameter( "p_invoice_number",
                                        Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_line_number", Types.NUMERIC ) );
    declareParameter( new SqlParameter( "p_internal", Types.CHAR ) );
    declareParameter( new SqlParameter( "p_user_name", Types.VARCHAR ) );
    declareParameter( new SqlParameter( "p_note", Types.VARCHAR ) );
    compile();
  }

  private Map execute()
  {
    Map input;
    Map out;

    out = null;
    input = new HashMap();

    input.put( "p_invoice_number", getData().getInvoiceNumber() );
    input.put( "p_line_number", getData().getLineNumber() );
    input.put( "p_internal", ( getData().isInternal() ? "Y": "N" ) );
    input.put( "p_user_name", getData().getCreatedBy() );
    input.put( "p_note", getData().getNote() );

    out = execute( input );

    return out;
  }
}
