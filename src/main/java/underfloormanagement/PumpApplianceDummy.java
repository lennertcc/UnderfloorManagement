/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package underfloormanagement;

import java.io.IOException;

/**
 *
 * @author lenne
 */
public class PumpApplianceDummy extends PumpAppliance {
    
    private boolean dummyState;
    
    public PumpApplianceDummy(NoNc relaisType, long overrunMinutes) throws IOException, InterruptedException
    {
          super(relaisType, overrunMinutes);
    }
    
    @Override
    protected void InitPump() throws IOException, InterruptedException {
        UnderfloorManagement.logInfo("Raspberry Pi Model dummy");
    }
    
    @Override
    public void Switch(ApplianceState state) throws IOException, InterruptedException {
                if ((state == ApplianceState.on && relaisType == NoNc.nc)
                || (state == ApplianceState.off && relaisType == NoNc.no)) {
            dummyState = true;
        } else {
            dummyState = false;
        }

        UnderfloorManagement.logInfo("State of dummy: " + dummyState);

    }
    
}
