package com.ft.whakataki.lambda.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Preconditions;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ConfigurationLoader {

    private static final String PROPERTIES_FILE_SUFFIX = "-blazegraph.yaml";

    private static final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    private static Map<String, Configuration> configMap = new HashMap<String, Configuration>();

    protected ConfigurationLoader() {}

    public static Configuration getBlazeGraphRepositoryConfiguration(String env) {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        Preconditions.checkNotNull(env,"Environment is null: " + env);
        Configuration configuration = configMap.get(env);
        if (configuration == null) {

            String propertiesFileName = "/"+ env + PROPERTIES_FILE_SUFFIX;
            try {
                configuration = objectMapper.readValue(IOUtils.toString(ConfigurationLoader.class.getResourceAsStream(propertiesFileName), "UTF-8"), Configuration.class);
                configMap.put(env, configuration);
            } catch (IOException e) {
                throw new RuntimeException(propertiesFileName, e);
            }
        }
        return configuration;
    }

}
