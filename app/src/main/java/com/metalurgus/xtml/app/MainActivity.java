package com.metalurgus.xtml.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.metalurgus.xtml.XTML;

import org.jsoup.Jsoup;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TestClass testClass = XTML.fromHTML(Jsoup.parse(TestClass.HTML).body().child(0), TestClass.class);
    }
}
