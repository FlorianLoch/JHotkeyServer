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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
    private int heightOffset;
    
    public HotkeyEditorFrame(Configuration conf) throws HeadlessException {
        this.conf = conf;
        
        ArrayList<HotkeyEntry> hotkeys = new ArrayList<HotkeyEntry>();
        hotkeys.add(new HotkeyEntry("Pause", "Win+A", true));
        hotkeys.add(new HotkeyEntry("Play", "Win+P", false));
        
        this.buildFrame(hotkeys);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //this.setBounds(10, 10, 300, 500);
        this.setVisible(true);
    }
    
    public static void main(String[] args) {
        new HotkeyEditorFrame(null);
    }
    
    private void saveHotkeySettings() {
        
    }
    
    private void buildFrame(ArrayList<HotkeyEntry> hotkeys) {
        this.setLayout(new FlowLayout());
       
        final JPanel pnl = new JPanel();
        pnl.setLayout(null);
       
        JScrollPane sPane = new JScrollPane(pnl);
        sPane.setPreferredSize(new Dimension(600, 500));
        sPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        this.add(sPane);
        System.out.println(this.getHeight() - this.getContentPane().getHeight());
        int x = (this.getToolkit().getScreenSize().width / 2) - (620 / 2); 
        int y = (this.getToolkit().getScreenSize().height / 2) - (600 / 2);
        this.setBounds(x, y, 620, 600);
        this.setResizable(false);
           
        heightOffset = 5;
        
        for (HotkeyEntry hotkey : hotkeys) {         
            this.addHotkeySettingsPanel(hotkey, pnl);
            
            heightOffset += 25;
        }
        
        JButton jBtn_addNew = new JButton("Register new hotkey");
        jBtn_addNew.addActionListener(new ActionListener() {
            private HotkeyEditorFrame parent;
            
            public ActionListener init(HotkeyEditorFrame parent) {
                this.parent = parent;
                
                return this;
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                this.parent.addHotkeySettingsPanel(null, pnl);
                
                heightOffset += 25;
            }
        }.init(this));
        
        JButton jBtn_cancel = new JButton("Cancel");
        jBtn_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                
                getContentPane().removeAll();
            }
        });
        
        JButton jBtn_save = new JButton("Save");
        jBtn_save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveHotkeySettings();
                
                //setVisible(false);
                
                getContentPane().removeAll();
            }
        });
        
        this.add(jBtn_addNew);
        this.add(jBtn_cancel);
        this.add(jBtn_save);
    } 

    public void addHotkeySettingsPanel(HotkeyEntry hotkey, JPanel pnl) {
        HotkeySettingsPanel hKPnl = new HotkeySettingsPanel(hotkey, false);
        hKPnl.setBounds(0, heightOffset, 600, 20);

        hKPnl.addActionListenerToRemoveBtn(new ActionListener() {
            private JPanel jPnl_grandparent;
            private HotkeySettingsPanel hSP_parent;

            public ActionListener init(JPanel jPnl_parent, HotkeySettingsPanel hSP) {
                this.jPnl_grandparent = jPnl_parent;
                this.hSP_parent = hSP;

                return this;
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                this.jPnl_grandparent.remove(this.hSP_parent);
                jPnl_grandparent.revalidate();
                jPnl_grandparent.repaint();
            }         
        }.init(pnl, hKPnl));      
        
        pnl.add(hKPnl);
        
        pnl.setPreferredSize(new Dimension(600, heightOffset));
        pnl.revalidate();
        pnl.repaint();
    }
    
}
