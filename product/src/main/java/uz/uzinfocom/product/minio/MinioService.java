package uz.uzinfocom.product.minio;

import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

public interface MinioService {

  void createBucket(String bucketName);

  boolean bucketExists(String bucketName);

  void uploadFile(MultipartFile file, String objectName);

  InputStream getFile(String objectName);

  void deleteFile(String objectName);

  String getFileUrl(String objectName);

}