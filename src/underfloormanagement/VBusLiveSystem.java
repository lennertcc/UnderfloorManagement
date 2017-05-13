/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package underfloormanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Optional;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 *
 * @author lenne
 */
public class VBusLiveSystem {

    private final URL apiUrl;

    public VBusLiveSystem(String apiUrl) throws IOException, MalformedURLException, NoSuchAlgorithmException, KeyManagementException {
        InitializeSSL();
        this.apiUrl = new URL(apiUrl);
    }

    public boolean IsPumpBRunningFile() throws IOException, InterruptedException {
        System.out.println("VBus pump status from file");
        FileInputStream content = new FileInputStream("vbus.log");
        ObjectMapper mapper = new ObjectMapper();

        //JSON from file to Object
        VBusLiveSystemData[] liveSystem = mapper.readValue(content, VBusLiveSystemData[].class);

        Optional<VBusLiveSystemData> PWMB = Arrays.stream(liveSystem).filter(x -> x.getFieldIdentifier().equals("039_1_0")).findAny();
        Optional<VBusLiveSystemData> PWMA = Arrays.stream(liveSystem).filter(x -> x.getFieldIdentifier().equals("038_1_0")).findAny();
        
        Optional<VBusLiveSystemData> SystemTime = Arrays.stream(liveSystem).filter(x -> x.getFieldIdentifier().equals("000_4_0")).findAny();

        if (PWMB.isPresent()) {
            System.out.println("VBus " + SystemTime.get().getName() + ": " + SystemTime.get().getValue());
            System.out.println("VBus " + PWMB.get().getName() + ": " + PWMB.get().getValue());
            if (PWMA.isPresent()){
                System.out.println("VBus " + PWMA.get().getName() + ": " + PWMA.get().getValue());
            }
            return !PWMB.get().getValue().equals("0");
        } else {
            System.out.println("VBus PWM B not present in diagnostics");
        }
        return false;
    }
    
    public boolean IsPumpBRunningHttps() throws IOException, InterruptedException {
        System.out.println("VBus pump status");

        HttpsURLConnection connection = (HttpsURLConnection) apiUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
          
        
        InputStream content = (InputStream) connection.getInputStream();
        ObjectMapper mapper = new ObjectMapper();

        //JSON from file to Object
        VBusLiveSystemData[] liveSystem = mapper.readValue(content, VBusLiveSystemData[].class);

        Optional<VBusLiveSystemData> PWMB = Arrays.stream(liveSystem).filter(x -> x.getFieldIdentifier().equals("039_1_0")).findAny();
        Optional<VBusLiveSystemData> SystemTime = Arrays.stream(liveSystem).filter(x -> x.getFieldIdentifier().equals("000_4_0")).findAny();

        if (PWMB.isPresent()) {
            System.out.println(SystemTime.get().getName() + ": " + SystemTime.get().getValue());
            System.out.println(PWMB.get().getName() + ": " + PWMB.get().getValue());
            System.out.println();
            return !PWMB.get().getValue().equals("0");
        } else {
            System.out.println("PWM B not present in diagnostics");
            System.out.println();
        }
        return false;
    }

    public static void InitializeSSL() throws IOException, NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, null, null);
        SSLContext.setDefault(sslContext);
    }

}
