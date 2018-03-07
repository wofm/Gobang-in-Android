package com.example.chess;

import android.app.Activity;
import android.app.Dialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import java.util.logging.LogRecord;

public class Game extends Activity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    public Socket socket = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        Intent intent = getIntent();
        Game_view gv = (Game_view) findViewById(R.id.te);
        gv.mode = intent.getIntExtra("mode", 5);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void restart(View view) {
        Game_view gv = (Game_view) findViewById(R.id.te);
        if (gv.mode == 3) {
            Toast.makeText(Game.this, "对战功能下不可重开", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setMessage("是否确认重新开始?"); //设置内容
        builder.setIcon(R.drawable.bg);//设置图标，图片id即可
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Game_view gv = (Game_view) findViewById(R.id.te);
                for (int i = 1; i <= gv.heng; i++) {
                    for (int j = 1; j <= gv.zong; j++)
                        gv.board[i][j] = 0;
                }
                gv.moveRecord.clear();
                gv.wturn = false;
                gv.invalidate();
                dialog.dismiss(); //关闭dialog
                //Toast.makeText(Game.this, "确认" + which, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //Toast.makeText(MainActivity.this, "取消" + which, Toast.LENGTH_SHORT).show();
            }
        });

        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }

    public void onChange(View view) {
        Game_view gv = (Game_view) findViewById(R.id.te);
        if (gv.mode == 3) {
            Toast.makeText(this, "对战模式不可悔棋",
                    Toast.LENGTH_SHORT).show();
            return;
        } else if (gv.mode == 1) {
            if (gv.moveRecord.empty()) {
                Toast.makeText(this, "棋局为空，不可悔棋",
                        Toast.LENGTH_SHORT).show();
            } else {
                Point last = gv.moveRecord.pop();
                gv.wturn = !gv.wturn;
                gv.board[last.x][last.y] = 0;
                gv.invalidate();
            }
        } else if (gv.mode == 2) {
            if (gv.moveRecord.empty()) {
                Toast.makeText(this, "棋局为空，不可悔棋",
                        Toast.LENGTH_SHORT).show();
            } else {
                Point last = gv.moveRecord.pop();
                gv.board[last.x][last.y] = 0;
                last = gv.moveRecord.pop();
                gv.board[last.x][last.y] = 0;
                gv.invalidate();
            }
        }
    }

    public void setAI(View view) {
        Game_view GV = (Game_view) findViewById(R.id.te);
        if (GV.mode == 2) {

            final int lastTal = GV.aiTal;
            GV.aiTal = 2;//由于默认初始位置在中间
            String[] PLANETS = new String[]{"门外汉", "新手", "小成"};
            View outerView = LayoutInflater.from(this).inflate(R.layout.wheel_view, null);
            WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
            wv.setOffset(2);
            wv.setItems(Arrays.asList(PLANETS));
            wv.setSeletion(1);
            wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                @Override
                public void onSelected(int selectedIndex, String item) {
                    //Log.d("a", "[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);
                    if (item == "门外汉") {
                        Game_view gv = (Game_view) findViewById(R.id.te);
                        Log.d("a", "[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);
                        gv.aiTal = 1;
                    } else if (item == "新手") {
                        Game_view gv = (Game_view) findViewById(R.id.te);
                        Log.d("a", "[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);
                        gv.aiTal = 2;
                    } else if (item == "小成") {
                        Game_view gv = (Game_view) findViewById(R.id.te);
                        Log.d("a", "[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);
                        gv.aiTal = 3;
                    }
                }
            });
            new AlertDialog.Builder(this)
                    .setTitle("选择难度：")
                    .setView(outerView)
                    .setPositiveButton("确定", null)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Game_view gv = (Game_view) findViewById(R.id.te);
                            gv.aiTal = lastTal;
                            dialog.dismiss();
                            Toast.makeText(Game.this, "取消修改", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
        } else
            Toast.makeText(Game.this, "非人机对战模式此功能不可用", Toast.LENGTH_SHORT).show();
    }

    public void onFind(View view) {
        Game_view gv = (Game_view) findViewById(R.id.te);
        if (gv.mode == 3) {
            if (gv.ifConnected == false) {
                //sendMsg();
                new ConnectThread().start();
            } else {
                Toast.makeText(Game.this, "您已连接！", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Game.this, "非联网对战模式此功能不可用", Toast.LENGTH_SHORT).show();
        }
    }



    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Game Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    Handler cHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle b = msg.getData();
            int temp = b.getInt("ifok");
            if (temp == 1) {

                Game_view gv = (Game_view) findViewById(R.id.te);
                gv.ifConnected=true;
                gv.Conid=b.getInt("id");
                if(gv.Conid==1) {
                    gv.ifYourturn = true;
                    Toast.makeText(Game.this, "连接成功,您执黑子", Toast.LENGTH_SHORT).show();
                    new getInfo().start();
                }
                else{

                    //gv.board[b.getInt("x")][b.getInt("y")]=1;
                    gv.invalidate();
                    Toast.makeText(Game.this, "连接成功,您执白子", Toast.LENGTH_SHORT).show();
                    new getInfo().start();
                    //gv.ifYourturn=true;
                }

            } else {
                Toast.makeText(Game.this, "连接失败", Toast.LENGTH_SHORT).show();
            }
        }
    };
    public class ConnectThread extends Thread {
        public void run() {
            super.run();
            Looper.prepare();



            String got;
            try {
                int cata = 1;//类型为1代表请求连接操作
                socket = new Socket("101.200.46.165", 10001);
                Game_view gv = (Game_view) findViewById(R.id.te);
                //gv.socket=socket;
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                PrintWriter out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);
                out.println(cata);
                got = br.readLine();
                Bundle b = new Bundle();
                if (Integer.parseInt(got) == 1) {
                    b.putInt("ifok", 1);
                    got = br.readLine();
                    int id=Integer.parseInt(got);
                    b.putInt("id", id);
                    /*if(id==2){
                        int x=Integer.parseInt(br.readLine());
                        int y=Integer.parseInt(br.readLine());
                        b.putInt("x",x);
                        b.putInt("y",y);
                    }*/
                } else {
                    b.putInt("ifok", 0);
                }
                Message msg = cHandler.obtainMessage();
                msg.setData(b);
                //cHandler.sendEmptyMessage(2);
                cHandler.sendMessage(msg);
                Looper.loop();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onFlash(View view){
        Game_view gv = (Game_view) findViewById(R.id.te);
        if(gv.mode==3 && gv.ifConnected){
            new getInfo().start();
        }else if(gv.ifConnected==false &&gv.mode==3){
            Toast.makeText(Game.this, "未连接不可用", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(Game.this, "非对战模式不可用", Toast.LENGTH_SHORT).show();
        }
    }

    Handler getinfoHandler=new Handler(){
      @Override

        public void handleMessage(Message msg){

          Game_view gv = (Game_view) findViewById(R.id.te);
          Bundle b=msg.getData();
          int temp=b.getInt("ifok");
          if(temp==1){
              int x=b.getInt("x");
              int y=b.getInt("y");
              if(gv.Conid==1){
                  gv.board[x][y]=2;
              }else{
                  gv.board[x][y]=1;
              }

              gv.ifYourturn=true;
              gv.invalidate();
              if(gv.Conid==1){//处理对面是否获胜
                  if(gv.checkifwin(x,y,true)){
                      gv.windiag2(2);
                      gv.ifConnected=false;
                      gv.ifYourturn=false;
                      new endThread().start();
                  }
              }else{
                  if(gv.checkifwin(x,y,false)){
                      gv.windiag2(1);
                      gv.ifConnected=false;
                      gv.ifYourturn=false;
                      new endThread().start();
                  }
              }


          }else if(temp==0){
              //Toast.makeText(Game.this, "棋盘没有动静", Toast.LENGTH_SHORT).show();
          }
      }
    };
    public class getInfo extends Thread{
        @Override
        public void run(){
            super.run();
            Looper.prepare();

            String got;
            Game_view gv = (Game_view) findViewById(R.id.te);
            while(gv.ifConnected==true) {
                try {
                    int cata = 2;

                    socket = new Socket("101.200.46.165", 10001);
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream())), true);
                    out.println(cata);//获取信息的操作：类型2
                    out.println(gv.Conid);//输入是在对弈的哪一方
                    got = br.readLine();
                    Bundle b = new Bundle();
                    if (Integer.parseInt(got) == 1) {//有信息传回
                        b.putInt("ifok", 1);
                        got = br.readLine();
                        b.putInt("x", Integer.parseInt(got));
                        got = br.readLine();
                        b.putInt("y", Integer.parseInt(got));
                    } else if (Integer.parseInt(got) == 0) {//没有坐标信息
                        b.putInt("ifok", 0);
                    }
                    Message msg = getinfoHandler.obtainMessage();
                    msg.setData(b);
                    getinfoHandler.sendMessage(msg);
                    try{
                        this.sleep(500);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Looper.loop();
        }
    }
    public class endThread extends  Thread{
        @Override
        public void run(){
            super.run();
            Looper.prepare();
            try{
                socket = new Socket("101.200.46.165", 10001);
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);
                int cata=4;//类型4代表通知服务器游戏结束，一切回归初始
                out.println(cata);
            }catch (IOException e){
                e.printStackTrace();
            }

            Looper.loop();
        }
    }


}
