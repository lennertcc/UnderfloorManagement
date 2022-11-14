/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package underfloormanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import underfloormanagement.data.VBusLiveSystemReading;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Optional;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author lenne
 */
public class VBusLiveSystem {

    private final URL apiUrl;
    private static final Logger logger = LogManager.getLogger(VBusLiveSystem.class);

    public VBusLiveSystem(String apiUrl) throws IOException, MalformedURLException, NoSuchAlgorithmException,
            KeyManagementException, URISyntaxException {
        InitializeSSL();
        this.apiUrl = new URL(apiUrl);
    }

    public VBusLiveSystemReading[] readLiveSystem() throws Exception, IOException {
        logger.info("VBus readLiveSystem");

        // String body =
        // "filter:%7B%22onlyVisible%22%3Atrue%2C%22fields%22%3A%5B%7B%22id%22%3A%2200_0010_2360_0100_004_2_0%22%2C%22filteredId%22%3A%22ui-id-1%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_0_1_DegreesCelsius%22%2C%22name%22%3A%22Collector%20temperatuur%22%2C%22unit%22%3A%22%20%C2%B0C%22%2C%22unit_code%22%3A%22DegreesCelsius%22%2C%22unitFamily%22%3A%22Temperature%22%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_006_2_0%22%2C%22filteredId%22%3A%22ui-id-2%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_0_1_DegreesCelsius%22%2C%22name%22%3A%22Onder%20temperatuur%22%2C%22unit%22%3A%22%20%C2%B0C%22%2C%22unit_code%22%3A%22DegreesCelsius%22%2C%22unitFamily%22%3A%22Temperature%22%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_008_2_0%22%2C%22filteredId%22%3A%22ui-id-3%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_0_1_DegreesCelsius%22%2C%22name%22%3A%22Boven%20temperatuur%22%2C%22unit%22%3A%22%20%C2%B0C%22%2C%22unit_code%22%3A%22DegreesCelsius%22%2C%22unitFamily%22%3A%22Temperature%22%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_010_2_0%22%2C%22filteredId%22%3A%22ui-id-4%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_0_1_DegreesCelsius%22%2C%22name%22%3A%22CH%20Retour%20temperatuur%22%2C%22unit%22%3A%22%20%C2%B0C%22%2C%22unit_code%22%3A%22DegreesCelsius%22%2C%22unitFamily%22%3A%22Temperature%22%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_012_2_0%22%2C%22filteredId%22%3A%22ui-id-5%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_0_1_DegreesCelsius%22%2C%22name%22%3A%22Kamer%20temperatuur%22%2C%22unit%22%3A%22%20%C2%B0C%22%2C%22unit_code%22%3A%22DegreesCelsius%22%2C%22unitFamily%22%3A%22Temperature%22%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_038_1_0%22%2C%22filteredId%22%3A%22ui-id-6%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_Percent%22%2C%22name%22%3A%22PWM%20A%22%2C%22unit%22%3A%22%25%22%2C%22unit_code%22%3A%22Percent%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_039_1_0%22%2C%22filteredId%22%3A%22ui-id-7%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_Percent%22%2C%22name%22%3A%22PWM%20B%22%2C%22unit%22%3A%22%25%22%2C%22unit_code%22%3A%22Percent%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_096_4_0%22%2C%22filteredId%22%3A%22ui-id-8%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_None%22%2C%22name%22%3A%22Error%20mask%22%2C%22unit%22%3A%22%22%2C%22unit_code%22%3A%22None%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_040_4_0%22%2C%22filteredId%22%3A%22ui-id-9%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_WattHours%22%2C%22name%22%3A%22Heat%20quantity%22%2C%22unit%22%3A%22%20Wh%22%2C%22unit_code%22%3A%22WattHours%22%2C%22unitFamily%22%3A%22Energy%22%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_100_4_0%22%2C%22filteredId%22%3A%22ui-id-10%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_None%22%2C%22name%22%3A%22WAMA%20%28Counter%29%22%2C%22unit%22%3A%22%22%2C%22unit_code%22%3A%22None%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_034_1_0%22%2C%22filteredId%22%3A%22ui-id-11%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_Percent%22%2C%22name%22%3A%22Pump%20speed%20relay%201%22%2C%22unit%22%3A%22%25%22%2C%22unit_code%22%3A%22Percent%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_035_1_0%22%2C%22filteredId%22%3A%22ui-id-12%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_Percent%22%2C%22name%22%3A%22Pump%20speed%20relay%202%22%2C%22unit%22%3A%22%25%22%2C%22unit_code%22%3A%22Percent%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_036_1_0%22%2C%22filteredId%22%3A%22ui-id-13%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_Percent%22%2C%22name%22%3A%22Pump%20speed%20relay%203%22%2C%22unit%22%3A%22%25%22%2C%22unit_code%22%3A%22Percent%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_037_1_0%22%2C%22filteredId%22%3A%22ui-id-14%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_Percent%22%2C%22name%22%3A%22Pump%20speed%20relay%204%22%2C%22unit%22%3A%22%25%22%2C%22unit_code%22%3A%22Percent%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_044_2_0%22%2C%22filteredId%22%3A%22ui-id-18%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_0_01_None%22%2C%22name%22%3A%22SV%20Version%22%2C%22unit%22%3A%22%22%2C%22unit_code%22%3A%22None%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_000_4_0%22%2C%22filteredId%22%3A%22ui-id-19%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22DateTime_1_None%22%2C%22name%22%3A%22System%20date%22%2C%22unit%22%3A%22%22%2C%22unit_code%22%3A%22None%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%5D%7D";
        // String body =
        // "filter:{\"onlyVisible\":true,\"fields\":[{\"id\":\"00_0010_2360_0100_004_2_0\",\"filteredId\":\"ui-id-1\",\"conversions\":[],\"type\":\"Number_0_1_DegreesCelsius\",\"name\":\"Collector
        // temperatuur\",\"unit\":\"
        // °C\",\"unit_code\":\"DegreesCelsius\",\"unitFamily\":\"Temperature\",\"offset\":null,\"display_unit\":null},{\"id\":\"00_0010_2360_0100_006_2_0\",\"filteredId\":\"ui-id-2\",\"conversions\":[],\"type\":\"Number_0_1_DegreesCelsius\",\"name\":\"Onder
        // temperatuur\",\"unit\":\"
        // °C\",\"unit_code\":\"DegreesCelsius\",\"unitFamily\":\"Temperature\",\"offset\":null,\"display_unit\":null},{\"id\":\"00_0010_2360_0100_008_2_0\",\"filteredId\":\"ui-id-3\",\"conversions\":[],\"type\":\"Number_0_1_DegreesCelsius\",\"name\":\"Boven
        // temperatuur\",\"unit\":\"
        // °C\",\"unit_code\":\"DegreesCelsius\",\"unitFamily\":\"Temperature\",\"offset\":null,\"display_unit\":null},{\"id\":\"00_0010_2360_0100_010_2_0\",\"filteredId\":\"ui-id-4\",\"conversions\":[],\"type\":\"Number_0_1_DegreesCelsius\",\"name\":\"CH
        // Retour temperatuur\",\"unit\":\"
        // °C\",\"unit_code\":\"DegreesCelsius\",\"unitFamily\":\"Temperature\",\"offset\":null,\"display_unit\":null},{\"id\":\"00_0010_2360_0100_012_2_0\",\"filteredId\":\"ui-id-5\",\"conversions\":[],\"type\":\"Number_0_1_DegreesCelsius\",\"name\":\"Kamer
        // temperatuur\",\"unit\":\"
        // °C\",\"unit_code\":\"DegreesCelsius\",\"unitFamily\":\"Temperature\",\"offset\":null,\"display_unit\":null},{\"id\":\"00_0010_2360_0100_038_1_0\",\"filteredId\":\"ui-id-6\",\"conversions\":[],\"type\":\"Number_1_Percent\",\"name\":\"PWM
        // A\",\"unit\":\"%\",\"unit_code\":\"Percent\",\"unitFamily\":null,\"offset\":null,\"display_unit\":null},{\"id\":\"00_0010_2360_0100_039_1_0\",\"filteredId\":\"ui-id-7\",\"conversions\":[],\"type\":\"Number_1_Percent\",\"name\":\"PWM
        // B\",\"unit\":\"%\",\"unit_code\":\"Percent\",\"unitFamily\":null,\"offset\":null,\"display_unit\":null},{\"id\":\"00_0010_2360_0100_096_4_0\",\"filteredId\":\"ui-id-8\",\"conversions\":[],\"type\":\"Number_1_None\",\"name\":\"Error
        // mask\",\"unit\":\"\",\"unit_code\":\"None\",\"unitFamily\":null,\"offset\":null,\"display_unit\":null},{\"id\":\"00_0010_2360_0100_040_4_0\",\"filteredId\":\"ui-id-9\",\"conversions\":[],\"type\":\"Number_1_WattHours\",\"name\":\"Heat
        // quantity\",\"unit\":\"
        // Wh\",\"unit_code\":\"WattHours\",\"unitFamily\":\"Energy\",\"offset\":null,\"display_unit\":null},{\"id\":\"00_0010_2360_0100_100_4_0\",\"filteredId\":\"ui-id-10\",\"conversions\":[],\"type\":\"Number_1_None\",\"name\":\"WAMA
        // (Counter)\",\"unit\":\"\",\"unit_code\":\"None\",\"unitFamily\":null,\"offset\":null,\"display_unit\":null},{\"id\":\"00_0010_2360_0100_034_1_0\",\"filteredId\":\"ui-id-11\",\"conversions\":[],\"type\":\"Number_1_Percent\",\"name\":\"Pump
        // speed relay
        // 1\",\"unit\":\"%\",\"unit_code\":\"Percent\",\"unitFamily\":null,\"offset\":null,\"display_unit\":null},{\"id\":\"00_0010_2360_0100_035_1_0\",\"filteredId\":\"ui-id-12\",\"conversions\":[],\"type\":\"Number_1_Percent\",\"name\":\"Pump
        // speed relay
        // 2\",\"unit\":\"%\",\"unit_code\":\"Percent\",\"unitFamily\":null,\"offset\":null,\"display_unit\":null},{\"id\":\"00_0010_2360_0100_036_1_0\",\"filteredId\":\"ui-id-13\",\"conversions\":[],\"type\":\"Number_1_Percent\",\"name\":\"Pump
        // speed relay
        // 3\",\"unit\":\"%\",\"unit_code\":\"Percent\",\"unitFamily\":null,\"offset\":null,\"display_unit\":null},{\"id\":\"00_0010_2360_0100_037_1_0\",\"filteredId\":\"ui-id-14\",\"conversions\":[],\"type\":\"Number_1_Percent\",\"name\":\"Pump
        // speed relay
        // 4\",\"unit\":\"%\",\"unit_code\":\"Percent\",\"unitFamily\":null,\"offset\":null,\"display_unit\":null},{\"id\":\"00_0010_2360_0100_044_2_0\",\"filteredId\":\"ui-id-18\",\"conversions\":[],\"type\":\"Number_0_01_None\",\"name\":\"SV
        // Version\",\"unit\":\"\",\"unit_code\":\"None\",\"unitFamily\":null,\"offset\":null,\"display_unit\":null},{\"id\":\"00_0010_2360_0100_000_4_0\",\"filteredId\":\"ui-id-19\",\"conversions\":[],\"type\":\"DateTime_1_None\",\"name\":\"System
        // date\",\"unit\":\"\",\"unit_code\":\"None\",\"unitFamily\":null,\"offset\":null,\"display_unit\":null}]}";
        // String body =
        // "filter%3A%7B%22onlyVisible%22%3Atrue%2C%22fields%22%3A%5B%7B%22id%22%3A%2200_0010_2360_0100_004_2_0%22%2C%22filteredId%22%3A%22ui-id-1%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_0_1_DegreesCelsius%22%2C%22name%22%3A%22Collector%20temperatuur%22%2C%22unit%22%3A%22%20%C2%B0C%22%2C%22unit_code%22%3A%22DegreesCelsius%22%2C%22unitFamily%22%3A%22Temperature%22%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_006_2_0%22%2C%22filteredId%22%3A%22ui-id-2%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_0_1_DegreesCelsius%22%2C%22name%22%3A%22Onder%20temperatuur%22%2C%22unit%22%3A%22%20%C2%B0C%22%2C%22unit_code%22%3A%22DegreesCelsius%22%2C%22unitFamily%22%3A%22Temperature%22%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_008_2_0%22%2C%22filteredId%22%3A%22ui-id-3%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_0_1_DegreesCelsius%22%2C%22name%22%3A%22Boven%20temperatuur%22%2C%22unit%22%3A%22%20%C2%B0C%22%2C%22unit_code%22%3A%22DegreesCelsius%22%2C%22unitFamily%22%3A%22Temperature%22%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_010_2_0%22%2C%22filteredId%22%3A%22ui-id-4%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_0_1_DegreesCelsius%22%2C%22name%22%3A%22CH%20Retour%20temperatuur%22%2C%22unit%22%3A%22%20%C2%B0C%22%2C%22unit_code%22%3A%22DegreesCelsius%22%2C%22unitFamily%22%3A%22Temperature%22%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_012_2_0%22%2C%22filteredId%22%3A%22ui-id-5%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_0_1_DegreesCelsius%22%2C%22name%22%3A%22Kamer%20temperatuur%22%2C%22unit%22%3A%22%20%C2%B0C%22%2C%22unit_code%22%3A%22DegreesCelsius%22%2C%22unitFamily%22%3A%22Temperature%22%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_038_1_0%22%2C%22filteredId%22%3A%22ui-id-6%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_Percent%22%2C%22name%22%3A%22PWM%20A%22%2C%22unit%22%3A%22%25%22%2C%22unit_code%22%3A%22Percent%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_039_1_0%22%2C%22filteredId%22%3A%22ui-id-7%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_Percent%22%2C%22name%22%3A%22PWM%20B%22%2C%22unit%22%3A%22%25%22%2C%22unit_code%22%3A%22Percent%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_096_4_0%22%2C%22filteredId%22%3A%22ui-id-8%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_None%22%2C%22name%22%3A%22Error%20mask%22%2C%22unit%22%3A%22%22%2C%22unit_code%22%3A%22None%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_040_4_0%22%2C%22filteredId%22%3A%22ui-id-9%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_WattHours%22%2C%22name%22%3A%22Heat%20quantity%22%2C%22unit%22%3A%22%20Wh%22%2C%22unit_code%22%3A%22WattHours%22%2C%22unitFamily%22%3A%22Energy%22%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_100_4_0%22%2C%22filteredId%22%3A%22ui-id-10%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_None%22%2C%22name%22%3A%22WAMA%20%28Counter%29%22%2C%22unit%22%3A%22%22%2C%22unit_code%22%3A%22None%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_034_1_0%22%2C%22filteredId%22%3A%22ui-id-11%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_Percent%22%2C%22name%22%3A%22Pump%20speed%20relay%201%22%2C%22unit%22%3A%22%25%22%2C%22unit_code%22%3A%22Percent%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_035_1_0%22%2C%22filteredId%22%3A%22ui-id-12%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_Percent%22%2C%22name%22%3A%22Pump%20speed%20relay%202%22%2C%22unit%22%3A%22%25%22%2C%22unit_code%22%3A%22Percent%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_036_1_0%22%2C%22filteredId%22%3A%22ui-id-13%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_Percent%22%2C%22name%22%3A%22Pump%20speed%20relay%203%22%2C%22unit%22%3A%22%25%22%2C%22unit_code%22%3A%22Percent%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_037_1_0%22%2C%22filteredId%22%3A%22ui-id-14%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_Percent%22%2C%22name%22%3A%22Pump%20speed%20relay%204%22%2C%22unit%22%3A%22%25%22%2C%22unit_code%22%3A%22Percent%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_044_2_0%22%2C%22filteredId%22%3A%22ui-id-18%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_0_01_None%22%2C%22name%22%3A%22SV%20Version%22%2C%22unit%22%3A%22%22%2C%22unit_code%22%3A%22None%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_000_4_0%22%2C%22filteredId%22%3A%22ui-id-19%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22DateTime_1_None%22%2C%22name%22%3A%22System%20date%22%2C%22unit%22%3A%22%22%2C%22unit_code%22%3A%22None%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%5D%7D";
        // String body =
        // "filter=%7B%22onlyVisible%22%3Atrue%2C%22fields%22%3A%5B%7B%22id%22%3A%2200_0010_2360_0100_004_2_0%22%2C%22filteredId%22%3A%22ui-id-1%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_0_1_DegreesCelsius%22%2C%22name%22%3A%22Collector%20temperatuur%22%2C%22unit%22%3A%22%20%C2%B0C%22%2C%22unit_code%22%3A%22DegreesCelsius%22%2C%22unitFamily%22%3A%22Temperature%22%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_006_2_0%22%2C%22filteredId%22%3A%22ui-id-2%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_0_1_DegreesCelsius%22%2C%22name%22%3A%22Onder%20temperatuur%22%2C%22unit%22%3A%22%20%C2%B0C%22%2C%22unit_code%22%3A%22DegreesCelsius%22%2C%22unitFamily%22%3A%22Temperature%22%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_008_2_0%22%2C%22filteredId%22%3A%22ui-id-3%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_0_1_DegreesCelsius%22%2C%22name%22%3A%22Boven%20temperatuur%22%2C%22unit%22%3A%22%20%C2%B0C%22%2C%22unit_code%22%3A%22DegreesCelsius%22%2C%22unitFamily%22%3A%22Temperature%22%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_010_2_0%22%2C%22filteredId%22%3A%22ui-id-4%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_0_1_DegreesCelsius%22%2C%22name%22%3A%22CH%20Retour%20temperatuur%22%2C%22unit%22%3A%22%20%C2%B0C%22%2C%22unit_code%22%3A%22DegreesCelsius%22%2C%22unitFamily%22%3A%22Temperature%22%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_012_2_0%22%2C%22filteredId%22%3A%22ui-id-5%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_0_1_DegreesCelsius%22%2C%22name%22%3A%22Kamer%20temperatuur%22%2C%22unit%22%3A%22%20%C2%B0C%22%2C%22unit_code%22%3A%22DegreesCelsius%22%2C%22unitFamily%22%3A%22Temperature%22%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_038_1_0%22%2C%22filteredId%22%3A%22ui-id-6%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_Percent%22%2C%22name%22%3A%22PWM%20A%22%2C%22unit%22%3A%22%25%22%2C%22unit_code%22%3A%22Percent%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_039_1_0%22%2C%22filteredId%22%3A%22ui-id-7%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_Percent%22%2C%22name%22%3A%22PWM%20B%22%2C%22unit%22%3A%22%25%22%2C%22unit_code%22%3A%22Percent%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_096_4_0%22%2C%22filteredId%22%3A%22ui-id-8%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_None%22%2C%22name%22%3A%22Error%20mask%22%2C%22unit%22%3A%22%22%2C%22unit_code%22%3A%22None%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_040_4_0%22%2C%22filteredId%22%3A%22ui-id-9%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_WattHours%22%2C%22name%22%3A%22Heat%20quantity%22%2C%22unit%22%3A%22%20Wh%22%2C%22unit_code%22%3A%22WattHours%22%2C%22unitFamily%22%3A%22Energy%22%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_100_4_0%22%2C%22filteredId%22%3A%22ui-id-10%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_None%22%2C%22name%22%3A%22WAMA%20%28Counter%29%22%2C%22unit%22%3A%22%22%2C%22unit_code%22%3A%22None%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_034_1_0%22%2C%22filteredId%22%3A%22ui-id-11%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_Percent%22%2C%22name%22%3A%22Pump%20speed%20relay%201%22%2C%22unit%22%3A%22%25%22%2C%22unit_code%22%3A%22Percent%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_035_1_0%22%2C%22filteredId%22%3A%22ui-id-12%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_Percent%22%2C%22name%22%3A%22Pump%20speed%20relay%202%22%2C%22unit%22%3A%22%25%22%2C%22unit_code%22%3A%22Percent%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_036_1_0%22%2C%22filteredId%22%3A%22ui-id-13%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_Percent%22%2C%22name%22%3A%22Pump%20speed%20relay%203%22%2C%22unit%22%3A%22%25%22%2C%22unit_code%22%3A%22Percent%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_037_1_0%22%2C%22filteredId%22%3A%22ui-id-14%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_1_Percent%22%2C%22name%22%3A%22Pump%20speed%20relay%204%22%2C%22unit%22%3A%22%25%22%2C%22unit_code%22%3A%22Percent%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_044_2_0%22%2C%22filteredId%22%3A%22ui-id-18%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22Number_0_01_None%22%2C%22name%22%3A%22SV%20Version%22%2C%22unit%22%3A%22%22%2C%22unit_code%22%3A%22None%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%2C%7B%22id%22%3A%2200_0010_2360_0100_000_4_0%22%2C%22filteredId%22%3A%22ui-id-19%22%2C%22conversions%22%3A%5B%5D%2C%22type%22%3A%22DateTime_1_None%22%2C%22name%22%3A%22System%20date%22%2C%22unit%22%3A%22%22%2C%22unit_code%22%3A%22None%22%2C%22unitFamily%22%3Anull%2C%22offset%22%3Anull%2C%22display_unit%22%3Anull%7D%5D%7D";
        // byte[] postData = body.getBytes(StandardCharsets.UTF_8);

        HttpsURLConnection connection = (HttpsURLConnection) apiUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Accept", "application/json");
        connection.setConnectTimeout(5000);

        int httpResult = connection.getResponseCode();
        logger.info(String.format("VBus readLiveSystem response code %d", httpResult));
        if (httpResult == HttpURLConnection.HTTP_OK) {

            InputStream content = (InputStream) connection.getInputStream();
            String stringContent = new String(content.readAllBytes(), StandardCharsets.UTF_8);
            content.close();

            logger.info(String.format("String content length: %d", stringContent.length()));

            // JSON from file to Object
            ObjectMapper mapper = new ObjectMapper();
            VBusLiveSystemReading[] liveSystem = mapper.readValue(stringContent, VBusLiveSystemReading[].class);

            return liveSystem;
        } else {
            String errorMessage = String.format("HttpResponseCode: %d, HttpResponseMessage: %s", httpResult,
                    connection.getResponseMessage());
            logger.error(errorMessage, (Exception) null);
            throw new Exception(errorMessage);
        }
    }

    public String PercentagePumpBRunning(VBusLiveSystemReading[] liveSystem) throws IOException {
        logger.info("VBus pump status");

        Optional<VBusLiveSystemReading> PWMB = Arrays.stream(liveSystem).filter(x -> x.getName().equals("PWM B"))
                .findAny();
        Optional<VBusLiveSystemReading> SystemTime = Arrays.stream(liveSystem)
                .filter(x -> x.getName().equals("System date")).findAny();

        if (PWMB.isPresent()) {
            logger.info("SystemTime: " + SystemTime.get().getValue());
            logger.info("PWM B: " + PWMB.get().getValue());
            logger.info("");
            return PWMB.get().getValue();
        } else {
            logger.info("PWM B not present in diagnostics");
            logger.info("");
        }
        return "0";
    }

    public static void InitializeSSL() throws IOException, NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, null, null);
        SSLContext.setDefault(sslContext);
    }

}
