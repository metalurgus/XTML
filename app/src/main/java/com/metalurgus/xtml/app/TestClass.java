package com.metalurgus.xtml.app;

import com.metalurgus.xtml.annotation.XTMLMapping;

/**
 * @author Vladislav Matvienko
 */
public class TestClass {
    @XTMLMapping(type = XTMLMapping.Type.TAG, name = "div")
    public String field1;
    @XTMLMapping(type = XTMLMapping.Type.ATTRIBUTE, name = "id")
    public String field2;
}
