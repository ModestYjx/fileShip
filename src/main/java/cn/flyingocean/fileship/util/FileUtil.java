package cn.flyingocean.fileship.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {
    public static String BASE_STORAGE_PATH;
    public static String OSName = System.getProperty("os.name");
    public static String ZIP_TEMP = "zip_temp";
    // 移动地合并
    public static String MOVE_MERGE = "move";
    // 复制地合并
    public static String COPY_MERGE = "copy";
    // 关联地合并
    public static String ASSOCIATE_MERGE = "associate";


    static {
        if (OSName.equals("Windows 10")){
            BASE_STORAGE_PATH = "F:"+File.separator+"fileship"+File.separator;
        }
        else {
            BASE_STORAGE_PATH = "/home/fileship/";
        }
    }

    /**
     * 为用户创建一个目录，并以用户ID为命名
     * @param userId
     * @return
     */
    public static void createNewUserDirectory(int userId){
        File file = new File(BASE_STORAGE_PATH+userId);
        file.mkdir();
    }

    /**
     * 为指定用户创建一个文件仓库文件夹
     * @param userId
     * @param uuid
     * @throws IOException
     */
    public static void createWareHouseDirectory(int userId,String uuid) throws IOException {
        Path path = Paths.get(BASE_STORAGE_PATH,String.valueOf(userId),uuid);
        Files.createDirectory(path);
    }
    /**
     * 将 file:MultipartFile 写入路径 BASE_STORAGE_PATH/userId/uuid/ 下
     * @param userId
     * @param file
     * @return  uuid 该文件的父目录名
     */
    public static String storeFile(int userId, MultipartFile file){

        StringBuilder sb = new StringBuilder(BASE_STORAGE_PATH);
        String uuid = UuidUtil.generate();
        sb.append(userId).append(File.separator).append(uuid);
        File f = new File(sb.toString());
        f.mkdir();

        // 写入本地文件
        try {
            byte[] bytes = file.getBytes();
            sb.append(File.separator).append(file.getOriginalFilename());
            Path path = Paths.get(sb.toString());
            // 该文件可以不存在但必须保证父目录存在
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uuid;
    }

    /**
     * 用将用户的某个文件移动到另一个用户的文件夹下。
     * 表示用户将自己文件提交到别人的多人文件仓库中
     *
     * @param srcUserId         源用户的ID
     * @param fileUUID 源文件目录的UUID
     * @param destUseId         目标用户的ID
     * @param warehouseFileUUID 目标文件仓库目录的UUID
     * @return
     */
    //todo 这里应该细分IOException 来给出错误信息
    public static void join(String srcUserId, String fileUUID,
                            String destUseId, String warehouseFileUUID) throws IOException {
        Path oldPath = Paths.get(BASE_STORAGE_PATH, srcUserId, fileUUID);
        Path newPath = Paths.get(BASE_STORAGE_PATH, destUseId, warehouseFileUUID);
        Files.move(oldPath, newPath.resolve(oldPath.getFileName()));
    }

    /**
     * 用将用户的某个文件夹下的所有文件移动到另一个用户的文件夹下。
     * 表示用户将自己多人文件仓库合并到别人的多人文件仓库中
     *
     * @param srcUserId         源用户的ID
     * @param srcWarehouseUUID  源多人文件仓库目录的UUID
     * @param destUseId         目标用户的ID
     * @param destWarehouseUUID 目标多人文件仓库目录的UUID
     */
    //todo 这里应该细分IOException 来给出错误信息
    public static void moveMerge(String srcUserId, String srcWarehouseUUID,
                             String destUseId, String destWarehouseUUID) throws IOException {
        Path oldPath = Paths.get(BASE_STORAGE_PATH, srcUserId, srcWarehouseUUID);
        Path newPath = Paths.get(BASE_STORAGE_PATH, destUseId, destWarehouseUUID);
        DirectoryStream<Path> stream = Files.newDirectoryStream(oldPath);
        for (Path path : stream) {
            Files.move(oldPath.resolve(path.getFileName()), newPath.resolve(path.getFileName()));
        }
    }


    /**
     * 复制地合并
     */
    public static void copyMerge(String srcUserId, String srcWarehouseUUID,List<String> fileUUIDs,
                                 List<String> fileNames,
                                 String destUseId, String destWarehouseUUID) throws IOException {
        int size = fileNames.size();
        Path oldPath = Paths.get(BASE_STORAGE_PATH, srcUserId, srcWarehouseUUID);
        Path newPath = Paths.get(BASE_STORAGE_PATH, destUseId, destWarehouseUUID);
        for (int i=0;i<size;i++){
            // 先创建文件夹
            Files.createDirectory(newPath.resolve(fileUUIDs.get(i)));
            // 复制文件过去
            Files.copy(oldPath.resolve(fileUUIDs.get(i)).resolve(fileNames.get(i)),
                    newPath.resolve(fileUUIDs.get(i)).resolve(fileNames.get(i)));
        }
    }


    /**
     * 返回由参数指定的 java.io.File
     * @param userId
     * @param fileName
     * @param fileUUID
     * @return
     */
    public static File getFile(String userId, String fileUUID, String fileName) {
        Path path = Paths.get(BASE_STORAGE_PATH, userId, fileUUID,fileName);
        return path.toFile();
    }

    /**
     * 返回由参数指定的 java.io.File
     * @param userId
     * @param warehouseUUID
     * @return
     */
    public static File getWarehouse(String userId, String warehouseUUID) {
        Path path = Paths.get(BASE_STORAGE_PATH, userId, warehouseUUID);
        return path.toFile();
    }


    /**
     * 返回由参数指定的 java.io.File
     * @param userId
     * @param warehouseUUID
     * @param fileUUID
     * @param filename
     * @return
     */
    public static File getFileInWarehouse(String userId,String warehouseUUID,String fileUUID,String filename){
        Path path = Paths.get(BASE_STORAGE_PATH,userId,warehouseUUID,fileUUID,filename);
        return path.toFile();
    }

    /**
     * 文件重命名 不在任何仓库中
     * @param userId
     * @param fileUUID
     * @param oldName
     * @param newName
     * @throws IOException
     */
    public static void rename(String userId,String fileUUID,String oldName,String newName) throws IOException {
        Path oldPath = Paths.get(BASE_STORAGE_PATH,userId,fileUUID,oldName);
        Path newPath = Paths.get(BASE_STORAGE_PATH,userId,fileUUID,newName);
        Files.move(oldPath,newPath);
    }

    /**
     * 文件重命名 在任何仓库中
     * @param userId
     * @param fileUUID
     * @param oldName
     * @param newName
     * @throws IOException
     */
    public static void rename(String userId,String warehouseUUID,String fileUUID,String oldName,String newName) throws IOException {
        Path oldPath = Paths.get(BASE_STORAGE_PATH,userId,warehouseUUID,fileUUID,oldName);
        Path newPath = Paths.get(BASE_STORAGE_PATH,userId,warehouseUUID,fileUUID,newName);
        Files.move(oldPath,newPath);
    }

    /**
     *
     * @param zipFileName  压缩包文件名
     * @param fileList  需要压缩的文件
     * @return
     */
    public static File packageZip(String zipFileName,List<File> fileList){
        //图片打包操作
        ZipOutputStream zipStream = null;
        FileInputStream zipSource = null;
        BufferedInputStream bufferStream = null;
        File zipFile = null;
        try {
            zipFile = Paths.get(BASE_STORAGE_PATH,ZIP_TEMP,zipFileName).toFile();
            if (zipFile.exists())   return null;

            zipStream = new ZipOutputStream(new FileOutputStream(zipFile));// 用这个构造最终压缩包的输出流
            for (File file : fileList) {
                zipSource = new FileInputStream(file);

                byte[] bufferArea = new byte[1024 * 10];// 读写缓冲区

                // 压缩条目不是具体独立的文件，而是压缩包文件列表中的列表项，称为条目，就像索引一样
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zipStream.putNextEntry(zipEntry);// 定位到该压缩条目位置，开始写入文件到压缩包中

                bufferStream = new BufferedInputStream(zipSource, 1024 * 10);// 输入缓冲流
                int read = 0;

                // 在任何情况下，b[0] 到 b[off] 的元素以及 b[off+len] 到 b[b.length-1]
                // 的元素都不会受到影响。这个是官方API给出的read方法说明，经典！
                while ((read = bufferStream.read(bufferArea, 0, 1024 * 10)) != -1) {
                    zipStream.write(bufferArea, 0, read);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        } finally {
            // 关闭流
            try {
                if (null != bufferStream)
                    bufferStream.close();
                if (null != zipStream)
                    zipStream.close();
                if (null != zipSource)
                    zipSource.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return null;
            }
        }
        return zipFile;
    }

    /**
     * 删除文件
     * @param userId
     * @param fileUUID
     * @param fileName
     * @throws IOException
     */
    public static void deleteFile(String userId,String fileUUID,String fileName) throws IOException {
        Path path = Paths.get(BASE_STORAGE_PATH,userId,fileUUID,fileName);
        Files.deleteIfExists(path);
    }

    /**
     * 删除文件
     * @param userId
     * @param warehouseUUID
     * @param fileUUID
     * @param fileName
     * @throws IOException
     */
    public static void deleteFile(String userId,String warehouseUUID,String fileUUID,String fileName) throws IOException {
        Path path = Paths.get(BASE_STORAGE_PATH,userId,warehouseUUID,fileUUID,fileName);
        Files.deleteIfExists(path);
    }


    /**
     * 删除文件目录
     * @param userId
     * @param fileUUID
     * @throws IOException
     */
    public static void deleteFileDir(String userId,String fileUUID) throws IOException {
        Path path = Paths.get(BASE_STORAGE_PATH,userId,fileUUID);
        Files.deleteIfExists(path);
    }

    /**
     * 删除文件目录
     * @param userId
     * @param fileUUID
     * @throws IOException
     */
    public static void deleteFileDir(String userId,String warehouseUUID,String fileUUID) throws IOException {
        Path path = Paths.get(BASE_STORAGE_PATH,userId,warehouseUUID,fileUUID);
        Files.deleteIfExists(path);
    }

    /**
     * 删除仓库目录
     * @param userId
     * @param warehouseUUID
     * @throws IOException
     */
    public static void deleteWarehouseDir(String userId,String warehouseUUID) throws IOException {
        Path path = Paths.get(BASE_STORAGE_PATH,userId,warehouseUUID);
        Files.deleteIfExists(path);
    }
}
