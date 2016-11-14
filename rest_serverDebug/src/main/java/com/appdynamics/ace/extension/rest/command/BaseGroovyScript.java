package com.appdynamics.ace.extension.rest.command;

import com.singularity.ee.controller.api.exceptions.ServerException;

import com.appdynamics.ace.extension.rest.util.BeanLocator;

import groovy.lang.GroovyRuntimeException;
import groovy.lang.Script;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;

public abstract class BaseGroovyScript extends Script {

    private GroovyConsoleService _console;
    private HashSet<String> _scripts;

    public Object invokeMethod(String name, Object args) {

        if (!hasLocalMethod(name) && hasScript(name))
            return executeScript(name,args);

        else return super.invokeMethod(name,args);
    }

    private Object executeScript(String name, Object args)  {
       try {
           BaseGroovyScript script = _console.createScript(name,args,_console.createBinding());

             return  script.run();
       } catch (Exception e) {
           throw new GroovyRuntimeException("Error while Executing Script",e);
       }
    }

    private boolean hasScript(String name) {
        return _scripts.contains(name);
    }

    private boolean hasLocalMethod(String name) {
        for (Method m : this.getClass().getMethods()) {
            if (m.getName().equals(name)) return true;
        }
        return false;
    }

    public void setConsole(GroovyConsoleService console) {
        _console = console;
        _scripts = new HashSet<String>();
        _scripts.addAll(console.listScripts());
    }

    public GroovyConsoleService getConsole() {
        return _console;
    }

    public String helloWorld (String world) throws ServerException {

        return world.toUpperCase();
    }
}