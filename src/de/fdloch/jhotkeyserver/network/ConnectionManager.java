/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.fdloch.jhotkeyserver.network;

import de.fdloch.jhotkeyserver.JHotkeyServer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Florian
 */
public class ConnectionManager {
    private ServerSocket ssckt;
    private PlainTCPConnectingThread cT;
    private WebsocketConnections wSC;
    private ArrayList<Connection> conns;
    private JHotkeyServer parent;
    
    public ConnectionManager(int plainTcpPort, int webSocketPort, JHotkeyServer parent) throws IOException {
        this.conns = new ArrayList<Connection>();
        this.ssckt = new ServerSocket(plainTcpPort);
        this.parent = parent;
        
        this.cT = new PlainTCPConnectingThread(this.ssckt, this);
        this.cT.start();
        
        this.wSC = new WebsocketConnections(webSocketPort, this);
        this.wSC.start();
    }
    
    public JHotkeyServer getHotkeyServer() {
        return this.parent;
    }
    
    public void propagateHotkeyPressed(String hotkey) {
        for (Connection con : this.conns) {
            if (con.isListeningToHotkey(hotkey)) {
                con.sendHotkeyNotification(hotkey);
            }
        }
    }
    
    public void addConnection(Connection con) {
        this.conns.add(con);
        //System.out.println("New connection from " + sckt.getRemoteSocketAddress() + " accepted.");
    }
    
    public void removeConnection(Connection con) {
        this.conns.remove(con);
    }
    
    private class PlainTCPConnectingThread extends Thread {
        private boolean run;
        private ServerSocket ssckt;
        private ConnectionManager conMan;

        public PlainTCPConnectingThread(ServerSocket ssckt, ConnectionManager conMan) {
            this.setDaemon(true);
            this.run = true;
            this.ssckt = ssckt;
            this.conMan = conMan;
        }
        
        public synchronized void stopThread() {
            this.run = false;
        }
        
        private synchronized boolean isRunning() {
            return this.run;
        }
        
        @Override
        public void run() {
            while (this.isRunning()) {
                Socket sckt;
                try {
                    sckt = ssckt.accept();
                    //Check if only local connections are allowed and whether this is a local one or not 
                    if ((this.conMan.getHotkeyServer().getConf().isLocalConnectiosOnly() && (sckt.getInetAddress().isAnyLocalAddress() || sckt.getInetAddress().isLoopbackAddress())) || (!this.conMan.getHotkeyServer().getConf().isLocalConnectiosOnly())) {
                        this.conMan.addConnection(new PlainTCPConnection(sckt, conMan));
                    }
                    else {
                        System.out.println("Rejected connection due to accept-only-local-connections policy.");
                        sckt.close();
                    }
                } catch (IOException ex) {
                    System.out.println("ServerSocket could not accept new connection! " + ex.toString());
                }
            }
        }
    }
}
