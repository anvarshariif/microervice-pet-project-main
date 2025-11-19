package uz.uzinfocom.product.minio;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioServiceImpl implements MinioService {

  private final MinioClient minioClient;
  private final MinioProperties minioProperties;

  @Override
  public void createBucket(String bucketName) {
    try {
      boolean found = minioClient.bucketExists(
          BucketExistsArgs.builder()
              .bucket(bucketName)
              .build()
      );

      if (!found) {
        minioClient.makeBucket(
            MakeBucketArgs.builder()
                .bucket(bucketName)
                .build()
        );
        log.info("Bucket created: {}", bucketName);
      } else {
        log.info("Bucket already exists: {}", bucketName);
      }
    } catch (Exception e) {
      log.error("Error creating bucket: {}", bucketName, e);
      throw new RuntimeException("Error creating bucket", e);
    }
  }

  @Override
  public boolean bucketExists(String bucketName) {
    try {
      return minioClient.bucketExists(
          BucketExistsArgs.builder()
              .bucket(bucketName)
              .build()
      );
    } catch (Exception e) {
      log.error("Error checking bucket existence: {}", bucketName, e);
      return false;
    }
  }

  @Override
  public String uploadFile(MultipartFile file, String objectName) {
    try {
      // Bucket mavjudligini tekshirish
      String bucketName = minioProperties.getBucketName();
      if (!bucketExists(bucketName)) {
        createBucket(bucketName);
      }

      // Faylni yuklash
      minioClient.putObject(
          PutObjectArgs.builder()
              .bucket(bucketName)
              .object(objectName)
              .stream(file.getInputStream(), file.getSize(), -1)
              .contentType(file.getContentType())
              .build()
      );

      log.info("File uploaded successfully: {}", objectName);
      return objectName;

    } catch (Exception e) {
      log.error("Error uploading file: {}", objectName, e);
      throw new RuntimeException("Error uploading file to MinIO", e);
    }
  }

  @Override
  public InputStream getFile(String objectName) {
    try {
      return minioClient.getObject(
          GetObjectArgs.builder()
              .bucket(minioProperties.getBucketName())
              .object(objectName)
              .build()
      );
    } catch (Exception e) {
      log.error("Error getting file: {}", objectName, e);
      throw new RuntimeException("Error getting file from MinIO", e);
    }
  }

  @Override
  public void deleteFile(String objectName) {
    try {
      minioClient.removeObject(
          RemoveObjectArgs.builder()
              .bucket(minioProperties.getBucketName())
              .object(objectName)
              .build()
      );
      log.info("File deleted successfully: {}", objectName);
    } catch (Exception e) {
      log.error("Error deleting file: {}", objectName, e);
      throw new RuntimeException("Error deleting file from MinIO", e);
    }
  }

  @Override
  public String getFileUrl(String objectName) {
    try {
      // Presigned URL yaratish (7 kunlik)
      return minioClient.getPresignedObjectUrl(
          GetPresignedObjectUrlArgs.builder()
              .bucket(minioProperties.getBucketName())
              .object(objectName)
              .method(Method.GET)
              .expiry(7, TimeUnit.DAYS)
              .build()
      );
    } catch (Exception e) {
      log.error("Error generating file URL: {}", objectName, e);
      throw new RuntimeException("Error generating file URL", e);
    }
  }
}