package underfloormanagement;

import java.io.IOException;
import java.util.Map;
import org.juurlink.atagone.AtagOneConnectorFactory;
import org.juurlink.atagone.AtagOneConnectorInterface;
import org.juurlink.atagone.domain.Configuration;
import underfloormanagement.AtagOneBoilerStatus.BoilerStatus;

/**
 *
 * @author lenne
 */
public class AtagOne {

    Configuration configuration;
    AtagOneConnectorInterface atagOneConnector;
    
    
    public AtagOne(String email, String password) throws IOException {
        //todo: configuration for MAC
        configuration = Configuration.builder().mac("bc:30:7e:de:8d:66").build();
        //configuration = Configuration.builder().email(email).password(password).build();
        atagOneConnector = new AtagOneConnectorFactory().getInstance(configuration);
        atagOneConnector.login(); // Login; Either local or remote.
    }
    
    public boolean IsRunningLocal() throws IOException {
        System.out.println("Atag Local");
        
        Map<String, Object> diagnostics = atagOneConnector.getDiagnostics();
        
        System.out.println("Atag " + AtagOneConnectorInterface.VALUE_LATEST_REPORT_TIME + ": " + diagnostics.get(AtagOneConnectorInterface.VALUE_LATEST_REPORT_TIME));
        System.out.println("Atag " + AtagOneConnectorInterface.VALUE_FLAME_STATUS + ": " + diagnostics.get(AtagOneConnectorInterface.VALUE_FLAME_STATUS));
        
        AtagOneBoilerStatus chStatus = new AtagOneBoilerStatus((int)diagnostics.get("chStatus"));
        AtagOneBoilerStatus dhwStatus = new AtagOneBoilerStatus((int)diagnostics.get("dhwStatus"));
        AtagOneBoilerStatus boilerStatus = new AtagOneBoilerStatus((int)diagnostics.get("boilerStatus"));
        
        System.out.println("Atag chStatus: " + chStatus);
        System.out.println("Atag dhwStatus: " + dhwStatus);
        System.out.println("Atag boilerStatus: " + boilerStatus);
        //System.out.println(atagOneConnector.dump());
        
        return chStatus.getBoilerStatus().contains(BoilerStatus.Unknown4) && diagnostics.get(AtagOneConnectorInterface.VALUE_FLAME_STATUS).equals("On");
    }
    
    public boolean IsRunningOnline() throws IOException {
        System.out.println("Atag Online");
        
        Map<String, Object> diagnostics = atagOneConnector.getDiagnostics();
        
        System.out.println("Atag " + AtagOneConnectorInterface.VALUE_LATEST_REPORT_TIME + ": " + diagnostics.get(AtagOneConnectorInterface.VALUE_LATEST_REPORT_TIME));
        System.out.println("Atag " + AtagOneConnectorInterface.VALUE_FLAME_STATUS + ": " + diagnostics.get(AtagOneConnectorInterface.VALUE_FLAME_STATUS));
        System.out.println("Atag " + AtagOneConnectorInterface.VALUE_BOILER_HEATING_FOR + ": " + diagnostics.get(AtagOneConnectorInterface.VALUE_BOILER_HEATING_FOR));
        
        return diagnostics.get(AtagOneConnectorInterface.VALUE_BOILER_HEATING_FOR).equals("CV");
    }

}
