package underfloormanagement;

import org.apache.logging.log4j.Logger;
import underfloormanagement.data.TemperatureReading;
import org.apache.logging.log4j.LogManager;

public class TemperatureSensorsDummy extends TemperatureSensors {

    private static final Logger logger = LogManager.getLogger(TemperatureSensorsDummy.class);

    public TemperatureSensorsDummy() {
        super();
    }

    @Override
    public TemperatureReading ReadTemperatures() throws InterruptedException {

        TemperatureReading reading = new TemperatureReading();
        reading.setAanvoerTemp((long) 13.37);
        reading.setRetourTemp((long) 13.38);
        reading.setKastTemp((long) 13.39);
        logger.info(reading);

        return reading;
    }

}
