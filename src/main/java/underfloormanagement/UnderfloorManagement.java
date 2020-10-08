package underfloormanagement;

import java.io.IOException;
import java.net.InetAddress;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * @author lenne
 */
public class UnderfloorManagement {

    private static final Logger logger = Logger.getLogger(UnderfloorManagement.class.getName());
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Amsterdam"));
        
        UnderfloorProperties properties = new UnderfloorProperties();
        
        String hostName = InetAddress.getLocalHost().getHostName();
        logger.info("Hello from " + hostName + " in " + TimeZone.getDefault().getDisplayName());
        
        final AtagOne one = initializeOne(properties);
        
        VBusLiveSystem vbus;
        try {
            System.out.println("Initializing VBus...");
            vbus = new VBusLiveSystem(properties.vbusApiUrl);
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException ex) {
            logger.log(Level.SEVERE, null, ex);
            return;
        }
        
        PumpAppliance pump;
        try {
            if (hostName.equals("raspberrypi")) {
                pump = new PumpAppliance(properties.relaisType, properties.overrunMinutes);
            } else {
                pump = new PumpApplianceDummy(properties.relaisType, properties.overrunMinutes);
            }
        } catch (IOException | UnsupportedOperationException | InterruptedException ex) {
            logger.log(Level.SEVERE, null, ex);
            return;
        }
        
        Thread AtagOneMonitor = new Thread(() -> {
            while (true) {
                
                logger.info("Atag Thread");
                
                try {
                    if (one.IsRunningLocal()) {
                        pump.StartOrExtendPump();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(UnderfloorManagement.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                UnderfloorManagement.Sleep(properties.atagInterval);
            }
        });

        Thread VBusSystemMonitor = new Thread(() -> {
            while (true) {
                
                logger.info("VBus Thread");
                
                try {
                    if (vbus.IsPumpBRunningFile()) {
                        pump.StartOrExtendPump();
                    }
                } catch (IOException | InterruptedException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
                
                UnderfloorManagement.Sleep(properties.vbusInterval);
            }
        });

/*
        Thread aaCACommandListener = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {

                    logger.info("aaCA Command Listener Thread");
                    
                    FTPClient ftpClient = new FTPClient();
                    try {
                        ftpClient.connect(properties.aaCAFtpUrl);
                        ftpClient.login(properties.aacaFtpUsername, properties.aacaFtpPassword);
                        InputStream inputStream = ftpClient.retrieveFileStream(properties.aaCACommandFile);
                        
                        StringWriter writer = new StringWriter();
                        IOUtils.copy(inputStream, writer, "UTF-8");
                        String command = writer.toString();
                        
                        boolean success = ftpClient.completePendingCommand();
                        if (success) {
                            System.out.println("File #2 has been downloaded successfully.");
                        }
                        inputStream.close();
                        
                    } catch (IOException ex) {
                        Logger.getLogger(UnderfloorManagement.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    
                            
                    

                    UnderfloorManagement.Sleep(properties.aaCACommandInterval);
                }
            }
        });
*/
        AtagOneMonitor.start();
        VBusSystemMonitor.start();
        //aaCACommandListener.start();
    }

    public static void Sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            Logger.getLogger(UnderfloorManagement.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static AtagOne initializeOne(UnderfloorProperties properties) {
        while (true) {
            try {
                System.out.println("Initializing AtagOne...");
                return new AtagOne(properties.atagEmail, properties.atagPassword);
            } catch (IOException | NullPointerException ex) {
                logger.log(Level.SEVERE, null, ex);
            } catch(Exception rest) {
                logger.log(Level.SEVERE, null, rest);
                return null;
            }
        }
    }
}
