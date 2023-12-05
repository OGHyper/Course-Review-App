package edu.virginia.sde.reviews;
//---------------------------------------ADAPT THIS TO THE PROJECT---------------------------------------

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class Configuration {
    public static final String configurationFilename = "config.json";   // Might need more in config.json

    private String databaseFilename;

    public Configuration() { }

    public String getDatabaseFilename() {
        if (databaseFilename == null) {
            parseJsonConfigFile();
        }
        return databaseFilename;
    }

    private void parseJsonConfigFile() {
        try (InputStream inputStream = Objects.requireNonNull(Configuration.class.getResourceAsStream(configurationFilename));
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
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
        databaseFilename = configJson.getString("database");
    }
}