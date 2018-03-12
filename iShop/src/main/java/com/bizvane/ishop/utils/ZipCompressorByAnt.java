package com.bizvane.ishop.utils;
import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;
/**
 * Created by PC on 2017/3/20.
 */
/**
 * @ClassName: ZipCompressorByAnt
 * @CreateTime Apr 28, 2013 1:23:45 PM
 * @author : Mayi
 * @Description: 压缩文件的通用工具类-采用ant中的org.apache.tools.ant.taskdefs.Zip来实现，更加简单。
 *
 */
public class ZipCompressorByAnt {

    private File zipFile;

    /**
     * 压缩文件构造函数
     * @param path 最终压缩生成的压缩文件：目录+压缩文件名.zip
     */
    public ZipCompressorByAnt(String path,String fileName) {
        zipFile = new File(path,fileName);
    }

    /**
     * 执行压缩操作
     * @param fileName 需要被压缩的文件/文件夹
     */
    public void compressExe(String path,String fileName) {
        File srcdir = new File(path,fileName);
        if (!srcdir.exists()){
            throw new RuntimeException(fileName + "不存在");
        }

        Project prj = new Project();
        Zip zip = new Zip();
        zip.setProject(prj);
        zip.setDestFile(zipFile);
        FileSet fileSet = new FileSet();
        fileSet.setProject(prj);
      //  fileSet.setFile(srcdir);
        fileSet.setDir(srcdir);
        //fileSet.setIncludes("**/*.java"); //包括哪些文件或文件夹 eg:zip.setIncludes("*.java");
        //fileSet.setExcludes(...); //排除哪些文件或文件夹
        zip.addFileset(fileSet);
        zip.execute();
    }

    /**
     * 执行压缩操作
     * @param fileName 需要被压缩的文件/文件夹
     */
    public void compressExeVip(String path_root,String[] path,String path_s,String fileName) {
        for (int i = 0; i < path.length; i++) {
            Project prj = new Project();
            Copy copy=new Copy();
            copy.setProject(prj);
            String image_url = path_root + path[i];
            copy.setFile(new File(image_url));
            copy.setTodir(new File(path_s,fileName));
            System.out.println("-----image_url-2222----"+image_url);
            copy.execute();
        }

        File srcdir = new File(path_s,fileName);
        if (!srcdir.exists()){
            throw new RuntimeException(fileName + "不存在");
        }

        Project prj = new Project();
        Zip zip = new Zip();
        zip.setProject(prj);
        zip.setDestFile(zipFile);
        FileSet fileSet = new FileSet();
        fileSet.setProject(prj);
        //  fileSet.setFile(srcdir);
        fileSet.setDir(srcdir);
        //fileSet.setIncludes("**/*.java"); //包括哪些文件或文件夹 eg:zip.setIncludes("*.java");
        //fileSet.setExcludes(...); //排除哪些文件或文件夹
        zip.addFileset(fileSet);
        zip.execute();
    }
}
