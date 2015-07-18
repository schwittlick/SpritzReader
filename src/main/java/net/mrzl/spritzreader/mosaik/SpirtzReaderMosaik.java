package net.mrzl.spritzreader.mosaik;

import net.mrzl.spritzreader.SpritzParser;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;
import processing.core.PFont;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: mrzl
 * Date: 15.03.14
 * Time: 18:47
 * Project: SpritzReader
 */
public class SpirtzReaderMosaik extends PApplet {

    private int displayCountX, displayCountY;
    private SpritzParser[][] displays;
    // private ArrayList<SpritzParser> displays;
    private String text;
    OscP5 oscP5;
    public void setup() {
        size( 1280, 720 );
        frameRate( 60 );

        oscP5 = new OscP5(this,12000);

        println( PFont.list());
        PFont font = createFont( "Replica-Bold", 32 );
        textFont(font);

        textSize( 10 );

        displayCountX = 80;
        displayCountY = 800;
        displays = new SpritzParser[ displayCountX ][ displayCountY ];
        text = "";

        //String[] stuff = loadStrings( "anna_karenina.txt" );
        //for ( String s : stuff ) {
        //    text += s;
        // }
        try {
            text = readFile( "nietzsche_menschliches.txt", Charset.defaultCharset() );
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        //System.out.println( "Lines: " + countLines( text ) );
        String[] lines = getLines( text );
        Pattern regex = Pattern.compile( "^(([0-9]{1,3})(\\.))" );
        // <ChapterIndex, LineNumber>
        LinkedHashMap< Integer, Integer > chaptersMeta = new LinkedHashMap< Integer, Integer >();
        int lineCount = 0;
        for ( String line : lines ) {
            Matcher matches = regex.matcher( line );
            if ( matches.find() ) {
                String substr = line.substring( 0, line.length( ) - 1 );
                //System.out.println( substr );
                try {
                    int chapIndex = Integer.parseInt( substr );
                    chaptersMeta.put( chapIndex, lineCount );
                } catch ( NumberFormatException e ) {
                    System.out.println(e );
                }
            }
            lineCount++;
        }

        ArrayList< String > chapters = new ArrayList< String >();

        Iterator it = chaptersMeta.entrySet().iterator();
        while ( it.hasNext() ) {
            Map.Entry pairs = ( Map.Entry ) it.next();
            Integer from = chaptersMeta.get( ( ( Integer ) pairs.getKey() ) ) + 2;
            Integer to = chaptersMeta.get( ( Integer ) ( pairs.getKey() ) + 1 );
            //System.out.println( from + " " + to );
            try {
                String[] subtext = Arrays.copyOfRange( lines, from, to );
                StringBuilder builder = new StringBuilder();
                for ( String s : subtext ) {
                    builder.append( s );
                }
                String finishedString = builder.toString();
                chapters.add( finishedString );
                //System.out.println( finishedString );

                //text.substring( ,   )
            } catch( NullPointerException e ) {
                System.out.println( "Couldn't add text." );
            }
        }

        System.out.println( "Sections: " + chaptersMeta.size() );
        System.out.println( "Sections: " + chapters.size() );

        int globalIndex = 0;
        for ( int i = 0; i < displayCountY; i++ ) {
            for ( int j = 0; j < displayCountX; j++ ) {

                //println( globalIndex);
                SpritzParser r = new SpritzParser( 300, 300, false );
                r.setWpm( 200 );
                int lengthOfStringDevidedByAllDisplays = text.length() / ( displayCountY * displayCountX );
                //System.out.println( text.length() );
                String textToAdd = text.substring( lengthOfStringDevidedByAllDisplays * globalIndex, lengthOfStringDevidedByAllDisplays * ( globalIndex + 1 ) );
                try {
                    r.addText( chapters.get( globalIndex ) );
                } catch( Exception e ) {
                    globalIndex = (int)(random(chapters.size()));
                }
                displays[ j ][ i ] = r;
                globalIndex++;
            }
        }
    }

    private static int countLines( String str ) {
        String[] lines = str.split( System.getProperty( "line.separator" ) );
        return lines.length;
    }

    private static String[] getLines( String text ) {
        return text.split( System.getProperty( "line.separator" ) );
    }

    static String readFile( String path, Charset encoding )
            throws IOException {
        byte[] encoded = Files.readAllBytes( Paths.get( path ) );
        return encoding.decode( ByteBuffer.wrap( encoded ) ).toString();
    }

    public void draw() {

        background( 20 );
        fill( 255 );
        int textSize;
        if( displayCountX > displayCountY ) {
            textSize = constrain( ( int ) ( map( displayCountX, 1, 80, 80, 3 ) ), 3, 80 );
        } else {
            textSize = constrain( ( int ) ( map( displayCountY, 1, 70, 80, 3 ) ), 3, 80 );
        }


        textSize( textSize);
        try {
            translate( ( width / displayCountX + 1 ) / 2, ( height /  displayCountY + 1  ) / 2 + 8 );
            for ( int i = 0; i < displayCountX; i++ ) {
                for ( int j = 0; j < displayCountY; j++ ) {
                    try {
                        SpritzParser p = displays[ i ][ j ];
                        String newWord = "";
                        try {
                            newWord = p.nextWord( millis() + (int)(random(0, 200)) );

                        } catch ( Exception e ) {
                            //e.printStackTrace();
                        }
                        float textWidth = textWidth( newWord );
                        text( newWord, ( width / displayCountX + 1 ) * i - textWidth / 2, ( height / displayCountY + 1 ) * j );
                    } catch ( ArrayIndexOutOfBoundsException e ) {
                        System.err.println( "Catching an ArrayIndexOutOfBoundsException." );
                    }
                }
            }
        } catch( ArithmeticException e ) {
            System.err.println( "Catching an ArithmeticException." );
        }
    }

    public void keyPressed() {
        startAllReaders();
    }

    private void startAllReaders() {
        for ( int i = 0; i < displays.length; i++ ) {
            for ( int j = 0; j < displays[0].length; j++ ) {

                SpritzParser p = displays[ i ][ j ];
                p.start();
            }
        }
    }

    public void mouseDragged() {
        displayCountX = (int)( map(mouseX, 0, width, 1, 70) );
        displayCountY = (int)( map(mouseY, 0, height, 1, 800) );
    }

    public void oscEvent( OscMessage theOscMessage) {
  /* print the address pattern and the typetag of the received OscMessage */
        //print("### received an osc message.");
        //print(" addrpattern: "+theOscMessage.address() + " . " +theOscMessage.addrPattern());
        //println(" typetag: "+theOscMessage.typetag());
        //println();
        if(theOscMessage.checkAddrPattern("/x")==true) {
            if(theOscMessage.checkTypetag("f")) {
                println( "x" );
                displayCountX = ( int ) theOscMessage.get( 0 ).floatValue();
            }
        } else if(theOscMessage.checkAddrPattern("/y")==true) {
            if(theOscMessage.checkTypetag("f")) {
                displayCountY = ( int ) theOscMessage.get( 0 ).floatValue();
            }
        }
        println( "x: " + displayCountX + " y: " + displayCountY );
    }

    public static void main( String[] args ) {
        PApplet.main( new String[]{ "net.mrzl.spritzreader.mosaik.SpirtzReaderMosaik" } );
    }
}
