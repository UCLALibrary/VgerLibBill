package edu.ucla.library.libservices.webservices.invoices.vger.generators;

import edu.ucla.library.libservices.webservices.invoices.vger.beans.PatronBill;

import edu.ucla.library.libservices.webservices.invoices.vger.db.source.DataSourceFactory;

import edu.ucla.library.libservices.webservices.invoices.vger.db.mappers.PatronBillMapper;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class PatronBillGenerator
{
  private static final String QUERY = 
    "SELECT ff.patron_id, ff.item_id, il.location_code, unifix(bt.title) AS title, " 
    + "unifix(bt.author) AS author, ib.item_barcode, mm.normalized_call_no, ff.fine_fee_balance," 
    + " ff.fine_fee_type, ff.fine_fee_id FROM ucladb.fine_fee ff INNER JOIN " 
    + "ucladb.patron p ON ff.patron_id = p.patron_id INNER JOIN ucladb.patron_barcode" 
    + " pb ON pb.patron_id = p.patron_id AND pb.barcode_status = 1 INNER JOIN " 
    + "ucladb.patron_group pg ON pg.patron_group_id = pb.patron_group_id INNER" 
    + " JOIN ucladb.item i ON ff.item_id = i.item_id INNER JOIN ucladb.location" 
    + " il ON i.perm_location = il.location_id INNER JOIN ucladb.bib_item bi ON" 
    + " ff.item_id = bi.item_id INNER JOIN ucladb.bib_text bt ON bi.bib_id = " 
    + "bt.bib_id INNER JOIN ucladb.item_barcode ib ON i.item_id = ib.item_id " 
    + "INNER JOIN ucladb.mfhd_item mi on i.item_id = mi.item_id INNER JOIN " 
    + "ucladb.mfhd_master mm ON mi.mfhd_id = mm.mfhd_id WHERE " 
    + "(pg.patron_group_id IN (1,3,8,12,13,16,36,38,39,40,44,45,46,48,50,51)) " 
    + "AND (ff.fine_fee_type IN (2,3)) AND ff.fine_fee_balance > 0 AND " 
    + "p.expire_date > sysdate AND not exists  ( SELECT * FROM " 
    + "ucladb.patron_barcode pb2 WHERE pb2.patron_id = p.patron_id AND " 
    + "pb2.patron_barcode_id <> pb.patron_barcode_id AND pb2.barcode_status = 1 )" 
    + " AND trunc(ff.create_date) BETWEEN trunc(TO_DATE('11/01/2014','MM/DD/YYYY'))" 
    + " AND trunc(TO_DATE('12/31/2014','MM/DD/YYYY')) ORDER BY ff.patron_id";

  private DataSource ds;
  private String dbName;
  private List<PatronBill> patrons;

  public PatronBillGenerator()
  {
    super();
  }

  private void makeConnection()
  {
    //ds = DataSourceFactory.createDataSource( getDbName() );
    ds = DataSourceFactory.createVgerSource();
  }

  public List<PatronBill> getPatrons()
  {
    makeConnection();
    patrons = new JdbcTemplate(ds).query( QUERY, new PatronBillMapper() );
    return patrons;
  }

  public void setDbName( String dbName )
  {
    this.dbName = dbName;
  }

  public String getDbName()
  {
    return dbName;
  }
}
