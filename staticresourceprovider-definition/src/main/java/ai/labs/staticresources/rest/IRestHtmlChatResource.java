package ai.labs.staticresources.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * @author ginccc
 */

@Path("/chat")
public interface IRestHtmlChatResource {

    @GET
    String viewDefault();

    @GET
    @Path("{path:.*}")
    String viewHtml(@PathParam("path") String path);
}
