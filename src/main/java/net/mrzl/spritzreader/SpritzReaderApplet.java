package net.mrzl.spritzreader;

import processing.core.PApplet;

/**
 * Author: mrzl
 * Date: 09.03.14
 * Time: 23:03
 * Project: SpritzReader
 */
public class SpritzReaderApplet extends PApplet {

    private SpritzParser parser;

    public void setup() {
        size( 1400, 400 );
        textSize( 70 );

        parser = new SpritzParser( 150, 250, true );
    }

    public void draw() {
        background( 0 );
        String newWord;
        try {
            fill( 255 );
            newWord = parser.nextWord( millis() );
        } catch ( Exception e ) {
            fill( 255, 0, 0 );
            newWord = "Finished.";
        }
        drawTextAtCenter( newWord );
    }

    private void drawTextAtCenter( String text ) {
        float textWidth = textWidth( text );
        text( text, this.width / 2 - textWidth / 2, this.height / 2 );
    }
}
