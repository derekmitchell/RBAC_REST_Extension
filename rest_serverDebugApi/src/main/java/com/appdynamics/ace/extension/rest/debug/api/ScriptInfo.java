package com.appdynamics.ace.extension.rest.debug.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: stefan.marx
 * Date: 08.11.13
 * Time: 22:37
 * To change this template use File | Settings | File Templates.
 */
public class ScriptInfo {
    private int _lastVersion;

    public void setName(String name) {
        _name = name;
    }

    public void setLastVersion(int lastVersion) {
        _lastVersion = lastVersion;
    }

    private String _name;

    public void setVersionInfos(List<ScriptVersionInfo> versionInfos) {
        _versionInfos = versionInfos;
    }

    private List<ScriptVersionInfo> _versionInfos = new ArrayList<ScriptVersionInfo>();

    public ScriptInfo() {}

    public ScriptInfo(String scriptName, int currentScriptVersion) {
        _lastVersion = currentScriptVersion;
        _name = scriptName;

    }

    public int getLastVersion() {
        return _lastVersion;
    }

    public String getName() {
        return _name;
    }

    public List<ScriptVersionInfo> getHistoryVersions() {
        //TODO :
        return _versionInfos;
    }
}
