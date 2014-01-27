/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fdloch.jhotkeyserver.gui;

import de.fdloch.jhotkeyserver.conf.HotkeyEntry;
import java.awt.Dimension;
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
    private JTextField jTF_combination;
    private JTextField jTF_name;
    private JCheckBox jChkbx_popup;
    private JButton jBtn_remove;

    private boolean modifiedFlag = false;
    private boolean newFlag = false;
    private String origName;

    public HotkeySettingsPanel(HotkeyEntry hotkey, boolean newFlag) {
        if (hotkey == null) {
            hotkey = new HotkeyEntry("", "", false);
        }
        
        this.origName = hotkey.getName();
        this.newFlag = newFlag;
        
        this.setLayout(null);

        this.initComponents(hotkey);
    }

    public HotkeyEntry getSettings() {
        return new HotkeyEntry(this.jTF_name.getText(), this.jTF_combination.getText(), this.jChkbx_popup.isSelected());
    }

    public String getOrigName() {
        return this.origName;
    }
 
    public boolean isModified() {
        return this.modifiedFlag;
    }

    public boolean isNew() {
        return this.newFlag;
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

        this.jTF_name = new JTextField(hotkey.getName());
        this.jTF_name.setBounds(5, 0, 100, 20);
        this.jTF_name.addKeyListener(changeListener);

        this.jTF_combination = new JTextField(hotkey.getCombination());
        this.jTF_combination.setBounds(110, 0, 100, 20);
        this.jTF_combination.addKeyListener(changeListener);

        this.jChkbx_popup = new JCheckBox("Show popup when hotkey pressed");
        this.jChkbx_popup.setSelected(false);
        this.jChkbx_popup.setBounds(215, 0, 230, 20);
        this.jChkbx_popup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifiedFlag = true;
            }
        });

        this.jBtn_remove = new JButton("Remove");
        this.jBtn_remove.setBounds(450, 0, 120, 20);

        this.setPreferredSize(new Dimension(570, 20));
        
        this.add(this.jTF_name);
        this.add(this.jTF_combination);
        this.add(this.jChkbx_popup);
        this.add(this.jBtn_remove);
    }

}
