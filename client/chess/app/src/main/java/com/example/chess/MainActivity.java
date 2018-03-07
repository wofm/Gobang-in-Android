package com.example.chess;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    public void onClick(View view){
        Intent in=new Intent();
        in.setClass(this,Game.class);
        in.putExtra("mode",1);
        startActivity(in);
    }

    public void onClick2(View view){
        Intent in=new Intent();
        in.setClass(this,Game.class);
        in.putExtra("mode",2);
        startActivity(in);
    }

    public void onClick3(View view){
        Intent in=new Intent();
        in.setClass(this,Game.class);
        in.putExtra("mode",3);
        startActivity(in);
    }

    public void onClick4(View view){
        Intent in =new Intent();
        in.setClass(this,Rules.class);
        startActivity(in);
    }
}
