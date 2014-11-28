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
public class ElementsWithSameIDException extends Exception {

    /**
     *
     */
    public ElementsWithSameIDException() {
        System.out.println("There are two elements of the same time with the "
                + "same ID");
    }

    /**
     *
     * @param string
     */
    public ElementsWithSameIDException(String string) {
        super(string);
    }
    
    
    
}
