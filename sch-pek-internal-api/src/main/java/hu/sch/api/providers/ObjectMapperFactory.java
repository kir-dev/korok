package hu.sch.api.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import hu.sch.services.config.Configuration;
import hu.sch.services.config.Environment;

/**
 * Custom factory for creating a pre-configured ObjectMapper for PÉK.
 * @author tomi
 */
public class ObjectMapperFactory {

    private Configuration config;

    public ObjectMapperFactory(Configuration config) {
        this.config = config;
    }

    public ObjectMapper createMapper() {
        return new ObjectMapper()
                .registerModule(new Hibernate4Module())
                .configure(SerializationFeature.INDENT_OUTPUT, indent());
    }

    private boolean indent() {
        return config.getEnvironment() == Environment.DEVELOPMENT;
    }
}
