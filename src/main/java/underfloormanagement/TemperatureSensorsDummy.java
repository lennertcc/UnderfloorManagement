package underfloormanagement;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
public class TemperatureSensorsDummy extends TemperatureSensors {

    private static final Logger logger = LogManager.getLogger(TemperatureSensorsDummy.class);


    public TemperatureSensorsDummy() {
        super();
    }

    @Override
    public TemperatureReading ReadTemperatures() throws InterruptedException {

        TemperatureReading reading = new TemperatureReading();
        reading.aanvoerTemp = (long) 13.37;
        reading.retourTemp = (long)13.38;
        reading.kastTemp = (long)13.39;
        logger.info(reading);

        return reading;
    }

}
