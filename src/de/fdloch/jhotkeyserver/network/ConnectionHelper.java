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
public class ConnectionHelper {
    
    public static void processCommand(String cmd, Connection conn, ConnectionManager parent) {
        String inp = cmd.toLowerCase();
        
        //If no authentification needed automaitcally set to authentificated state
        if (!conn.isAuthorized() && parent.getHotkeyServer().getConf().getHashOfPw() == null) {
            conn.setAuthorizedTrue();
        }
        
        if (!conn.isAuthorized()) {
            if (inp.startsWith("HELO") && inp.split(" ").length == 2) {
                if (parent.getHotkeyServer().getConf().getHashOfPw().equals(inp.split(" ")[1])) {
                    conn.setAuthorizedTrue();
                }
                else {
                    conn.closeConnection("Not authentificated.");
                    return;
                }
            }
            else {
                conn.closeConnection("Not authentificated.");
                return;
            }
        }
        
        if (inp.startsWith("register")) {
            String[] hotkeys = inp.substring(inp.indexOf(' ') + 1).split(",");
            String notRegisteredHotkeys = "";
            
            for (String hotkey : hotkeys) {
                if (parent.getHotkeyServer().isNameRegisteredHotkey(hotkey)) {
                    conn.registerNewHotkey(hotkey);
                }
                else {
                    notRegisteredHotkeys += (notRegisteredHotkeys.equals("") == true ? hotkey : ("," + hotkey));
                }
            }
            
            sendAcknowledgement(conn, notRegisteredHotkeys);
            
            System.out.println("REGISTER command from " + conn.getRemoteAdress() + " received and processed: " + inp);
        }
        else {
            conn.sendString("COMMAND '" + inp + "' UNKNOWN!\n");
        }        
    }
    
    public static void sendHotkeyNotificationCommand(String hotkey, Connection conn) {
        conn.sendString("NOTIFICATION " + hotkey + " PRESSED!\n");
    }
    
    public static void sendAcknowledgement(Connection conn, String notRegisteredHotkeys) {
        if (notRegisteredHotkeys.equals("")) {
            conn.sendString("REGISTERED\n");
        }
        else {
            conn.sendString("REGISTERED BUT " + notRegisteredHotkeys + " IS/ARE UNKNOWN!\n");
        }
    }
    
}
