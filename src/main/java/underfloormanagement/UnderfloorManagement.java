/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package underfloormanagement;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.State;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.Logger;
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

        UnderfloorProperties properties = new UnderfloorProperties();

        PumpAppliance pump;
        try {

            if (hostName.equals("raspberrypi")) {
                pump = new PumpAppliance(properties.relaisType, properties.overrunMinutes);
            } else {
                pump = new PumpApplianceDummy(properties.relaisType, properties.overrunMinutes);
            }
        } catch (IOException | UnsupportedOperationException | InterruptedException ex) {
            logger.error(ex);
            return;
        }

        TemperatureSensors sensors;
        try {
            if (hostName.equals("raspberrypi")) {
                sensors = new TemperatureSensors(properties.temp1, properties.temp2, properties.temp3);
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
            one = new AtagOne(properties.atagEmail, properties.atagPassword);
        } catch (IOException ex) {
            logger.error(ex);
            return;
        }

        VBusLiveSystem vbus;
        try {
            vbus = new VBusLiveSystem(properties.vbusApiUrl);
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException ex) {
            logger.error(ex);
            return;
        }

        Thread SystemMonitor = new Thread(() -> {
            Thread AtagOneMonitor = CreateAtagOneMonitorThread(one, pump, properties);
            AtagOneMonitor.setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());

            Thread VBusSystemMonitor = CreateVBusSystemMonitorThread(vbus, pump, properties);
            VBusSystemMonitor.setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());

            Thread TemperatureSensorMonitor = CreateTemperatureSensorsThread(sensors, pump, properties);
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

                    AtagOneMonitor = CreateAtagOneMonitorThread(one, pump, properties);
                    AtagOneMonitor.start();
                }

                if (!VBusSystemMonitor.isAlive()) {
                    logger.error("VBusSystemMonitor died. Restarting...");

                    VBusSystemMonitor = CreateVBusSystemMonitorThread(vbus, pump, properties);
                    VBusSystemMonitor.start();
                }

                if (!TemperatureSensorMonitor.isAlive()) {
                    logger.error("TemperatureSensorMonitor died. Restarting...");

                    TemperatureSensorMonitor = CreateTemperatureSensorsThread(sensors, pump, properties);
                    TemperatureSensorMonitor.start();
                }

                UnderfloorManagement.Sleep(properties.systemMonitorInterval);
            }
        });

        SystemMonitor.setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());
        SystemMonitor.start();
    }

    private static Thread CreateAtagOneMonitorThread(AtagOne one, PumpAppliance pump, UnderfloorProperties properties) {
        return new Thread(() -> {
            while (true) {

                logger.info("Atag Thread");

                try {
                    AtagOne.ValveMode mode = one.IsRunning();
                    switch (mode) {
                        case running:
                            pump.StartOrExtendPump();
                            break;
                        case fireplace:
                            pump.StopPump();
                            break;
                    }

                } catch (IOException ex) {
                    logger.error(ex);
                }

                UnderfloorManagement.Sleep(properties.atagInterval);
            }
        });
    }

    private static Thread CreateVBusSystemMonitorThread(VBusLiveSystem vbus, PumpAppliance pump,
            UnderfloorProperties properties) {
        return new Thread(() -> {
            while (true) {

                logger.info("VBus Thread");

                try {
                    VBusLiveSystemData[] liveSystem = vbus.readLiveSystem();

                    if (vbus.IsPumpBRunning(liveSystem)) {
                        pump.StartOrExtendPump();
                    }

                    // vbus.LogToAzureIoT(liveSystem);

                } catch (IOException ex) {
                    logger.error(ex);
                } catch (Exception e) {
                    logger.error(e);
                }

                UnderfloorManagement.Sleep(properties.vbusInterval);
            }
        });
    }

    private static Thread CreateTemperatureSensorsThread(TemperatureSensors sensors, PumpAppliance pump,
            UnderfloorProperties properties) {
        return new Thread(() -> {
            while (true) {

                logger.info("Temperature Thread");

                try {
                    TemperatureSensors.TemperatureReading readings = sensors.ReadTemperatures();

                } catch (InterruptedException ex) {
                    logger.error("Crash in ReadTemperatures", ex);
                }

                UnderfloorManagement.Sleep(properties.tempInterval);
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
