package underfloormanagement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.RollingFileAppender;

import underfloormanagement.AtagOne.ValveMode;
import underfloormanagement.data.TemperatureReading;

public class CsvReporter {
  private static final Logger csvLogger = LogManager.getLogger("temperature_logger");
  String logFileName;

  TemperatureReading temperatureReading;
  String percentagePwbB;
  ValveMode atagValveMode;
  boolean pumpRunning;

  public CsvReporter() {
    temperatureReading = new TemperatureReading();
    percentagePwbB = "0";
    atagValveMode = ValveMode.off;

    logFileName = "";
  }

  public void reportTemperatureReading(TemperatureReading reading) {
    this.temperatureReading = reading;
    writeReport();
  }

  public void reportPercentagePwmB(String percentage) {
    this.percentagePwbB = percentage;
    writeReport();
  }

  public void reportAtagValveMode(ValveMode mode) {
    this.atagValveMode = mode;
    writeReport();
  }

  public void reportPumpRunning(boolean running) {
    this.pumpRunning = running;
  }

  private void writeReport() {
    org.apache.logging.log4j.core.Logger loggerImpl = (org.apache.logging.log4j.core.Logger) csvLogger;
    RollingFileAppender appender = (RollingFileAppender) loggerImpl.getAppenders().get("temperature_logger_file");
    String filename = appender.getFileName();
    if (filename != this.logFileName) {
      csvLogger.info("kast;aanvoer;retour;atag;vbus,pump");
      this.logFileName = filename;
    }

    csvLogger.info("{};{};{};{};{}",
        temperatureReading.getKastTemp() / 1000.000,
        temperatureReading.getAanvoerTemp() / 1000.000,
        temperatureReading.getRetourTemp() / 1000.000,
        (atagValveMode == ValveMode.running ? 100 : 0),
        percentagePwbB,
        (pumpRunning ? 100 : 0));
  }

}
