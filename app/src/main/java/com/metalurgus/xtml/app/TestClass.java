package com.metalurgus.xtml.app;

import com.metalurgus.xtml.annotation.XTMLClass;
import com.metalurgus.xtml.annotation.XTMLMapping;

import java.util.List;

/**
 * @author Vladislav Matvienko
 */
@XTMLClass
public class TestClass {

    public static String HTML =
            "<div test1 = '111'>" +
                "<div id='testId' test2='222'/>" +
                "<div test2='333'/>" +
                "<div id='testId2'>" +
                    "<div test2='333'/>" +
                    "<div test2='333'/>" +
                    "<div test2='333'/>" +
                "</div>" +
                "<div id='testId3'>" +
                    "<div test2='333'>222.22</div>" +
                    "<div test2='333'>333.33</div>" +
                    "<div test2='333'>444.44</div>" +
                "</div>" +
            "</div>";
    @XTMLMapping(type = XTMLMapping.Type.ATTRIBUTE, name = "test1")
    public int field1;
    @XTMLMapping(type = XTMLMapping.Type.TAG, select = "#testId")
    public InnerTestClass innerTestClass1;
    @XTMLMapping(type = XTMLMapping.Type.TAG, index = 1)
    public InnerTestClass innerTestClass2;
    @XTMLMapping(type = XTMLMapping.Type.COLLECTION, select = "#testId2 > *")
    public List<InnerTestClass> innerTestClassList;
    @XTMLMapping(type = XTMLMapping.Type.COLLECTION, select = "#testId3 > *")
    public List<Double> integerList;

    @XTMLClass
    class InnerTestClass {
        @XTMLMapping(type = XTMLMapping.Type.ATTRIBUTE, name = "test2")
        public String field3;
    }
}
