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
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class PumpAppliance implements Runnable {

    private static final Logger logger = LogManager.getLogger(PumpAppliance.class);

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
        logger.info("PumpAppliance running extended until: " + pumpRunningUntil.toString());
        reporterDecider.reportPumpRunning(true);
    }

    @Override
    public void run() {
        logger.info("PumpAppliance start running");

        this.extendThen();

        try {
            this.Switch(PumpAppliance.ApplianceState.on);
        } catch (IOException ex) {
            logger.error(ex);
            return;
        } catch (InterruptedException ex) {
            logger.error(ex);
            return;
        }

        while (pumpRunningUntil.isAfter(LocalDateTime.now())) {
            UnderfloorManagement.Sleep(1000);
        }
        reporterDecider.reportPumpRunning(true);

        logger.info("PumpAppliance stopping " + LocalDateTime.now().toString());

        try {
            this.Switch(PumpAppliance.ApplianceState.off);
        } catch (IOException ex) {
            logger.error(ex);
        } catch (InterruptedException ex) {
            logger.error(ex);
        }

        reporterDecider.reportPumpRunning(false);
        logger.info("PumpAppliance stopped");
        logger.info("");
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
    private GpioPinDigitalOutput pin;
    private GpioController gpio;

    ReporterDecider reporterDecider;

    /**
     * Instantiates the GPIO to control the pump
     *
     * @param relaisType
     * @param overrunMinutes
     * @throws IOException
     * @throws InterruptedException
     * @throws UnsupportedOperationException
     */
    public PumpAppliance(NoNc relaisType, long overrunMinutes, ReporterDecider reporterDecider)
            throws IOException, InterruptedException, UnsupportedOperationException {
        this.relaisType = relaisType;
        this.overrunMinutes = overrunMinutes;
        this.reporterDecider = reporterDecider;
        InitPump();
    }

    protected void InitPump() throws IOException, InterruptedException {
        SystemInfo.BoardType piBoardType;
        piBoardType = SystemInfo.getBoardType();

        String boardName = piBoardType.name();
        String boardRevision = String.valueOf(Gpio.piBoardRev());

        gpio = GpioFactory.getInstance();
        pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01); // https://pi4j.com/1.2/pins/model-b-plus.html

        logger.info("Raspberry Pi Model " + boardName + " Revision " + boardRevision);
        logger.info("Pump off");
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
            gpio.setState(PinState.HIGH, pin);
        } else {
            gpio.setState(PinState.LOW, pin);
        }

        logger.info("State of Pin:" + gpio.getState(pin));
    }

}
