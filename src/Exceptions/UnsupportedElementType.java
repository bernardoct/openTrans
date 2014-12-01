/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Exceptions;

/**
 *
 * @author bernardoct
 */
public class UnsupportedElementType extends Exception{

    public UnsupportedElementType(String getClassElType) {
        super("Unsupported " + getClassElType);
    }
    
    
    
}
