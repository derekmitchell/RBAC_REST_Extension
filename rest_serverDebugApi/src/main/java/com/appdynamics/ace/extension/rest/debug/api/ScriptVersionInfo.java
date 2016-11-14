package com.appdynamics.ace.extension.rest.debug.api;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: stefan.marx
 * Date: 09.11.13
 * Time: 00:10
 * To change this template use File | Settings | File Templates.
 */
public class ScriptVersionInfo {
    private int _version = 0;
    private Date _lastModified = null;

    public int getVersion() {
        return _version;
    }

    public void setVersion(int version) {
        _version = version;
    }

    public Date getLastModifyDate() {
        return _lastModified;
    }

    public void setLastModifyDate(Date lastModified) {
        _lastModified = lastModified;
    }

    public ScriptVersionInfo(){};
    public ScriptVersionInfo(int versionNumber,Date lastModified) {
        _version = versionNumber;
        _lastModified = lastModified;

    }


}
