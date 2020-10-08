package underfloormanagement;

import java.io.IOException;
import java.util.Map;
import org.juurlink.atagone.AtagOneConnectorFactory;
import org.juurlink.atagone.AtagOneConnectorInterface;
import org.juurlink.atagone.domain.Configuration;

public class AtagOne {

    Configuration configuration;
    AtagOneConnectorInterface atagOneConnector;
    
    enum ValveMode {
        running,
        off,
        fireplace
    }
 
    
    public AtagOne(String email, String password) throws IOException {
        Configuration configuration = Configuration.builder().email(email).password(password).build();
        atagOneConnector = new AtagOneConnectorFactory().getInstance(configuration);
        atagOneConnector.login(); // Login; Either local or remote.
    }
    
    public ValveMode IsRunning() throws IOException {
        
        Map<String, Object> diagnostics = atagOneConnector.getDiagnostics();
        System.out.println(AtagOneConnectorInterface.VALUE_CURRENT_MODE + ": " + diagnostics.get(AtagOneConnectorInterface.VALUE_CURRENT_MODE));
        System.out.println(AtagOneConnectorInterface.VALUE_LATEST_REPORT_TIME + ": " + diagnostics.get(AtagOneConnectorInterface.VALUE_LATEST_REPORT_TIME));
        System.out.println(AtagOneConnectorInterface.VALUE_FLAME_STATUS + ": " + diagnostics.get(AtagOneConnectorInterface.VALUE_FLAME_STATUS));
        System.out.println(AtagOneConnectorInterface.VALUE_BOILER_HEATING_FOR + ": " + diagnostics.get(AtagOneConnectorInterface.VALUE_BOILER_HEATING_FOR));
        System.out.println();
        
        if (diagnostics.get(AtagOneConnectorInterface.VALUE_CURRENT_MODE).equals("fireplace"))
        {
            return ValveMode.fireplace;
        }
        else if (diagnostics.get(AtagOneConnectorInterface.VALUE_BOILER_HEATING_FOR).equals("CV"))
        {
            return ValveMode.running;
        }
        else
        {
            return ValveMode.off;
        }
    }
   

}
