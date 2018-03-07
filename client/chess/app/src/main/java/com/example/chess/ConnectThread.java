package com.example.chess;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by paul on 2017/7/10.
 */

public class ConnectThread extends Thread {
    public void run(){
        Socket socket=null;
        String got;
        try{
            int cata=1;//类型为1代表请求连接操作
            BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socket=new Socket("10.137.18.203",10001);
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())), true);
            out.println(cata);
            got=br.readLine();
            if(Integer.parseInt(got)==1) Log.d("", "Connected");
            else {

            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
