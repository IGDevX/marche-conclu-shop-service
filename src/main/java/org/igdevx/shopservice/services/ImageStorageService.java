package org.igdevx.shopservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.igdevx.shopservice.config.MinioProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageStorageService {

    private final S3Client s3Client;
    private final MinioProperties minioProperties;

    @PostConstruct
    public void init() {
        createBucketIfNotExists();
    }

    private void createBucketIfNotExists() {
        try {
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(minioProperties.getBucketName())
                    .build();
            s3Client.headBucket(headBucketRequest);
            log.info("Bucket '{}' already exists", minioProperties.getBucketName());
        } catch (NoSuchBucketException e) {
            log.info("Bucket '{}' does not exist. Creating...", minioProperties.getBucketName());
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(minioProperties.getBucketName())
                    .build();
            s3Client.createBucket(createBucketRequest);
            log.info("Bucket '{}' created successfully", minioProperties.getBucketName());
        } catch (Exception e) {
            log.error("Error checking/creating bucket: {}", e.getMessage(), e);
        }
    }

    /**
     * Upload an image file to MinIO/S3
     * @param file The image file to upload
     * @return The unique key for the uploaded file
     */
    public String uploadImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload empty file");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        // Generate unique file key
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
        String fileKey = "products/" + UUID.randomUUID() + extension;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(minioProperties.getBucketName())
                    .key(fileKey)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            log.info("Image uploaded successfully with key: {}", fileKey);
            return fileKey;
        } catch (S3Exception e) {
            log.error("Error uploading image: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    /**
     * Get the public URL for an image
     * @param imageKey The unique key of the image
     * @return The full URL to access the image
     */
    public String getImageUrl(String imageKey) {
        if (imageKey == null || imageKey.isEmpty()) {
            return null;
        }
        return String.format("%s/%s/%s", minioProperties.getEndpoint(), minioProperties.getBucketName(), imageKey);
    }

    /**
     * Delete an image from storage
     * @param imageKey The unique key of the image to delete
     */
    public void deleteImage(String imageKey) {
        if (imageKey == null || imageKey.isEmpty()) {
            return;
        }

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(minioProperties.getBucketName())
                    .key(imageKey)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("Image deleted successfully: {}", imageKey);
        } catch (S3Exception e) {
            log.error("Error deleting image: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete image", e);
        }
    }

    /**
     * Check if an image exists in storage
     * @param imageKey The unique key of the image
     * @return true if the image exists, false otherwise
     */
    public boolean imageExists(String imageKey) {
        if (imageKey == null || imageKey.isEmpty()) {
            return false;
        }

        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(minioProperties.getBucketName())
                    .key(imageKey)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            log.error("Error checking if image exists: {}", e.getMessage(), e);
            return false;
        }
    }
}
