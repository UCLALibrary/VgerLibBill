package edu.ucla.library.libservices.webservices.invoices.vger.db.source;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class DataSourceFactory
{
  public static DataSource createVgerSource()
  {
    DriverManagerDataSource ds;

    ds = new DriverManagerDataSource();
    ds.setDriverClassName( "oracle.jdbc.OracleDriver" );
    ds.setUrl( "orable_url" );
    ds.setUsername( "oracle_user" );
    ds.setPassword( "oracle_pwd" );

    return ds;
  }

  public static DataSource createBillSource()
  {
    DriverManagerDataSource ds;

    ds = new DriverManagerDataSource();
    ds.setDriverClassName( "oracle.jdbc.OracleDriver" );
    ds.setUrl( "oracle_url" );
    ds.setUsername( "other_oracle_user" );
    ds.setPassword( "other_oracle__pwd" );

    return ds;
  }
}
