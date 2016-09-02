package com.metalurgus.xtml;

import android.app.Application;
import android.test.ApplicationTestCase;

import org.jsoup.Jsoup;

import java.net.URL;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    private static final String AUTHOR = "metalurgus";
    private static final int FILES_COUNT = 11;
    private static final String GITHUB_HOME_URL = "https://github.com/metalurgus/XTML";

    public ApplicationTest() {
        super(Application.class);
    }

    public void testGithub() throws Exception {
        GithubPageModel model = XTML.fromHTML(Jsoup.parse(new URL(GITHUB_HOME_URL), 3000).body(), GithubPageModel.class);
        assertNotNull(model);
        assertEquals(model.author, AUTHOR);
        assertTrue(model.branches > 0);
        assertTrue(model.commits > 0);
        assertTrue(model.contributors > 0);
        assertTrue(model.releases > 0);
        assertNotNull(model.files);
        assertEquals(model.files.size(), FILES_COUNT);

    }
}