/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.fdloch.jhotkeyserver.conf;

import de.fdloch.jhotkeyserver.util.SHA1;
import de.fdloch.jsimplexml.xml.XMLNode;
import de.fdloch.jsimplexml.xml.XMLParser;
import de.fdloch.jsimplexml.xml.XMLUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Florian
 */
public class Configuration {
    private File confFile;
    private boolean fileLoaded = false;
    private XMLNode rootNode = null;
    
    public Configuration(File confFile) throws FileNotFoundException {
        if (confFile.exists()) {
            this.confFile = confFile;
        }
        else {
            this.confFile = null;
            throw new FileNotFoundException();
        }
    }
    
    public boolean load() throws IOException {
        if (this.confFile == null) {
            return false;
        }
        
        XMLParser parser = new XMLParser(confFile);
        try {
            this.rootNode = parser.parseXML();
        } catch (Exception ex) {
            System.out.println("XMLParser could not process the configuration file: " + ex);
            return false;
        }
        
        this.fileLoaded = true;
        return true;
    }
    
    public boolean write() throws IOException {
        if (!this.fileLoaded) {
            return false;
        }
        
        XMLUtils.writeAsXMLToFile(this.rootNode, this.confFile, "\t");
        
        return true;
    }
    
    public boolean isLocalConnectiosOnly() {
        try {
            XMLNode node = this.rootNode.getChildNodesByType("local-only")[0];
            
            return !node.getValue().equalsIgnoreCase("false"); //In case it is neither "true" nor "false" it is consired to be more safe to block remote connections
        }
        catch (Exception ex) {
            return true; //See comment above
        }
    }
    
    //Returns null if there is no passwort or if an error due to corrupted xml file happened
    public String getHashOfPw(String nonce) {
        try {
            XMLNode node = this.rootNode.getChildNodesByType("passcode")[0];
            System.out.println("Raw: " + node.getValue() + nonce);
            return SHA1.makeHash(node.getValue() + nonce);
        } catch (Exception ex) {
            try {
                System.out.println("Raw: " + nonce);
                return SHA1.makeHash(nonce);
            } catch (Exception ex1) {
                return null;
            }
        }
    }
    
    public ArrayList<KeyValue<String, String>> getHotkeys() {
        try {
            ArrayList<KeyValue<String, String>> res = new ArrayList<KeyValue<String, String>>();
            XMLNode hotkeysNode = this.rootNode.getChildNodesByType("hotkeys")[0];
            XMLNode[] hotkeyNodes = hotkeysNode.getChildNodesByType("hotkey");
            
            for (XMLNode node : hotkeyNodes) {
                XMLNode name = node.getChildNodesByType("name")[0];
                XMLNode combination = node.getChildNodesByType("comb")[0];
                
                res.add(new KeyValue<String, String>(name.getValue(), combination.getValue()));
            }
            
            return res;
        } catch (Exception ex) {
            return null;
        }
    }
    
    public boolean updateHotkeyBinding(String key, KeyValue<String, String> kV) {
        if (!this.fileLoaded) {
            return false;
        }
        
        try {
            XMLNode hotkeysNode = this.rootNode.getChildNodesByType("hotkeys")[0];
            XMLNode[] hotkeyNodes = hotkeysNode.getChildNodesByType("hotkey");
            
            for (XMLNode node : hotkeyNodes) {
                XMLNode name = node.getChildNodesByType("name")[0];
                if (name.getValue().equalsIgnoreCase(key)) {
                    name.setValue(kV.getKey());
                    node.getChildNodesByType("comb")[0].setValue(kV.getValue());
                    
                    return true;
                }
            }
            
            return false;
        } catch (Exception ex) {
            return false;
        }     
    }
    
    public boolean addHotkeyBinding(KeyValue<String, String> kV) {
        if (!this.fileLoaded) {
            return false;
        }
        
        try {
            XMLNode hotkeysNode = this.rootNode.getChildNodesByType("hotkeys")[0];
            XMLNode newBinding  = new XMLNode("hotkey");
            newBinding.addChildNode(new XMLNode("name", kV.getKey()));
            newBinding.addChildNode(new XMLNode("comb", kV.getValue()));
            hotkeysNode.addChildNode(newBinding);
        } catch (Exception ex) {
            return false;
        }
        
        return true;
    }
    
    public boolean removeHotkeyBinding(String key) {
        if (!this.fileLoaded) {
            return false;
        }     
            
        try {
            XMLNode hotkeysNode = this.rootNode.getChildNodesByType("hotkeys")[0];
            XMLNode[] hotkeys = hotkeysNode.getChildNodesByType("hotkey");
            
            for (XMLNode node : hotkeys) {
                if (node.getChildNodesByType("name")[0].getValue().equalsIgnoreCase(key)) {
                    hotkeysNode.removeChild(node);
                    return true;
                }
            }
            
            return false;
        } catch (Exception ex) {
            return false;
        }
    } 
    
    public boolean isDispalyMessageActiveForKey(String key) {
        if (!this.fileLoaded) {
            return false;
        }     
            
        try {
            XMLNode hotkeysNode = this.rootNode.getChildNodesByType("hotkeys")[0];
            XMLNode[] hotkeys = hotkeysNode.getChildNodesByType("hotkey");
            
            for (XMLNode node : hotkeys) {
                if (node.getChildNodesByType("name")[0].getValue().equalsIgnoreCase(key)) {
                    if (node.getParameter("tray-popup").equalsIgnoreCase("true")) {
                        return true;
                    }
                    else {
                        return false;
                    }
                }
            }
            
            return false;
        } catch (Exception ex) {
            return false;
        }        
    }
}
