/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fdloch.jhotkeyserver.gui;

import de.fdloch.jhotkeyserver.conf.HotkeyEntry;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Florian
 */
public class HotkeySettingsPanel extends JPanel {
    private JButton jBtn_remove;
    private boolean modifiedFlag = false;
    
    public HotkeySettingsPanel(HotkeyEntry hotkey) {
        this.setLayout(null);
        
        this.initComponents(hotkey);
    }
    
    public boolean isModified() {
        return this.modifiedFlag;
    }

    public void addActionListenerToRemoveBtn(ActionListener aL) {
        this.jBtn_remove.addActionListener(aL);
    }
    
    private void initComponents(HotkeyEntry hotkey) {
        KeyListener changeListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                setModifiedFlag();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                setModifiedFlag();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                setModifiedFlag();
            }
            
            private void setModifiedFlag() {
                modifiedFlag = true;
            }
        };
        
        JTextField jTF_name = new JTextField(hotkey.getName());
        jTF_name.setBounds(5, 0, 100, 20);
        jTF_name.addKeyListener(changeListener);

        JTextField jTF_combination = new JTextField(hotkey.getCombination());
        jTF_combination.setBounds(110, 0, 100, 20);
        jTF_combination.addKeyListener(changeListener);

        JCheckBox jChkbx_popup = new JCheckBox("Show popup when hotkey pressed");
        jChkbx_popup.setSelected(false);
        jChkbx_popup.setBounds(215, 0, 250, 20);
        jChkbx_popup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifiedFlag = true;
            }
        });        

        this.jBtn_remove = new JButton("Remove");
        this.jBtn_remove.setBounds(470, 0, 120, 20);
        
        this.add(jTF_name);
        this.add(jTF_combination);
        this.add(jChkbx_popup);
        this.add(this.jBtn_remove);
    }

}
