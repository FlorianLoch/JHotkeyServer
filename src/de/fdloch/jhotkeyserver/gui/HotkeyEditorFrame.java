/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.fdloch.jhotkeyserver.gui;

import de.fdloch.jhotkeyserver.conf.Configuration;
import de.fdloch.jhotkeyserver.conf.HotkeyEntry;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

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
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(10, 10, 300, 500);
        this.setVisible(true);
    }
    
    public static void main(String[] args) {
        new HotkeyEditorFrame(null);
    }
    
    private void buildFrame(ArrayList<HotkeyEntry> hotkeys) {
        this.setLayout(new FlowLayout());
        

        JPanel pnl = new JPanel();
        pnl.setLayout(null);
        JScrollPane sPane = new JScrollPane(pnl);
        sPane.setPreferredSize(new Dimension(600, 500));
        //sPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(sPane);
        
        int heightOffset = 0;
        
        int i = 0;
        while (i < 20) {
        for (HotkeyEntry hotkey : hotkeys) {
            JTextField tfName = new JTextField(hotkey.getName());
            tfName.setBounds(0, heightOffset, 100, 20);
            
            pnl.add(tfName);
            
            heightOffset += 25;
            
            pnl.setPreferredSize(new Dimension(600, heightOffset));
        }
        
        pnl.revalidate();
        pnl.repaint();
            i++;
        }
    } 
    
}
