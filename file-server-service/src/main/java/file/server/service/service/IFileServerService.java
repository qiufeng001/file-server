package file.server.service.service;

import file.server.model.FileServerEntity;
import wx.base.service.IService;

import java.io.IOException;

/**
 * 文件服务器接口
 *
 * @author LiYanCheng@HF
 * @version 1.0.0
 * @since 2016年9月1日09:42:19
 */
public interface IFileServerService extends IService<FileServerEntity, String> {

    /**
     * 上传文件
     *
     * @param file 上传文件类型
     *
     * @param data   字节信息
     * @return 返回信息
     * <ul>
     * <li>{@linkplain FileServerEntity#id 主键}</li>
     * <li>{@linkplain FileServerEntity#filePath 文件路径}</li>
     * </ul>
     * @since 2017年3月13日19:33:03
     */
    FileServerEntity storageFile(FileServerEntity file, byte[] data) throws IOException;

    /**
     * 上传文件不保存数据库
     *
     * @param file 上传文件类型
     *
     * @param data   字节信息
     * @return 返回信息
     * <ul>
     * <li>{@linkplain FileServerEntity#id UUID}</li>
     * <li>{@linkplain FileServerEntity#filePath 文件路径}</li>
     * </ul>
     * @since 2017年3月13日19:33:03
     */
    FileServerEntity upload(FileServerEntity file, byte[] data) throws IOException;

    /**
     * 通过fileId 获取文件路径
     *
     * @param fileId 文件索引
     * @return file path
     * @since 2016年9月2日14:10:03
     */
    String getFilePath(String fileId);

    /**
     * 获取文件路径
     *
     * @param peFile 文件
     * @return file path
     * @since 2016年9月2日14:10:03
     */
    String getFilePath(FileServerEntity peFile);
}
