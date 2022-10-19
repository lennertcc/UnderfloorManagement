/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package underfloormanagement;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileSystemException;
import underfloormanagement.PumpAppliance.NoNc;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author lenne
 */
public class UnderfloorProperties implements FileListener {

    private static final Logger logger = LogManager.getLogger(UnderfloorProperties.class);

    public NoNc relaisType;
    public String atagEmail;
    public String atagPassword;
    public String vbusApiUrl;
    public long vbusInterval;
    public long atagInterval;
    public long overrunMinutes;
    public long systemMonitorInterval;
    public long tempInterval;

    public String temp1;
    public String temp2;
    public String temp3;

    private static String defaultFilename = "config.properties";
    private static String filename = "config.properties";

    public UnderfloorProperties() throws IOException {
        InitializeProperties();
    }

    private void InitializeProperties() throws IOException {
        Properties properties = new Properties();
        InputStream defaultInput = null;
        InputStream input = null;
        try {
            defaultInput = UnderfloorProperties.class.getClassLoader().getResourceAsStream(defaultFilename);
            LoadProperties(properties, defaultInput);

            input = new FileInputStream(filename);
            LoadProperties(properties, input);

        } catch (IOException ex) {
            logger.error("Looking in " + Paths.get(".").toAbsolutePath().normalize().toString(), ex);
            throw ex;
        } finally {
            if (defaultInput != null) {
                try {
                    defaultInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void LoadProperties(Properties properties, InputStream input) throws IOException {
        if (input == null) {
            logger.info("Sorry, unable to find config file");
            throw new FileSystemException("File not found");
        }

        properties.load(input);
        this.relaisType = (properties.containsKey("RelaisType") ? NoNc.valueOf(properties.getProperty("RelaisType"))
                : this.relaisType);
        this.atagEmail = (properties.containsKey("AtagEmail") ? properties.getProperty("AtagEmail") : this.atagEmail);
        this.atagPassword = (properties.containsKey("AtagPassword") ? properties.getProperty("AtagPassword")
                : this.atagPassword);
        this.vbusApiUrl = (properties.containsKey("VBusApiUrl") ? properties.getProperty("VBusApiUrl")
                : this.vbusApiUrl);
        this.atagInterval = (properties.containsKey("AtagInterval")
                ? Long.parseLong(properties.getProperty("AtagInterval"))
                : this.atagInterval);
        this.vbusInterval = (properties.containsKey("VBusInterval")
                ? Long.parseLong(properties.getProperty("VBusInterval"))
                : this.vbusInterval);
        this.tempInterval = (properties.containsKey("TempInterval")
                ? Long.parseLong(properties.getProperty("TempInterval"))
                : this.tempInterval);
        this.overrunMinutes = (properties.containsKey("OverrunMinutes")
                ? Long.parseLong(properties.getProperty("OverrunMinutes"))
                : this.overrunMinutes);
        this.systemMonitorInterval = (properties.containsKey("SystemMonitorInterval")
                ? Long.parseLong(properties.getProperty("SystemMonitorInterval"))
                : this.systemMonitorInterval);
        this.temp1 = (properties.containsKey("Temp1") ? properties.getProperty("Temp1") : this.temp1);
        this.temp2 = (properties.containsKey("Temp2") ? properties.getProperty("Temp2") : this.temp2);
        this.temp3 = (properties.containsKey("Temp3") ? properties.getProperty("Temp3") : this.temp3);
    }

    @Override
    public void fileChanged(FileChangeEvent fce) throws Exception {
        InitializeProperties();
    }

    @Override
    public void fileCreated(FileChangeEvent fce) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }

    @Override
    public void fileDeleted(FileChangeEvent fce) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }

}
