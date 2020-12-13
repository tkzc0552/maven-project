package com.zhm.util;

import java.io.*;

public class IOUtil {

    private static final String EMPTY = "";

    public static String read(String filePaht) {
        FileReader fr = null;
        BufferedReader br = null;
        String lineText = null;
        StringBuilder builder = new StringBuilder(1024);
        try {
            fr = new FileReader(filePaht);
            br = new BufferedReader(fr);
            while (null != (lineText = br.readLine())) {
                builder.append(lineText).append("\r\n");
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean write(String filePath, String fileName, String content) {
        File file = new File(filePath);
        if(!file.exists()){
            file.mkdirs();
        }
        return write(new StringBuilder().append(filePath).append(fileName).toString(),content);
    }

    private static boolean write(String filePath,String content){
        FileWriter fw = null;
        BufferedWriter bw = null;
        try{
            fw=new FileWriter(filePath);
            bw=new BufferedWriter(fw);
            bw.write(content);
            bw.flush();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            try{
                if(null!=bw){
                    bw.close();
                }
                if (null!=fw){
                    fw.close();
                }
            }catch (IOException e1){
                e1.printStackTrace();
            }
        }
    }
}
