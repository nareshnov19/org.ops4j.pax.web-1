package org.ops4j.pax.web.itest;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.linkBundle;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.systemTimeout;
import static org.ops4j.pax.exam.CoreOptions.when;
import static org.ops4j.pax.web.itest.TestConfiguration.logbackBundles;
import static org.ops4j.pax.web.itest.TestConfiguration.mojarraBundles;
import static org.ops4j.pax.web.itest.TestConfiguration.paxCdiSharedBundles;
import static org.ops4j.pax.web.itest.TestConfiguration.paxCdiWithWeldBundles;
import static org.ops4j.pax.web.itest.TestConfiguration.undertowBundles;
import static org.ops4j.pax.web.itest.TestConfiguration.workspaceBundle;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.io.StreamUtils;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;


@RunWith(PaxExam.class)
public class ServletCdiTest {

    private static boolean consoleEnabled = Boolean.valueOf(System.getProperty("equinox.console",
        "true"));
    private static String httpPortNumber = System.getProperty("test.http.port", "8080");
    
    @Inject
    private ServletContext servletContext;
    

    @Configuration
    public Option[] config() {

        return options(
            when(consoleEnabled).useOptions(
                systemProperty("osgi.console").value("6666"),
                systemProperty("osgi.console.enable.builtin").value("true")),
                systemTimeout(100000000),

            undertowBundles(),

            linkBundle("org.apache.felix.scr"),
            linkBundle("org.apache.xbean.bundleutils"),
            linkBundle("org.apache.xbean.finder"),
            linkBundle("org.objectweb.asm.all"),
            
            linkBundle("pax-cdi-sample1-web"),
            mavenBundle("org.primefaces", "primefaces", "5.0"),
            workspaceBundle("org.ops4j.pax.web", "pax-web-extender"),
            workspaceBundle("org.ops4j.pax.web", "pax-web-api"),
            workspaceBundle("org.ops4j.pax.web", "pax-web-undertow"),
            
            mojarraBundles(),
            paxCdiSharedBundles(),
            paxCdiWithWeldBundles(),
            
            logbackBundles(),
            junitBundles());
    }
    
    @Test
    public void runCdiServlet() throws Exception {
        assertThat(servletContext.getContextPath(), is("/sample1"));

        URL url = new URL(String.format("http://localhost:%s/sample1/message", httpPortNumber));
        InputStream is = url.openStream();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        StreamUtils.copyStream(is, os, true);
        assertThat(os.toString(), containsString("Message from managed bean"));
    }
}