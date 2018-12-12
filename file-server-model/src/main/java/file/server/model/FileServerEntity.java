package file.server.model;

import wx.security.BasicEntity;

public class FileServerEntity extends BasicEntity {

    /**
     * 文件常量
     */
    public static final String IMAGE_NAME = "o";
    public static final String DATA = "data";
    public static final String COVER = "cover";
    public static final String THUMB = "thumb";
    public static final String IMAGE = "image";
    public static final String MEDIA = "media";
    public static final String HANDOUT = "handout";
    public static final String _fileName = "fileName";
    public static final String _fsType = "fsType";
    public static final String _templateType = "templateType";
    public static final String _processorType = "processorType";
    public static final String _referId = "referId";
    public static final String _fileSize = "fileSize";
    public static final String _fileLength = "fileLength";
    public static final String _suffix = "suffix";

    public enum FsType {
        COMMON("公用功能", "common"),
        DOCUMENT("文件", "document"),
        VIDEO("视频", "video"),
        IMAGE("图片", "IMAGE");

        private String description;

        private String text;

        private FsType(String description, String text) {
            this.text = text;
            this.description = description;
        }

        public String getText() {
            return text;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum ProcessorType {
        VIDEO("视频", "video"),
        IMAGE("图片", "image"),
        DOCUMENT("文件", "document");

        private String description;

        private String text;

        private ProcessorType(String description, String text) {
            this.text = text;
            this.description = description;
        }

        public String getText() {
            return text;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum TemplateType {
        USER("用户模板", "user");


        private String description;

        private String text;

        private TemplateType(String description, String text) {
            this.text = text;
            this.description = description;
        }

        public String getText() {
            return text;
        }

        public String getDescription() {
            return description;
        }
    }


    /**
     * 功能类型
     */
    private FsType fsType;

    /**
     * 模板类型
     */
    private TemplateType templateType;

    /**
     * 文件类型
     */
    private ProcessorType processorType;

    /**
     * 创建视频课程可能会产生很多文件，为了把文件放到一个地方，第一次上传文件产生封面文件的fileId，后面所有文件都放在以这个
     * id作为文件夹的下面
     */
    private String tempFileId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件后缀名
     */
    private String suffix;

    /**
     * 关联ID
     */
    private String referId;

    /**
     * 视频文件时长
     */
    private Long fileLength;

    /**
     * 文件路径
     */
    private String filePath;
    private String examId;
    private String arrangeId;
    private String fileTimeFrames;

    public FsType getFsType() {
        return fsType;
    }

    public void setFsType(FsType fsType) {
        this.fsType = fsType;
    }

    public TemplateType getTemplateType() {
        return templateType;
    }

    public void setTemplateType(TemplateType templateType) {
        this.templateType = templateType;
    }

    public ProcessorType getProcessorType() {
        return processorType;
    }

    public void setProcessorType(ProcessorType processorType) {
        this.processorType = processorType;
    }

    public String getTempFileId() {
        return tempFileId;
    }

    public void setTempFileId(String tempFileId) {
        this.tempFileId = tempFileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getReferId() {
        return referId;
    }

    public void setReferId(String referId) {
        this.referId = referId;
    }

    public Long getFileLength() {
        return fileLength;
    }

    public void setFileLength(Long fileLength) {
        this.fileLength = fileLength;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getArrangeId() {
        return arrangeId;
    }

    public void setArrangeId(String arrangeId) {
        this.arrangeId = arrangeId;
    }

    public String getFileTimeFrames() {
        return fileTimeFrames;
    }

    public void setFileTimeFrames(String fileTimeFrames) {
        this.fileTimeFrames = fileTimeFrames;
    }
}
