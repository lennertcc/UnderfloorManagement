/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package underfloormanagement;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.State;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.logging.*;

public class UnderfloorManagement {
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

        SimpleFormatter formatter = new SimpleFormatter() {
            private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            @Override
            public String format(LogRecord record) {
                String thrown;
                if (record.getThrown() == null) {
                    thrown = "";
                } else {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    pw.println();
                    record.getThrown().printStackTrace(pw);
                    thrown = sw.toString();
                }
                return String.format("[%s] [%s]: %s%s%n", dateFormat.format(record.getMillis()),
                        record.getLevel(), record.getMessage(), thrown);
            }
        };

        Handler errorFileHandler = new FileHandler("UnderfloorErrors.log");
        errorFileHandler.setFormatter(formatter);
        Logger errorLog = Logger.getLogger("errorLog");
        errorLog.addHandler(errorFileHandler);

        Handler infoFileHandler = new FileHandler("UnderfloorInfo.log");
        infoFileHandler.setFormatter(formatter);
        Logger infoLog = Logger.getLogger("infoLog");
        infoLog.addHandler(infoFileHandler);

        String hostName = InetAddress.getLocalHost().getHostName();
        logInfo("Hello from " + hostName);
        logInfo("Running " + System.getProperty("java.vm.name") + " version " + System.getProperty("java.version") + " from " + System.getProperty("java.vendor"));

        UnderfloorProperties properties = new UnderfloorProperties();

        PumpAppliance pump;
        try {

            if (hostName.equals("raspberrypi")) {
                pump = new PumpAppliance(properties.relaisType, properties.overrunMinutes);
            } else {
                pump = new PumpApplianceDummy(properties.relaisType, properties.overrunMinutes);
            }
        } catch (IOException | UnsupportedOperationException | InterruptedException ex) {
            logError(null, ex);
            return;
        }

        TemperatureSensors sensors;
        try
        {
            if (hostName.equals("raspberrypi")) {
                sensors = new TemperatureSensors(properties.temp1, properties.temp2, properties.temp3);
                sensors.ReadTemperatures();
            }
            else
            {
                sensors = new TemperatureSensorsDummy();
                sensors.ReadTemperatures();
            }
        } catch (InterruptedException ex) {
            logError("Init ReadTemperatures failed", ex);
            return;
        }

        AtagOne one;
        try {
            one = new AtagOne(properties.atagEmail, properties.atagPassword);
        } catch (IOException ex) {
            logError(null, ex);
            return;
        }

        VBusLiveSystem vbus;
        try {
            vbus = new VBusLiveSystem(properties.vbusApiUrl);
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException ex) {
            logError(null, ex);
            return;
        }


        Thread SystemMonitor = new Thread(() -> {
            Thread AtagOneMonitor = CreateAtagOneMonitorThread(one, pump, properties);
            AtagOneMonitor.setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());
            
            Thread VBusSystemMonitor = CreateVBusSystemMonitorThread(vbus, pump, errorLog, properties);
            VBusSystemMonitor.setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());

            Thread TemperatureSensorMonitor = CreateTemperatureSensorsThread(sensors, pump, properties);
            TemperatureSensorMonitor.setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());
            
            AtagOneMonitor.start();
            VBusSystemMonitor.start();
            TemperatureSensorMonitor.start();
            
            while (true) {    
                logInfo("SystemMonitor");
                State AtagOneMonitorState = AtagOneMonitor.getState();
                State VBusSystemMonitorState = VBusSystemMonitor.getState();
                State TemperatureSensorMonitorState = TemperatureSensorMonitor.getState();

                logInfo("AtagOneMonitor: " + AtagOneMonitorState);
                logInfo("VBusSystemMonitor: " + VBusSystemMonitorState);
                logInfo("TemperatureSensorMonitor: " + TemperatureSensorMonitorState);
                
                if (!AtagOneMonitor.isAlive())
                {
                    logError("AtagOneMonitor died. Restarting...", null);
                    
                    AtagOneMonitor = CreateAtagOneMonitorThread(one, pump, properties);
                    AtagOneMonitor.start();
                }
                
                if (!VBusSystemMonitor.isAlive())
                {
                    logError("VBusSystemMonitor died. Restarting...", null);

                    VBusSystemMonitor = CreateVBusSystemMonitorThread(vbus, pump, errorLog, properties);
                    VBusSystemMonitor.start();
                }

                if (!TemperatureSensorMonitor.isAlive())
                {
                    logError("TemperatureSensorMonitor died. Restarting...", null);

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
                
                logInfo("Atag Thread");
                
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
                    logError(null, ex);
                }
                
                UnderfloorManagement.Sleep(properties.atagInterval);
            }
        });
    }

    private static Thread CreateVBusSystemMonitorThread(VBusLiveSystem vbus, PumpAppliance pump, Logger errorLog, UnderfloorProperties properties) {
        return new Thread(() -> {
            while (true) {
                
                logInfo("VBus Thread");
                
                try {
                    VBusLiveSystemData[] liveSystem = vbus.readLiveSystem();
                    
                    if (vbus.IsPumpBRunning(liveSystem)) {
                        pump.StartOrExtendPump();
                    }
                    
                    //vbus.LogToAzureIoT(liveSystem);
                    
                } catch (IOException ex) {
                    logError(null, ex);
                } catch (Exception e)
                {
                    logError(null, e);
                }
                
                UnderfloorManagement.Sleep(properties.vbusInterval);
            }
        });
    }

    private static Thread CreateTemperatureSensorsThread(TemperatureSensors sensors, PumpAppliance pump, UnderfloorProperties properties) {
        return new Thread(() -> {
            while (true) {

                logInfo("Temperature Thread");

                try {
                    TemperatureSensors.TemperatureReading readings = sensors.ReadTemperatures();

                } catch (InterruptedException ex) {
                    logError("Crash in ReadTemperatures", ex);
                }

                UnderfloorManagement.Sleep(properties.tempInterval);
            }
        });
    }


    public static void Sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            logError(null, ex);
        }
    }

    public static void logError(Object message, Throwable throwable) {
        Logger.getLogger("errorLog").log(Level.SEVERE, message.toString(), throwable);
        logInfo(message);
    }

    public static void logInfo(Object message) {
        System.out.println(message);
        Logger.getLogger("infoLog").log(Level.INFO, message.toString());
    }



}
