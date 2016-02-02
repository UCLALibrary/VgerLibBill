package edu.ucla.library.libservices.webservices.invoices.vger.client;

import edu.ucla.library.libservices.webservices.invoices.vger.db.procs.AddInvoiceProcedure;
import edu.ucla.library.libservices.invoicing.webservices.invoices.beans.InsertHeaderBean;

import java.util.Properties;

public class HeaderClient
{
  private InsertHeaderBean theHeader;
  private Properties props;

  public HeaderClient()
  {
    super();
  }

  public String insertHeader()
  {
    AddInvoiceProcedure proc;
    String invoiceNumber;
    
    invoiceNumber = null;
    
    proc = new AddInvoiceProcedure();
    proc.setData( getTheHeader() );
    proc.setProps( getProps() );
    
    invoiceNumber = proc.addInvoice();
    
    return invoiceNumber;
  }

  public void setTheHeader( InsertHeaderBean theHeader )
  {
    this.theHeader = theHeader;
  }

  private InsertHeaderBean getTheHeader()
  {
    return theHeader;
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
