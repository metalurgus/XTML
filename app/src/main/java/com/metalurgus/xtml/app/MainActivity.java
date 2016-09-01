package com.metalurgus.xtml.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.metalurgus.xtml.XTML;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TestClass testClass = XTML.fromHTML(null, TestClass.class);
    }
}
