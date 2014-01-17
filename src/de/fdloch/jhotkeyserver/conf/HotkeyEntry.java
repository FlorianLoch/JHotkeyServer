/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.fdloch.jhotkeyserver.conf;

/**
 *
 * @author Florian
 */
public class HotkeyEntry {
    
    private String name;
    private String combination;
    private boolean trayPopup;

    public HotkeyEntry(String name, String combination, boolean trayPopup) {
        this.name = name;
        this.combination = combination;
        this.trayPopup = trayPopup;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the combination
     */
    public String getCombination() {
        return combination;
    }

    /**
     * @param combination the combination to set
     */
    public void setCombination(String combination) {
        this.combination = combination;
    }

    /**
     * @return the trayPopup
     */
    public boolean isTrayPopup() {
        return trayPopup;
    }

    /**
     * @param trayPopup the trayPopup to set
     */
    public void setTrayPopup(boolean trayPopup) {
        this.trayPopup = trayPopup;
    }
    
}
