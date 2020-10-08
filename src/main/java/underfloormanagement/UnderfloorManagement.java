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
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author lenne
 */
public class UnderfloorManagement {
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

        Handler fileHandler = new FileHandler("errors.log");
        fileHandler.setFormatter(new SimpleFormatter());
        Logger errorLog = Logger.getLogger("errorLog");
        errorLog.addHandler(fileHandler);
        
        UnderfloorProperties properties = new UnderfloorProperties();

        PumpAppliance pump;
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            System.out.println("Hello from " + hostName);
            System.out.println("Running " + System.getProperty("java.vm.name") + " version " + System.getProperty("java.version") + " from " + System.getProperty("java.vendor"));
            
            if (hostName.equals("raspberrypi")) {
                pump = new PumpAppliance(properties.relaisType, properties.overrunMinutes);
            } else {
                pump = new PumpApplianceDummy(properties.relaisType, properties.overrunMinutes);
            }
        } catch (IOException | UnsupportedOperationException | InterruptedException ex) {
            Logger.getLogger(UnderfloorManagement.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        AtagOne one;
        try {
            one = new AtagOne(properties.atagEmail, properties.atagPassword);
        } catch (IOException ex) {
            Logger.getLogger(UnderfloorManagement.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        VBusLiveSystem vbus;
        try {
            vbus = new VBusLiveSystem(properties.vbusApiUrl);
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException ex) {
            Logger.getLogger(UnderfloorManagement.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        
        Thread SystemMonitor = new Thread(() -> {
            Thread AtagOneMonitor = CreateAtagOneMonitorThread(one, pump, properties);
            AtagOneMonitor.setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());
            
            Thread VBusSystemMonitor = CreateVBusSystemMonitorThread(vbus, pump, errorLog, properties);
            VBusSystemMonitor.setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());
            
            AtagOneMonitor.start();
            VBusSystemMonitor.start();
            
            while (true) {    
                System.out.println("SystemMonitor");
                State AtagOneMonitorState = AtagOneMonitor.getState();
                State VBusSystemMonitorState = VBusSystemMonitor.getState();
                        
                System.out.println("AtagOneMonitor: " + AtagOneMonitorState);
                System.out.println("VBusSystemMonitor: " + VBusSystemMonitorState);
                
                if (!AtagOneMonitor.isAlive())
                {
                    errorLog.severe("AtagOneMonitor died. Restarting...");
                    
                    AtagOneMonitor = CreateAtagOneMonitorThread(one, pump, properties);
                    AtagOneMonitor.start();
                }
                
                if (!VBusSystemMonitor.isAlive())
                {
                    errorLog.severe("VBusSystemMonitor died. Restarting...");

                    VBusSystemMonitor = CreateVBusSystemMonitorThread(vbus, pump, errorLog, properties);
                    VBusSystemMonitor.start();
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
                
                System.out.println("Atag Thread");
                
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
                    Logger.getLogger(UnderfloorManagement.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                UnderfloorManagement.Sleep(properties.atagInterval);
            }
        });
    }

    private static Thread CreateVBusSystemMonitorThread(VBusLiveSystem vbus, PumpAppliance pump, Logger errorLog, UnderfloorProperties properties) {
        return new Thread(() -> {
            while (true) {
                
                System.out.println("VBus Thread");
                
                try {
                    VBusLiveSystemData[] liveSystem = vbus.readLiveSystem();
                    
                    if (vbus.IsPumpBRunning(liveSystem)) {
                        pump.StartOrExtendPump();
                    }
                    
                    //vbus.LogToAzureIoT(liveSystem);
                    
                } catch (IOException ex) {
                    Logger.getLogger(UnderfloorManagement.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception e)
                {
                    Logger.getLogger(UnderfloorManagement.class.getName()).log(Level.SEVERE, null, e);
                }
                
                UnderfloorManagement.Sleep(properties.vbusInterval);
            }
        });
    }

    public static void Sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            Logger.getLogger(UnderfloorManagement.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
}
