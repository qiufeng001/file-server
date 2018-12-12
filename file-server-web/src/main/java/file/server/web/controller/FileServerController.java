package file.server.web.controller;

import file.server.model.FileServerEntity;
import file.server.service.service.IFileServerService;
import file.server.web.utils.FileServerUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import wx.query.Pagenation;
import wx.query.Query;
import wx.security.JsonResult;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * auther: kiven on 2018/7/26/026 21:00
 * try it bast!
 */
@Controller
@SuppressWarnings("ALL")
public class FileServerController {
    protected Log logger = LogFactory.getLog(getClass());

    @Autowired
    private IFileServerService service;

    @ResponseBody
    @RequestMapping("/upload")
    public JsonResult<Map<String, String>> uploadFile(@RequestParam(value = "uploadFile") MultipartFile multipartFile,
                                                      HttpServletRequest request)
            throws IOException {

        try {
            FileServerEntity file = new FileServerEntity();
            Map<String, String> values = new HashMap<>();
            setFileAttribute(multipartFile, file);
            service.storageFile(file, multipartFile.getBytes());
            String filePath = service.getFilePath(file.getId());
            values.put("path", filePath);

            values.put("fileId", file.getId());
            return new JsonResult<>(true, "上传成功", values);
        } catch (Exception ex) {
            logger.error(ex);
            return new JsonResult<>(false, ex.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping("/filePath")
    public List<FileServerEntity> getFiles(HttpServletRequest request, Query query, Pagenation page) {
        List<FileServerEntity> list = service.selectByPage(query, page);

        List<FileServerEntity> result = new ArrayList<>();
        for(FileServerEntity file : list) {
            String filePath = service.getFilePath(file);
            file.setFilePath(filePath);
            result.add(file);
        }
        return result;
    }

    /**
     * 对上传的文件进行验证
     *
     * @param multipartFile 文件的对象
     * @param file 用于存储文件相关的信息
     * @return 返回验证的结果
     */
    private void setFileAttribute(MultipartFile multipartFile, FileServerEntity file) {
        file.setFileSize(multipartFile.getSize());
        String fileName = multipartFile.getOriginalFilename();
        file.setFileName(fileName);
        String fileSuffix = FileServerUtils.getFileSuffix(fileName);

        if (StringUtils.isNotBlank(fileSuffix)) {
            String lowerCasefileSuffix = fileSuffix.toLowerCase();
            file.setSuffix(lowerCasefileSuffix);
            // 设置文件类型
            FileServerUtils.setFileType(file, lowerCasefileSuffix);
        }

        long fileSize = multipartFile.getSize();
        file.setFileSize(fileSize);
    }

    @ResponseBody
    @RequestMapping("/test")
    public String test(HttpServletRequest request) {
        IFileServerService serverService = service;
        return "1111！";
    }
}
