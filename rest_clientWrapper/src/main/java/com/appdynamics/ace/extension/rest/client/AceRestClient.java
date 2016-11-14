package com.appdynamics.ace.extension.rest.client;

import com.appdynamics.ace.extension.rest.command.api.Constants;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.json.JSONConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: stefan.marx
 * Date: 22.10.13
 * Time: 02:54
 * To change this template use File | Settings | File Templates.
 */
public class AceRestClient {
    private final String _url;
    private final String _username;
    private final String _password;

    public AceRestClient(String url, String username, String password) {
        _url = url;
        _username = username;
        _password = password;
    }

    public WebResource createClient() {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        Client client = Client.create(clientConfig);
        client.addFilter(new HTTPBasicAuthFilter(_username,_password));
        WebResource webResource = client.resource(_url+"controller/"+ Constants.PREFIX);
        return webResource;
    }
}
