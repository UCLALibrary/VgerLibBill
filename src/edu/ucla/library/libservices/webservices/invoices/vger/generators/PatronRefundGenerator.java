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
  private static final String QUERY =
    "SELECT fft_r.fine_fee_trans_id,fft_r.trans_amount,substr(fft_t.trans_note" 
    + ",40,8) as invoice_no,substr(fft_t.trans_note,54) as line_no," 
    + "fft_r.trans_note as refund_note FROM ucladb.fine_fee ff inner join " 
    + "ucladb.fine_fee_transactions fft_r on ff.fine_fee_id = fft_r.fine_fee_id" 
    + " inner join ucladb.fine_fee_transactions fft_t on ff.fine_fee_id = " 
    + "fft_t.fine_fee_id inner join ucladb.fine_fee_trans_type fftt_t on " 
    + "fft_t.trans_type = fftt_t.transaction_type inner join " 
    + "ucladb.fine_fee_trans_type fftt_r on fft_r.trans_type = " 
    + "fftt_r.transaction_type WHERE fftt_r.transaction_desc = 'LibBill Refund'" 
    + " AND fftt_t.transaction_desc = 'LibBill Transfer' and " 
    + "(nvl(substr(fft_r.trans_note, 10, 19), '<NULL>') <> " 
    + "'Credited in LibBill') ORDER BY ff.patron_id";

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
        new JdbcTemplate( ds ).query( QUERY, new PatronRefundMapper() );
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
