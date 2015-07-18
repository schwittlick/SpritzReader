package net.mrzl.spritzreader.gui;

import javax.swing.*;

/**
 * Author: mrzl
 * Date: 10.03.14
 * Time: 11:05
 * Project: SpritzReader
 */
public class TaskBarElement extends JFrame {
    public TaskBarElement( String title ) {
        super( title );
        setUndecorated( true );
        setVisible( true );
        setLocationRelativeTo( null );
    }
}