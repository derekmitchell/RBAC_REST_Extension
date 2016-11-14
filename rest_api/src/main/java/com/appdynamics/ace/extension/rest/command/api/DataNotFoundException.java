package com.appdynamics.ace.extension.rest.command.api;

import javax.ws.rs.core.Response;

/**
 * Created with IntelliJ IDEA.
 * User: stefan.marx
 * Date: 22.10.13
 * Time: 01:59
 * To change this template use File | Settings | File Templates.
 */
public class DataNotFoundException extends RestException {
    public DataNotFoundException(String message) {
        super(Response.Status.NOT_FOUND,message);
    }

}
