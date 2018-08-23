package edu.ucla.library.libservices.webservices.invoices.vger.generators;

import edu.ucla.library.libservices.webservices.invoices.vger.beans.PatronRefund;
import edu.ucla.library.libservices.webservices.invoices.vger.db.mappers.PatronRefundMapper;
import edu.ucla.library.libservices.webservices.invoices.vger.db.source.DataSourceFactory;

import java.util.List;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class PatronRefundGenerator
{
  private static final String QUERY = "query.refund";

  private DataSource ds;
  private List<PatronRefund> patrons;
  private Properties props;

  public PatronRefundGenerator()
  {
    super();
  }

  private void makeConnection()
  {
    ds = DataSourceFactory.createVgerSource(getProps());
  }

  public List<PatronRefund> getPatrons()
  {
    makeConnection();
    patrons =
        new JdbcTemplate( ds ).query( props.getProperty( QUERY ), new PatronRefundMapper() );
    return patrons;
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
