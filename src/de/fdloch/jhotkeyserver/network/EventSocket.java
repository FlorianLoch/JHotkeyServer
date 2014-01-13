/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.fdloch.jhotkeyserver.network;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author Florian
 */
public abstract class EventSocket {
    protected Socket sckt;
    protected PrintWriter outputPrinter;
    
    public EventSocket(String host, int port) throws UnknownHostException, IOException {
        this(new Socket(host, port));
    }
    
    public EventSocket(Socket sckt) throws IOException {
        this.sckt = sckt;
        this.outputPrinter = new PrintWriter(this.sckt.getOutputStream());
        ListenThread t = new ListenThread(this);
        t.start();        
    }
    
    public void sendString(String str) {
        this.outputPrinter.write(str);
        this.outputPrinter.flush();
    }
    
    public abstract void onInputReceived(InputStream in);
    
    public void onInputError(Throwable ex) {
        System.out.println("ERROR in " + this.hashCode() + " (Class " + this.getClass().getCanonicalName() + "): " + ex.toString());
    }
    
    private class ListenThread extends Thread {
        private EventSocket parent;
        private InputStream in;
        
        public ListenThread(EventSocket parent) throws IOException {
            this.parent = parent;
            this.setDaemon(true);
       
            try {
                this.in = this.parent.sckt.getInputStream();
            } catch (IOException ex) {
                throw(new IOException("Could not open InputStream.", ex));
            }
        }
        
        @Override
        public void run() {
            while (this.parent.sckt.isConnected() && !this.parent.sckt.isInputShutdown()) {
                try {
                    if (this.in.available() > 0) {
                        this.parent.onInputReceived(this.in);
                    }
                    
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        System.out.println("ListenThread " + this.getId() + "was interrupted during sleep.");
                    }
                } catch (IOException ex) {
                    this.parent.onInputError(ex);
                }
            }
        }
        
    }
}
