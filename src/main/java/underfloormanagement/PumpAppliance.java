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

    public void StopPump() {
        this.pumpRunningUntil = LocalDateTime.now();
    }
    
    public void extendThen() {
        pumpRunningUntil = LocalDateTime.now().plus(Duration.ofMinutes(this.overrunMinutes));
        System.out.println("PumpAppliance running extended until: " + pumpRunningUntil.toString());
        System.out.println();
    }

    @Override
    public void run() {
        System.out.println("PumpAppliance start running");

        this.extendThen();

        try {
            this.Switch(PumpAppliance.ApplianceState.on);
        } catch (IOException ex) {
            Logger.getLogger(UnderfloorManagement.class.getName()).log(Level.SEVERE, null, ex);
            return;
        } catch (InterruptedException ex) {
            Logger.getLogger(UnderfloorManagement.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        while (pumpRunningUntil.isAfter(LocalDateTime.now())) {
            UnderfloorManagement.Sleep(1000);
        }

        System.out.println("PumpAppliance stopping " + LocalDateTime.now().toString());

        try {
            this.Switch(PumpAppliance.ApplianceState.off);
        } catch (IOException ex) {
            Logger.getLogger(UnderfloorManagement.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(UnderfloorManagement.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("PumpAppliance stopped");
        System.out.println();
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

        
        System.out.println("Raspberry Pi Model " + boardName + " Revision " + boardRevision);

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

        System.out.println("State of Pin 4:" + gpio.getState(pin4));
    }

}
