package edu.ucla.library.libservices.webservices.invoices.vger.generators;

import edu.ucla.library.libservices.webservices.invoices.vger.beans.PatronBill;
import edu.ucla.library.libservices.webservices.invoices.vger.db.mappers.PatronBillMapper;
import edu.ucla.library.libservices.webservices.invoices.vger.db.source.DataSourceFactory;

import java.util.List;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class PatronBillGenerator
{
  private static final String QUERY = 
    "SELECT ff.patron_id, ff.item_id, bi.bib_id, il.location_code, unifix(bt.title) AS title, " 
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
    + "AND (ff.fine_fee_type IN (1,2,3)) AND ff.fine_fee_balance > 0 AND " 
    + "p.expire_date > sysdate AND not exists  ( SELECT * FROM " 
    + "ucladb.patron_barcode pb2 WHERE pb2.patron_id = p.patron_id AND " 
    + "pb2.patron_barcode_id <> pb.patron_barcode_id AND pb2.barcode_status = 1 )" 
    + " AND trunc(ff.create_date) BETWEEN trunc(TO_DATE('06/01/2018','MM/DD/YYYY'))" 
    + " AND trunc(TO_DATE('07/31/2018','MM/DD/YYYY')) ORDER BY ff.patron_id";
//    + "AND p.patron_id IN (48263,52895,58321,363429) ORDER BY ff.patron_id"; //20714,21182,48263,52895,58321,157427,

  private DataSource ds;
  private List<PatronBill> patrons;
  private Properties props;

  public PatronBillGenerator()
  {
    super();
  }

  private void makeConnection()
  {
    //ds = DataSourceFactory.createDataSource( getDbName() );
    ds = DataSourceFactory.createVgerSource(getProps());
  }

  public List<PatronBill> getPatrons()
  {
    makeConnection();
    patrons = new JdbcTemplate(ds).query( QUERY, new PatronBillMapper() );
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
