/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package underfloormanagement;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.system.SystemInfo;
import com.pi4j.wiringpi.Gpio;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lenne
 */
public class PumpAppliance implements Runnable {

    protected static final Logger logger = Logger.getLogger(UnderfloorManagement.class.getName());
    LocalDateTime pumpRunningUntil = LocalDateTime.now();
    Thread pumpThread = null;
    
    
    public void StartOrExtendPump() {
        if (pumpThread != null && pumpThread.isAlive()) {
            this.extendThen();
        } else {
            pumpThread = new Thread(this);
            pumpThread.start();
        }
    }

    public void extendThen() {
        pumpRunningUntil = LocalDateTime.now().plus(Duration.ofMinutes(this.overrunMinutes));
        logger.info("PumpAppliance running extended until: " + pumpRunningUntil.toString());
    }

    @Override
    public void run() {
        logger.info("PumpAppliance start running");

        this.extendThen();

        try {
            this.Switch(PumpAppliance.ApplianceState.on);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
            return;
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, null, ex);
            return;
        }

        while (pumpRunningUntil.isAfter(LocalDateTime.now())) {
            UnderfloorManagement.Sleep(5000);
            logger.info("PumpAppliance afterburn until " + pumpRunningUntil.toString());
        }

        logger.info("PumpAppliance stopping " + LocalDateTime.now().toString());

        try {
            this.Switch(PumpAppliance.ApplianceState.off);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        logger.info("PumpAppliance stopped");
    }

    enum ApplianceState {
        on,
        off
    }

    enum NoNc {
        no,
        nc
    }

    protected final NoNc relaisType;
    private long overrunMinutes;
    private GpioPinDigitalOutput pin4;
    private GpioController gpio;

    /**
     * Instantiates the GPIO to control the pump
     *
     * @param relaisType
     * @param overrunMinutes
     * @throws IOException
     * @throws InterruptedException
     * @throws UnsupportedOperationException
     */
    public PumpAppliance(NoNc relaisType, long overrunMinutes) throws IOException, InterruptedException, UnsupportedOperationException {
        this.relaisType = relaisType;
        this.overrunMinutes = overrunMinutes;
        InitPump();
    }

    protected void InitPump() throws IOException, InterruptedException {
        SystemInfo.BoardType piBoardType;
        piBoardType = SystemInfo.getBoardType();

        String boardName = piBoardType.name();
        String boardRevision = String.valueOf(Gpio.piBoardRev());
        
        gpio = GpioFactory.getInstance();
        pin4 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07);
        logger.info("Raspberry Pi Model " + boardName + " Revision " + boardRevision);
        
        System.out.println("Pump off");
        Switch(ApplianceState.off);
    }

    /**
     * Switches the pump appliance on or off based on parameter state
     *
     * @param state
     * @throws IOException
     * @throws InterruptedException
     */
    public void Switch(ApplianceState state) throws IOException, InterruptedException {

        if ((state == ApplianceState.on && relaisType == NoNc.no)
                || (state == ApplianceState.off && relaisType == NoNc.nc)) {
            gpio.setState(PinState.HIGH, pin4);
        } else {
            gpio.setState(PinState.LOW, pin4);
        }

        System.out.println("State of Pin 4: " + gpio.getState(pin4));
    }

}
