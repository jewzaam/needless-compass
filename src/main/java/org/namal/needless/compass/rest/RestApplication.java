package org.namal.needless.compass.rest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.jboss.as.quickstarts.html5rest.HelloWorld;

@ApplicationPath("/rest")
public class RestApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        return new HashSet<>(Arrays.asList(RestResource.class, HelloWorld.class));
    }
}
