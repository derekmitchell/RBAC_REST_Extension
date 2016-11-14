package com.appdynamics.ace.extension.rest.command.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: stefan.marx
 * Date: 22.10.13
 * Time: 01:59
 * To change this template use File | Settings | File Templates.
 */
public class RestException extends WebApplicationException {
    public RestException(String message) {
        super(Response.status(Response.Status.BAD_REQUEST)
                .entity(message).type(MediaType.TEXT_PLAIN).build());
    }

    public RestException(Response.Status code,String message) {
        super(Response.status(code)
                .entity(message).type(MediaType.TEXT_PLAIN).build());
    }
}
