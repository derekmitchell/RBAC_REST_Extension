/*
 * Copyright (c) AppDynamics Inc
 * All rights reserved
 */

package com.appdynamics.ace.extension.rest.util;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.Class;import java.lang.Override;import java.lang.String;import java.lang.StringBuilder;import java.lang.System;import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.appdynamics.ace.extension.rest.util.command.ExtensionInfo;
import com.appdynamics.ace.extension.rest.util.command.ExtensionInfoRest;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ScanningResourceConfig;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.core.spi.scanning.FilesScanner;
import com.sun.jersey.core.spi.scanning.Scanner;
import com.sun.jersey.spi.container.ReloadListener;
import com.sun.jersey.spi.scanning.AnnotationScannerListener;
import com.sun.jersey.spi.scanning.PathProviderScannerListener;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.jaxrs.JsonMappingExceptionMapper;
import org.codehaus.jackson.jaxrs.JsonParseExceptionMapper;

/**
* Created with IntelliJ IDEA.
* User: stefan.marx
* Date: 03.12.13
* Time: 16:41
* To change this template use File | Settings | File Templates.
*/
class RestExtensionsResource extends DefaultResourceConfig implements ReloadListener
{
    private static final Logger LOGGER =
            Logger.getLogger(ScanningResourceConfig.class.getName());

    private com.sun.jersey.core.spi.scanning.Scanner scanner;

    private final Set<Class<?>> cachedClasses = new HashSet<Class<?>>();

    private List<File> _files;
    private File _jarPath;
    private String _content;
    private Thread _updater;
    private RestExtServlet _servlet;

    public RestExtensionsResource(File jarPath) {
        super();
        setJarPath(jarPath);

        init();
    }


    private void init() {
        _files = getJars();
        System.out.println("Files Found !!");
        for (File f : _files) {
            System.out.println("F:"+f.getAbsolutePath());
        }

        scanner = new FilesScanner(_files.toArray(new File [] {}));
        init(scanner);


        _content = getExtensionContentHash();

        _updater = new Thread(new Updater(this));

        _updater.start();

    }

    private String getExtensionContentHash() {
        List<File> jars = getJars();

        StringBuilder c = new StringBuilder("CH:");
        for (File jar : jars) {
            c.append(jar.getName()).append(":").append(jar.lastModified()).append(":").append(jar.length());
            c.append("\n");
        }
        return c.toString();
    }


    private void init(Scanner scanner) {

        this.scanner = scanner;

        URLClassLoader cl = new URLClassLoader(getExtURLs(), ReflectionHelper.getContextClassLoader());

        final AnnotationScannerListener asl = new PathProviderScannerListener(cl);
        scanner.scan(asl);


        getClasses().addAll(asl.getAnnotatedClasses());
        addAdditionalClasses(getClasses());
        logClasses("found directly:", get(Path.class));
        logClasses("helper found directly:", get(Provider.class));




        if (LOGGER.isLoggable(Level.INFO) && !getClasses().isEmpty()) {
            final Set<Class> rootResourceClasses = get(Path.class);
            if (rootResourceClasses.isEmpty()) {
                LOGGER.log(Level.INFO, "No root resource classes found.");
            } else {
                logClasses("Root resource classes found:", rootResourceClasses);
            }

            final Set<Class> providerClasses = get(Provider.class);
            if (providerClasses.isEmpty()) {
                LOGGER.log(Level.INFO, "No provider classes found.");
            } else {
                logClasses("Provider classes found:", providerClasses);
            }

        }



        cachedClasses.clear();
        cachedClasses.addAll(getClasses());



    }

    private void addAdditionalClasses( Set<Class<?>> cl) {


        cl.add(JacksonJaxbJsonProvider.class);
        cl.add(JacksonJsonProvider.class);
        cl.add(JsonMappingExceptionMapper.class);
        cl.add(JsonParseExceptionMapper.class);

        cl.add(ExtensionInfoRest.class);


    }

    private URL[] getExtURLs() {

        ArrayList<URL> ul = new ArrayList<URL>();
        for (File f:_files) {
            try {
                ul.add(f.toURI().toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return ul.toArray(new URL[]{});
    }


    private List<File> getJars() {
        ArrayList<File> erg = new ArrayList<File>();


        File[] jarfiles = getJarPath().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.endsWith(".jar");
            }
        });

        if (jarfiles != null)  Collections.addAll(erg,jarfiles);


        return erg;

    }

    public void setJarPath(File jarPath) {
        _jarPath = jarPath;
    }

    public File getJarPath() {
        return _jarPath;
    }

    /**
     * Perform a new search for resource classes and provider classes.
     */
    @Override
    public void onReload() {

        Set<Class<?>> classesToRemove = new HashSet<Class<?>>();
        Set<Class<?>> classesToAdd = new HashSet<Class<?>>();

        for(Class c : getClasses())
            if(!cachedClasses.contains(c))
                classesToAdd.add(c);

        for(Class c : cachedClasses)
            if(!getClasses().contains(c))
                classesToRemove.add(c);

        getClasses().clear();

        //init(scanner);
        init();
        getClasses().addAll(classesToAdd);
        getClasses().removeAll(classesToRemove);
    }



    private Set<Class> get(Class<? extends Annotation> ac) {
        Set<Class> s = new HashSet<Class>();
        for (Class c : getClasses())
            if (c.isAnnotationPresent(ac))
                s.add(c);
        return s;
    }

    private void logClasses(String s, Set<Class> classes) {
        final StringBuilder b = new StringBuilder();
        b.append(s);
        for (Class c : classes)
            b.append('\n').append("  ").append(c);

        LOGGER.log(Level.INFO, b.toString());
    }

    public void setServlet(RestExtServlet servlet) {
        _servlet = servlet;
    }

    public RestExtServlet getServlet() {
        return _servlet;
    }

    private class Updater implements Runnable {


        private final RestExtensionsResource _res;

        public Updater(RestExtensionsResource restExtensionsResource) {
            _res = restExtensionsResource;
        }
        @Override
        public void run() {
            try {

                while (true){
                    Thread.sleep(1000*60*1);
                    String currentContent = getExtensionContentHash();

                    if (!currentContent.equals(_content)) {
                        _res.getServlet().reloadContext();
                        _content = currentContent;
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
