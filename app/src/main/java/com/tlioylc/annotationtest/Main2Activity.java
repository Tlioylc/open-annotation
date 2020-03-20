package com.tlioylc.annotationtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.tlioylc.openannotation.annotation.OpenBuilder;
import com.tlioylc.openannotation.annotation.Optional;
import com.tlioylc.openannotation.annotation.Require;

@OpenBuilder
public class Main2Activity extends AppCompatActivity {
    @Require
    Integer targetValue;
    @Require
    String targetValue2;
    @Require
    private Datat targetValue3;
    @Optional
    int targetValue4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toast.makeText(this,targetValue3.getS(),Toast.LENGTH_SHORT).show();
    }
}
