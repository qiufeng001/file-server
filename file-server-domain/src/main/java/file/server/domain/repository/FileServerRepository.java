package file.server.domain.repository;

import file.server.model.FileServerEntity;
import org.apache.ibatis.annotations.Mapper;
import wx.base.domain.IRepository;

/**
 * auther: kiven on 2018/7/26/026 17:47
 * try it bast!
 */
@Mapper
public interface FileServerRepository extends IRepository<FileServerEntity, String> {

}
