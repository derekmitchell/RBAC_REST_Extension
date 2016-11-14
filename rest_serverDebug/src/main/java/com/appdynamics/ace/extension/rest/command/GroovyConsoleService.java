package com.appdynamics.ace.extension.rest.command;

import com.appdynamics.ace.extension.rest.command.api.RestException;
import com.appdynamics.ace.extension.rest.debug.api.*;
import groovy.lang.Binding;
import groovy.lang.GroovyObject;
import groovy.lang.Script;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import org.codehaus.groovy.control.CompilerConfiguration;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: stefan.marx
 * Date: 08.11.13
 * Time: 22:24
 * To change this template use File | Settings | File Templates.
 */
@Path("aceGroovy")
public class GroovyConsoleService {
    public static final String VERSION_POSTFIX = "_V_";
    public static final String GROOVY_POSTFIX = "groovy";
    private final File _path;
    private final File _historyPath;
    private final GroovyScriptEngine _gse;

    public GroovyConsoleService() throws Exception {
        this("../state/scripts");
    }
    public GroovyConsoleService(String path) throws Exception {
        _path = new File(path);

        _historyPath = new File(_path,"hist");

        if (_path.exists() &&  !(_path.isDirectory() && _path.canWrite()) ) {
            throw new Exception("Error, couldn't write to Path "+_path.getAbsolutePath());
        }
        createScriptDirs();

        String[] roots = new String[] {_path.getAbsolutePath()};
        _gse = new GroovyScriptEngine(roots);
        _gse.getConfig().setScriptBaseClass("com.appdynamics.ace.extension.rest.command.BaseGroovyScript");
    }

    private void createScriptDirs() throws Exception {
        if (!_path.exists()) {
            if (!_path.mkdirs()) throw new Exception ("Error while creating path !!! ");
        }
        if (!_historyPath.exists()) {
            if (!_historyPath.mkdirs()) throw new Exception ("Error while creating path !!! ");
        }
    }

    public void deleteAllScripts() throws RestException {
        // Be Carefull this is DANGEROUS
        for (File f :_path.listFiles()) {
            if (!f.isDirectory())f.delete();
        }
        clearScriptHistory();

    }



    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("script/{name}")
    public ScriptInfo uploadGroovy(@PathParam("name") String scriptName, String newSrc) {
        File scriptFile = new File(_path, scriptName + "." + GROOVY_POSTFIX);
        if (scriptFile.exists()) {
            try {
                String oldSRC = readFile(scriptFile);
                if (!oldSRC.equals(newSrc)) moveToNewVersion(scriptName,scriptFile);
                else return getScriptInfo(scriptName);
            } catch (IOException e) {
                throw new RestException(Response.Status.INTERNAL_SERVER_ERROR,"Error while comparing with old File / Versioning");
            }
        }

        Writer wrt = null;
        try {
            wrt = new BufferedWriter(new FileWriter(scriptFile));
            wrt.write(newSrc);
            wrt.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RestException(Response.Status.INTERNAL_SERVER_ERROR,"Error while writing File // ");
        }   finally {

            if (wrt != null) {
                try {wrt.close();} catch (IOException e) {}
            }
        }

        return getScriptInfo(scriptName);
    }

    private void moveToNewVersion(String scriptName, File scriptFile) {
        String name = scriptFile.getName();

        int currentVersion = findCurrentScriptVersion(scriptName);
        String newName = name+VERSION_POSTFIX+currentVersion;
        scriptFile.renameTo(new File(_historyPath,newName));

    }

    private int findCurrentScriptVersion(String name) {
        String[] versions = _historyPath.list(new VersionFileFilter(name));
        String postfix = "." + GROOVY_POSTFIX + VERSION_POSTFIX;

        int highestVersion = 0;
        for (String vname : versions) {
            int i = Integer.parseInt(vname.substring(name.length()+postfix.length()));
            highestVersion = (i>highestVersion)?i:highestVersion;
        }
        return highestVersion+1;
    }

    private String readFile(File scriptFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(scriptFile));
        StringBuilder sb = new StringBuilder();
        String line;
        boolean firstLine = true;
        while ((line = reader.readLine())!= null) {

            if (firstLine) firstLine = false;
            else sb.append("\n");

            sb.append(line);
        }

        reader.close();
        return sb.toString();
    }

    @GET
    @Produces (MediaType.APPLICATION_JSON)
    @Path("scripts")
    public List<String> listScripts() {

        String[] names = _path.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("." + GROOVY_POSTFIX);
            }
        });;


        ArrayList<String> erg = new ArrayList<String>();

        for (String t: names) {
             erg.add(t.substring(0,t.length()-(GROOVY_POSTFIX.length()+1)));
        }

        return erg;
    }

    @GET
    @Produces (MediaType.APPLICATION_JSON)
    @Path("scriptinfo/{name}")
    public ScriptInfo getScriptInfo(@PathParam("name") String scriptName) {

        ScriptInfo si = new ScriptInfo(scriptName, findCurrentScriptVersion(scriptName));
        si.getHistoryVersions().addAll(findAvailableVersions(scriptName));
        return si;
    }

    private Collection<ScriptVersionInfo> findAvailableVersions(String scriptName) {
        ArrayList<ScriptVersionInfo> versionInfos = new ArrayList<ScriptVersionInfo>();

        File[] versions = _historyPath.listFiles(new VersionFileFilter(scriptName));
        String postfix = "." + GROOVY_POSTFIX + VERSION_POSTFIX;

        for (File vFile : versions) {
            int i = Integer.parseInt(vFile.getName().substring(scriptName.length() + postfix.length()));

            versionInfos.add(new ScriptVersionInfo(i,new Date(vFile.lastModified())));
        }

        return versionInfos;
    }

    @GET
    @Produces (MediaType.TEXT_PLAIN)
    @Path("script/{name}")
    public String getScriptSource(@PathParam("name") String scriptName) {

        File f = new File (_path,scriptName+"."+GROOVY_POSTFIX);
        if (!f.exists()) throw new RestException(Response.Status.NOT_FOUND,"Script Not Found :"+scriptName);
        if (!f.canRead()) throw new RestException(Response.Status.INTERNAL_SERVER_ERROR,"Script Not Readable :"+scriptName + "(PATH:"+f.getAbsolutePath());

        try {
            return readFile(f);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RestException(Response.Status.INTERNAL_SERVER_ERROR,"Error while Reading:"
                        +scriptName + "(PATH:"+f.getAbsolutePath()+"  (Error:"+e.getMessage());
        }
    }

    @GET
    @Produces (MediaType.TEXT_PLAIN)
    @Path("script/{name}/{version}")
    public String getScriptSource(@PathParam("name")String scriptName, @PathParam("version") int version) {
        File f = new File (_historyPath,scriptName+"."+GROOVY_POSTFIX+VERSION_POSTFIX+version);
        if (!f.exists()) throw new RestException(Response.Status.NOT_FOUND,"Script Not Found :"+scriptName+" with Version "+version);
        if (!f.canRead()) throw new RestException(Response.Status.INTERNAL_SERVER_ERROR,"Script Not Readable :"+scriptName + "(PATH:"+f.getAbsolutePath());

        try {
            return readFile(f);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RestException(Response.Status.INTERNAL_SERVER_ERROR,"Error while Reading:"
                    +scriptName + "(PATH:"+f.getAbsolutePath()+"  (Error:"+e.getMessage());
        }
    }

    @GET
    @Path("admin/clearHistory")
    public void clearScriptHistory() {
        for (File f :_historyPath.listFiles()) {
            if (!f.isDirectory())f.delete();
        }
    }

    @DELETE
    @Path("script/{name}")
    public void deleteScript(@PathParam("name")String name) {
        try {
            moveToNewVersion(name,new File(_path,name+"."+GROOVY_POSTFIX));
        } catch (Exception ex) {};

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("execute/{name}")
    public ScriptResult executeScript(@PathParam("name") String scriptname) {
        try {


            Binding binding = createBinding();

            BaseGroovyScript originalScript = (BaseGroovyScript) _gse.createScript(scriptname + "." + GROOVY_POSTFIX, binding);

            originalScript.setConsole(this);

            Object erg = originalScript.run();


            return wrapResult(erg);
        } catch (Exception e) {
            e.printStackTrace();


            return wrapException(e);
        }
    }

    BaseGroovyScript createScript(String scriptName, Binding binding) throws ResourceException, ScriptException {

        String filename = scriptName + "." + GROOVY_POSTFIX;
        File scriptFile = new File(_path,filename);
        if (!scriptFile.exists() || !scriptFile.canRead()) return null;

        BaseGroovyScript originalScript = (BaseGroovyScript) _gse.createScript(filename, binding);

        originalScript.setConsole(this);
        return originalScript;
    }

    BaseGroovyScript createScript(String scriptName, Object args, Binding binding ) throws ResourceException, ScriptException {

        String filename = scriptName + "." + GROOVY_POSTFIX;
        File scriptFile = new File(_path,filename);
        if (!scriptFile.exists() || !scriptFile.canRead()) return null;

        binding.setVariable("args", args);
        BaseGroovyScript originalScript = (BaseGroovyScript) _gse.createScript(filename, binding);
        originalScript.setConsole(this);
        return originalScript;
    }




    private ScriptResult wrapException(Exception e) {
        ScriptResult sr = new ScriptResult(ScriptStatus.ERROR);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        sr.setStringResult(sw.toString());
        sr.setClassname(e.getClass().getName());

        //TODO :
        return sr;
    }

    private ScriptResult wrapResult(Object erg) {
        ScriptResult sr = new ScriptResult(ScriptStatus.OK);
        sr.setStringResult(erg.toString());
        sr.setClassname(erg.getClass().getName());
        return sr;
    }

    public ScriptResult executeGroovy(String scriptName, String src) {
        uploadGroovy(scriptName, src);
        return executeScript(scriptName);
    }

    public Binding createBinding() {
        Binding binding = new Binding();
        return binding;
    }

    private class VersionFileFilter implements FilenameFilter {
        private final String _scriptName;

        public VersionFileFilter(String name) {
            _scriptName = name;
        }

        @Override
        public boolean accept(File dir, String name) {
            return name.startsWith(_scriptName + "."+GROOVY_POSTFIX+VERSION_POSTFIX);
        }
    }

    @GET
    @Path("info")
    @Produces(MediaType.APPLICATION_JSON)
    public ConsoleInfo getConsoleInfo(){
        return new ConsoleInfo(_path,listScripts().size());
    }


}
