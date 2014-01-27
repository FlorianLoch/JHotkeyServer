/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.fdloch.jhotkeyserver.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.ArrayList;

/**
 *
 * @author Florian
 */
public class ListLayout implements LayoutManager {

    private ArrayList<Component> comps;
    private int leftMargin;
    private int topMargin;
    private int vGap;

    public ListLayout(int leftMargin, int topMargin, int vGap) {
        this.comps = new ArrayList<Component>();
        this.leftMargin = leftMargin;
        this.topMargin = topMargin;
        this.vGap = vGap;
    }
    
    @Override
    public void addLayoutComponent(String name, Component comp) {}

    @Override
    public void removeLayoutComponent(Component comp) {}

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        int prefHeight = this.topMargin;
        int maxWidth = 0;
        
        for (Component com : parent.getComponents()) {
            prefHeight += com.getPreferredSize().height;
            
            if (com.getPreferredSize().width > maxWidth) {
                maxWidth = com.getPreferredSize().width;
            }
        }
        
        prefHeight += this.vGap * (parent.getComponentCount() - 1);
        
        return new Dimension(maxWidth, prefHeight);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return this.preferredLayoutSize(parent);
    }

    @Override
    public void layoutContainer(Container parent) {
        int heightOffset = this.topMargin;
        
        for (Component com : parent.getComponents()) {
            Dimension d = com.getPreferredSize();
            
            com.setBounds(this.leftMargin, heightOffset, d.width, d.height);
            
            heightOffset = heightOffset +  com.getHeight() + this.vGap;
        }
    }
    
}
