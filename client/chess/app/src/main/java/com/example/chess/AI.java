package com.example.chess;

import android.graphics.Point;

/**
 * Created by paul on 2017/7/7.
 */

public class AI {
    public static Point giveAns1(int [][]board, int heng, int zong){
        java.util.Random r=new java.util.Random();
        int x=Math.abs(r.nextInt())%heng+1;
        int y=Math.abs(r.nextInt())%zong+1;
        while(board[x][y]>0){
            x=Math.abs(r.nextInt())%heng+1;
            y=Math.abs(r.nextInt())%zong+1;
        }
        return new Point(x,y);
    }

    public static int naiveScore(int[][]board,int heng,int zong,int a,int b){//纯防守策略下的打分机制
        int score=0,inta=0,intb=0;
        for(int i=1;i<=4;i++){
            if(a-i<1 || b-i<1)
                break;
            else if(board[a-i][b-i]!=1)
                break;
            else if(board[a-i][b-i]==1)
                inta++;
        }
        for(int i=1;i<=4;i++){
            if(a+i>heng || b+i>heng)
                break;
            else if(board[a+i][b+i]!=1)
                break;
            else if(board[a+i][b+i]==1)
                intb++;
        }
        switch (inta+intb){
            case 0:
                break;
            case 1:
                score+=1;
                break;
            case 2:
                score+=10;
                break;
            case 3:
                score+=100;
                break;
            default:
                score+=1000;
        }
        inta=intb=0;


        for(int i=1;i<=4;i++){
            if( b-i<1)
                break;
            else if(board[a][b-i]!=1)
                break;
            else if(board[a][b-i]==1)
                inta++;
        }
        for(int i=1;i<=4;i++){
            if(b+i>heng)
                break;
            else if(board[a][b+i]!=1)
                break;
            else if(board[a][b+i]==1)
                intb++;
        }
        switch (inta+intb){
            case 0:
                break;
            case 1:
                score+=1;
                break;
            case 2:
                score+=10;
                break;
            case 3:
                score+=100;
                break;
            default:
                score+=1000;
        }
        inta=intb=0;


        for(int i=1;i<=4;i++){
            if(a+i>heng || b-i<1)
                break;
            else if(board[a+i][b-i]!=1)
                break;
            else if(board[a+i][b-i]==1)
                inta++;
        }
        for(int i=1;i<=4;i++){
            if(a-i<1 || b+i>heng)
                break;
            else if(board[a-i][b+i]!=1)
                break;
            else if(board[a-i][b+i]==1)
                intb++;
        }
        switch (inta+intb){
            case 0:
                break;
            case 1:
                score+=1;
                break;
            case 2:
                score+=10;
                break;
            case 3:
                score+=100;
                break;
            default:
                score+=1000;
        }
        inta=intb=0;



        for(int i=1;i<=4;i++){
            if(a-i<1 )
                break;
            else if(board[a-i][b]!=1)
                break;
            else if(board[a-i][b]==1)
                inta++;
        }
        for(int i=1;i<=4;i++){
            if(a+i>heng )
                break;
            else if(board[a+i][b]!=1)
                break;
            else if(board[a+i][b]==1)
                intb++;
        }
        switch (inta+intb){
            case 0:
                break;
            case 1:
                score+=1;
                break;
            case 2:
                score+=10;
                break;
            case 3:
                score+=100;
                break;
            default:
                score+=1000;
        }

        return score;
    }


    public static int attaScore(int[][]board,int heng,int zong,int a,int b){//纯进攻策略下的打分机制
        int score=0,inta=0,intb=0;
        for(int i=1;i<=4;i++){
            if(a-i<1 || b-i<1)
                break;
            else if(board[a-i][b-i]!=2)
                break;
            else if(board[a-i][b-i]==2)
                inta++;
        }
        for(int i=1;i<=4;i++){
            if(a+i>heng || b+i>heng)
                break;
            else if(board[a+i][b+i]!=2)
                break;
            else if(board[a+i][b+i]==2)
                intb++;
        }
        switch (inta+intb){
            case 0:
                break;
            case 1:
                score+=1;
                break;
            case 2:
                score+=10;
                break;
            case 3:
                score+=100;
                break;
            default:
                score+=1000;
        }
        inta=intb=0;


        for(int i=1;i<=4;i++){
            if( b-i<1)
                break;
            else if(board[a][b-i]!=2)
                break;
            else if(board[a][b-i]==2)
                inta++;
        }
        for(int i=1;i<=4;i++){
            if(b+i>heng)
                break;
            else if(board[a][b+i]!=2)
                break;
            else if(board[a][b+i]==2)
                intb++;
        }
        switch (inta+intb){
            case 0:
                break;
            case 1:
                score+=1;
                break;
            case 2:
                score+=10;
                break;
            case 3:
                score+=100;
                break;
            default:
                score+=1000;
        }
        inta=intb=0;


        for(int i=1;i<=4;i++){
            if(a+i>heng || b-i<1)
                break;
            else if(board[a+i][b-i]!=2)
                break;
            else if(board[a+i][b-i]==2)
                inta++;
        }
        for(int i=1;i<=4;i++){
            if(a-i<1 || b+i>heng)
                break;
            else if(board[a-i][b+i]!=2)
                break;
            else if(board[a-i][b+i]==2)
                intb++;
        }
        switch (inta+intb){
            case 0:
                break;
            case 1:
                score+=1;
                break;
            case 2:
                score+=10;
                break;
            case 3:
                score+=100;
                break;
            default:
                score+=1000;
        }
        inta=intb=0;



        for(int i=1;i<=4;i++){
            if(a-i<1 )
                break;
            else if(board[a-i][b]!=2)
                break;
            else if(board[a-i][b]==2)
                inta++;
        }
        for(int i=1;i<=4;i++){
            if(a+i>heng )
                break;
            else if(board[a+i][b]!=2)
                break;
            else if(board[a+i][b]==2)
                intb++;
        }
        switch (inta+intb){
            case 0:
                break;
            case 1:
                score+=1;
                break;
            case 2:
                score+=10;
                break;
            case 3:
                score+=100;
                break;
            default:
                score+=1000;
        }

        return score;
    }


    public static Point giveAns2(int [][]board,int heng,int zong){//中等难度：纯防守策略的AI处理
        int maxScore=0;
        Point ans=new Point();
        for(int i=1;i<=heng;i++){
            for(int j=1;j<=zong;j++){
                if(board[i][j]==0){
                    int Score=naiveScore(board,heng,zong,i,j);
                    if(Score>maxScore){
                        maxScore=Score;
                        ans.x=i;
                        ans.y=j;
                    }
                }
            }
        }
        return ans;
    }

    public static Point giveAns3(int[][]board,int heng,int zong){
        int maxdefScore=0,maxattaScore=0;
        Point defPoint=new Point();//最佳防守点
        Point attaPoint=new Point();//最佳进攻点
        for(int i=1;i<=heng;i++){
            for(int j=1;j<=zong;j++){
                if(board[i][j]==0){
                    int Score=naiveScore(board,heng,zong,i,j);
                    if(Score>maxdefScore){
                        maxdefScore=Score;
                        defPoint.x=i;
                        defPoint.y=j;
                    }
                }
            }
        }
        for(int i=1;i<=heng;i++){
            for(int j=1;j<=zong;j++){
                if(board[i][j]==0){
                    int Score=attaScore(board,heng,zong,i,j);
                    if(Score>maxattaScore){
                        maxattaScore=Score;
                        attaPoint.x=i;
                        attaPoint.y=j;
                    }
                }
            }
        }
        if(2*maxdefScore>maxattaScore)
            return defPoint;
        else
            return attaPoint;
    }
}
