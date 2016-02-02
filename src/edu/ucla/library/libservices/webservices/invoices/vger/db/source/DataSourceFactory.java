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
    ds.setUrl( "jdbc:oracle:thin:@ils-db-test.library.ucla.edu:1521:VGER" );
    ds.setUsername( "vger_support" );
    ds.setPassword( "vger_support_pwd" );

    return ds;
  }

  public static DataSource createBillSource()
  {
    DriverManagerDataSource ds;

    ds = new DriverManagerDataSource();
    ds.setDriverClassName( "oracle.jdbc.OracleDriver" );
    ds.setUrl( "jdbc:oracle:thin:@ils-db-test.library.ucla.edu:1521:VGER" );
    ds.setUsername( "invoice_service" ); // "invoice_service_dev" );
    ds.setPassword( "invoice_service_pwd" ); //"invoice_service_dev_pwd" );

    return ds;
  }
}
