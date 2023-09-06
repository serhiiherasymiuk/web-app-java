package org.example.storage;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
public class FileSystemStorageService implements StorageService {
    private final Path rootLocation;

    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void init() {
        try {
            if(!Files.exists(rootLocation))
                Files.createDirectories(rootLocation);
        }catch(IOException e) {
            throw new StorageException("Creating folder error", e);
        }
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable())
                return resource;
            else
                throw new StorageException("Reading file error: "+filename);
        } catch(MalformedURLException e) {
            throw new StorageException("File is not found: "+ filename, e);
        }
    }

    @Override
    public String save(String base64) {
        try {
            if(base64.isEmpty()) {
                throw new StorageException("Empty base64");
            }
            UUID uuid = UUID.randomUUID();
            String [] charArray = base64.split(",");
            String extension;
            System.out.println("-----------------"+ charArray[0]);
            switch (charArray[0]) {
                case "data:image/png;base64":
                    extension = "png";
                    break;
                default:
                    extension = "jpg";
                    break;
            }
            String randomFileName = uuid.toString()+"."+extension;
            java.util.Base64.Decoder decoder = Base64.getDecoder();
            byte[] bytes = new byte[0];
            bytes = decoder.decode(charArray[1]);
            int [] imageSize = {32, 150, 300, 600, 1200};
            try (var byteStream = new ByteArrayInputStream(bytes)) {
                var image = ImageIO.read(byteStream);
                for(int size : imageSize){
                    String directory= rootLocation.toString() +"/"+size+"_"+randomFileName;

                    BufferedImage newImg = ImageUtils.resizeImage(image,
                            extension=="jpg"? ImageUtils.IMAGE_JPEG : ImageUtils.IMAGE_PNG, size,size);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    ImageIO.write(newImg, extension, byteArrayOutputStream);
                    byte [] newBytes = byteArrayOutputStream.toByteArray();
                    FileOutputStream out = new FileOutputStream(directory);
                    out.write(newBytes);
                    out.close();
                }
            }

            return randomFileName;
        } catch (IOException e) {
            throw new StorageException("\n" + "Base64 conversion and save problem", e);
        }
    }

    @Override
    public  void  removeFile(String removeFile){
        int [] imageSize = {32, 150, 300, 600, 1200};
        for (int size : imageSize) {
            Path filePath = load(size+"_"+removeFile);
            File file = new File(filePath.toString());
            if (file.delete()) {
                System.out.println(removeFile + "File deleted");
            } else System.out.println(removeFile + "File is not found");
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public String saveMultipartFile(MultipartFile file) {
        try {
            String extension="jpg";
            UUID uuid = UUID.randomUUID();
            String randomFileName = uuid.toString()+"."+extension;
            byte[] bytes = new byte[0];
            bytes = file.getBytes();
            int [] imageSize = {32, 150, 300, 600, 1200};
            try (var byteStream = new ByteArrayInputStream(bytes)) {
                var image = ImageIO.read(byteStream);
                for(int size : imageSize){
                    String directory= rootLocation.toString() +"/"+size+"_"+randomFileName;

                    BufferedImage newImg = ImageUtils.resizeImage(image,
                            extension=="jpg"? ImageUtils.IMAGE_JPEG : ImageUtils.IMAGE_PNG, size,size);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    ImageIO.write(newImg, extension, byteArrayOutputStream);
                    byte [] newBytes = byteArrayOutputStream.toByteArray();
                    FileOutputStream out = new FileOutputStream(directory);
                    out.write(newBytes);
                    out.close();
                }
            }

            return randomFileName;
        } catch (IOException e) {
            throw new StorageException("Base64 conversion and save problem", e);
        }
    }

    @Override
    public String saveThumbnailator(MultipartFile file, FileSaveFormat format) {
        try {
            String extension = format.name().toLowerCase();
            UUID uuid = UUID.randomUUID();
            String randomFileName = uuid.toString() + "." + extension;
            int[] imageSize = {32, 150, 300, 600, 1200};
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));

            for (int size : imageSize) {
                String directory = rootLocation.toString() + "/" + size + "_" + randomFileName;
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                Thumbnails.of(bufferedImage)
                        .size(size, size)
                        .outputFormat(extension)
                        .toFile(directory);
            }
            return randomFileName;
        } catch (IOException e) {
            throw new StorageException("Base64 conversion and save problem", e);
        }
    }
}

