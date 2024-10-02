package org.mansumugang.mansumugang_service.service.file;

import org.mansumugang.mansumugang_service.dto.file.AudioFileSaveDto;
import org.mansumugang.mansumugang_service.exception.InternalErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    String saveImageFile(MultipartFile file) throws InternalErrorException;

    AudioFileSaveDto saveAudioFile(MultipartFile file) throws InternalErrorException;

    void deleteImageFile(String fileName) throws InternalErrorException;

    void deleteImageFiles(List<String> fileNames) throws InternalErrorException;

    void deleteAudioFile(String fileName) throws InternalErrorException;
}
