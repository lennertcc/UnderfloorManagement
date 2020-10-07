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
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import underfloormanagement.PumpAppliance.NoNc;

/**
 *
 * @author lenne
 */
public class UnderfloorProperties implements FileListener {

    public NoNc relaisType;
    public String atagEmail;
    public String atagPassword;
    public String vbusApiUrl;
    public long vbusInterval;
    public long atagInterval;
    public long aaCACommandInterval;
    public String aaCACommandFile;
    public String aaCAFtpUrl;
    public String aacaFtpUsername;
    public String aacaFtpPassword;
    public long overrunMinutes;
    
    
    private static String defaultFilename = "underfloormanagement/config.properties";
    private static String filename = "dist/config.properties";
    
    
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
            
        }catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Looking in " + Paths.get(".").toAbsolutePath().normalize().toString());
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
            System.out.println("Sorry, unable to find config file");
            throw new FileSystemException("File not found");
        }
        
        properties.load(input);
        this.relaisType = (properties.containsKey("RelaisType") ? NoNc.valueOf(properties.getProperty("RelaisType")) : this.relaisType);
        this.atagEmail = (properties.containsKey("AtagEmail") ? properties.getProperty("AtagEmail") : this.atagEmail);
        this.atagPassword = (properties.containsKey("AtagPassword") ? properties.getProperty("AtagPassword") : this.atagPassword);
        this.vbusApiUrl = (properties.containsKey("VBusApiUrl") ? properties.getProperty("VBusApiUrl") : this.vbusApiUrl);
        this.atagInterval = (properties.containsKey("AtagInterval") ? Long.parseLong(properties.getProperty("AtagInterval")) : this.atagInterval);
        this.vbusInterval = (properties.containsKey("VBusInterval") ? Long.parseLong(properties.getProperty("VBusInterval")) : this.vbusInterval);
        this.aaCACommandInterval = (properties.containsKey("aaCACommandInterval") ? Long.parseLong(properties.getProperty("aaCACommandInterval")) : this.aaCACommandInterval);
        this.aaCACommandFile = (properties.containsKey("aaCACommandFile") ? properties.getProperty("aaCACommandFile") : this.aaCACommandFile);
        this.aaCAFtpUrl = (properties.containsKey("aaCAFtpUrl") ? properties.getProperty("aaCAFtpUrl") : this.aaCAFtpUrl);
        this.aacaFtpUsername = (properties.containsKey("aacaFtpUsername") ? properties.getProperty("aacaFtpUsername") : this.aacaFtpUsername);
        this.aacaFtpPassword = (properties.containsKey("aacaFtpPassword") ? properties.getProperty("aacaFtpPassword") : this.aacaFtpPassword);
        
        this.overrunMinutes = (properties.containsKey("OverrunMinutes") ? Long.parseLong(properties.getProperty("OverrunMinutes")) : this.overrunMinutes);
    }
    
    @Deprecated
    private void SetFileSystemHook() throws FileSystemException {
        FileSystemManager fsManager = VFS.getManager();
        org.apache.commons.vfs2.FileObject listendir = fsManager.resolveFile(filename);

        DefaultFileMonitor fm = new DefaultFileMonitor(this);
        fm.setRecursive(true);
        fm.addFile(listendir);
        fm.start();
    }

    @Override
    public void fileChanged(FileChangeEvent fce) throws Exception {
        InitializeProperties();
    }

    @Override
    public void fileCreated(FileChangeEvent fce) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fileDeleted(FileChangeEvent fce) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    

}
