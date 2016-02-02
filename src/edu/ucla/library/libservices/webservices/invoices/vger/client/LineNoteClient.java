package edu.ucla.library.libservices.webservices.invoices.vger.client;

import edu.ucla.library.libservices.invoicing.webservices.invoices.beans.LineItemNote;

import edu.ucla.library.libservices.webservices.invoices.vger.db.procs.AddLineItemNoteProcedure;

import java.util.Properties;

public class LineNoteClient
{
  private LineItemNote theNote;
  private Properties props;
  
  public void setTheNote( LineItemNote theNote )
  {
    this.theNote = theNote;
  }

  private LineItemNote getTheNote()
  {
    return theNote;
  }

  public void setProps( Properties props )
  {
    this.props = props;
  }

  private Properties getProps()
  {
    return props;
  }
  
  public void insertNote()
  {
    AddLineItemNoteProcedure proc;
    
    proc = new AddLineItemNoteProcedure();
    proc.setData( getTheNote() );
    proc.setProps( getProps() );
    proc.addNote();
  }
}
