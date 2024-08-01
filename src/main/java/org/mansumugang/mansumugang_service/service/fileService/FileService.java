package org.mansumugang.mansumugang_service.service.fileService;

import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.InternalErrorType;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.exception.InternalErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class FileService {
    @Value("${file.upload.image.path}")
    private String uploadPath;

    public boolean checkImageFile(MultipartFile file) {
        return file.getContentType() != null && file.getContentType().startsWith("image");
    }

    public String saveImageFiles(MultipartFile file) throws InternalErrorException {
        try {
            String uniqueFileName = generateUniqueFileName(Objects.requireNonNull(file.getOriginalFilename()));
            Path filePath = Paths.get(uploadPath + "/" + uniqueFileName);
            Files.copy(file.getInputStream(), filePath);
            return uniqueFileName;
        } catch (Exception e) {
            throw new InternalErrorException(InternalErrorType.ImageSaveError);
        }
    }

    public void deleteImageFile(String fileName) throws InternalErrorException {
        try {
            String filePath = uploadPath + "/" + fileName;
            File file = new File(filePath);
            if (!file.delete()) {
                log.error("파일 삭제를 실패했습니다.");
                log.error("파일 위치: {}", filePath);
            }
        } catch (Exception e) {
            throw new InternalErrorException(InternalErrorType.ImageDeleteError);
        }
    }

    public static String generateUniqueFileName(String originalFileName) {
        // 파일 확장자 추출
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

        // UUID 생성 및 하이픈 제거
        String uniqueID = UUID.randomUUID().toString().replaceAll("-", "");

        // 고유한 파일 이름 생성
        return uniqueID + fileExtension;
    }
}