/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.fdloch.jhotkeyserver.network;

/**
 *
 * @author Florian
 */
public interface Connection {
    
    public boolean isListeningToHotkey(String hotkey);
    
    public void sendHotkeyNotification(String hotkey);
    
    public void sendString(String str);
    
    public void registerNewHotkey(String hotkey);
    
    public String getRemoteAdress();
    
    public boolean isAuthorized();
    
    public void setAuthorizedTrue();
    
    public void closeConnection(String reason);
    
    public String getNonce();
    
}
