package file.server.web.utils;

import com.mchange.lang.ByteUtils;
import file.server.model.FileServerEntity;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.web.multipart.MultipartFile;
import wx.contants.WxConstant;
import wx.security.JsonResult;
import wx.util.PropertiesUtils;
import wx.util.WxNumberUtils;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * file utils
 *
 * @author LiYanCheng@HF
 * @version 1.0.0
 * @since 2016年10月10日17:36:20
 */
public class FileServerUtils {

    private static final Log LOG = LogFactory.getLog(FileServerUtils.class);
    private static final String imgType[] = {"bmp", "jpg", "jpeg", "png", "tiff", "gif", "pcx", "tga", "exif", "fpx", "svg", "psd",
            "cdr", "pcd", "dxf", "ufo", "eps", "ai", "raw", "wmf"};
    private static final String documentType[] = {"doc", "docx", "pdf", "ppt", "pptx"};
    private static final String videoType[] = {"mp4", "avi", "mov", "wmv", "asf", "navi", "3gp", "mkv", "f4v", "rmvb", "webm"};

    /**
     * 根据文件类型获取文件大小最大限制
     *
     * @param processorType 文件类型
     * @return 大小限制
     * @since 2016年10月10日17:40:01
     */
    public static long getFileSize(FileServerEntity.ProcessorType processorType) {
        String maxSize = null;
        switch (processorType) {
            case IMAGE:
                maxSize = PropertiesUtils.getConfigProp().getProperty("image.maxSize");
                break;
            case VIDEO:
                maxSize = PropertiesUtils.getConfigProp().getProperty("video.maxSize");
                break;
            case DOCUMENT:
                maxSize = PropertiesUtils.getConfigProp().getProperty("document.maxSize");
                break;
        }

        return WxNumberUtils.transformLong(maxSize) * 1024;
    }

    /**
     * 根据文件类型获取文件允许类型
     *
     * @param processorType 文件类型
     * @return 允许类型
     * @since 2016年10月10日17:40:01
     */
    public static String getFileType(FileServerEntity.ProcessorType processorType) {
        switch (processorType) {
            case IMAGE:
                return PropertiesUtils.getConfigProp().getProperty("image.type");
            case VIDEO:
                return PropertiesUtils.getConfigProp().getProperty("video.type");
            case DOCUMENT:
                return PropertiesUtils.getConfigProp().getProperty("document.type");
            default:
                return StringUtils.EMPTY;
        }
    }

    /**
     * 获取文件后缀名
     *
     * @param fileName 文件名称
     * @return 后缀名
     * @since 2016年10月10日18:23:34
     */
    public static String getFileSuffix(String fileName) {
        int lastIndex = fileName.lastIndexOf(WxConstant.POINT);
        return fileName.substring(lastIndex + 1);
    }

    /**
     * 字符串压缩
     *
     * @param paramString 字符串
     * @return 压缩后的 ascii 字符串
     * @since 2016年8月30日18:02:22
     */
    public static String compress(String paramString) {
        if (StringUtils.isBlank(paramString)) {
            return null;
        }

        ByteArrayOutputStream byteArrayOutputStream = null;
        ZipOutputStream zipOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
            zipOutputStream.putNextEntry(new ZipEntry("0"));
            zipOutputStream.write(paramString.getBytes());
            zipOutputStream.closeEntry();
            byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
            return ByteUtils.toHexAscii(arrayOfByte);
        } catch (IOException e) {
            LOG.error(e);
            return null;
        } finally {
            try {
                if (zipOutputStream != null) {
                    zipOutputStream.close();
                }
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
            } catch (IOException ex) {
                LOG.error(ex);
            }
        }
    }

    /**
     * 字符串解压
     *
     * @param paramString 字符串
     * @return 解压后的字符串
     * @since 2016年8月30日18:02:22
     */
    public static String decompress(String paramString) {
        if (StringUtils.isBlank(paramString)) {
            return null;
        }

        ByteArrayOutputStream byteArrayOutputStream = null;
        ByteArrayInputStream byteArrayInputStream = null;
        ZipInputStream zipInputStream = null;
        try {
            byte[] data = ByteUtils.fromHexAscii(paramString);
            byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayInputStream = new ByteArrayInputStream(data);
            zipInputStream = new ZipInputStream(byteArrayInputStream);
            zipInputStream.getNextEntry();
            byte[] arrayOfByte = new byte[1024];
            int index;
            while ((index = zipInputStream.read(arrayOfByte)) > 0) {
                byteArrayOutputStream.write(arrayOfByte, 0, index);
            }

            return byteArrayOutputStream.toString();
        } catch (IOException e) {
            LOG.error(e);
            return null;
        } finally {
            try {
                if (zipInputStream != null) {
                    zipInputStream.close();
                }

                if (byteArrayInputStream != null) {
                    byteArrayInputStream.close();
                }

                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
            } catch (IOException ex) {
                LOG.error(ex);
            }
        }
    }

    public static boolean executeCommand(String command, boolean verbose) {
        return executeCommand(command, verbose, null);
    }

    //执行命令
    public static boolean executeCommand(String command, boolean verbose,
                                         File workingDir) {
        LOG.info("Executing: " + command);
        try {
            Process process = Runtime.getRuntime().exec(command, null,
                    workingDir);

            InputStream inputStream = process.getInputStream();

            String line = null;
            LineNumberReader reader = new LineNumberReader(
                    new InputStreamReader(inputStream));

            while ((line = reader.readLine()) != null) {
                // Do nothing.
                if (verbose || line.startsWith("FATAL")
                        || line.startsWith("fatal")) {
                    LOG.info(line);
                }
            }

            LOG.info("Completed: " + command);

            return true;
        } catch (IOException e) {

            e.printStackTrace();
            LOG.info("Completed: " + command);
            return false;
        }
    }

    /**
     * 根据文件来获取文件类型
     *
     * @param file 文件对象
     * @param suffix 文件后缀名
     */
    public static void setFileType(FileServerEntity file, String suffix) {
        // 图片
        if (ArrayUtils.contains(imgType, suffix)) {
            file.setFsType(FileServerEntity.FsType.IMAGE);
            file.setProcessorType(FileServerEntity.ProcessorType.IMAGE);
            return;
        }

        // 视频
        if (ArrayUtils.contains(videoType, suffix)) {
            file.setFsType(FileServerEntity.FsType.VIDEO);
            file.setProcessorType(FileServerEntity.ProcessorType.VIDEO);
            return;
        }

        // 文件
        if (ArrayUtils.contains(documentType, suffix)) {
            file.setFsType(FileServerEntity.FsType.DOCUMENT);
            file.setProcessorType(FileServerEntity.ProcessorType.DOCUMENT);
            return;
        }
    }

    /**
     * 判断上传的文件是否加密
     *
     * @param wxFile 保存文件相关信息的对象
     * @param bytes  上传的文件的字节数组
     * @return 是否需要加密的布尔类型（true为加密，false为没有加密）
     */
    public static boolean checkFilePwd(FileServerEntity wxFile, byte[] bytes) {
        String suffix = wxFile.getSuffix();
        boolean flag = true;
        switch (suffix) {
            case "doc":
                flag = isHasPwdWord2003(bytes);
                break;
            case "docx":
                flag = isHasPwdWord2007(bytes);
                break;
            case "ppt":
                flag = isHasPwdPPT(bytes);
                break;
            case "pptx":
                flag = isHasPwdPPT(bytes);
                break;
           /* case "pdf":
                flag = isHasPwdPDF(bytes);
                break;*/
            default:
                flag = false;
                break;
        }

        return flag;
    }

    /**
     * 判断word2003文档是否加密方法
     * true 为加密，false为未加密
     *
     * @param data 上传的文档字节数组
     * @return 判断文档是否加密
     */
    private static boolean isHasPwdWord2003(byte[] data) {
        boolean flag = true;
        InputStream inputStream = null;
        HWPFDocument hwpfDocument = null;
        try {
            inputStream = new ByteArrayInputStream(data);
            hwpfDocument = new HWPFDocument(inputStream);
            flag = false;
        } catch (Exception e) {
            LOG.error(e);
        } finally {
            try {
                inputStream.close();
                if (hwpfDocument != null) {
                    hwpfDocument.close();
                }
            } catch (IOException e2) {
                LOG.error(e2);
            }
            return flag;
        }
    }

    /**
     * 判断word2007文档是否加密方法
     * true 为加密，false为未加密
     *
     * @param data 上传的文档字节数组
     * @return 判断文档是否加密
     */
    private static boolean isHasPwdWord2007(byte[] data) {
        boolean flag = true;
        InputStream inputStream = null;
        POIFSFileSystem pfs = null;
        try {
            inputStream = new ByteArrayInputStream(data);
            pfs = new POIFSFileSystem(inputStream);

        } catch (Exception e) {
            flag = false;
            LOG.error(e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e2) {
                LOG.error(e2);
            }
            return flag;
        }
    }

    /**
     * 判断ppt文档是否加密方法
     * true 为加密，false为未加密
     *
     * @param data 上传的文档字节数组
     * @return 判断文档是否加密
     */
    private static boolean isHasPwdPPT(byte[] data) {
        boolean flag = true;
        InputStream inputStream = null;
        NPOIFSFileSystem fs = null;
        try {
            inputStream = new ByteArrayInputStream(data);
            fs = new NPOIFSFileSystem(inputStream);
        } catch (Exception e) {
            flag = false;
            LOG.error(e);
        } finally {
            try {
                inputStream.close();
                if (fs != null) {
                    fs.close();
                }

            } catch (IOException e) {
                LOG.error(e);
            }
        }
        return flag;
    }

    public static JsonResult<Map<String, String>> checkFile(MultipartFile multipartFile, FileServerEntity wxFile) throws IOException {

        if (multipartFile == null || multipartFile.isEmpty()) {
            return new JsonResult<Map<String, String>>(false, "请选择文件上传！");
        }

        String fileName = multipartFile.getOriginalFilename();
        String fileSuffix = FileServerUtils.getFileSuffix(fileName);

        if (StringUtils.isNotBlank(fileSuffix)) {
            String lowerCasefileSuffix = fileSuffix.toLowerCase();
            wxFile.setSuffix(lowerCasefileSuffix);
        }

        if (checkFilePwd(wxFile, multipartFile.getBytes())) {
            return new JsonResult<>(false, "文档上传失败，不能上传加密文件！请解密后重新上传！");
        }

        long fileSize = multipartFile.getSize();
        if (!FileServerEntity.FsType.COMMON.equals(wxFile.getFsType())) {
            return new JsonResult<>(true, JsonResult.SUCCESS);
        }


        long maxSize = FileServerUtils.getFileSize(wxFile.getProcessorType());
        if (maxSize > 0 && maxSize < fileSize) {/*
            switch (peFile.getFsType()) {
                case VIDEO:
                    return new JsonResult<Map<String, String>>(false, "视频文件大小已经超过上限，上限 " + (maxSize / 1024) + "KB");
                case "FILE":
                    return new JsonResult<Map<String, String>>(false, "讲义文件大小已经超过上限，上限 " + (maxSize / 1024) + "KB");
                case "IMAGE":
                    return new JsonResult<Map<String, String>>(false, "封面图片文件大小已经超过上限，上限 " + (maxSize / 1024) + "KB");
            }*/

        }

        String fileTypes = FileServerUtils.getFileType(wxFile.getProcessorType());
        if (StringUtils.isNotBlank(fileTypes)) {
            List<String> fileTypeList = Arrays.asList(fileTypes.split(WxConstant.COMMA));
            if (!fileTypeList.contains(wxFile.getSuffix())) {/*
                switch (peFile.getFileType()) {
                    case "VIDEO":
                        return new JsonResult<Map<String, String>>(false, "视频文件格式不正确，上限 " + (maxSize / 1024) + "KB");
                    case "FILE":
                        return new JsonResult<Map<String, String>>(false, "讲义文件格式不正确，上限 " + (maxSize / 1024) + "KB");
                    case "IMAGE":
                        return new JsonResult<Map<String, String>>(false, "封面图片文件格式不正确，上限 " + (maxSize / 1024) + "KB");

                }*/
            }
        }
        return new JsonResult<>(true, JsonResult.SUCCESS);
    }

    public static String getTimeFrames(long time) {
        String frames = "";
        long temp = 0;
        StringBuffer sb = new StringBuffer();
        temp = time / 3600;
        sb.append((temp < 10) ? "0" + temp + ":" : "" + temp + ":");

        temp = time % 3600 / 60;
        sb.append((temp < 10) ? "0" + temp + ":" : "" + temp + ":");

        temp = time % 3600 % 60;
        sb.append((temp < 10) ? "0" + temp : "" + temp);

        return sb.toString();
    }
}
