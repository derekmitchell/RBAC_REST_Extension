package com.appdynamics.ace.extension.rest.util;


import sun.misc.BASE64Decoder;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: stefan.marx
 * Date: 16.10.13
 * Time: 16:57
 * To change this template use File | Settings | File Templates.
 */

@WebFilter ({"/extensionApi/*"})
public class RestFilter implements Filter {
    private static final long serialVersionUID = 1L;

    private Logger _logger = Logger.getLogger(RestFilter.class.getName());


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpResp = (HttpServletResponse) response;


        doAuth(httpReq, httpResp);

        disableCache(httpReq,httpResp);



        chain.doFilter(request, response);
    }

    private void disableCache(HttpServletRequest httpReq, HttpServletResponse httpResp) {
           //These are the correct headers to disable caching in IE 8 and 9
            ((HttpServletResponse)httpResp).setHeader("cache-control", "no-cache, no-store, max-age=0, must-revalidate");
            ((HttpServletResponse)httpResp).setHeader("Pragma", "no-cache");
    }

    private void doAuth(HttpServletRequest httpReq, HttpServletResponse httpResp) throws IOException {
        String basicAuth = httpReq.getHeader("Authorization");
        if (basicAuth != null) {

            if (basicAuth.startsWith("Basic")) {
                String decoded = new String(new BASE64Decoder().decodeBuffer(basicAuth.substring(6)));
                int delimiterIndex = decoded.indexOf(":");

                if (delimiterIndex != -1) {
                    String userAndAccount = decoded.substring(0, delimiterIndex);
                    userAndAccount = URLDecoder.decode(userAndAccount, "UTF-8");
                    String password = decoded.substring(delimiterIndex+1);
                    password = URLDecoder.decode(password, "UTF-8");

                    if (userAndAccount != null && password != null &&
                            userAndAccount.length() > 0 && password.length() > 0) {
                        //HttpSession session = httpReq.getSession(false);
                        //if (session != null) {
                            try {
                                httpReq.login(userAndAccount, password);
                                _logger.log(Level.INFO, "Session created... returning SC_OK");
                            }
                            catch(ServletException se) {
                                _logger.log(Level.INFO, "HttpServletRequest.login failed", se);
                            }
                        //}
                    }
                } else {
                    _logger.log(Level.INFO,"Malformed basic auth string;");
                }
            }
        }  else {
            //_logger.log(Level.INFO,"Basic authorization not provided;");
        }
    }

    @Override
    public void destroy() {

    }
}
