package net.mrzl.spritzreader.gui;

import net.mrzl.spritzreader.SpritzParser;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class TextInput extends JDialog {
    private JPanel contentPane;
    private JButton clearButton;
    private JButton startButton;
    private JButton pasteButton;
    private JSlider wpmSlider;
    private JSpinner wpmSpinner;
    private JTextPane textPane;

    public TextInput( final SpritzParser p ) {
        super( new TaskBarElement( "Controls" ) );
        setContentPane( contentPane );
        setModal( false );

        wpmSpinner.setValue( wpmSlider.getValue() );

        clearButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                textPane.setText( "" );
                p.addText( "" );
            }
        } );

        startButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( startButton.getText().equals( "Start" ) ) {
                    p.start();
                    startButton.setText( "Pause" );
                } else {
                    p.pause();
                    startButton.setText( "Start" );
                }
            }
        } );

        textPane.addCaretListener( new CaretListener() {
            public void caretUpdate( CaretEvent e ) {
                JTextPane a = ( JTextPane ) e.getSource();

                styleDocument( p );
            }
        } );

        pasteButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                String textFromClipboard = null;
                try {
                    textFromClipboard = ( String ) Toolkit.getDefaultToolkit().getSystemClipboard().getData( DataFlavor.stringFlavor );
                } catch ( UnsupportedFlavorException e1 ) {
                    textFromClipboard = "Couldn't paste clipboard content.";
                } catch ( IOException e1 ) {
                    textFromClipboard = "Couldn't paste clipboard content.";
                }
                textPane.setText( textFromClipboard );
                p.addText( textFromClipboard );

                styleDocument( p );
            }
        } );

        wpmSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                JSpinner s = ( JSpinner ) e.getSource();
                wpmSlider.setValue( ( Integer ) s.getValue() );
                p.setWpm( ( Integer ) s.getValue() );
            }
        } );

        wpmSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                JSlider s = ( JSlider ) e.getSource();
                wpmSpinner.setValue( s.getValue() );
                p.setWpm( s.getValue() );
            }
        } );
    }

    public void styleDocument( SpritzParser p ) {
        StyledDocument doc = textPane.getStyledDocument();
        Style style = textPane.addStyle( "HighlightedSTyle", null );
        Style normalStyle = textPane.addStyle( "NormalStyle", null );

        StyleConstants.setForeground( style, Color.red );
        StyleConstants.setForeground( normalStyle, Color.black );


        int currentStartIndex = 0;
        int counter = 0;
        for ( String s : p.getDelimitedText() ) {
            if ( counter < p.getCurrentTextIndex() ) {
                currentStartIndex += s.length();
                counter++;
            }
        }

        int currentEndIndex = currentStartIndex + p.getDelimitedText().get( p.getCurrentTextIndex() ).length();
        try {
            doc.setCharacterAttributes( 0, currentStartIndex, normalStyle, true );
            doc.setCharacterAttributes( currentStartIndex, currentEndIndex, style, true );
        } catch ( Exception e ) {
            // ignore for now :)
        }
    }

}
