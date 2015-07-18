package net.mrzl.spritzreader;

import net.mrzl.spritzreader.gui.TextInput;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Author: mrzl
 * Date: 09.03.14
 * Time: 23:04
 * Project: SpritzReader
 */
public class SpritzParser {

    private final long MILLIS_PER_MINUTE = 60 * 1000;

    private ArrayList<String> delimitedText;
    private int currentTextIndex;
    private long lastTimeWordUpdate;
    private boolean running;
    private int wpm;

    private TextInput dialog;

    public SpritzParser( int minTimeBetweenWords, int maxTimeInbetweenWords, boolean withGUI ) {
        addText( "Placeholder String" );

        lastTimeWordUpdate = 0;
        running = false;

        wpm = maxTimeInbetweenWords;
        if ( withGUI ) {
            dialog = new TextInput( this );
            dialog.pack();
            dialog.setVisible( true );
        }
    }

    public void addText( String textToAdd ) {
        currentTextIndex = 0;
        delimitedText = new ArrayList<String>( Arrays.asList( PApplet.splitTokens( textToAdd ) ) );
    }

    public void setWpmByMillis( int millisInbetween ) {
        setWpm( millisInbetweenToWpm( millisInbetween ) );
    }

    public void setWpm( int wpm ) {
        this.wpm = wpm;
    }

    public String nextWord( int millis ) throws Exception {
        if ( ( millis - lastTimeWordUpdate > wpmToMillisInbetween( wpm ) ) && running ) {
            currentTextIndex++;
            lastTimeWordUpdate = millis;
        }

        try {
            return delimitedText.get( currentTextIndex );
        } catch ( Exception e ) {
            currentTextIndex = delimitedText.size();
            throw new Exception( "" );
        }
    }

    public void start() {
        if ( currentTextIndex == delimitedText.size() ) {
            currentTextIndex = 0;
        }

        running = true;
    }

    public void pause() {
        running = false;
    }

    public ArrayList<String> getDelimitedText() {
        return delimitedText;
    }

    public int getCurrentTextIndex() {
        return currentTextIndex;
    }

    private int millisInbetweenToWpm( int millisInbetween ) {
        return ( int ) ( MILLIS_PER_MINUTE / millisInbetween );
    }

    private int wpmToMillisInbetween( int wpm ) {
        return ( int ) ( MILLIS_PER_MINUTE / wpm );
    }

    public void styleDocument() {
        if ( running ) {
            dialog.styleDocument( this );
        }
    }
}
