/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.fdloch.jhotkeyserver.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author Florian
 */
public class PlainTCPConnection extends EventSocket implements Connection {
    private HashSet<String> registeredHotkeys;
    private ConnectionManager parent;
    private boolean authorized = false;
    
    public PlainTCPConnection(Socket sckt, ConnectionManager parent) throws IOException {
        super(sckt);
        this.parent = parent;
        this.registeredHotkeys = new HashSet<String>();
    }
    
    @Override
    public boolean isListeningToHotkey(String hotkey) {
        Iterator<String> itrtr = this.registeredHotkeys.iterator();
        
        while (itrtr.hasNext()) {
            if (itrtr.next().equals(hotkey)) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public void sendHotkeyNotification(String hotkey) {
        ConnectionHelper.sendHotkeyNotificationCommand(hotkey, this);
    }

    @Override
    public void onInputReceived(InputStream in) {
        BufferedReader bR = new BufferedReader(new InputStreamReader(in));
        String inp = "ERROR";
        try {
            inp = bR.readLine().toLowerCase();
        } catch (IOException ex) {
            System.out.println("ERROR reading line from network stream");
        }
        
        ConnectionHelper.processCommand(inp, this, parent);
    }

    @Override
    public String getRemoteAdress() {
        return this.sckt.getRemoteSocketAddress().toString();
    }

    @Override
    public void registerNewHotkey(String hotkey) {
        this.registeredHotkeys.add(hotkey);
    }

    @Override
    public boolean isAuthorized() {
       return this.authorized;
    }

    @Override
    public void setAuthorizedTrue() {
        this.authorized = true;
    }

    @Override
    public void closeConnection(String reason) {
        this.sendString(reason);
        this.parent.removeConnection(this);
        try {
            this.sckt.close();
        } catch (IOException ex) {
            //
        }
    }
    
}
