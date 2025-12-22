package ge.studio101.service.services;

import ge.studio101.service.dto.PhotoAdminDTO;
import ge.studio101.service.dto.PhotoDTO;
import ge.studio101.service.dto.PhotoNewDTO;
import ge.studio101.service.helpers.ImageRoutines;
import ge.studio101.service.mappers.PhotoMapper;
import ge.studio101.service.models.Color;
import ge.studio101.service.models.Photo;
import ge.studio101.service.repositories.ColorRepository;
import ge.studio101.service.repositories.PhotoRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PhotoService {
    private static final Logger log = LoggerFactory.getLogger(PhotoService.class);
    private final ColorRepository colorRepository;
    private final PhotoRepository photoRepository;
    private final PhotoMapper photoMapper;

    @Value("${image.cache.dir}")
    private String cacheDir;

    private Path cachePath;

    @PostConstruct
    public void init() {
        this.cachePath = Paths.get(cacheDir);
        try {
            Files.createDirectories(cachePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create cache directory: " + cacheDir, e);
        }
    }


    public List<PhotoDTO> findAll() {
        return photoMapper.toDTOList(photoRepository.findAll());
    }

    public PhotoDTO findById(Long id) {
        return photoRepository.findById(id)
                .map(photoMapper::toDTO)
                .orElse(null);
    }

    public List<PhotoDTO> saveAll(List<PhotoNewDTO> photoNewDTOList) {
        List<Photo> photosToSave = new ArrayList<>();

        for (PhotoNewDTO dto : photoNewDTOList) {
            Color color = colorRepository.findByItemIdAndName(dto.getItemId(), dto.getColorName())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Цвет '%s' для itemId=%d не найден",
                                    dto.getColorName(), dto.getItemId())));
            Photo photo = new Photo();
            photo.setImage(dto.getImage());
            photo.setColor(color);
            photosToSave.add(photo);
        }

        try {
            List<Photo> savedPhotos = photoRepository.saveAll(photosToSave);
            return savedPhotos.stream()
                    .map(photoMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<PhotoAdminDTO> findPhotosByItemAndColor(Long itemId, Long colorId) {
        if (itemId == null || colorId == null) {
            return List.of();
        }
        Color color = colorRepository.findById(colorId)
                .filter(found -> found.getItem() != null && itemId.equals(found.getItem().getId()))
                .orElseThrow(() -> new EntityNotFoundException("Цвет не найден для itemId=" + itemId));
        return photoRepository.findByColor_Id(color.getId()).stream()
                .map(photo -> {
                    PhotoAdminDTO dto = new PhotoAdminDTO();
                    dto.setId(photo.getId());
                    dto.setName("photo-" + photo.getId() + ".jpg");
                    try {
                        dto.setImage(Base64.getEncoder().encodeToString(
                                getPhotoBinary(photo.getId(), "400")
                        ));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public byte[] getPhotoBinary(Long id, String resolution) throws IOException {
        String resolutionId = resolution != null ? resolution : "1024";
        Path resolutionCachePath = cachePath.resolve(resolutionId);
        Path cachedImagePath = resolutionCachePath.resolve(id + ".jpg");


        if (Files.exists(cachedImagePath)) {
            try {
                log.info("hit: {}", cachedImagePath.getFileName());
                return Files.readAllBytes(cachedImagePath);
            } catch (IOException e) {
            }
        }


        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found"));
        byte[] imageBytes = photo.getImage();

        int width = 1024;
        int height = 1024;

        if (resolution != null && !resolution.isEmpty()) {
            try {
                int res = Integer.parseInt(resolution);
                width = res;
                height = res;
            } catch (NumberFormatException e) {
            }
        }

        byte[] processedImageBytes = resizeAndCompressImage(imageBytes, width, height, 0.8f);
        if (width >= 400) {
            processedImageBytes = addWatermark(processedImageBytes, "Studio101.ge");
        }

        try {
            Files.createDirectories(resolutionCachePath); // Ensure resolution directory exists
            Files.write(cachedImagePath, processedImageBytes);
        } catch (IOException e) {
        }

        return processedImageBytes;
    }

    private byte[] resizeAndCompressImage(byte[] imageBytes, int maxWidth, int maxHeight, float quality) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
        BufferedImage originalImage = ImageIO.read(bais);

        if (originalImage == null) {
            throw new IOException("Не удалось прочитать изображение");
        }

        originalImage = ImageRoutines.correctOrientation(originalImage, imageBytes);

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        boolean isWidthSmaller = originalWidth < originalHeight;

        if (maxWidth > 0 && maxHeight > 0) {
            if (isWidthSmaller) {
                double scaleFactor = (double) maxWidth / originalWidth;
                originalHeight = (int) (originalHeight * scaleFactor);
                originalWidth = maxWidth;
            } else {
                double scaleFactor = (double) maxHeight / originalHeight;
                originalWidth = (int) (originalWidth * scaleFactor);
                originalHeight = maxHeight;
            }
        }

        if (originalWidth <= maxWidth && originalHeight <= maxHeight) {
            return compressImage(originalImage, quality);
        }

        BufferedImage resizedImage = new BufferedImage(originalWidth, originalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, originalWidth, originalHeight, null);
        g2d.dispose();

        return compressImage(resizedImage, quality);
    }


    private byte[] compressImage(BufferedImage image, float quality) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();

        jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpgWriteParam.setCompressionQuality(quality);

        ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
        jpgWriter.setOutput(ios);
        jpgWriter.write(null, new javax.imageio.IIOImage(image, null, null), jpgWriteParam);

        ios.close();
        jpgWriter.dispose();
        return baos.toByteArray();
    }

    private byte[] addWatermark(byte[] imageBytes, String watermarkText) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
        BufferedImage originalImage = ImageIO.read(bais);
        bais.close();

        if (originalImage == null) {
            throw new IllegalArgumentException("Invalid image format");
        }

        BufferedImage watermarkedImage = new BufferedImage(
                originalImage.getWidth(),
                originalImage.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g2d = watermarkedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, null);

        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);
        g2d.setComposite(alphaComposite);
        g2d.setColor(java.awt.Color.decode("#FFFFFF"));
        g2d.setFont(new Font("Arial", Font.BOLD, Math.max(originalImage.getWidth() / 10, 100)));

        FontMetrics fontMetrics = g2d.getFontMetrics();
        int x = (originalImage.getWidth() - fontMetrics.stringWidth(watermarkText)) / 2;
        int y = originalImage.getHeight() / 2;

        g2d.drawString(watermarkText, x, y);
        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(watermarkedImage, "jpg", baos);
        baos.flush();
        byte[] watermarkedBytes = baos.toByteArray();
        baos.close();

        return watermarkedBytes;
    }


    public boolean delete(Long id) {
        Photo photo = photoRepository.findById(id)
                .orElse(null);
        if (photo == null) {
            return false;
        }

        try {
            photoRepository.delete(photo);
            // Delete cached files across all resolution subdirectories
            try (Stream<Path> resolutionDirs = Files.list(cachePath)) {
                resolutionDirs.filter(Files::isDirectory)
                        .forEach(resDir -> {
                            Path cachedImageFile = resDir.resolve(id + ".jpg");
                            if (Files.exists(cachedImageFile)) {
                                try {
                                    Files.delete(cachedImageFile);
                                } catch (IOException e) {
                                }
                            }
                        });
            } catch (IOException e) {
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении фото: " + e.getMessage(), e);
        }
        return true;
    }

}
