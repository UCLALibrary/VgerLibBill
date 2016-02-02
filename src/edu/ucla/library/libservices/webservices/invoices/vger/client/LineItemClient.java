package edu.ucla.library.libservices.webservices.invoices.vger.client;

import edu.ucla.library.libservices.invoicing.webservices.invoices.beans.LineItemBean;

import edu.ucla.library.libservices.webservices.invoices.vger.db.procs.AddLineItemProcedure;

import java.util.Properties;

public class LineItemClient
{
  private LineItemBean theLine;
  private Properties props;

  public LineItemClient()
  {
    super();
  }

  public void setTheLine( LineItemBean theLine )
  {
    this.theLine = theLine;
  }

  private LineItemBean getTheLine()
  {
    return theLine;
  }

  public void setProps( Properties props )
  {
    this.props = props;
  }

  private Properties getProps()
  {
    return props;
  }

  public void insertLine()
  {
    AddLineItemProcedure proc;
    
    proc = new  AddLineItemProcedure();
    proc.setData( getTheLine() );
    proc.setProps( getProps() );
    proc.addLineItem();
  }
}
