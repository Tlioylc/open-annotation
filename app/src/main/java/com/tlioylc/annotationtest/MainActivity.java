package com.tlioylc.annotationtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tlioylc.openannotation.annotation.OpenBuilder;

@OpenBuilder
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.hello_world).setOnClickListener(v -> {
            Datat data = new Datat();
            data.setS("hello world2");
            Main2ActivityBuilder.init(1,"hello world",data)
                    .open(this);
        });
    }
}
