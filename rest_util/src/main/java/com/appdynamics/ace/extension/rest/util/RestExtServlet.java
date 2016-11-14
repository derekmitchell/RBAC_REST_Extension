/*
 * Copyright (c) AppDynamics Inc
 * All rights reserved
 */

package com.appdynamics.ace.extension.rest.util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import java.io.File;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.spi.container.ContainerListener;
import com.sun.jersey.spi.container.ContainerNotifier;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;

/**
 * Created with IntelliJ IDEA.
 * User: stefan.marx
 * Date: 29.11.13
 * Time: 13:25
 * To change this template use File | Settings | File Templates.
 */

@WebServlet(urlPatterns="/extensionApi/*", initParams={
        @WebInitParam(name= JSONConfiguration.FEATURE_POJO_MAPPING, value="true")},
        loadOnStartup = 1)
public class RestExtServlet extends ServletContainer
{
    private static RestExtServlet _instance;
    private RestExtensionsResource _ctx;
    private Reloader _reloader;

    public RestExtServlet() {
        super();
        _instance = this;
    }



    private static final String CONTROLLER_HOME_PROPERTY_KEY = "appdynamics.controller.home";

    public static File getRestExtensionsRootDir()
    {
        File customDir = new File(System.getProperty(CONTROLLER_HOME_PROPERTY_KEY),"custom");
        return new File(customDir,"restExtensions");
    }


    @Override
    protected ResourceConfig getDefaultResourceConfig(Map<String, Object> props, WebConfig wc) throws ServletException
    {

        File jarPath = getRestExtensionsRootDir();

          //      new File("./build/extensions/webservices");

        try {

            if (!jarPath.exists())  new File(jarPath,".empty").mkdirs();
        }   catch (Exception ex ) {
            ex.printStackTrace();
            throw new ServletException("Cannot create Extensions Path/Ext Path "+jarPath.getAbsolutePath()+") doesn't exist)",ex);
        }

        if (!jarPath.canRead()) throw new ServletException("Cannot read in Ext Dir ("+jarPath.getAbsolutePath()+")");

          _ctx = new RestExtensionsResource(jarPath);
         _ctx.setServlet(this);

        _reloader = new Reloader();
        _ctx.getProperties().put(ResourceConfig.PROPERTY_CONTAINER_NOTIFIER, _reloader);
      return _ctx;
    }

    public static RestExtServlet getInstance() {
        return _instance;
    }

    public void reloadContext() {
        _reloader.reload();
        this.reload();
    }

    public class Reloader implements ContainerNotifier {
        List<ContainerListener> ls;

        public Reloader() {
            ls = new ArrayList<ContainerListener>();
        }

        public void addListener(ContainerListener l) {
            ls.add(l);
        }

        public void reload() {
            for (ContainerListener l : ls) {
                l.onReload();
            }
        }
    }
}
