package com.tidyquestion.backend.doc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class TextChunkerTest {

    @Test
    void shouldSplitTextWithOverlap() {
        TextChunker chunker = new TextChunker(10, 2);
        List<String> chunks = chunker.split("abcdefghijklmno");

        Assertions.assertEquals(2, chunks.size());
        Assertions.assertEquals("abcdefghij", chunks.get(0));
        Assertions.assertEquals("ijklmno", chunks.get(1));
    }

    @Test
    //先输入n（奇数）和m两个正整数
    int SearchPossibilities(int n,int m){
        if(n%2!=1){
            System.out.println("n必须为奇数");
            return 0;
        }
return 0;
    }
    public class Robot{
       static int [] dx={-1,1,0,0};
       static int [] dy={0,0,-1,1};
    }
    public static int minSteps(char[][] grid){
        int n =grid.length;
        int m=grid[0].length;
        int sx=-1,sy=-1,tx=-1,ty=-1;
        for(int i=0;i<n;i++){
            for(int j=0;j<m;j++){
                if(grid[i][j]=='S'){
                    sx=i;
                    sy=j;
                }
                if(grid[i][j]=='T'){
                    tx=i;
                    ty=j;
                }
            }
        }

    }
}
