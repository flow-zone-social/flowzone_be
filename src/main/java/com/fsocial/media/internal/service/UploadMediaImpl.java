package com.fsocial.media.internal.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.fsocial.media.internal.dto.AttachmentDTO;
import com.fsocial.media.api.MediaItemDTO;
import com.fsocial.media.api.AttachmentType;
import com.fsocial.shared.exception.AppException;
import com.fsocial.shared.exception.StatusCode;
import com.fsocial.media.internal.service.AttachmentsService;
import com.fsocial.media.internal.service.UploadMedia;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UploadMediaImpl implements UploadMedia {
    Cloudinary cloudinary;
    AttachmentsService attachmentsService;
    private static final int BUFFER_SIZE = 8192; // Tăng buffer size để upload nhanh hơn
    private static final long MAX_CLOUDINARY_FILE_SIZE = 10L * 1024 * 1024;
    private static final String CLOUDINARY_FILE_TOO_LARGE = "file size too large";
    private static final String[] SUPPORTED_IMAGE_TYPES = {"jpg", "jpeg", "png", "gif"};
    private static final String[] SUPPORTED_VIDEO_TYPES = {"mp4", "mov", "avi", "wmv"};
    private static final String FILE_TOO_LARGE_MESSAGE = "File %s exceeds the 10MB limit. Please choose a smaller file.";
    private static final String PARTIAL_UPLOAD_FAILED_MESSAGE = "Some files failed to upload. Please check the failed files.";
    private static final String FILE_NAME_FALLBACK = "unknown";
    private static final String UPLOAD_ERROR_MESSAGE = "Upload failed";

    @Override
    public MediaItemDTO[] uploadMedia(MultipartFile[] files, AttachmentType type) {
        if (files == null || files.length == 0) {
            throw new AppException("No files provided", StatusCode.FILE_NOT_FOUND);
        }
        validateAllBeforeUpload(files);
        String userId = currentUserId();
        MediaItemDTO[] mediaItems = new MediaItemDTO[files.length];
        int successCount = 0;
        boolean hasOversizeFailure = false;

        for (int i = 0; i < files.length; i++) {
            MultipartFile currentFile = files[i];
            if (currentFile == null || currentFile.isEmpty()) {
                continue;
            }
            try {
                mediaItems[i] = uploadOne(currentFile, userId, type);
                successCount++;
            } catch (AppException e) {
                mediaItems[i] = null;
                if (e.getErrorCode() == StatusCode.FILE_TOO_LARGE) {
                    hasOversizeFailure = true;
                }
                log.warn("Upload file thất bại tại index {} cho file {}: {}", i, safeFileName(currentFile), e.getMessage());
            }
        }

        if (successCount == 0) {
            throw new AppException(hasOversizeFailure ? StatusCode.FILE_TOO_LARGE : StatusCode.UPLOAD_MEDIA_FAILED);
        }

        if (successCount < countValidFiles(files)) {
            log.warn(PARTIAL_UPLOAD_FAILED_MESSAGE);
        }

        return Arrays.stream(mediaItems)
                .filter(Objects::nonNull)
                .toArray(MediaItemDTO[]::new);
    }

    @Override
    public MediaItemDTO uploadSingleMedia(MultipartFile file) {
        return uploadSingleMedia(file, null);
    }

    @Override
    public MediaItemDTO uploadSingleMedia(MultipartFile file, AttachmentType type) {
        if (file == null || file.isEmpty()) {
            throw new AppException("No file provided", StatusCode.FILE_NOT_FOUND);
        }
        return uploadOne(file, currentUserId(), type);
    }

    private MediaItemDTO uploadOne(MultipartFile file, String userId, AttachmentType type) {
        File tempFile = null;
        try {
            validateFileSize(file);
            String[] fileParts = extractFileParts(file.getOriginalFilename());
            String publicId = generatePublicId(fileParts[0]);
            String extension = fileParts[1].toLowerCase(Locale.ROOT);
            String resourceType = determineResourceType(extension);

            tempFile = convertToFile(file, extension);
            Map<String, Object> uploadParams = configureUploadParams(resourceType);
            uploadParams.put("public_id", publicId);
            uploadParams.put("resource_type", resourceType);
            uploadParams.put("overwrite", true);
            uploadParams.put("invalidate", true);

            Map uploadResult = cloudinary.uploader().upload(tempFile, uploadParams);

            String secureUrl = uploadResult.get("secure_url").toString();
            String resolvedType = uploadResult.get("resource_type").toString();
            Object rawWidth = uploadResult.get("width");
            Object rawHeight = uploadResult.get("height");
            Integer width = rawWidth != null ? Integer.parseInt(rawWidth.toString()) : null;
            Integer height = rawHeight != null ? Integer.parseInt(rawHeight.toString()) : null;
            AttachmentDTO saved = attachmentsService.save(AttachmentDTO.builder()
                    .publicId(uploadResult.get("public_id").toString())
                    .size(uploadResult.get("bytes").toString())
                    .resourceType(resolvedType)
                    .fileType(uploadResult.get("format").toString())
                    .ownerId(userId)
                    .url(secureUrl)
                    .type(type)
                    .width(width)
                    .height(height)
                    .build());

            return MediaItemDTO.builder()
                    .attachmentId(saved.getId())
                    .url(secureUrl)
                    .type(resolvedType)
                    .width(width)
                    .height(height)
                    .build();
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error uploading file {}: {}", safeFileName(file), e.getMessage(), e);
            if (isFileTooLargeException(e)) {
                throw new AppException(buildFileTooLargeMessage(file), StatusCode.FILE_TOO_LARGE);
            }
            throw new AppException(UPLOAD_ERROR_MESSAGE + ": " + e.getMessage(), StatusCode.INTERNAL_SERVER_ERROR);
        } finally {
            cleanUpTempFile(tempFile);
        }
    }

    private String currentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Map<String, Object> configureUploadParams(String resourceType) {
        if ("image".equals(resourceType)) {
            return ObjectUtils.asMap(
                    "quality", "auto",
                    "format", "png",
                    "transformation", new Transformation<>().quality("auto").fetchFormat("png")
            );
        } else if ("video".equals(resourceType)) {
            return ObjectUtils.asMap(
                    "video_codec", "h264",     // Sử dụng codec H.264
                    "quality", "auto"            // Giới hạn chất lượng video ở 70%
            );
        }
        return ObjectUtils.emptyMap(); // Mặc định nếu không xác định được
    }

    private String determineResourceType(String extension) {
        if (Arrays.stream(SUPPORTED_IMAGE_TYPES).anyMatch(extension::equals)) {
            return "image";
        } else if (Arrays.stream(SUPPORTED_VIDEO_TYPES).anyMatch(extension::equals)) {
            return "video";
        }
        throw new AppException("Unsupported file type: " + extension, StatusCode.UNSUPPORTED_MEDIA_TYPE);
    }

    // Fail fast: chặn toàn bộ request trước khi upload file đầu tiên,
    // tránh upload dở dang rồi mới phát hiện file sau vượt giới hạn/sai định dạng.
    private void validateAllBeforeUpload(MultipartFile[] files) {
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            validateFileSize(file);
            String extension = extractFileParts(file.getOriginalFilename())[1].toLowerCase(Locale.ROOT);
            determineResourceType(extension);
        }
    }

    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_CLOUDINARY_FILE_SIZE) {
            throw new AppException(buildFileTooLargeMessage(file), StatusCode.FILE_TOO_LARGE);
        }
    }

    private boolean isFileTooLargeException(Exception exception) {
        return exception.getMessage() != null
                && exception.getMessage().toLowerCase(Locale.ROOT).contains(CLOUDINARY_FILE_TOO_LARGE);
    }

    private String buildFileTooLargeMessage(MultipartFile file) {
        return String.format(FILE_TOO_LARGE_MESSAGE, safeFileName(file));
    }

    private String safeFileName(MultipartFile file) {
        if (file == null || file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
            return FILE_NAME_FALLBACK;
        }
        return file.getOriginalFilename();
    }

    private int countValidFiles(MultipartFile[] files) {
        int count = 0;
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    private File convertToFile(MultipartFile file, String extension) throws IOException {
        File tempFile = File.createTempFile(UUID.randomUUID().toString(), "." + extension);

        try (InputStream in = file.getInputStream();
             FileOutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
    }

    private String generatePublicId(String fileName) {
        return "media_" + UUID.randomUUID().toString() + "_" + fileName;
    }

    private String[] extractFileParts(String originalName) {
        if (originalName == null || !originalName.contains(".")) {
            throw new AppException("Invalid filename", StatusCode.UNSUPPORTED_MEDIA_TYPE);
        }
        int dotIndex = originalName.lastIndexOf('.');
        String name = originalName.substring(0, dotIndex);
        String extension = originalName.substring(dotIndex + 1);
        return new String[]{name, extension};
    }

    private void cleanUpTempFile(File file) {
        if (file != null && file.exists()) {
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                log.warn("Failed to delete temporary file: {}", e.getMessage());
            }
        }
    }
}