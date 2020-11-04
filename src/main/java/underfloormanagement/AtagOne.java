package underfloormanagement;

import java.io.IOException;
import java.util.Map;
import org.juurlink.atagone.AtagOneConnectorFactory;
import org.juurlink.atagone.AtagOneConnectorInterface;
import org.juurlink.atagone.domain.Configuration;

public class AtagOne {

    AtagOneConnectorInterface atagOneConnector;
    
    enum ValveMode {
        running,
        off,
        fireplace
    }
 
    
    public AtagOne(String email, String password) throws IOException {
        Configuration configuration = Configuration.builder().email(email).password(password).build();
        UnderfloorManagement.logInfo("Logging in to AtagOne");
        atagOneConnector = new AtagOneConnectorFactory().getInstance(configuration);
        atagOneConnector.login(); // Login; Either local or remote.
        UnderfloorManagement.logInfo("Login success");
    }
    
    public ValveMode IsRunning() throws IOException {
        
        Map<String, Object> diagnostics = atagOneConnector.getDiagnostics();
        UnderfloorManagement.logInfo(AtagOneConnectorInterface.VALUE_CURRENT_MODE + ": " + diagnostics.get(AtagOneConnectorInterface.VALUE_CURRENT_MODE));
        UnderfloorManagement.logInfo(AtagOneConnectorInterface.VALUE_LATEST_REPORT_TIME + ": " + diagnostics.get(AtagOneConnectorInterface.VALUE_LATEST_REPORT_TIME));
        UnderfloorManagement.logInfo(AtagOneConnectorInterface.VALUE_FLAME_STATUS + ": " + diagnostics.get(AtagOneConnectorInterface.VALUE_FLAME_STATUS));
        UnderfloorManagement.logInfo(AtagOneConnectorInterface.VALUE_BOILER_HEATING_FOR + ": " + diagnostics.get(AtagOneConnectorInterface.VALUE_BOILER_HEATING_FOR));
        UnderfloorManagement.logInfo("");
        
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
