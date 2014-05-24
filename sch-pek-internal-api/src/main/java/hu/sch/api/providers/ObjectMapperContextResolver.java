package hu.sch.api.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.sch.services.config.Configuration;
import javax.inject.Inject;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author tomi
 */
@Provider
public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

    @Inject
    private Configuration config;
    private ObjectMapper mapper;

    @Override
    public ObjectMapper getContext(Class<?> type) {
        if (mapper == null) {
            mapper = new ObjectMapperFactory(config).createMapper();
        }
        return mapper;
    }
}
