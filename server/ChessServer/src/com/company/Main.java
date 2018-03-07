package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Main {
    private static ServerSocket serverSocket=null;
    public static int waitfor=1;
    public static int newx=0,newy=0;//最新下子位置
    public static int connum=0;
    public static int whoturn=1;
    public static boolean ifbegin=false;
    public Socket cona=null,conb=null;
    public static void main(String[] args) {
        try{
            serverSocket=new ServerSocket(10001);
        }catch (IOException e){
            e.printStackTrace();
        }

        while(true){
            Socket socket=null;
            try{
                socket=serverSocket.accept();                        //主线程获取客户端连接
                Thread workThread=new Thread(new Handler(socket));
                //创建线程
                workThread.start();
                //启动线程

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }


    public static class Handler implements  Runnable{


        private Socket socket;
        private int towho=0;

        public Handler(Socket socket){
            this.socket=socket;
        }

        public void run() {
            try {
                System.out.println("ok");
                String got;
                BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);
                while(true) {
                    got=br.readLine();
                    if (Integer.parseInt(got) == 1) {//1类型的请求是连接请求
                        if (connum < 2) {//如果服务器端还有空位
                            connum++;
                            out.println(1);//1代表接受连接请求
                            out.println(connum);//这里代表分配下黑子或白子，1代表黑子，2代表白子

                        } else {
                            out.println(0);//0代表拒绝请求
                        }
                        socket.close();
                        break;
                    }
                    else if(Integer.parseInt(got)==2){//获得对面下子信息的请求
                        got=br.readLine();
                        if(Integer.parseInt(got)==waitfor){
                            int cata=1;
                            out.println(cata);//表示有信息可返回
                            out.println(newx);
                            out.println(newy);
                        }else{
                            int cata=0;
                            out.println(cata);//表示没有信息可返回
                        }
                        socket.close();
                        break;
                    }
                    else if(Integer.parseInt(got) == 3){
                        //System.out.println("handle 3");
                        got=br.readLine();

                        if(Integer.parseInt(got)==1){

                            newx =Integer.parseInt(br.readLine());
                            newy=Integer.parseInt(br.readLine());
                            waitfor=2;
                        }
                        else if(Integer.parseInt(got)==2){

                            newx =Integer.parseInt(br.readLine());
                            newy=Integer.parseInt(br.readLine());
                            waitfor=1;
                        }
                        socket.close();
                        break;
                    }else if(Integer.parseInt(got)==4){//处理游戏结束通知
                        waitfor=1;
                        connum=0;
                        newx=newy=0;
                        socket.close();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try{
                    socket.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
