/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.fdloch.jhotkeyserver.gui;

import de.fdloch.jhotkeyserver.JHotkeyServer;
import de.fdloch.jhotkeyserver.conf.Configuration;
import de.fdloch.jhotkeyserver.conf.HotkeyEntry;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 *
 * @author Florian
 */
public class HotkeyEditorFrame extends JFrame {

    private JHotkeyServer main;
    private Configuration conf;
    private JPanel pnl;
    
    public static void show(JHotkeyServer main) {
        new HotkeyEditorFrame(main).setVisible(true);
    }
    
    public HotkeyEditorFrame(JHotkeyServer main) throws HeadlessException {
        this.main = main;
        this.conf = main.getConf();
        
        this.buildFrame(this.conf.getHotkeys());
        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private boolean saveHotkeySettings() {
        boolean somethingChanged = false;
        
        for (Component com : this.pnl.getComponents()) {
            if (com instanceof HotkeySettingsPanel) {
                HotkeySettingsPanel hKSP = (HotkeySettingsPanel) com;
                
                if (hKSP.isNew()) {
                    this.conf.addHotkeyBinding(hKSP.getSettings());
                    
                    somethingChanged = true;
                }
                else if (hKSP.isModified()) {
                    this.conf.updateHotkeyBinding(hKSP.getOrigName(), hKSP.getSettings());
                
                    somethingChanged = true;
                }
            }
        }
        
        if (somethingChanged) {
            try {
                this.conf.write();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Could not save configuration - sorry for that!");
            }
        }
        
        return somethingChanged;
    }
    
    private void buildFrame(ArrayList<HotkeyEntry> hotkeys) {
        this.setLayout(new FlowLayout());
       
        this.pnl = new JPanel();
        pnl.setLayout(new ListLayout(0, 5, 5));
       
        JScrollPane sPane = new JScrollPane(pnl);
        sPane.setPreferredSize(new Dimension(600, 500));
        sPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        this.add(sPane);

        int x = (this.getToolkit().getScreenSize().width / 2) - (620 / 2); 
        int y = (this.getToolkit().getScreenSize().height / 2) - (600 / 2);
        this.setBounds(x, y, 620, 600);
        this.setResizable(false);
        
        for (HotkeyEntry hotkey : hotkeys) {         
            this.addHotkeySettingsPanel(hotkey, pnl);
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
                this.parent.addHotkeySettingsPanel(null, pnl, true);
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
                boolean somethingChanged = saveHotkeySettings();
                
                if (somethingChanged) {
                    main.refreshBindings();
                }
                
                setVisible(false);
                
                getContentPane().removeAll();
            }
        });
        
        this.add(jBtn_addNew);
        this.add(jBtn_cancel);
        this.add(jBtn_save);
    } 

    public void addHotkeySettingsPanel(HotkeyEntry hotkey, JPanel pnl) {
        this.addHotkeySettingsPanel(hotkey, pnl, false);
    }
    
    public void addHotkeySettingsPanel(HotkeyEntry hotkey, JPanel pnl, boolean newFlag) {
        HotkeySettingsPanel hKPnl = new HotkeySettingsPanel(hotkey, newFlag);
        hKPnl.setPreferredSize(new Dimension(600, 20));

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
        
        pnl.revalidate();
        pnl.repaint();
    }
    
}
