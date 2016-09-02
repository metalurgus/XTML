package com.metalurgus.xtml;

import com.metalurgus.xtml.annotation.XTMLClass;
import com.metalurgus.xtml.annotation.XTMLMapping;

import java.util.List;

/**
 * @author Vladislav Matvienko
 */
@XTMLClass
public class GithubPageModel {
    @XTMLMapping(type = XTMLMapping.Type.TAG, select = ".numbers-summary li span", index = 0)
    public int commits;
    @XTMLMapping(type = XTMLMapping.Type.TAG, select = ".numbers-summary li span", index = 1)
    public int branches;
    @XTMLMapping(type = XTMLMapping.Type.TAG, select = ".numbers-summary li span", index = 2)
    public int releases;
    @XTMLMapping(type = XTMLMapping.Type.TAG, select = ".numbers-summary li span", index = 3)
    public int contributors;
    @XTMLMapping(type = XTMLMapping.Type.TAG, select = ".author a", index = 0)
    public String author;
    @XTMLMapping(type = XTMLMapping.Type.COLLECTION, select = ".files .js-navigation-item .content a")
    public List<String> files;
}
