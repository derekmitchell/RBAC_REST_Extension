package com.appdynamics.ace.extension.rest.debug.api;


/**
 * Created with IntelliJ IDEA.
 * User: stefan.marx
 * Date: 11.11.13
 * Time: 19:16
 * To change this template use File | Settings | File Templates.
 */
public class ScriptResult {
    private String _stringResult;
    private String _classname;
    private ScriptStatus _status;

    public ScriptResult() {}

    public ScriptResult(ScriptStatus status) {
        setStatus(status);
    }

    public void setStringResult(String stringResult) {
        _stringResult = stringResult;
    }

    public String getStringResult() {
        return _stringResult;
    }

    public void setClassname(String classname) {
        _classname = classname;
    }

    public String getClassname() {
        return _classname;
    }

    public void setStatus(ScriptStatus status) {
        _status = status;
    }

    public ScriptStatus getStatus() {
        return _status;
    }
}
