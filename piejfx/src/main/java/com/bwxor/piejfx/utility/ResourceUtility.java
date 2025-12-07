package com.bwxor.piejfx.utility;

import java.io.InputStream;
import java.net.URL;

public class ResourceUtility {
    private static final String PACKAGE_NAME = "/com/bwxor/piejfx/";

    public static URL getResourceByName(String name) {
        String fullResourceName = name.startsWith(PACKAGE_NAME) ? name : PACKAGE_NAME + name;
        return ResourceUtility.class.getResource(fullResourceName);
    }

    public static InputStream getResourceByNameAsStream(String name) {
        String fullResourceName = name.startsWith(PACKAGE_NAME) ? name : PACKAGE_NAME + name;
        return ResourceUtility.class.getResourceAsStream(fullResourceName);
    }
}
