package underfloormanagement;

import com.pi4j.io.gpio.*;

import underfloormanagement.data.TemperatureReading;

import java.io.*;
import java.nio.file.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class TemperatureSensors {

    String[] sensorFilePath;
    private String temp1FilePath;
    private String temp2FilePath;
    private String temp3FilePath;

    private GpioPinDigitalOutput pin;
    private GpioController gpio;

    private static final Logger logger = LogManager.getLogger(TemperatureSensors.class);

    protected TemperatureSensors() {
    }
    public TemperatureSensors(String temp1FilePath, String temp2FilePath, String temp3FilePath) {
        this.temp1FilePath = temp1FilePath;
        this.temp2FilePath = temp2FilePath;
        this.temp3FilePath = temp3FilePath;

        gpio = GpioFactory.getInstance();
        pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00); // https://pi4j.com/1.2/pins/model-b-plus.html
        gpio.setState(PinState.HIGH, pin);
    }

    public TemperatureReading ReadTemperatures() throws InterruptedException {

        TemperatureReading temperatureReading = new TemperatureReading();
        boolean resetSensors = false;
        try {
            long reading = Long.parseLong(ReadFileLine(Paths.get(temp1FilePath)));
            temperatureReading.setAanvoerTemp(reading);
            if (reading == 0) {
                resetSensors = true;
            }
        } catch (IOException e) {
            logger.error("Cannot read temperature sensor 1", e);
            temperatureReading.setAanvoerTemp(Long.MIN_VALUE);
            resetSensors = true;
        }

        try {
            long reading = Long.parseLong(ReadFileLine(Paths.get(temp2FilePath)));
            temperatureReading.setRetourTemp(reading);
            if (reading == 0) {
                resetSensors = true;
            }
        } catch (IOException e) {
            logger.error("Cannot read temperature sensor 2", e);
            temperatureReading.setRetourTemp(Long.MIN_VALUE);
            resetSensors = true;
        }

        try {
            long reading = Long.parseLong(ReadFileLine(Paths.get(temp3FilePath)));
            temperatureReading.setKastTemp(reading);
            if (reading == 0) {
                resetSensors = true;
            }
        } catch (IOException e) {
            logger.error("Cannot read temperature sensor 3", e);
            temperatureReading.setKastTemp(Long.MIN_VALUE);
            resetSensors = true;
        }

        logger.info(temperatureReading);
        if (resetSensors) {
            logger.error("Temperature sensor lost. Resetting...");

            gpio.setState(PinState.LOW, pin);
            UnderfloorManagement.Sleep(2000);
            gpio.setState(PinState.HIGH, pin);
            UnderfloorManagement.Sleep(2000);

            TemperatureReading testReading = ReadTemperatures();
            if (testReading.getKastTemp() > 0) {
                logger.error("Temperature sensor lost. Reset success");
                return testReading;
            } else {
                logger.error("Temperature sensor lost. Reset failed");
            }
        }

        return temperatureReading;
    }

    private String ReadFileLine(Path path) throws IOException, InterruptedException {
        String s;
        String command = String.format("cat %s", path.toString());
        logger.info(command);
        Process p = Runtime.getRuntime().exec(command);
        p.waitFor();
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        s = br.readLine(); // only read first line
        p.destroy();


        if (s == null) {
            s = "0";
        }
        return s;
    }
}