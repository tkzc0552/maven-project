package com.zhm.util;

import java.io.File;
import java.io.FileFilter;

public class DAOGenerate {

    private static final String USER_DIR = System.getProperty("user.dir");

    private static final String BUILD_PATH = USER_DIR.replace("\\\\", "/") + "/dao/src/main/java/";

    private static String extDaoPackageName = null;


    public static void generate() {
        System.out.println("build_path:" + BUILD_PATH);

        String tplDaoPackageName = "com.zhm.template.mapper";
        String tplDaoPackageDirName = tplDaoPackageName.replace(".", "/");
        extDaoPackageName = "com.zhm.extension.mapper";
        String extDaoPackageDirName = extDaoPackageName.replace('.', '/');
        generateDAOEXT(extDaoPackageName,extDaoPackageDirName,tplDaoPackageName,tplDaoPackageDirName);


       String tplMappingPackageName = "com.zhm.template.mapping";
        String tplMappingPackageDirName = tplMappingPackageName.replace(".", "/");
        String extMappingPackageName = "com.zhm.extension.mapping";
        String extMappingPackageDirName = extMappingPackageName.replace('.', '/');
        generateMappingEXT(extDaoPackageName,extMappingPackageDirName,tplMappingPackageName,tplMappingPackageDirName);

        String tplModelPackageName = "com.zhm.template.entity";
        String tplModelPackageDirName = tplModelPackageName.replace(".", "/");
        String extModelPackageName = "com.zhm.extension.entity";
        String extModelPackageDirName = extModelPackageName.replace('.', '/');
        generateModelEXT(extModelPackageName,extModelPackageDirName,tplModelPackageName,tplModelPackageDirName);



    }

    private static void generateModelEXT(String extModelPackageName, String extModelPackageDirName, String tplModelPackageName, String tplModelPackageDirName) {
        String filePath = BUILD_PATH + tplModelPackageDirName + "/";

        File file=new File(filePath);

        File[] files=file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File t) {
                return (t.getName().endsWith(".java"))&&(!t.getName().matches(".*Example.java$"));
            }
        });
        for(File f:files){
            String cname=f.getName().substring(0,f.getName().length()-5);
            String path=BUILD_PATH+extModelPackageDirName+"/";

            String fileName=cname+"Ext.java";
            if(new File(path+fileName).exists()){
                System.out.println("existed:"+path+fileName);
            }else{
                StringBuilder builder=new StringBuilder(1024);
                builder.append("package ").append(extModelPackageName).append(";").append("\n");
                builder.append("\n");
                builder.append("import ").append(tplModelPackageName).append(".").append(cname).append(";").append("\n");
                builder.append("\n");
                builder.append("public class ").append(cname).append("Ext").append(" extends ").append(cname).append("{").append("\n");
                builder.append("\n");
                builder.append("}");
                boolean result=IOUtil.write(path,fileName,builder.toString());
                if(result){
                    System.out.println("generate:"+path+fileName);
                }
            }
        }

    }

    private static void generateMappingEXT(String extMappingPackageName, String extMappingPackageDirName, String tplMappingPackageName, String tplMappingPackageDirName) {


        String filePath = BUILD_PATH + tplMappingPackageDirName + "/";

        File file=new File(filePath);

        File[] files=file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File t) {
                return t.getName().endsWith(".xml");
            }
        });
        for(File f:files){
            String cname=f.getName().substring(0,f.getName().length()-4);
            String path=BUILD_PATH+extMappingPackageDirName+"/";

            String fileName=cname+"Ext.xml";
            if(new File(path+fileName).exists()){
                System.out.println("existed:"+path+fileName);
            }else{
                StringBuilder builder=new StringBuilder(1024);
                builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("\n");
                builder.append("\n");
                builder.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">").append("\n");
                builder.append("<mapper namespace=\"").append(extMappingPackageName).append(".").append(cname).append("Ext").append("\">").append("\n");
                builder.append("\n");
                builder.append("\n");
                builder.append("\n");
                builder.append("</mapper>");
                boolean result=IOUtil.write(path,fileName,builder.toString());
                if(result){
                    System.out.println("generate:"+path+fileName);
                }
            }
        }
    }


    private static void generateDAOEXT(String extDaoPackageName, String extDaoPackageDirName, String templateDaoPackageName, String templateDaoPackageDirName) {

        String filePath = BUILD_PATH + templateDaoPackageDirName + "/";

        File file=new File(filePath);

        File[] files=file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File t) {
                return t.getName().endsWith(".java");
            }
        });
        for(File f:files){
            String cname=f.getName().substring(0,f.getName().length()-5);
            String path=BUILD_PATH+extDaoPackageDirName+"/";

            String fileName=cname+"Ext.java";
            if(new File(path+fileName).exists()){
                System.out.println("existed:"+path+fileName);
            }else{
                StringBuilder builder=new StringBuilder(1024);
                builder.append("package ").append(extDaoPackageName).append(";").append("\n");
                builder.append("\n");
                builder.append("import ").append(templateDaoPackageName).append(".").append(cname).append(";").append("\n");
                builder.append("\n");
                builder.append("public interface ").append(cname).append("Ext").append(" extends ").append(cname).append("{").append("\n");
                builder.append("\n");
                builder.append("}");
                boolean result=IOUtil.write(path,fileName,builder.toString());
                if(result){
                    System.out.println("generate:"+path+fileName);
                }
            }
        }
    }

}
