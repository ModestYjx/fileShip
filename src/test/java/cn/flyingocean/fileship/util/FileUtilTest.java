package cn.flyingocean.fileship.util;

import org.junit.Test;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static cn.flyingocean.fileship.util.FileUtil.packageZip;
import static java.nio.file.Files.list;
import static org.junit.Assert.*;

public class FileUtilTest {
    // pass
    @Test
    public void moveMerge() throws Exception {
        FileUtil.moveMerge("14","1","15","2");
    }

    // pass
    @Test
    public void copyMerge() throws Exception {
        List<String> fileUUIDs = new ArrayList<>();
        fileUUIDs.add("1.1");
        fileUUIDs.add("1.2");
        List<String> fileNames = new ArrayList<>();
        fileNames.add("1.1.1.txt");
        fileNames.add("1.2.1.txt");

        FileUtil.copyMerge("14","1",fileUUIDs,fileNames,"15","2");
    }

    public static final String BASE_STORAGE_PATH = "F:"+File.separator+"fileship"+File.separator;

    @Test
    public void createNewUserDirectory() throws Exception {
        String fp = "F:"+File.separator+"fileship"+File.separator+"huwei";
        System.out.println(fp);
        File file = new File(fp);
        file.mkdir();
        // FileUtil.createNewUserDirectory(1);
    }

    @Test
    public void storeFile() throws Exception {
        try {
            byte[] bytes = "1234".getBytes();
            Path path = Paths.get("F:\\fileship\\234");
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // pass
    @Test()
    public void getFile() throws Exception {
        File file = FileUtil.getFile("1","asdft","asdfa.txt");
        System.out.println(file.getAbsolutePath());
    }

    // pass
    @Test
    public void join() throws Exception {
        FileUtil.join("3","1","4","1");
    }

    // pass
    @Test
    public void rename() throws Exception {
        FileUtil.rename("3","1","2.txt","3.txt");
    }


    // pass
    @Test
    public void createWareHouseDirectory() throws Exception {
        FileUtil.createWareHouseDirectory(3,"123");
    }

    @Test
    public void list() throws Exception {
       File file = FileUtil.getWarehouse("4","164D91C9B1E9439787E660C21955CEF5");
       String[] fs  = file.list();
        System.out.println(fs);
       for (String s:fs){
            System.out.println(s);
        }
        System.out.println(FileUtil.BASE_STORAGE_PATH);
    }


    // pass
    @Test
    public void testPackageZip() throws Exception {
        Path path = Paths.get("F:","fileship","test.zip");
        if (Files.exists(path)==false){
            System.out.println("asd");
            Files.createFile(path);
        }
        List<File> fileList = new ArrayList<>();
        fileList.add(new File("F:\\fileship\\14\\1.txt"));
        fileList.add(new File("F:\\fileship\\14\\2.txt"));
        fileList.add(new File("F:\\fileship\\14\\3.txt"));


     }


}