package com.appdynamics.ace.extension.rest.util.command;

import com.appdynamics.ace.extension.rest.util.RestExtServlet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by stefan.marx on 18.12.13.
 */
@Path("extensionInfo")
public class ExtensionInfoRest {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ExtensionInfo info(){
        ExtensionInfo info = new ExtensionInfo();
        return info;
    }

    @Path ("reload")
    @GET
    public String reloadResourceContainer () {
        RestExtServlet servlet = RestExtServlet.getInstance();
        servlet.reloadContext();

        return "Reloaded!";
    }
}
