package org.mansumugang.mansumugang_service.service.file;

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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class CommonFileService {
    @Value("${file.upload.audio.path}")
    private String audioUploadPath;

    protected String saveFileInLocal(MultipartFile file, String filePath) throws InternalErrorException {
        try {
            String uniqueFileName = generateUniqueFileName(file);
            Path fullFilePath = Paths.get(filePath + "/" + uniqueFileName);
            Files.copy(file.getInputStream(), fullFilePath);

            return uniqueFileName;
        } catch (Exception e) {
            throw new InternalErrorException(InternalErrorType.FileSaveError);
        }
    }

    protected void deleteFileInLocal(String filePath, String fileName) {
        try {
            String fullFilePath = filePath + "/" + fileName;
            File file = new File(fullFilePath);
            if (!file.delete()) {
                log.error("파일 삭제를 실패했습니다.");
                log.error("파일 위치: {}", filePath);
            }
        } catch (Exception e) {
            throw new InternalErrorException(InternalErrorType.FileDeleteError);
        }
    }

    protected Long getAudioDuration(String fileName) throws InternalErrorException {
        try {
            Path filePath = Paths.get(audioUploadPath + "/" + fileName);
            log.info("녹음파일이 저장된 경로 : {}", filePath);
            Mp3File recordFile = new Mp3File(filePath);
            return recordFile.getLengthInSeconds();
        } catch (IOException | InvalidDataException | UnsupportedTagException e) {
            throw new InternalErrorException(InternalErrorType.RecordMetaDataError);
        }
    }

    // 유효한 파일인지 확인
    protected void checkFileValid(MultipartFile file) {
        if (file.isEmpty() || Objects.isNull(file.getOriginalFilename())) {
            throw new InternalErrorException(InternalErrorType.EmptyFileError);
        }
    }

    // 유효한 이미지 파일인지 확인
    protected void checkImageFileValid(MultipartFile imageFile) {
        checkFileValid(imageFile);

        if(!(imageFile.getContentType() != null && imageFile.getContentType().startsWith("image"))) {
            throw new InternalErrorException(InternalErrorType.EmptyFileError);
        }

        // 파일 확장자 추출 및 검증
        String extension = getFileExtension(imageFile);
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png");
        if (!allowedExtentionList.contains(extension)) {
            throw new InternalErrorException(InternalErrorType.InvalidFileExtension);
        }
    }

    // 유효한 음성 파일인지 확인
    protected void checkAudioFileValid(MultipartFile audioFile) {
        checkFileValid(audioFile);

        // 파일 확장자 추출 및 검증
        String extension = getFileExtension(audioFile);
        if (!extension.equals("mp3")) {
            throw new InternalErrorException(InternalErrorType.InvalidFileExtension);
        }
    }

    // 파일 확장자 추출
    protected String getFileExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new InternalErrorException(InternalErrorType.NoFilenameError);
        }

        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new InternalErrorException(InternalErrorType.NoFileExtension);
        }

        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    // UUID를 이용하여 파일이름 생성
    protected String generateUniqueFileName(MultipartFile file) {
        // UUID 생성 및 하이픈 제거
        String fileExtension = getFileExtension(file);
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return uuid + "." + fileExtension;
    }
}
