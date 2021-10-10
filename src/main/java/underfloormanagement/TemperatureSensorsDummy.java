package underfloormanagement;

public class TemperatureSensorsDummy extends TemperatureSensors {

    public TemperatureSensorsDummy() {
        super();
    }

    @Override
    public TemperatureReading ReadTemperatures() throws InterruptedException {

        TemperatureReading reading = new TemperatureReading();
        reading.aanvoerTemp = (long) 13.37;
        reading.retourTemp = (long)13.38;
        reading.kastTemp = (long)13.39;
        UnderfloorManagement.logInfo(reading);

        return reading;
    }

}
