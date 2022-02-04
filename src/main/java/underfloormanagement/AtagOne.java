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

    private boolean isLocal;
    
    public AtagOne(String email, String password) throws IOException {

        try {
            Configuration configuration = Configuration.builder().build();
            UnderfloorManagement.logInfo("Logging in to AtagOne local");
            atagOneConnector = new AtagOneConnectorFactory().getInstance(configuration);
            atagOneConnector.login(); // Login local
            isLocal = true;
            UnderfloorManagement.logInfo("Login local success");
            return;
        }
        catch(IOException ioException)
        {
            UnderfloorManagement.logError("Login local failed", ioException);
        }

        try{
            Configuration configuration = Configuration.builder().email(email).password(password).build();
            UnderfloorManagement.logInfo("Logging in to AtagOne remote");
            atagOneConnector = new AtagOneConnectorFactory().getInstance(configuration);
            atagOneConnector.login(); // Login remote
            isLocal = false;
            UnderfloorManagement.logInfo("Login remote success");
        }
        catch(IOException ioException)
        {
            UnderfloorManagement.logError("Login remote failed", ioException);
            throw ioException;
        }
    }
    
    public ValveMode IsRunning() throws IOException {
        
        Map<String, Object> diagnostics = atagOneConnector.getDiagnostics();

        if (isLocal)
        {
            int chStatus = Integer.parseInt(diagnostics.get("chStatus").toString());
            int chMode = Integer.parseInt(diagnostics.get("chMode").toString());
            UnderfloorManagement.logInfo("ch_status: " + chStatus);
            UnderfloorManagement.logInfo("ch_status&4: " + (chStatus & 4));
            UnderfloorManagement.logInfo("ch_mode: " + chMode);
            UnderfloorManagement.logInfo("flameStatus: " + diagnostics.get(AtagOneConnectorInterface.VALUE_FLAME_STATUS));
            UnderfloorManagement.logInfo("");

            if (chMode == 5) {
                return ValveMode.fireplace;
            }
            else if ((chStatus & 4) == 4) {
                return ValveMode.running;
            }
            else {
                return ValveMode.off;
            }
        }
        else
        {
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
   

}
