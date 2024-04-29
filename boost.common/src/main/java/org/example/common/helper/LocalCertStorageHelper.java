package org.example.common.helper;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.example.common.BaseResult;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LocalCertStorageHelper {
    private static final String LOCAL_CERT_STORAGE_PATH = "/home/admin/application/boost.server/target/cert/";

    public BaseResult<Boolean> putCert(String certName, String certContent) {
        String filePath = LOCAL_CERT_STORAGE_PATH + certName;

        try {
            File file = new File(filePath);
            try (FileWriter writer = new FileWriter(file, false)) {
                writer.write(certContent);
            } catch (Exception e) {
                return BaseResult.fail("Error when writing the object: " + e.getMessage());
            }
            file.setReadable(true, false);
            file.setWritable(true, false);
            file.setExecutable(true, false);
        } catch (Exception e) {
            return BaseResult.fail("Error when ensuring the directory exists: " + e.getMessage());
        }
        return BaseResult.success(true);
    }

    public BaseResult<Boolean> deleteCert(String certName) {
        String filePath = LOCAL_CERT_STORAGE_PATH + certName;
        File file = new File(filePath);
        if (!file.exists()) {
            return BaseResult.fail("Object does not exist");
        } else {
            try {
                Files.delete(Paths.get(filePath));
            } catch (Exception e) {
                return BaseResult.fail("Error when deleting the object: " + e.getMessage());
            }
            return BaseResult.success(true);
        }
    }

    public boolean doesFileExist(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        Path filePath = Paths.get(path);
        return Files.exists(filePath) && Files.isRegularFile(filePath);
    }

}
