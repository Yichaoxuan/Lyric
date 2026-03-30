package com.lyric.lyric.Service.fileUpload;

import com.lyric.lyric.Config.fileUpload.UploadProperties;
import com.lyric.lyric.Enums.mediaFile.FileType;
import com.lyric.lyric.Enums.message.BusinessErrorMsgEnums;
import com.lyric.lyric.Enums.message.SuccessMsgEnums;
import com.lyric.lyric.Enums.message.SystemErrorMsgEnums;
import com.lyric.lyric.Exception.BusinessException;
import com.lyric.lyric.Exception.SystemException;
import com.lyric.lyric.Mapper.fileUpload.MediaFileMapper;
import com.lyric.lyric.POJO.fileUpload.MediaFilePojo;
import com.lyric.lyric.Utils.resultUtils.Result;
import com.lyric.lyric.Utils.resultUtils.ResultBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 媒体文件服务类，用于处理媒体文件的上传、查询、删除等操作
 *
 * @author Yichaoxuan
 * @since 2026/03/16
 */
@Slf4j
@Service
public class MediaFileService {

    private final MediaFileMapper mediaFileMapper;
    private final UploadProperties uploadProperties;
    private final Thumbnailator thumbnailator;

    /**
     * 构造方法，注入依赖的服务和配置
     *
     * @param mediaFileMapper  媒体文件数据访问层，用于数据库操作
     * @param uploadProperties 上传配置文件，包含上传目录、大小限制等配置信息
     * @param thumbnailator    缩略图生成器，用于生成图片的缩略图
     */
    public MediaFileService(MediaFileMapper mediaFileMapper, UploadProperties uploadProperties,
            Thumbnailator thumbnailator) {
        this.mediaFileMapper = mediaFileMapper;
        this.uploadProperties = uploadProperties;
        this.thumbnailator = thumbnailator;
    }

    /**
     * 上传媒体文件到服务器，并生成缩略图（针对图片文件）
     * 该方法会将文件保存到配置的上传目录，并在数据库中记录文件信息
     *
     * @param file     待上传的多部分文件对象，包含文件内容和元数据
     * @param diaryId  关联的日记 ID，用于将文件与特定日记条目关联
     * @param fileType 客户端指定的文件类型，用于验证和分类，可为 null
     * @return 上传成功后的媒体文件 POJO 对象，包含文件的完整信息（ID、路径、大小、类型等）
     * @throws BusinessException 当文件为空、文件大小超限或文件类型不支持时抛出业务异常
     */
    @Transactional
    public Result<MediaFilePojo> upload(MultipartFile file, Integer diaryId, FileType fileType) {
        try {
            // 验证diaryId不为空
            if (diaryId == null) {
                throw new BusinessException("日记ID不能为空");
            }

            validateFile(file, fileType);

            FileType actualType = determineFileType(file, fileType);

            String originalFilename = file.getOriginalFilename();
            String extension = getExtension(originalFilename);
            String newFileName = UUID.randomUUID() + "." + extension;

            File uploadDir = new File(uploadProperties.getUploadDir());
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    log.error("创建上传目录失败：{}", uploadProperties.getUploadDir());
                    throw new BusinessException(BusinessErrorMsgEnums.FILE_UPLOAD_FAILED);
                }
            }

            File targetFile = new File(uploadDir, newFileName);
            try {
                file.transferTo(targetFile);
            } catch (IOException e) {
                throw new BusinessException(BusinessErrorMsgEnums.FILE_UPLOAD_FAILED, e);
            }

            MediaFilePojo mediaFilePojo = new MediaFilePojo();
            mediaFilePojo.setDiaryId(diaryId);
            mediaFilePojo.setFileName(originalFilename);
            // 存储相对路径，便于前端访问
            mediaFilePojo.setFilePath("/uploads/" + newFileName);
            mediaFilePojo.setFileType(actualType);
            mediaFilePojo.setFileSize(file.getSize());
            mediaFilePojo.setUploadTime(LocalDateTime.now());

            if (actualType == FileType.JPEG || actualType == FileType.PNG || actualType == FileType.GIF) {
                try {
                    String thumbnailFileName = uploadProperties.getThumbnailSuffix() + newFileName;
                    File thumbnailFile = new File(uploadDir, thumbnailFileName);
                    thumbnailator.generateThumbnail(targetFile, thumbnailFile);
                } catch (IOException e) {
                    log.warn("缩略图生成失败: {}", e.getMessage());
                }
            }

            mediaFileMapper.insert(mediaFilePojo);
            log.info("文件上传成功: {}", originalFilename);

            return ResultBuilder.successWithDataAndMessage(SuccessMsgEnums.SAVE_SUCCESS, mediaFilePojo);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("文件上传失败", e);
        }
    }

    /**
     * 根据日记 ID 查询所有关联的媒体文件列表
     *
     * @param diaryId 日记 ID，用于筛选属于该日记的所有媒体文件
     * @return 媒体文件列表，包含该日记关联的所有媒体文件信息；若没有关联文件则返回空列表
     */
    public Result<List<MediaFilePojo>> getFilesByDiaryId(Integer diaryId) {
        try {
            List<MediaFilePojo> files = mediaFileMapper.selectByDiaryId(diaryId);
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, files);
        } catch (Exception e) {
            throw new BusinessException("查询文件列表失败", e);
        }
    }

    /**
     * 根据文件 ID 获取单个媒体文件的详细信息
     *
     * @param id 媒体文件的唯一标识 ID
     * @return 媒体文件 POJO 对象，包含文件的完整信息；若文件不存在则返回 null
     */
    public Result<MediaFilePojo> getFileById(Integer id) {
        try {
            MediaFilePojo file = mediaFileMapper.selectById(id);
            if (file == null) {
                throw new BusinessException(BusinessErrorMsgEnums.FILE_NOT_FOUND);
            }
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, file);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("查询文件失败", e);
        }
    }

    /**
     * 获取服务器上所有已上传的媒体文件列表
     *
     * @return 所有媒体文件列表，包含系统中所有已上传的媒体文件信息；若无任何文件则返回空列表
     */
    public Result<List<MediaFilePojo>> getAllFiles() {
        try {
            List<MediaFilePojo> files = mediaFileMapper.selectAll();
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, files);
        } catch (Exception e) {
            throw new BusinessException("查询文件列表失败", e);
        }
    }

    /**
     * 根据日记 ID 批量删除关联的所有媒体文件，同时删除物理文件和对应的缩略图
     * 该方法会从事务中删除数据库记录，并清理服务器上的物理文件
     *
     * @param diaryId 待删除媒体文件所属的日记 ID
     */
    @Transactional
    public void deleteFilesByDiaryId(Integer diaryId) {
        try {
            // 验证 diaryId 不为空
            if (diaryId == null) {
                throw new BusinessException("日记 ID 不能为空");
            }

            // 查询该日记关联的所有媒体文件
            List<MediaFilePojo> files = mediaFileMapper.selectByDiaryId(diaryId);
            if (files == null || files.isEmpty()) {
                log.info("日记 ID: {} 没有找到关联的媒体文件", diaryId);
                ResultBuilder.success(SuccessMsgEnums.DELETE_SUCCESS);
                return;
            }

            // 遍历并删除每个文件
            for (MediaFilePojo file : files) {
                deletePhysicalFile(file);
            }

            // 批量删除数据库记录
            int deletedCount = mediaFileMapper.deleteByDiaryId(diaryId);
            log.info("日记 ID: {} 成功删除 {} 个媒体文件", diaryId, deletedCount);

            ResultBuilder.success(SuccessMsgEnums.DELETE_SUCCESS);
        } catch (BusinessException e) {
            throw new BusinessException(BusinessErrorMsgEnums.FILE_NOT_FOUND, e);
        } catch (Exception e) {
            throw new SystemException(SystemErrorMsgEnums.SYSTEM_ERROR, e);
        }
    }

    /**
     * 删除指定 ID 的媒体文件，同时删除物理文件和对应的缩略图
     * 该方法会从事务中删除数据库记录，并清理服务器上的物理文件
     *
     * @param id 待删除的媒体文件 ID
     * @return 删除是否成功的布尔值；若文件不存在或删除失败则返回 false，否则返回 true
     */
    @Transactional
    public Result<Void> deleteFile(Integer id) {
        try {
            MediaFilePojo file = mediaFileMapper.selectById(id);
            if (file == null) {
                throw new BusinessException(BusinessErrorMsgEnums.FILE_NOT_FOUND);
            }

            // 删除物理文件和缩略图
            deletePhysicalFile(file);

            // 删除数据库记录
            boolean success = mediaFileMapper.deleteById(id) > 0;
            if (success) {
                return ResultBuilder.success(SuccessMsgEnums.DELETE_SUCCESS);
            } else {
                throw new BusinessException("文件删除失败");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("文件删除失败", e);
        }
    }

    /**
     * 验证上传文件的有效性，包括检查文件是否为空以及是否超过大小限制
     *
     * @param file     待验证的多部分文件对象
     * @param fileType 文件类型，用于确定最大允许的文件大小；为 null 时不进行大小验证
     * @throws BusinessException 当文件为空或超过指定文件类型的最大尺寸限制时抛出业务异常
     */
    private void validateFile(MultipartFile file, FileType fileType) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(BusinessErrorMsgEnums.FILE_EMPTY);
        }

        if (fileType != null) {
            long maxSize = uploadProperties.getMaxSizeForType(fileType);
            if (file.getSize() > maxSize) {
                throw new BusinessException(BusinessErrorMsgEnums.FILE_SIZE_EXCEEDED);
            }
        }
    }

    /**
     * 确定文件的实际类型，优先使用客户端指定的类型，若未指定则通过 MIME 类型或文件扩展名进行识别
     * 该方法支持多种文件类型识别方式，确保文件能够被正确分类和处理
     *
     * @param file       待识别类型的多部分文件对象
     * @param clientType 客户端指定的文件类型，可为 null；若不为 null 则直接返回该类型
     * @return 文件的实际类型枚举值；若无法识别则抛出业务异常
     * @throws BusinessException 当无法通过任何方式识别文件类型时抛出业务异常
     */
    private FileType determineFileType(MultipartFile file, FileType clientType) {
        if (clientType != null) {
            if (!clientType.getMimeType().equals(file.getContentType())) {
                log.warn("客户端指定类型与MIME不符: {} vs {}", clientType, file.getContentType());
            }
            return clientType;
        }

        String mimeType = file.getContentType();
        FileType detected = FileType.fromMimeType(mimeType);
        if (detected == null) {
            String originalName = file.getOriginalFilename();
            if (originalName != null) {
                String ext = originalName.substring(originalName.lastIndexOf(".") + 1);
                detected = FileType.fromExtension(ext);
            }
        }
        if (detected == null) {
            throw new BusinessException(BusinessErrorMsgEnums.FILE_TYPE_NOT_SUPPORTED);
        }

        return detected;
    }

    /**
     * 从文件名中提取文件扩展名，用于后续的文件类型判断和保存
     * 若文件名为空或不包含扩展名，则默认返回 "jpg"
     *
     * @param filename 原始文件名，可能包含路径信息
     * @return 文件扩展名（不包含点号），如 "jpg"、"png"、"pdf" 等；默认返回 "jpg"
     */
    private String getExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "jpg";
        }
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot + 1) : "jpg";
    }

    /**
     * 删除媒体文件的物理文件和缩略图
     * 该方法会根据文件路径删除服务器上的实际文件，包括原文件和对应的缩略图
     *
     * @param file 待删除的媒体文件 POJO 对象，包含文件路径信息
     */
    private void deletePhysicalFile(MediaFilePojo file) {
        try {
            // 构建完整的物理文件路径
            String fileName = file.getFilePath().substring(file.getFilePath().lastIndexOf('/') + 1);
            File physicalFile = new File(uploadProperties.getUploadDir(), fileName);
            if (physicalFile.exists()) {
                boolean deleted = physicalFile.delete();
                if (!deleted) {
                    log.warn("物理文件删除失败：{}", file.getFilePath());
                }
            }

            // 删除对应的缩略图
            String thumbnailFileName = uploadProperties.getThumbnailSuffix() + physicalFile.getName();
            File thumbnailFile = new File(physicalFile.getParent(), thumbnailFileName);
            if (thumbnailFile.exists()) {
                boolean thumbDeleted = thumbnailFile.delete();
                if (!thumbDeleted) {
                    log.warn("缩略图文件删除失败：{}", thumbnailFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            log.error("删除物理文件失败：{}", file.getFilePath(), e);
        }
    }
}
