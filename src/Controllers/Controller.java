/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

/**
 *
 * @author bernardoct
 */
public abstract class Controller {
    
    int ID;

    public Controller(int ID) {
        this.ID = ID;
    }
    
    
    
    /**
     *
     * @param v
     * @return
     */
    public abstract double getNewSetValue(double v);
    
}
