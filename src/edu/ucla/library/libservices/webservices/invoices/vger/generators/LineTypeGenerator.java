package edu.ucla.library.libservices.webservices.invoices.vger.generators;

import edu.ucla.library.libservices.webservices.invoices.vger.db.source.DataSourceFactory;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class LineTypeGenerator
{
  private static final String QUERY =
    "SELECT location_service_id FROM location_service_vw WHERE location_code " 
    + "= 'CS' AND subtype_name = ?";

  private DataSource ds;
  private int lineType;
  private String serviceName;
  private Properties props;
                 
  public LineTypeGenerator()
  {
    super();
  }

  private void makeConnection()
  {
    ds = DataSourceFactory.createBillSource(getProps());
  }

  public int getLineType()
  {
    makeConnection();
    lineType = new JdbcTemplate( ds ).queryForInt( 
        QUERY, new Object[] { getServiceName() } );

    return lineType;
  }

  public void setServiceName( String serviceName )
  {
    this.serviceName = serviceName;
  }

  private String getServiceName()
  {
    return serviceName;
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
