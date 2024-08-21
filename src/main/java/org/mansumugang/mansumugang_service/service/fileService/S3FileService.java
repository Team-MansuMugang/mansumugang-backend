package org.mansumugang.mansumugang_service.service.fileService;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.constant.FileType;
import org.mansumugang.mansumugang_service.constant.InternalErrorType;
import org.mansumugang.mansumugang_service.exception.InternalErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3FileService {
    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String saveImageFile(MultipartFile image) throws IOException {
        checkImageFileValid(image);

        String extension = getFileExtension(image);
        return uploadFileToS3("image/" + extension, "images/", image);
    }

    public String savePostImageFile(MultipartFile image) throws IOException {
        checkImageFileValid(image);

        String extension = getFileExtension(image);
        return uploadFileToS3("image/" + extension, "postImages/", image);
    }

    public String saveRecordFile(MultipartFile audio) throws InternalErrorException, IOException {
        checkAudioFileValid(audio);

        return uploadFileToS3("audio/mpeg", "audios/", audio);
    }

    private String uploadFileToS3(String contentType, String filePath, MultipartFile file) throws IOException {
        String uniqueFilenameWithoutPath = generateUniqueFileName(file);
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
            throw new InternalErrorException(InternalErrorType.S3_PUT_OBJECT_ERROR);
        } finally {
            byteArrayInputStream.close();
            is.close();
        }

        return uniqueFilenameWithoutPath;
    }

    public void deleteFileFromS3(String fileName, FileType fileType) {
        try {
            s3Client.deleteObject(bucket, fileType.getS3Path() + fileName);
        } catch (Exception e) {
            throw new InternalErrorException(InternalErrorType.S3_DELETE_OBJECT_ERROR);
        }
    }


    // 유효한 파일인지 확인
    private void checkFileValid(MultipartFile file) {
        if (file.isEmpty() || Objects.isNull(file.getOriginalFilename())) {
            throw new InternalErrorException(InternalErrorType.EMPTY_FILE_EXCEPTION);
        }
    }

    // 유효한 이미지 파일인지 확인
    private void checkImageFileValid(MultipartFile imageFile) {
        checkFileValid(imageFile);

        // 파일 확장자 추출 및 검증
        String extension = getFileExtension(imageFile);
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png");
        if (!allowedExtentionList.contains(extension)) {
            throw new InternalErrorException(InternalErrorType.INVALID_FILE_EXTENSION);
        }
    }

    // 유효한 음성 파일인지 확인
    private void checkAudioFileValid(MultipartFile imageFile) {
        checkFileValid(imageFile);

        // 파일 확장자 추출 및 검증
        String extension = getFileExtension(imageFile);
        List<String> allowedExtentionList = List.of("mp3");
        if (!extension.equals("mp3")) {
            throw new InternalErrorException(InternalErrorType.INVALID_FILE_EXTENSION);
        }
    }

    // 파일 확장자 추출
    private String getFileExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new InternalErrorException(InternalErrorType.NO_FILENAME_ERROR);
        }

        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new InternalErrorException(InternalErrorType.NO_FILE_EXTENSION);
        }

        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    // UUID를 이용하여 파일이름 생성
    private String generateUniqueFileName(MultipartFile file) {
        // UUID 생성 및 하이픈 제거
        String fileExtension = getFileExtension(file);
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return uuid + "." + fileExtension;
    }
}