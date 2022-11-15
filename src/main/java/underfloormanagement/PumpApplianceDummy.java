/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package underfloormanagement;

import java.io.IOException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author lenne
 */
public class PumpApplianceDummy extends PumpAppliance {

    private boolean dummyState;
    private static final Logger logger = LogManager.getLogger(PumpApplianceDummy.class);

    public PumpApplianceDummy(NoNc relaisType, long overrunMinutes, ReporterDecider reporter)
            throws IOException, InterruptedException {
        super(relaisType, overrunMinutes, reporter);
    }

    @Override
    protected void InitPump() throws IOException, InterruptedException {
        logger.info("Raspberry Pi Model dummy");
    }

    @Override
    public void Switch(ApplianceState state) throws IOException, InterruptedException {
        if ((state == ApplianceState.on && relaisType == NoNc.nc)
                || (state == ApplianceState.off && relaisType == NoNc.no)) {
            dummyState = true;
        } else {
            dummyState = false;
        }

        logger.info("State of dummy: " + dummyState);

    }

}
