/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.fdloch.jhotkeyserver;

/**
 *
 * @author Florian
 */
public class KeyValue<K, V> {
    private K key;
    private V value;

    public KeyValue(K key, V value) {
        this.key = key;
        this.value = value;
    }
    
    public K getKey() {
        return this.key;
    }
    
    public V getValue() {
        return this.value;
    }
    
    public void setKey(K key) {
        this.key = key;
    }
    
    public void setValue(V value) {
        this.value = value;
    }
}
