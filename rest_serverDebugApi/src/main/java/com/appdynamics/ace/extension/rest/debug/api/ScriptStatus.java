package com.appdynamics.ace.extension.rest.debug.api;

/**
 * Created with IntelliJ IDEA.
 * User: stefan.marx
 * Date: 11.11.13
 * Time: 19:21
 * To change this template use File | Settings | File Templates.
 */
public enum ScriptStatus {
    ERROR(2),FATAL(3),OK(1);

    private final int _status;

    ScriptStatus(int statusCode) {
        _status = statusCode;

    }

}
