package com.appdynamics.ace.extension.rest.debug.api;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: stefan.marx
 * Date: 12.11.13
 * Time: 00:25
 * To change this template use File | Settings | File Templates.
 */
public class ConsoleInfo {
    private String _scriptPath;
    private int _numScripts;

    public ConsoleInfo() {

    }

    public ConsoleInfo(File path, int size) {
        setScriptPath(path.getAbsolutePath());
        setNumScripts(size);

    }

    public void setScriptPath(String scriptPath) {
        _scriptPath = scriptPath;
    }

    public String getScriptPath() {
        return _scriptPath;
    }

    public void setNumScripts(int numScripts) {
        _numScripts = numScripts;
    }

    public int getNumScripts() {
        return _numScripts;
    }
}
