package hu.sch.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Common base class for all api endpoints. Provides Accept and Content-Type
 * headers.
 *
 * @author tomi
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public abstract class Base {
}
