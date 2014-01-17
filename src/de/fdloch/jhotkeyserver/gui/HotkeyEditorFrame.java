/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.fdloch.jhotkeyserver.gui;

import de.fdloch.jhotkeyserver.conf.Configuration;
import de.fdloch.jhotkeyserver.conf.HotkeyEntry;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.ScrollPane;
import java.awt.TextField;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Florian
 */
public class HotkeyEditorFrame extends JFrame {

    private Configuration conf;
    
    public HotkeyEditorFrame(Configuration conf) throws HeadlessException {
        this.conf = conf;
        
        ArrayList<HotkeyEntry> hotkeys = new ArrayList<HotkeyEntry>();
        hotkeys.add(new HotkeyEntry("Pause", "Win+A", true));
        hotkeys.add(new HotkeyEntry("Play", "Win+P", false));
        
        this.buildFrame(hotkeys);
        
        this.setBounds(10, 10, 300, 500);
        this.setVisible(true);
    }
    
    public static void main(String[] args) {
        new HotkeyEditorFrame(null);
    }
    
    private void buildFrame(ArrayList<HotkeyEntry> hotkeys) {
        this.setLayout(new FlowLayout());
        
        ScrollPane sPane = new ScrollPane();
        sPane.setSize(300, 500);
        JPanel pnl = new JPanel();
        pnl.setLayout(null);
        sPane.add(pnl);
        this.add(sPane);
        
        int heightOffset = 0;
        for (HotkeyEntry hotkey : hotkeys) {
            TextField tfName = new TextField(hotkey.getName());
            tfName.setBounds(0, heightOffset, 50, 20);
            
            pnl.add(tfName);
            
            heightOffset += 25;
        }
    } 
    
}
