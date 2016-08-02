package edu.ucla.library.libservices.webservices.invoices.vger.db.source;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class DataSourceFactory
{
  public static DataSource createVgerSource(Properties props)
  {
    DriverManagerDataSource ds;

    ds = new DriverManagerDataSource();
    ds.setDriverClassName( "oracle.jdbc.OracleDriver" );
    ds.setUrl( props.getProperty( "vger.url" ) );
    ds.setUsername( props.getProperty( "vger.user" ) );
    ds.setPassword( props.getProperty( "vger.password" ) );

    return ds;
  }

  public static DataSource createBillSource(Properties props)
  {
    DriverManagerDataSource ds;

    ds = new DriverManagerDataSource();
    ds.setDriverClassName( "oracle.jdbc.OracleDriver" );
    ds.setUrl( props.getProperty( "bill.url" ) );
    ds.setUsername( props.getProperty( "bill.user" ) ); 
    ds.setPassword( props.getProperty( "bill.password" ) ); 

    return ds;
  }
}
