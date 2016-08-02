package edu.ucla.library.libservices.webservices.invoices.vger.beans;

public class PatronBill
{
  private int patronID;
  private int itemID;
  private int bibID;
  private String locationCode;
  private String title;
  private String author;
  private String itemBarcode;
  private String normalizedCallNo;
  private int fineFeeBalance;
  private int fineFeeType;
  private int fineFeeID;
  private String transNote;

  public PatronBill()
  {
    super();
  }

  public void setPatronID( int patronID )
  {
    this.patronID = patronID;
  }

  public int getPatronID()
  {
    return patronID;
  }

  public void setItemID( int itemID )
  {
    this.itemID = itemID;
  }

  public int getItemID()
  {
    return itemID;
  }

  public void setLocationCode( String locationCode )
  {
    this.locationCode = locationCode;
  }

  public String getLocationCode()
  {
    return locationCode;
  }

  public void setTitle( String title )
  {
    this.title = title;
  }

  public String getTitle()
  {
    return title;
  }

  public void setAuthor( String author )
  {
    this.author = author;
  }

  public String getAuthor()
  {
    return author;
  }

  public void setItemBarcode( String itemBarcode )
  {
    this.itemBarcode = itemBarcode;
  }

  public String getItemBarcode()
  {
    return itemBarcode;
  }

  public void setNormalizedCallNo( String normalizedCallNo )
  {
    this.normalizedCallNo = normalizedCallNo;
  }

  public String getNormalizedCallNo()
  {
    return normalizedCallNo;
  }

  public void setFineFeeBalance( int fineFeeBalance )
  {
    this.fineFeeBalance = fineFeeBalance;
  }

  public int getFineFeeBalance()
  {
    return fineFeeBalance;
  }

  public void setFineFeeType( int fineFeeType )
  {
    this.fineFeeType = fineFeeType;
  }

  public int getFineFeeType()
  {
    return fineFeeType;
  }

  public void setFineFeeID( int fineFeeID )
  {
    this.fineFeeID = fineFeeID;
  }

  public int getFineFeeID()
  {
    return fineFeeID;
  }

  public String toString()
  {
    return "PatronBill: $" + getFineFeeBalance() + " fine/fee on " +
      getItemBarcode() + " for patron " + getPatronID();
  }

  public void setTransNote( String transNote )
  {
    this.transNote = transNote;
  }

  public String getTransNote()
  {
    return transNote;
  }

  public void setBibID( int bibID )
  {
    this.bibID = bibID;
  }

  public int getBibID()
  {
    return bibID;
  }
}
