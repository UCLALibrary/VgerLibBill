package edu.ucla.library.libservices.webservices.invoices.vger.client;

import edu.ucla.library.libservices.invoicing.webservices.adjustments.beans.LineItemAdjustment;
import edu.ucla.library.libservices.webservices.invoices.vger.db.procs.AddLineItemAdjustmentProcedure;

public class RefundClient
{
  LineItemAdjustment theRefund;
  
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
    proc.addAdjustment();
  }
}
