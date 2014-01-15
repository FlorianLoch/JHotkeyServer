/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.fdloch.jhotkeyserver.network;

import de.fdloch.jhotkeyserver.SHA1;

/**
 *
 * @author Florian
 */
public class ConnectionHelper {
    
    public static void processCommand(String cmd, Connection conn, ConnectionManager parent) {
        String inp = cmd.toLowerCase();
        
        if (!conn.isAuthorized()) {
            String nonce = conn.getNonce();
            String received = "";
            
            if (inp.startsWith("helo") && inp.split(" ").length == 2) {
                received = inp.split(" ")[1];
            }
            else {
                conn.closeConnection("Not authentificated.");
                return;
            }
            
            if (parent.getHotkeyServer().getConf().getHashOfPw(nonce).equalsIgnoreCase(received)) {
                conn.setAuthorizedTrue();
                conn.sendString("AUTH OK");
            }
            else {
                System.out.println("Received:" + received);
                System.out.println("Expected:" + parent.getHotkeyServer().getConf().getHashOfPw(nonce));
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
        else if (!inp.startsWith("helo")) {
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
    
    public static String sendNonce(Connection conn) {
        String nonce = generateNonce();
        
        conn.sendString("HELO " + nonce);
        
        return nonce;
    }
        
    private static String generateNonce() {
        return "tmp";
    }
    
}
