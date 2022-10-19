package underfloormanagement;

import com.pi4j.io.gpio.*;

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

    
    protected TemperatureSensors(){
    }
    

    public TemperatureSensors(String temp1FilePath, String temp2FilePath, String temp3FilePath) {
        this.temp1FilePath = temp1FilePath;
        this.temp2FilePath = temp2FilePath;
        this.temp3FilePath = temp3FilePath;

        gpio = GpioFactory.getInstance();
        pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00); //https://pi4j.com/1.2/pins/model-b-plus.html
        gpio.setState(PinState.HIGH, pin);
    }

    public TemperatureReading ReadTemperatures() throws InterruptedException {

        TemperatureReading reading = new TemperatureReading();
        boolean resetSensors = false;
        try
        {
            reading.aanvoerTemp = Long.parseLong(ReadFileLine(Paths.get(temp1FilePath)));
            if (reading.aanvoerTemp == 0) {
                resetSensors = true;
            }
        }
        catch (IOException e)
        {
            logger.error("Cannot read temperature sensor 1", e);
            reading.aanvoerTemp = Long.MIN_VALUE;
            resetSensors = true;
        }

        try {
            reading.retourTemp = Long.parseLong(ReadFileLine(Paths.get(temp2FilePath)));
            if (reading.retourTemp == 0) {
                resetSensors = true;
            }
        }
        catch (IOException e)
        {
            logger.error("Cannot read temperature sensor 2", e);
            reading.retourTemp = Long.MIN_VALUE;
            resetSensors = true;
        }

        try
        {
            reading.kastTemp = Long.parseLong(ReadFileLine(Paths.get(temp3FilePath)));
            if (reading.kastTemp == 0) {
                resetSensors = true;
            }
        }
        catch (IOException e)
        {
            logger.error("Cannot read temperature sensor 3", e);
            reading.kastTemp = Long.MIN_VALUE;
            resetSensors = true;
        }

        logger.info(reading);
        if (resetSensors)
        {
            logger.error("Temperature sensor lost. Resetting...");

            gpio.setState(PinState.LOW, pin);
            UnderfloorManagement.Sleep(2000);
            gpio.setState(PinState.HIGH, pin);
            UnderfloorManagement.Sleep(2000);

            TemperatureReading testReading = ReadTemperatures();
            if (testReading.kastTemp > Long.MIN_VALUE) {
                logger.error("Temperature sensor lost. Reset success");
            }
            else
            {
                logger.error("Temperature sensor lost. Reset failed");
            }
        }

        return reading;
    }

    private String ReadFileLine(Path path) throws IOException, InterruptedException {
        String s;
        String command =  String.format("cat %s", path.toString());
        logger.info(command);
        Process p = Runtime.getRuntime().exec(command);
        p.waitFor();
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        s = br.readLine(); //only read first line
        p.destroy();

        /*InputStream is = Files.newInputStream(path, StandardOpenOption.READ);
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader lineReader = new BufferedReader(reader);
        String line = lineReader.readLine();*/

        if (s == null)
        {
            s = "0";
        }
        return s;
    }

    class TemperatureReading {
        long aanvoerTemp;
        long retourTemp;
        long kastTemp;

        @Override
        public String toString() {
            return String.format("Temperatuur:\nAanvoer: %d\nRetour: %d\nKast: %d\n", this.aanvoerTemp, this.retourTemp, this.kastTemp);
        }

    }
}
