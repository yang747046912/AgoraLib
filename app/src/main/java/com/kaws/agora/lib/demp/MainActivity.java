package com.kaws.agora.lib.demp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.kaws.agora.lib.AgoraViewActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void onClick(View view) {
        AgoraViewActivity.start(this, "aeb30445cd9f43f8ae1190e7c3300f3b", "123456");
    }
}
