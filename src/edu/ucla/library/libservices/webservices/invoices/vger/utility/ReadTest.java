package edu.ucla.library.libservices.webservices.invoices.vger.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.io.IOException;

import java.util.ArrayList;

public class ReadTest
{
  public ReadTest()
  {
    super();
  }

  public static void main( String[] args )
    throws FileNotFoundException, IOException
  {
    BufferedReader br;
    ArrayList<String> lines;
    String line;

    br =
        new BufferedReader( new FileReader( new File( "C:\\Temp\\libbill\\post_libbill_trans" ) ) );
    lines = new ArrayList<String>();
    while ( ( line = br.readLine() ) != null )
    {
      lines.add( line );
    }

    for ( String theLine: lines )
    {
      String[] tokens;
      tokens = theLine.split( "\t" );
      //System.out.println( "for line " + theLine + "\n\ttoken count = " + tokens.length );
    }
  }
}
