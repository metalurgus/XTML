package com.metalurgus.xtml.app;

import com.metalurgus.xtml.annotation.XTMLClass;
import com.metalurgus.xtml.annotation.XTMLMapping;
import com.metalurgus.xtml.annotation.XTMLMappings;

import java.util.List;

/**
 * @author Vladislav Matvienko
 */
@XTMLClass
public class TestClass {
    public static final String MAPPING_ONE = "mapping_one";
    public static final String MAPPING_TWO = "mapping_two";

    public static String HTML_MAPPING_ONE =
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

    public static String HTML_MAPPING_TWO =
            "<div test1x = '111'>" +
                    "<div id='testIdx' test2x='222'/>" +
                    "<div test2x='333'/>" +
                    "<div id='testId2x'>" +
                    "<div test2x='333'/>" +
                    "<div test2x='333'/>" +
                    "<div test2x='333'/>" +
                    "</div>" +
                    "<div id='testId3x'>" +
                    "<div test2x='333'>222.22</div>" +
                    "<div test2x='333'>333.33</div>" +
                    "<div test2x='333'>444.44</div>" +
                    "</div>" +
                    "</div>";
    @XTMLMappings({
            @XTMLMapping(type = XTMLMapping.Type.ATTRIBUTE, name = "test1", mappingName = MAPPING_ONE),
            @XTMLMapping(type = XTMLMapping.Type.ATTRIBUTE, name = "test1x", mappingName = MAPPING_TWO)
    })
    public int field1;
    @XTMLMappings({
            @XTMLMapping(type = XTMLMapping.Type.TAG, select = "#testId", mappingName = MAPPING_ONE),
            @XTMLMapping(type = XTMLMapping.Type.TAG, select = "#testIdx", mappingName = MAPPING_TWO)
    })
    public InnerTestClass innerTestClass1;
    @XTMLMappings({
            @XTMLMapping(type = XTMLMapping.Type.TAG, index = 1, mappingName = MAPPING_ONE),
            @XTMLMapping(type = XTMLMapping.Type.TAG, index = 1, mappingName = MAPPING_ONE)
    })
    public InnerTestClass innerTestClass2;
    @XTMLMappings({
            @XTMLMapping(type = XTMLMapping.Type.COLLECTION, select = "#testId2 > *", mappingName = MAPPING_ONE),
            @XTMLMapping(type = XTMLMapping.Type.COLLECTION, select = "#testId2x > *", mappingName = MAPPING_TWO)
    })
    public List<InnerTestClass> innerTestClassList;
    @XTMLMappings({
            @XTMLMapping(type = XTMLMapping.Type.COLLECTION, select = "#testId3 > *", mappingName = MAPPING_ONE),
            @XTMLMapping(type = XTMLMapping.Type.COLLECTION, select = "#testId3x > *", mappingName = MAPPING_TWO)
    })
    public List<Double> integerList;

    @XTMLClass
    class InnerTestClass {
        @XTMLMappings({
                @XTMLMapping(type = XTMLMapping.Type.ATTRIBUTE, name = "test2", mappingName = MAPPING_ONE),
                @XTMLMapping(type = XTMLMapping.Type.ATTRIBUTE, name = "test2x", mappingName = MAPPING_TWO)
        })
        public String field3;
    }
}
