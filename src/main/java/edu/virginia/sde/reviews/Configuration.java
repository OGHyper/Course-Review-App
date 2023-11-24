package edu.virginia.sde.reviews;
//---------------------------------------ADAPT THIS TO THE PROJECT---------------------------------------
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class Configuration {
    public static final String configurationFilename = "config.json";   // Might need more in config.json

    private URL busStopsURL;

    private URL busLinesURL;

    private String databaseFilename;

    public Configuration() { }

    public URL getBusStopsURL() {
        if (busStopsURL == null) {
            parseJsonConfigFile();
        }
        return busStopsURL;
    }

    public URL getBusLinesURL() {
        if (busLinesURL == null) {
            parseJsonConfigFile();
        }
        return busLinesURL;
    }

    public String getDatabaseFilename() {
        if (databaseFilename == null) {
            parseJsonConfigFile();
        }
        return databaseFilename;
    }

    /**
     * Parse the JSON file config.json to set all three of the fields:
     *  busStopsURL, busLinesURL, databaseFilename
     */
    private void parseJsonConfigFile() {
        try (InputStream inputStream = Objects.requireNonNull(Configuration.class.getResourceAsStream(configurationFilename));
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            //TODO: Parse config.json to set the three fields

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            parseConfig(sb);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseConfig(StringBuilder sb) {
        JSONObject configJson = new JSONObject(sb.toString());
        JSONObject endpoints = configJson.getJSONObject("endpoints");
        try {
            busStopsURL = new URL(endpoints.getString("stops"));
            busLinesURL = new URL(endpoints.getString("lines"));
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid URL", e);
        }
        databaseFilename = configJson.getString("database");
    }
}