package org.mansumugang.mansumugang_service.service.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.FileType;
import org.mansumugang.mansumugang_service.constant.InternalErrorType;
import org.mansumugang.mansumugang_service.dto.file.AudioFileSaveDto;
import org.mansumugang.mansumugang_service.exception.InternalErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("prod-profile")
public class S3FileService implements FileService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${file.upload.audio.path}")
    private String audioUploadPath;

    private final AmazonS3 s3Client;

    private final org.mansumugang.mansumugang_service.service.file.CommonFileService commonFileService;

    @Override
    public String saveImageFile(MultipartFile image) throws InternalErrorException {
        commonFileService.checkImageFileValid(image);
        String extension = commonFileService.getFileExtension(image);

        try {
            return uploadFileToS3("image/" + extension, "images/", image);
        } catch (IOException e) {
            throw new InternalErrorException(InternalErrorType.FileSaveError);
        }
    }

    @Override
    public AudioFileSaveDto  saveAudioFile(MultipartFile file) throws InternalErrorException {
        commonFileService.checkAudioFileValid(file);

        try {
            String LocalAudioFileName = commonFileService.saveFileInLocal(file, audioUploadPath);
            Long audioDuration = commonFileService.getAudioDuration(LocalAudioFileName);
            commonFileService.deleteFileInLocal(audioUploadPath, LocalAudioFileName);

            String s3AudioFileName = uploadFileToS3("audio/mpeg", "audios/", file);

            return new AudioFileSaveDto(s3AudioFileName, audioDuration);
        } catch (IOException e) {
            throw new InternalErrorException(InternalErrorType.FileSaveError);
        }
    }

    @Override
    public void deleteImageFile(String fileName) throws InternalErrorException {
        deleteFileFromS3(fileName, FileType.IMAGE);
    }

    @Override
    public void deleteImageFiles(List<String> fileNames) throws InternalErrorException {
        for (String fileName : fileNames) {
            deleteFileFromS3(fileName, FileType.IMAGE);
        }
    }

    @Override
    public void deleteAudioFile(String fileName) throws InternalErrorException {
        deleteFileFromS3(fileName, FileType.AUDIO);
    }

    private void deleteFileFromS3(String fileName, FileType fileType) throws InternalErrorException {
        try {
            s3Client.deleteObject(bucket, fileType.getS3Path() + fileName);
        } catch (Exception e) {
            throw new InternalErrorException(InternalErrorType.FileDeleteError);
        }
    }

    private String uploadFileToS3(String contentType, String filePath, MultipartFile file) throws InternalErrorException, IOException {
        String uniqueFilenameWithoutPath = commonFileService.generateUniqueFileName(file);
        String uniqueFilename = filePath + uniqueFilenameWithoutPath;

        InputStream is = file.getInputStream();
        byte[] bytes = IOUtils.toByteArray(is); // MultipartFile을 byte[]로 변환

        ObjectMetadata metadata = new ObjectMetadata(); //metadata 생성
        metadata.setContentType(contentType);
        metadata.setContentLength(bytes.length);

        //S3에 요청할 때 사용할 byteInputStream 생성
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try {
            // S3로 putObject 할 때 사용할 요청 객체 생성
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucket, uniqueFilename, byteArrayInputStream, metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead);

            // S3에 파일 업로드
            s3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new InternalErrorException(InternalErrorType.FileSaveError);
        } finally {
            byteArrayInputStream.close();
            is.close();
        }

        return uniqueFilenameWithoutPath;
    }

}