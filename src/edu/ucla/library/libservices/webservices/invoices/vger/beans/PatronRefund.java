package edu.ucla.library.libservices.webservices.invoices.vger.beans;

public class PatronRefund
{
  private String invoiceNumber;
  private int lineNumber;
  private int amount;
  private String reason;
  private int vgerTransID;
  
  public PatronRefund()
  {
    super();
  }

  public void setInvoiceNumber( String invoiceNumber )
  {
    this.invoiceNumber = invoiceNumber;
  }

  public String getInvoiceNumber()
  {
    return invoiceNumber;
  }

  public void setLineNumber( int lineNumber )
  {
    this.lineNumber = lineNumber;
  }

  public int getLineNumber()
  {
    return lineNumber;
  }

  public void setAmount( int amount )
  {
    this.amount = amount;
  }

  public int getAmount()
  {
    return amount;
  }

  public void setReason( String reason )
  {
    this.reason = reason;
  }

  public String getReason()
  {
    return reason;
  }

  public void setVgerTransID( int vgerTransID )
  {
    this.vgerTransID = vgerTransID;
  }

  public int getVgerTransID()
  {
    return vgerTransID;
  }
}
