package com.example.chess;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Stack;

import java.util.logging.LogRecord;

/**
 * Created by paul on 2017/7/6.
 */

public class Game_view extends View {
    public int mode;//游戏模式，1代表双人对战，2代表单人对战AI
    public int aiTal=3;//AI的游戏水平，分为1,2,3,1最低，3最高
    public boolean wturn=false;//true代表现在是白棋的机会，false代表现在是黑棋的机会
    public int board[][]=new int[100][100];//存储棋盘状态,0代表无子，1代表黑子，2代表白子
    final int width=90;//存储棋盘格子大小
    public int heng,zong;//存储棋盘大小
    private  Paint p=new Paint();
    public Stack<Point>moveRecord=new Stack<Point>();//存储行棋路径
    public boolean ifConnected=false;//存储对战模式下是否找到了对手
    public boolean ifYourturn=false;//存储对战模式下现在是否可以下子
    public int Conid;//存储对战时被分配的id
    public int lastx=0,lasty=0;//存储刚刚下的位置，用于向服务器发送
    public Socket socket=null;//用于跟服务器连接时的信息传输
    public Game_view(Context context){
        super(context,null);
    }

    public Game_view(Context context, AttributeSet attrs)
    {
        super(context, attrs, 0);
    }
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        double a=View.MeasureSpec.getSize(heightMeasureSpec);
        int b=View.MeasureSpec.getSize(widthMeasureSpec);
        a*=0.9;
        zong=(int)(a-width/2)/width;
        heng=(int)(b-width/2)/width;
        for(int i=i=1;i<=heng;i++){
            for(int j=1;j<=zong;j++)
                this.board[i][j]=0;
        }
        setMeasuredDimension(b,(int)a);

        //RelativeLayout.LayoutParams layoutpara=(RelativeLayout.LayoutParams)this.getLayoutParams();
        //Display display = getWindowManager().getDefaultDisplay();
    }

    @Override
    protected void onDraw(Canvas canvas){
        Log.d("Game_view",Integer.toString(this.mode));
        super.onDraw(canvas);
        p.setColor(Color.BLACK);
        p.setAntiAlias(true);
        p.setStrokeWidth(5);
        for(int i=0;i<getMeasuredWidth()-width/2;i+=width)
            canvas.drawLine(i,0,i,getMeasuredHeight(),p);
        for(int i=0;i<getMeasuredHeight()-width/2;i+=width)
            canvas.drawLine(0,(float)i,getMeasuredWidth(),i,p);
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.black);
        Bitmap bitmap2= BitmapFactory.decodeResource(getResources(),R.drawable.white);
        for(int i=1;i<=heng;i++){
            for(int j=1;j<=zong;j++){
                if(board[i][j]==0)
                    continue;
                else if(board[i][j]==1)
                    canvas.drawBitmap(bitmap,new Rect(0,0,bitmap.getWidth(),bitmap.getHeight()),new Rect(i*width-width/2,j*width-width/2,i*width+width/2,j*width+width/2),p);
                else if(board[i][j]==2)
                    canvas.drawBitmap(bitmap2,new Rect(0,0,bitmap2.getWidth(),bitmap2.getHeight()),new Rect(i*width-width/2,j*width-width/2,i*width+width/2,j*width+width/2),p);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean value = super.onTouchEvent(event);
        float x=event.getX();
        float y=event.getY();
        int posx=(int)((x+width/2)/width);
        int posy=(int)((y+width/2)/width);

        if(mode==1){//人人对战逻辑
            if(board[posx][posy]==0 && wturn==false)
            {
                board[posx][posy]=1;
                this.invalidate();
                boolean ans=checkifwin(posx,posy,wturn);
                moveRecord.push(new Point(posx,posy));
                if(ans){
                    windiag(1);
                    Toast.makeText(this.getContext(), "黑色方获胜",
                            Toast.LENGTH_SHORT).show();
                }
                wturn=true;

            }
            else if(board[posx][posy]==0 && wturn==true)
            {
                board[posx][posy]=2;
                this.invalidate();
                boolean ans=checkifwin(posx,posy,wturn);
                moveRecord.push(new Point(posx,posy));
                if(ans){
                    windiag(2);
                    Toast.makeText(this.getContext(), "白色方获胜",
                            Toast.LENGTH_SHORT).show();
                }
                wturn=false;

            }
        }
        else if(mode==2){//人机对战逻辑
            if(board[posx][posy]==0){
                board[posx][posy]=1;
                this.invalidate();
                boolean ans=checkifwin(posx,posy,false);
                moveRecord.push(new Point(posx,posy));
                if(ans){
                    windiag(1);
                    Toast.makeText(this.getContext(), "黑色方获胜",
                            Toast.LENGTH_SHORT).show();
                    return value;
                }
                Point re=new Point();
                if(aiTal==1)
                    re=AI.giveAns1(board,heng,zong);
                else if(aiTal==2)
                    re=AI.giveAns2(board,heng,zong);
                else if(aiTal==3)
                    re=AI.giveAns3(board,heng,zong);
                board[re.x][re.y]=2;
                moveRecord.push(new Point(re.x,re.y));
                this.invalidate();
                ans=checkifwin(re.x,re.y,true);
                if(ans){
                    windiag(2);
                    Toast.makeText(this.getContext(), "白色方获胜",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }else if(mode==3){//联网对战逻辑
            if(ifConnected==false){
                Toast.makeText(this.getContext(), "未连接状态不能下子",
                        Toast.LENGTH_SHORT).show();
            }else{
                if(ifYourturn && board[posx][posy]==0){

                        /*while(board[posx][posy]>0){
                            Toast.makeText(this.getContext(), "此处已下子",
                                    Toast.LENGTH_SHORT).show();
                            x=event.getX();
                            y=event.getY();
                            posx=(int)((x+width/2)/width);
                            posy=(int)((y+width/2)/width);
                        }*/
                    if(Conid==1)
                        board[posx][posy]=1;
                    else
                        board[posx][posy]=2;
                    if(Conid==1){
                        if(checkifwin(posx,posy,false)){
                            windiag2(1);
                        }
                    }else{
                        if(checkifwin(posx,posy,true)){
                            windiag2(2);
                        }
                    }
                    ifYourturn=false;
                    lastx=posx;lasty=posy;
                    this.invalidate();
                    Log.d(" ", "invalidated");
                    new doThread().start();
                }
                else if(!ifYourturn){
                    Toast.makeText(this.getContext(),"未到您的机会！",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this.getContext(), "此处已下子",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

        return value;
    }



    public boolean checkifwin(int x,int y,boolean ifwhite){//
        int intforcheck=0;//初始化判断条件
        if(ifwhite==true){
            intforcheck=2;
        }
        else{
            intforcheck=1;
        }
        Log.d("checkfirst", "ok ");

        int equalnum1=0,equalnum2=0;
        for(int i=1;i<=4;i++){
            if(x-i<1 || y-i<1)
                break;
            else if(board[x-i][y-i]!=intforcheck)
                break;
            else if(board[x-i][y-i]==intforcheck){
                equalnum1++;
            }
        }
        for(int i=1;i<=4;i++){
            if(x+i>heng || y+i>zong)
                break;
            else if(board[x+i][y+i]!=intforcheck)
                break;
            else if(board[x+i][y+i]==intforcheck){
                equalnum2++;
            }
        }
        if((equalnum1+equalnum2)>=4)
            return true;
        else{
            equalnum1=equalnum2=0;
        }
        Log.d("checkdir1", "ok ");

        for(int i=1;i<=4;i++){
            if(y-i<1)
                break;
            else if(board[x][y-i]!=intforcheck)
                break;
            else if(board[x][y-i]==intforcheck){
                equalnum1++;
            }
        }
        for(int i=1;i<=4;i++){
            if(y+i>zong)
                break;
            else if(board[x][y+i]!=intforcheck)
                break;
            else if(board[x][y+i]==intforcheck){
                equalnum2++;
            }
        }
        if((equalnum1+equalnum2)>=4)
            return true;
        else{
            equalnum1=equalnum2=0;
        }
        Log.d("checkdir2", "ok ");



        for(int i=1;i<=4;i++){
            if(x+i>heng || y-i<1)
                break;
            else if(board[x+i][y-i]!=intforcheck)
                break;
            else if(board[x+i][y-i]==intforcheck){
                equalnum1++;
            }
        }
        for(int i=1;i<=4;i++){
            if(x-i<1 || y+i>zong)
                break;
            else if(board[x-i][y+i]!=intforcheck)
                break;
            else if(board[x-i][y+i]==intforcheck){
                equalnum2++;
            }
        }
        if((equalnum1+equalnum2)>=4)
            return true;
        else{
            equalnum1=equalnum2=0;
        }
        Log.d("checkdir3", "ok ");




        for(int i=1;i<=4;i++){
            if(x-i<1 )
                break;
            else if(board[x-i][y]!=intforcheck)
                break;
            else if(board[x-i][y]==intforcheck){
                equalnum1++;
            }
        }
        for(int i=1;i<=4;i++){
            if(x+i>heng )
                break;
            else if(board[x+i][y]!=intforcheck)
                break;
            else if(board[x+i][y]==intforcheck){
                equalnum2++;
            }
        }
        if((equalnum1+equalnum2)>=4)
            return true;


        Log.d("checkdir4", "ok ");


        return false;
    }

    public void windiag(int whowin){//传参1代表黑色赢，2代表白色赢
        AlertDialog.Builder builder=new AlertDialog.Builder(this.getContext());  //先得到构造器
        builder.setTitle("提示"); //设置标题
        if(whowin==1)
            builder.setMessage("黑色方获得胜利，是否重新开始?");
        else
            builder.setMessage("白色方获得胜利，是否重新开始?");
        builder.setIcon(R.drawable.bg);//设置图标，图片id即可
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Game_view gv=(Game_view)findViewById(R.id.te);
                for(int i=1;i<=gv.heng;i++){
                    for(int j=1;j<=gv.zong;j++)
                        gv.board[i][j]=0;
                }
                gv.moveRecord.clear();
                gv.wturn=false;
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


    public void windiag2(int whowin){//传参1代表黑色赢，2代表白色赢,处理对战模式下的结果对话框
        AlertDialog.Builder builder=new AlertDialog.Builder(this.getContext());  //先得到构造器
        builder.setTitle("提示"); //设置标题
        if(whowin==1)
            builder.setMessage("黑色方获得胜利，连接即将中断");
        else
            builder.setMessage("白色方获得胜利，连接即将中断");
        builder.setIcon(R.drawable.bg);//设置图标，图片id即可
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Game_view gv=(Game_view)findViewById(R.id.te);
                for(int i=1;i<=gv.heng;i++){
                    for(int j=1;j<=gv.zong;j++)
                        gv.board[i][j]=0;
                }
                gv.moveRecord.clear();
                gv.wturn=false;
                gv.ifConnected=false;
                gv.ifYourturn=false;
                gv.invalidate();
                //new endThread().start();
                dialog.dismiss(); //关闭dialog
                //Toast.makeText(Game.this, "确认" + which, Toast.LENGTH_SHORT).show();
            }
        });

        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }





    Handler doHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            Bundle b=msg.getData();
            int posx=b.getInt("x");
            int posy=b.getInt("y");
            if(Conid==1)
                board[posx][posy]=1;
            else
                board[posx][posy]=2;
            Game_view gv=(Game_view)findViewById(R.id.te);
            gv.ifYourturn=false;
            gv.invalidate();

        }
    };
    public class doThread extends Thread{
        public void run(){
            Looper.prepare();

            super.run();

            try{
                socket=new Socket("101.200.46.165", 10001);
                String got;
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);
                int cata=3;
                out.println(cata);//类型为3的操作：下子
                out.println(Conid);//传递id
                out.println(lastx);//传递x坐标
                out.println(lasty);//传递y坐标

                /*got=br.readLine();//接收返回的坐标
                Bundle b=new Bundle();
                b.putInt("x",Integer.parseInt(got));
                got=br.readLine();
                b.putInt("y",Integer.parseInt(got));

                Message msg=doHandler.obtainMessage();
                msg.setData(b);
                doHandler.sendMessage(msg);*/
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public Handler waitHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            Bundle b=msg.getData();
            int x=b.getInt("x");
            int y=b.getInt("y");
            if(Conid==1)
                board[x][y]=2;
            else
                board[x][y]=1;
            //this.inva
        }
    };
    public class waitThread extends  Thread{
        public void run(){
            Looper.prepare();
            super.run();
            try{
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);
                int x=Integer.parseInt(br.readLine());
                int y=Integer.parseInt(br.readLine());
                Bundle b=new Bundle();
                b.putInt("x",x);
                b.putInt("y",y);
                Message msg=waitHandler.obtainMessage();
                msg.setData(b);
                waitHandler.sendMessage(msg);
            }catch (IOException e){
                e.printStackTrace();
            }
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
