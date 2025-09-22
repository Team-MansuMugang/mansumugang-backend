package org.mansumugang.mansumugang_service.service.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.dto.file.AudioFileSaveDto;
import org.mansumugang.mansumugang_service.exception.InternalErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"local-profile", "dev-profile"})
public class GeneralFileService implements org.mansumugang.mansumugang_service.service.file.FileService {
    @Value("${file.upload.image.path}")
    private String imageUploadPath;

    @Value("${file.upload.audio.path}")
    private String audioUploadPath;

    private final org.mansumugang.mansumugang_service.service.file.CommonFileService commonFileService;

    @Override
    public String saveImageFile(MultipartFile file) throws InternalErrorException {
        commonFileService.checkImageFileValid(file);

        return commonFileService.saveFileInLocal(file, imageUploadPath);
    }

    @Override
    public AudioFileSaveDto saveAudioFile(MultipartFile file) throws InternalErrorException {
        commonFileService.checkAudioFileValid(file);

        String audioFileName = commonFileService.saveFileInLocal(file, audioUploadPath);
        Long audioDuration = commonFileService.getAudioDuration(audioFileName);
        return new AudioFileSaveDto(audioFileName, audioDuration);
    }


    @Override
    public void deleteImageFile(String fileName) throws InternalErrorException {
        commonFileService.deleteFileInLocal(imageUploadPath, fileName);
    }

    @Override
    public void deleteImageFiles(List<String> fileNames) throws InternalErrorException {
        if (fileNames != null) {
            for (String fileName : fileNames) {
                commonFileService.deleteFileInLocal(imageUploadPath, fileName);
            }
        }
    }

    @Override
    public void deleteAudioFile(String fileName) throws InternalErrorException {
        commonFileService.deleteFileInLocal(audioUploadPath, fileName);
    }


}