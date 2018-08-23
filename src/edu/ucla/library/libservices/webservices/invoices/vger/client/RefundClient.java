package edu.ucla.library.libservices.webservices.invoices.vger.client;

import edu.ucla.library.libservices.invoicing.webservices.adjustments.beans.LineItemAdjustment;
import edu.ucla.library.libservices.webservices.invoices.vger.db.procs.AddLineItemAdjustmentProcedure;

import java.util.Properties;

public class RefundClient
{
  private LineItemAdjustment theRefund;
  private Properties props;
  
  public RefundClient()
  {
    super();
  }

  public void setTheRefund( LineItemAdjustment theRefund )
  {
    this.theRefund = theRefund;
  }

  private LineItemAdjustment getTheRefund()
  {
    return theRefund;
  }

  public void insertRefund()
  {
    AddLineItemAdjustmentProcedure proc;
    
    proc = new  AddLineItemAdjustmentProcedure();
    proc.setData( getTheRefund() );
    proc.setProps( getProps() );
    proc.addAdjustment();
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
