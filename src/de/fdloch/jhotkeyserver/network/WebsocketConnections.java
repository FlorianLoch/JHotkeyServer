/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.fdloch.jhotkeyserver.network;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import org.java_websocket.WebSocket;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 *
 * @author Florian
 */
public class WebsocketConnections extends WebSocketServer implements Connection {
    private ConnectionManager parent;
    private ArrayList<WebsocketConnData> conns;
    private WebsocketConnData currentWCD;
    
    public WebsocketConnections(int port, ConnectionManager parent) throws UnknownHostException {
        super(new InetSocketAddress(port));
        this.conns = new ArrayList<WebsocketConnData>();
        this.parent = parent;
        this.parent.addConnection(this);
    }
    
    @Override
    public void onOpen(WebSocket ws, ClientHandshake ch) {
        if ((this.parent.getHotkeyServer().getConf().isLocalConnectiosOnly() && (ws.getRemoteSocketAddress().getAddress().isAnyLocalAddress() || ws.getRemoteSocketAddress().getAddress().isLoopbackAddress())) || (!this.parent.getHotkeyServer().getConf().isLocalConnectiosOnly())) {
            this.conns.add(new WebsocketConnData(ws));
            System.out.println("New Websocket-Connection from " + ws.getRemoteSocketAddress() + ".");
        }
        else {
            ws.close(CloseFrame.REFUSE);
            System.out.println("Rejected connection due to accept-only-local-connections policy.");
        }        
    }

    @Override
    public void onClose(WebSocket ws, int i, String string, boolean bln) {
        for (WebsocketConnData wCD : this.conns) {
            if (wCD.getWebSocket().equals(ws)) {
                this.conns.remove(wCD);
                break;
            }
        }
        
        System.out.println("Websocket-Connection from " + ws.getRemoteSocketAddress() + " has been closed.");
    }

    @Override
    public void onMessage(WebSocket ws, String string) {
        this.currentWCD = this.getWCDForWS(ws);
        ConnectionHelper.processCommand(string, this, this.parent);
    }
    
    private WebsocketConnData getWCDForWS(WebSocket ws) {
        for (WebsocketConnData wCD : this.conns) {
            if (wCD.getWebSocket().equals(ws)) {  
                return wCD;
            }      
        }
        return null;
    }
    
    @Override
    public void onError(WebSocket ws, Exception excptn) {
        System.out.println("An Exception occured during communication with " + ws.getRemoteSocketAddress() + ": " + excptn);
    }

    @Override
    public boolean isListeningToHotkey(String hotkey) {
        return true; //Real check will be done by "sendHotkeyNotification()" because this Connection actually multiplexes all Websocket-Connections
    }

    @Override
    public void sendHotkeyNotification(String hotkey) {
        for (WebsocketConnData wCD : this.conns) {
            if (wCD.containsHotkey(hotkey)) {
                this.currentWCD = wCD;
                ConnectionHelper.sendHotkeyNotificationCommand(hotkey, this);
            }
        }
    }

    @Override
    public void sendString(String str) {
        this.currentWCD.getWebSocket().send(str);
    }

    @Override
    public void registerNewHotkey(String hotkey) {
        this.currentWCD.addHotkey(hotkey);
    }

    @Override
    public String getRemoteAdress() {
        return this.currentWCD.getWebSocket().getRemoteSocketAddress().toString();
    }

    @Override
    public boolean isAuthorized() {
        return this.currentWCD.getAuthorized();
    }

    @Override
    public void setAuthorizedTrue() {
        this.currentWCD.setAuthorized(true);
    }

    @Override
    public void closeConnection(String reason) {
        this.conns.remove(this.currentWCD);
        this.sendString(reason);
        this.currentWCD.getWebSocket().close(CloseFrame.REFUSE);
    }
    
    private class WebsocketConnData {
        private WebSocket ws;
        private ArrayList<String> hotkeys;
        private boolean authorized = false;

        public WebsocketConnData(WebSocket ws) {
            this.ws = ws;
            this.hotkeys = new ArrayList<String>();
        }
        
        public void addHotkey(String hotkey) {
            this.hotkeys.add(hotkey.toLowerCase());
        }
        
        public WebSocket getWebSocket() {
            return this.ws;
        }
        
        public boolean containsHotkey(String hotkey) {
            hotkey = hotkey.toLowerCase();
            
            for (String str : this.hotkeys) {
                if (str.equals(hotkey)) {
                    return true;
                }
            }
            
            return false;
        }
        
        public boolean getAuthorized() {
            return this.authorized;
        }
        
        public void setAuthorized(boolean val) {
            this.authorized = val;
        }
    }
}
