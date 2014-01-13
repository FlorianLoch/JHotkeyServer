/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.fdloch.jhotkeyserver;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import de.fdloch.jhotkeyserver.network.ConnectionManager;
import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 *
 * @author Florian
 */
public class JHotkeyServer implements HotkeyListener {
    public static int PORT_PLAIN_TCP = 28882;
    public static int PORT_WEBSOCKET = 28883;
    
    private TrayIcon trayIcon;
    private ArrayList<String> hotkeys;
    private ConnectionManager conMan;
    private Configuration conf;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, AWTException {
        new JHotkeyServer();
    }

    public JHotkeyServer() throws IOException, AWTException {
        this.conf = new Configuration(new File("conf.xml"));
        this.conf.load();
        
        SystemTray tray = SystemTray.getSystemTray();
        this.trayIcon = new TrayIcon(ImageIO.read(new File("S:\\EIGENE DATEIEN\\Eigene Entwicklung\\JAVA SE\\JHotkeyServer\\addon_icon64x64.png")));
        this.trayIcon.setImageAutoSize(true);
        tray.add(this.trayIcon);
        
        this.hotkeys = new ArrayList<String>();
        
        JIntellitype.getInstance().addHotKeyListener(this);
        this.registerHotkeys();
        
        this.conMan = new ConnectionManager(PORT_PLAIN_TCP, PORT_WEBSOCKET, this);
    }
    
    public boolean isNameRegisteredHotkey(String name) {
        name = name.toLowerCase();
        
        for (String str : this.hotkeys) {
            if (str.equals(name)) {
                return true;
            }
        }
        
        return false;
    }
    
    private void registerHotkeys() {
        ArrayList<KeyValue<String, String>> hotkeyList = this.conf.getHotkeys();
        
        if (hotkeyList != null) {
            for (KeyValue<String, String> kV : hotkeyList) {
                this.registerHotkey(kV.getKey(), kV.getValue());
            }
        }
    }
    
    private void registerHotkey(String name, String modifierAndKeyCode) {
        JIntellitype.getInstance().registerHotKey(this.hotkeys.size(), modifierAndKeyCode);
        this.hotkeys.add(name.toLowerCase());
    }

    private void addNewHotkey(String name, String modifierAndKeyCode) {
        this.conf.addHotkeyBinding(new KeyValue<String, String>(name, modifierAndKeyCode));
        registerHotkey(name, modifierAndKeyCode);
    }
    
    @Override
    public void onHotKey(int i) {
        String hotkey = this.hotkeys.get(i);
        
        this.conMan.propagateHotkeyPressed(hotkey);
        System.out.println(hotkey + " pressed!");
        
        if (this.conf.isDispalyMessageActiveForKey(hotkey)) {
            this.trayIcon.displayMessage("Hotkey pressed!", "\"" + hotkey + "\" has been pressed!", TrayIcon.MessageType.INFO);
        }
        
        
        if (hotkey.equalsIgnoreCase("quit")) {
            JIntellitype.getInstance().cleanUp();
            
            try {
                this.conf.write();
            } catch (Exception ex) {
                System.out.println(ex);
            }
            
            System.exit(0);
        }
    }
 
    public Configuration getConf() {
        return this.conf;
    }
}
