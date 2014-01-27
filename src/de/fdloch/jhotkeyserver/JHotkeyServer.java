/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.fdloch.jhotkeyserver;

import de.fdloch.jhotkeyserver.conf.Configuration;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import de.fdloch.jhotkeyserver.conf.HotkeyEntry;
import de.fdloch.jhotkeyserver.gui.HotkeyEditorFrame;
import de.fdloch.jhotkeyserver.network.ConnectionManager;
import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Florian
 */
public class JHotkeyServer implements HotkeyListener {
    public static int PORT_PLAIN_TCP = 28882;
    public static int PORT_WEBSOCKET = 28883;
    
    private TrayIcon trayIcon;
    private ConnectionManager conMan;
    private Configuration conf;
    private int lastTimeRegisteredHotkeys = 0;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, AWTException {
        new JHotkeyServer();
    }

    public JHotkeyServer() throws IOException, AWTException {
        JIntellitype.getInstance().addHotKeyListener(this);
        System.out.println("JIntellitype.dll successfully loaded.");
        
        this.conf = new Configuration(new File("conf.xml"));
        this.conf.load();
        
        SystemTray tray = SystemTray.getSystemTray();
        this.trayIcon = new TrayIcon(ImageIO.read(new File("S:\\EIGENE DATEIEN\\Eigene Entwicklung\\JAVA SE\\JHotkeyServer\\addon_icon64x64.png")));
        this.trayIcon.setImageAutoSize(true);
        
        PopupMenu popup = new PopupMenu();
        MenuItem mIQuit = new MenuItem("Quit");
        mIQuit.addActionListener(new ActionListener() {
            private JHotkeyServer parent;
            
            public ActionListener init(JHotkeyServer parent) {
                this.parent = parent;
                
                return this;
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                this.parent.exit();
            }
        }.init(this));
        
        MenuItem mIHotkeys = new MenuItem("Edit hotkey bindings");
        mIHotkeys.addActionListener(new ActionListener() {
            private JHotkeyServer parent;
            
            public ActionListener init(JHotkeyServer parent) {
                this.parent = parent;
                
                return this;
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                HotkeyEditorFrame.show(this.parent);
            }
        }.init(this));        
        
        popup.add(mIHotkeys);
        popup.addSeparator();
        popup.add(mIQuit);
        
        this.trayIcon.setPopupMenu(popup);
        tray.add(this.trayIcon);
        
        this.registerHotkeys();
        
        this.conMan = new ConnectionManager(PORT_PLAIN_TCP, PORT_WEBSOCKET, this);
    }
    
    public boolean isNameRegisteredHotkey(String name) {
        return this.conf.isNameRegisteredHotkey(name);
    }
    
    public void refreshBindings() {
        //Unregsiter all current hotkeys
        for (int i = 0; i <= this.lastTimeRegisteredHotkeys; i++) {
            JIntellitype.getInstance().unregisterHotKey(i);
        }
        
        this.registerHotkeys();
    }
    
    private void registerHotkeys() {      
        int i = 0;
        while (i < this.conf.getHotkeys().size()) {
            HotkeyEntry entry = this.conf.getHotkeys().get(i);
            this.registerHotkey(i, entry.getName(), entry.getCombination());
            
            i++;
        }
        
        this.lastTimeRegisteredHotkeys = i;
    }
    
    private void registerHotkey(int id, String name, String modifierAndKeyCode) {
        JIntellitype.getInstance().registerHotKey(id, modifierAndKeyCode);
    }

    private void addNewHotkey(String name, String modifierAndKeyCode, boolean trayPopup) {
        this.conf.addHotkeyBinding(new HotkeyEntry(name, modifierAndKeyCode, trayPopup));
        
        this.registerHotkey(this.conf.getHotkeys().size(), name, modifierAndKeyCode);
    }
    
    public void requestForNewHotkey(String key) {
        
    }
    
    @Override
    public void onHotKey(int i) {
        HotkeyEntry hotkey = this.conf.getHotkeys().get(i);
        
        this.conMan.propagateHotkeyPressed(hotkey.getName());
        System.out.println(hotkey.getName() + " pressed!");
        
        if (hotkey.isTrayPopup()) {
            this.trayIcon.displayMessage("Hotkey pressed!", "\"" + hotkey.getName() + "\" has been pressed!", TrayIcon.MessageType.INFO);
        }
    }
 
    public void exit() {
        JIntellitype.getInstance().cleanUp();

        try {
            this.conf.write();
        } catch (Exception ex) {
            System.out.println(ex);
        }

        System.exit(0);        
    }
    
    public Configuration getConf() {
        return this.conf;
    }
}
