package org.mansumugang.mansumugang_service.service.fileService;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.InternalErrorType;
import org.mansumugang.mansumugang_service.exception.InternalErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class FileService {
    @Value("${file.upload.image.path}")
    private String imageUploadPath;
    @Value("${file.upload.audio.path}")
    private String audioUploadPath;

    public boolean checkImageFile(MultipartFile file) {
        return file.getContentType() != null && file.getContentType().startsWith("image");
    }

    public boolean checkRecordFile(MultipartFile file){
        return file.getContentType() != null && file.getContentType().equals("audio/mpeg");
    }

    public String saveImageFiles(MultipartFile file) throws InternalErrorException {
        try {
            String uniqueFileName = generateUniqueFileName(Objects.requireNonNull(file.getOriginalFilename()));
            Path filePath = Paths.get(imageUploadPath + "/" + uniqueFileName);
            Files.copy(file.getInputStream(), filePath);
            return uniqueFileName;
        } catch (Exception e) {
            throw new InternalErrorException(InternalErrorType.ImageSaveError);
        }
    }

    public String saveRecordFile(MultipartFile file) throws InternalErrorException {
        try {
            log.info("녹음파일 원래이름 -> UUID로 변경 시작");
            String uniqueFileName = generateUniqueFileName(Objects.requireNonNull(file.getOriginalFilename()));
            Path filePath = Paths.get(audioUploadPath + "/" + uniqueFileName);

            Files.copy(file.getInputStream(), filePath);

            return uniqueFileName;
        } catch (Exception e) {
            throw new InternalErrorException(InternalErrorType.RecordSaveError);
        }
    }

    public void deleteImageFile(String fileName) throws InternalErrorException {
        try {
            String filePath = imageUploadPath + "/" + fileName;
            File file = new File(filePath);
            if (!file.delete()) {
                log.error("파일 삭제를 실패했습니다.");
                log.error("파일 위치: {}", filePath);
            }
        } catch (Exception e) {
            throw new InternalErrorException(InternalErrorType.ImageDeleteError);
        }
    }

    public void deleteRecordFile(String fileName) throws InternalErrorException {
        try {
            String filePath = audioUploadPath + "/" + fileName;
            File file = new File(filePath);

                if (!file.delete()) {
                    log.error("파일 삭제를 실패했습니다.");
                    log.error("파일 위치: {}", filePath);
                }

            } catch(Exception e){
                throw new InternalErrorException(InternalErrorType.RecordDeleteError);
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

    public Long getRecordDuration(String fileName) throws InternalErrorException {
        try {
            Path filePath = Paths.get(audioUploadPath + "/" + fileName);
            log.info("녹음파일이 저장된 경로 : {}", filePath);
            return getMp3Duration(filePath.toString());
        } catch (IOException | InvalidDataException | UnsupportedTagException e) {
            throw new InternalErrorException(InternalErrorType.RecordMetaDataError);
        }
    }

    public Long getMp3Duration(String filePath) throws IOException, InvalidDataException, UnsupportedTagException {
        Mp3File recordFile = new Mp3File(filePath);
        return recordFile.getLengthInSeconds();
    }
}