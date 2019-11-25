package edu.ucla.library.libservices.webservices.invoices.vger.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class PdfClient
{
  private static final String URI_BASE = "https://webservices.library.ucla.edu/pdfoutput/pdfs/mail_invoice/";
  private String invoiceNo;
  
  public PdfClient()
  {
    super();
  }
  
  public void mailPdf()
  {
    Client client;
    WebResource webResource;
    
    client = Client.create();
    webResource = client.resource( URI_BASE.concat( getInvoiceNo() ) );
    webResource.get( String.class );
  }
  
  public void setInvoiceNo( String invoiceNo )
  {
    this.invoiceNo = invoiceNo;
  }

  private String getInvoiceNo()
  {
    return invoiceNo;
  }
}
