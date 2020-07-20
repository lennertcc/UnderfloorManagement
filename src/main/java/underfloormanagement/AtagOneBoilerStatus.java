/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package underfloormanagement;

import java.util.EnumSet;

/**
 *
 * @author lenne
 */
public class AtagOneBoilerStatus {
    
    
    public enum BoilerStatus {
        Unknown1 (1<<0), 
        Unknown2 (1<<1), //chHeating
        Unknown4 (1<<2), //dhwHeating 
        Unknown8 (1<<3), //boilerHeating 
        Unknown16 (1<<4), 
        Unknown32 (1<<5), 
        Unknown64 (1<<6), 
        Unknown128 (1<<7), 
        Unknown256 (1<<8), 
        Unknown512 (1<<9);

        public final int weight;

        BoilerStatus(int weight) {
            this.weight = weight;
        }

        //public static final EnumSet<BoilerStatus> ALL_FLAGS = EnumSet.allOf(BoilerStatus.class);
    }
    
    private final int originalInt;
    private final EnumSet<BoilerStatus> currentBoilerStatus = EnumSet.noneOf(BoilerStatus.class);


    
    public AtagOneBoilerStatus(int currentStatus)
    {
        originalInt = currentStatus;
        for (BoilerStatus status : BoilerStatus.values()) {
            if ((currentStatus & status.weight) != 0) {
                currentBoilerStatus.add(status);
            }
        }
    }
    
    public EnumSet<BoilerStatus> getBoilerStatus() 
    {
        return currentBoilerStatus;
    }

    @Override
    public String toString() {
        return currentBoilerStatus.toString();
    }

}
