package underfloormanagement;

import java.io.IOException;
import java.util.Map;
import org.juurlink.atagone.AtagOneConnectorFactory;
import org.juurlink.atagone.AtagOneConnectorInterface;
import org.juurlink.atagone.domain.Configuration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class AtagOne {

    AtagOneConnectorInterface atagOneConnector;
    private static final Logger logger = LogManager.getLogger(AtagOne.class);

    enum ValveMode {
        running,
        off,
        fireplace
    }

    private boolean isLocal;

    public AtagOne(String email, String password) throws IOException {

        try {
            Configuration configuration = Configuration.builder().build();
            logger.info("Logging in to AtagOne local");
            atagOneConnector = new AtagOneConnectorFactory().getInstance(configuration);
            atagOneConnector.login(); // Login local
            isLocal = true;
            logger.info("Login local success");
            return;
        } catch (IOException ioException) {
            logger.error("Login local failed", ioException);
        }

        try {
            Configuration configuration = Configuration.builder().email(email).password(password).build();
            logger.info("Logging in to AtagOne remote");
            atagOneConnector = new AtagOneConnectorFactory().getInstance(configuration);
            atagOneConnector.login(); // Login remote
            isLocal = false;
            logger.info("Login remote success");
        } catch (IOException ioException) {
            logger.error("Login remote failed", ioException);
            throw ioException;
        }
    }

    public ValveMode IsRunning() throws IOException {

        Map<String, Object> diagnostics = atagOneConnector.getDiagnostics();

        if (isLocal) {
            int chStatus = Integer.parseInt(diagnostics.get("chStatus").toString());
            int chMode = Integer.parseInt(diagnostics.get("chMode").toString());
            logger.info("ch_status: " + chStatus);
            logger.info("ch_status&4: " + (chStatus & 4));
            logger.info("ch_mode: " + chMode);
            logger.info("flameStatus: " + diagnostics.get(AtagOneConnectorInterface.VALUE_FLAME_STATUS));
            logger.info("");

            if (chMode == 5) {
                return ValveMode.fireplace;
            } else if ((chStatus & 4) == 4) {
                return ValveMode.running;
            } else {
                return ValveMode.off;
            }
        } else {
            logger.info(AtagOneConnectorInterface.VALUE_CURRENT_MODE + ": "
                    + diagnostics.get(AtagOneConnectorInterface.VALUE_CURRENT_MODE));
            logger.info(AtagOneConnectorInterface.VALUE_LATEST_REPORT_TIME + ": "
                    + diagnostics.get(AtagOneConnectorInterface.VALUE_LATEST_REPORT_TIME));
            logger.info(AtagOneConnectorInterface.VALUE_FLAME_STATUS + ": "
                    + diagnostics.get(AtagOneConnectorInterface.VALUE_FLAME_STATUS));
            logger.info(AtagOneConnectorInterface.VALUE_BOILER_HEATING_FOR + ": "
                    + diagnostics.get(AtagOneConnectorInterface.VALUE_BOILER_HEATING_FOR));
            logger.info("");

            if (diagnostics.get(AtagOneConnectorInterface.VALUE_CURRENT_MODE).equals("fireplace")) {
                return ValveMode.fireplace;
            } else if (diagnostics.get(AtagOneConnectorInterface.VALUE_BOILER_HEATING_FOR).equals("CV")) {
                return ValveMode.running;
            } else {
                return ValveMode.off;
            }
        }

    }

}
