package file.server.service.service.impl;

import file.server.domain.repository.FileServerRepository;
import file.server.model.FileServerEntity;
import file.server.service.service.IFileServerService;
import file.server.service.utils.PropertiesUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import wx.base.domain.IRepository;
import wx.base.service.impl.BaseService;
import wx.contants.WxConstant;
import wx.security.BaseEntity;
import wx.security.User;
import wx.util.ShiroUtils;
import wx.util.UUIDUtils;
import wx.util.WxDateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * file server service
 *
 * @author LiYanCheng@HF
 * @version 1.0.0
 * @since 2016年10月10日10:29:26
 */
@Service
public class FileServerServiceImpl extends BaseService<FileServerEntity, String> implements IFileServerService {

    protected org.apache.commons.logging.Log logger = LogFactory.getLog(getClass());

    @Autowired
    private FileServerRepository respontory;

    @Override
    protected IRepository<FileServerEntity, String> getRepository() {
        return respontory;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public FileServerEntity storageFile(FileServerEntity file, byte[] data) throws IOException {
        if (file == null || file.getFsType() == null || StringUtils.isBlank(file.getSuffix()) ||
                ArrayUtils.isEmpty(data) || file.getProcessorType() == null ||
                file.getTemplateType() == null) {
            throw new IllegalArgumentException("Parameters is not valid!");
        }
        initEntry(file);
        respontory.insert(file);
        return upload(file, data);
    }

    @Override
    public FileServerEntity upload(FileServerEntity FileServerEntity, byte[] data) throws IOException {
        if (FileServerEntity == null || FileServerEntity.getFsType() == null || StringUtils.isBlank(FileServerEntity.getId()) ||
                StringUtils.isBlank(FileServerEntity.getSuffix()) || ArrayUtils.isEmpty(data) || FileServerEntity.getProcessorType() == null ||
                FileServerEntity.getTemplateType() == null) {
            throw new IllegalArgumentException("Parameters is not valid!");
        }

        String filePath = getStoragePath(FileServerEntity);
        String storageRootPath = PropertiesUtils.getEnvProp().getProperty("root.file.upload.path");
        File file = new File(storageRootPath + WxConstant.BACKSLASH + filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(data);
            filePath = PropertiesUtils.getEnvProp().getProperty("file.access.path") + WxConstant.BACKSLASH + filePath;
            FileServerEntity.setFilePath(filePath);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }

        return FileServerEntity;
    }

    private String getStoragePath(FileServerEntity file) {
        String storagePath = "";
        switch (file.getFsType()) {
            case COMMON:
                String timeStr = WxDateUtils.format(file.getCreateTime(), "yyMM");
                storagePath = storagePath + FileServerEntity.FsType.COMMON.getText() + WxConstant.BACKSLASH + timeStr
                        + WxConstant.BACKSLASH;
                if (StringUtils.isNotBlank(file.getReferId())) {
                    storagePath += file.getReferId() + WxConstant.BACKSLASH;
                }

                switch (file.getProcessorType()) {
                    case IMAGE:
                        return storagePath + file.getId() + WxConstant.BACKSLASH + FileServerEntity.IMAGE_NAME + WxConstant.POINT + file.getSuffix();
                    default:
                        return storagePath + file.getId() + WxConstant.POINT + file.getSuffix();
                }

            case VIDEO:
                timeStr = WxDateUtils.format(file.getCreateTime(), "yyMM");
                storagePath = storagePath + FileServerEntity.FsType.VIDEO.getText() + WxConstant.BACKSLASH + timeStr + WxConstant.BACKSLASH;
                if (StringUtils.isNotBlank(file.getExamId()) && StringUtils.isNotBlank(file.getArrangeId())) {
                    storagePath = storagePath + file.getExamId() + WxConstant.BACKSLASH + file.getArrangeId() + WxConstant.BACKSLASH;
                }

                switch (file.getProcessorType()) {
                    case IMAGE:
                        storagePath += file.getCreateUser() + WxConstant.BACKSLASH;
                        return storagePath + "images" + WxConstant.BACKSLASH + file.getId() + WxConstant.POINT + file.getSuffix();
                    default:
                        storagePath += file.getCreateUser() + WxConstant.BACKSLASH;
                        return storagePath + file.getId() + WxConstant.POINT + file.getSuffix();
                }

            case IMAGE:
                timeStr = WxDateUtils.format(file.getCreateTime(), "yyMM");
                storagePath = storagePath + FileServerEntity.FsType.IMAGE.getText() + WxConstant.BACKSLASH + timeStr + WxConstant.BACKSLASH;
                return storagePath + FileServerEntity.IMAGE + WxConstant.BACKSLASH + file.getId() + WxConstant.POINT + file.getSuffix();

            default:
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public String getFilePath(String fileId) {
        if (StringUtils.isBlank(fileId)) {
            logger.error("File id is blank!");
        }

        FileServerEntity FileServerEntity = respontory.findByPrimaryKey(fileId);
        if (FileServerEntity == null) {
            return null;
        }

        return getFilePath(FileServerEntity);
    }

    @Override
    public String getFilePath(FileServerEntity FileServerEntity) {
        if (FileServerEntity == null || StringUtils.isBlank(FileServerEntity.getId()) || FileServerEntity.getFsType() == null ||
                StringUtils.isBlank(FileServerEntity.getSuffix()) || FileServerEntity.getProcessorType() == null ||
                FileServerEntity.getTemplateType() == null) {
            throw new IllegalArgumentException("Parameters is not valid!");
        }

        String filePath = getStoragePath(FileServerEntity);
        return /*PropertiesUtils.getEnvProp().getProperty("file.access.path") + WxConstant.BACKSLASH + */filePath;
    }

    @SuppressWarnings({ "unchecked" })
    protected void initEntry(FileServerEntity entry) {
        BaseEntity<String> item = (BaseEntity<String>) entry;
        Date d = new Date();
        User user = ShiroUtils.getUser();
        if (user != null && item.getCreateTime() == null) {
            item.setCreateTime(d);
            item.setCreateUser(user.getName());
            item.setUpdateTime(d);
            item.setUpdateUser(item.getCreateUser());
            item.setId(UUIDUtils.getUUID());
        }
    }

}
