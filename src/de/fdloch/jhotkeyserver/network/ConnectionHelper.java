/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.fdloch.jhotkeyserver.network;

import de.fdloch.jhotkeyserver.util.SHA1;

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
        
        if (inp.startsWith("subscribe")) {
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
            
            System.out.println("SUBSCRIBE command from " + conn.getRemoteAdress() + " received and processed: " + inp);
        }
        else if (inp.startsWith("register")) {
            String[] blocks = inp.split(" ");
            String requester = blocks[1].substring(1, blocks[1].length() - 2);
            String[] hotkeys = blocks[2].split(",");
            
            for (String hotkey : hotkeys) {
                conn.registerNewHotkey(hotkey); //Register these hotkeys for the connection - if the user does not accept the hotkey-request they will not be propagated to it does not matter whether the hotkeys are listed in the connection or not
                
                parent.getHotkeyServer().requestForNewHotkey(hotkey);
            }
            
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
