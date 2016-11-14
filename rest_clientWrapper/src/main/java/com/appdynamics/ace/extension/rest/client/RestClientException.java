package com.appdynamics.ace.extension.rest.client;

import com.sun.jersey.api.client.ClientResponse;

/**
 * Created with IntelliJ IDEA.
 * User: stefan.marx
 * Date: 22.10.13
 * Time: 03:21
 * To change this template use File | Settings | File Templates.
 */
public class RestClientException extends Exception {
    public RestClientException(int code, String message) {
        super (code +" : "+message);
    }

    public RestClientException(ClientResponse response) {
        this (response.getStatus(),response.getEntity(String.class));
    }
}
