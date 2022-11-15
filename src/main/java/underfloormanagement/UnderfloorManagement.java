/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package underfloormanagement;

import java.io.IOException;
import java.lang.Thread.State;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import org.apache.logging.log4j.Logger;

import underfloormanagement.data.TemperatureReading;
import underfloormanagement.data.VBusLiveSystemReading;

import org.apache.logging.log4j.LogManager;

public class UnderfloorManagement {

    private static final Logger logger = LogManager.getLogger(UnderfloorManagement.class);

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

        String hostName = InetAddress.getLocalHost().getHostName();
        logger.info("Hello from " + hostName);
        logger.info("Running " + System.getProperty("java.vm.name") + " version " + System.getProperty("java.version")
                + " from " + System.getProperty("java.vendor"));

        UnderfloorSettings settings = UnderfloorSettings.getConfiguration();
        ReporterDecider reporterDecider = new ReporterDecider();

        PumpAppliance pump;
        try {

            if (hostName.equals("raspberrypi")) {
                pump = new PumpAppliance(settings.relaisType, settings.overrunMinutes, reporterDecider);
            } else {
                pump = new PumpApplianceDummy(settings.relaisType, settings.overrunMinutes, reporterDecider);
            }
        } catch (IOException | UnsupportedOperationException | InterruptedException ex) {
            logger.error(ex);
            return;
        }
        reporterDecider.setPumpAppliance(pump);

        TemperatureSensors sensors;
        try {
            if (hostName.equals("raspberrypi")) {
                sensors = new TemperatureSensors(settings.temp1, settings.temp2, settings.temp3);
                sensors.ReadTemperatures();
            } else {
                sensors = new TemperatureSensorsDummy();
                sensors.ReadTemperatures();
            }
        } catch (InterruptedException ex) {
            logger.error("Init ReadTemperatures failed", ex);
            return;
        }

        AtagOne one;
        try {
            one = new AtagOne(settings.atagEmail, settings.atagPassword);
        } catch (IOException ex) {
            logger.error(ex);
            return;
        }

        VBusLiveSystem vbus;
        try {
            vbus = new VBusLiveSystem(settings.vbusApiUrl);
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException ex) {
            logger.error(ex);
            return;
        }

        Thread SystemMonitor = new Thread(() -> {
            Thread AtagOneMonitor = CreateAtagOneMonitorThread(one, reporterDecider);
            AtagOneMonitor.setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());

            Thread VBusSystemMonitor = CreateVBusSystemMonitorThread(vbus, reporterDecider);
            VBusSystemMonitor.setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());

            Thread TemperatureSensorMonitor = CreateTemperatureSensorsThread(sensors, reporterDecider);
            TemperatureSensorMonitor.setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());

            AtagOneMonitor.start();
            VBusSystemMonitor.start();
            TemperatureSensorMonitor.start();

            while (true) {
                logger.info("SystemMonitor");
                State AtagOneMonitorState = AtagOneMonitor.getState();
                State VBusSystemMonitorState = VBusSystemMonitor.getState();
                State TemperatureSensorMonitorState = TemperatureSensorMonitor.getState();

                logger.info("AtagOneMonitor: " + AtagOneMonitorState);
                logger.info("VBusSystemMonitor: " + VBusSystemMonitorState);
                logger.info("TemperatureSensorMonitor: " + TemperatureSensorMonitorState);

                if (!AtagOneMonitor.isAlive()) {
                    logger.error("AtagOneMonitor died. Restarting...");

                    AtagOneMonitor = CreateAtagOneMonitorThread(one, reporterDecider);
                    AtagOneMonitor.start();
                }

                if (!VBusSystemMonitor.isAlive()) {
                    logger.error("VBusSystemMonitor died. Restarting...");

                    VBusSystemMonitor = CreateVBusSystemMonitorThread(vbus, reporterDecider);
                    VBusSystemMonitor.start();
                }

                if (!TemperatureSensorMonitor.isAlive()) {
                    logger.error("TemperatureSensorMonitor died. Restarting...");

                    TemperatureSensorMonitor = CreateTemperatureSensorsThread(sensors, reporterDecider);
                    TemperatureSensorMonitor.start();
                }

                UnderfloorManagement.Sleep(settings.systemMonitorInterval);
            }
        });

        SystemMonitor.setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());
        SystemMonitor.start();
    }

    private static Thread CreateAtagOneMonitorThread(AtagOne one, ReporterDecider reporterDecider) {

        return new Thread(() -> {
            while (true) {

                logger.info("Atag Thread");

                try {
                    AtagOne.ValveMode mode = one.IsRunning();
                    reporterDecider.reportAtagValveMode(mode);
                } catch (IOException ex) {
                    logger.error(ex);
                }

                UnderfloorManagement.Sleep(UnderfloorSettings.getConfiguration().atagInterval);
            }
        });
    }

    private static Thread CreateVBusSystemMonitorThread(VBusLiveSystem vbus, ReporterDecider reporterDecider) {
        return new Thread(() -> {
            while (true) {

                logger.info("VBus Thread");

                try {
                    VBusLiveSystemReading[] liveSystem = vbus.readLiveSystem();
                    String percentage = vbus.PercentagePumpBRunning(liveSystem);
                    reporterDecider.reportPercentagePwmB(percentage);

                } catch (IOException ex) {
                    logger.error(ex);
                } catch (Exception e) {
                    logger.error(e);
                }

                UnderfloorManagement.Sleep(UnderfloorSettings.getConfiguration().vbusInterval);
            }
        });
    }

    private static Thread CreateTemperatureSensorsThread(TemperatureSensors sensors, ReporterDecider reporterDecider) {
        return new Thread(() -> {
            while (true) {

                logger.info("Temperature Thread");

                try {
                    TemperatureReading readings = sensors.ReadTemperatures();
                    if (readings != null) {
                        reporterDecider.reportTemperatureReading(readings);
                    }
                } catch (InterruptedException ex) {
                    logger.error("Crash in ReadTemperatures", ex);
                }

                UnderfloorManagement.Sleep(UnderfloorSettings.getConfiguration().tempInterval);
            }
        });
    }

    public static void Sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            logger.error(ex);
        }
    }

}
