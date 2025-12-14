package ge.studio101.service.services;

import ge.studio101.service.dto.PhotoDTO;
import ge.studio101.service.dto.PhotoNewDTO;
import ge.studio101.service.helpers.ImageRoutines;
import ge.studio101.service.mappers.PhotoMapper;
import ge.studio101.service.models.Color;
import ge.studio101.service.models.Photo;
import ge.studio101.service.repositories.ColorRepository;
import ge.studio101.service.repositories.PhotoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhotoService {
    private final ColorRepository colorRepository;
    private final PhotoRepository photoRepository;
    private final PhotoMapper photoMapper;

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
            // Ищем сущность Color по комбинации itemId + colorName
            Color color = colorRepository.findByItemIdAndName(dto.getItemId(), dto.getColorName())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Цвет '%s' для itemId=%d не найден",
                                    dto.getColorName(), dto.getItemId())));

            // Создаём новую сущность Photo
            Photo photo = new Photo();
            photo.setImage(dto.getImage());
            photo.setColor(color);

            photosToSave.add(photo);
        }

        try {
            // Сохраняем все фото одним списком
            List<Photo> savedPhotos = photoRepository.saveAll(photosToSave);
            // Возвращаем список DTO
            return savedPhotos.stream()
                    .map(photoMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getPhotoBinary(Long id, String resolution) throws IOException {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found"));
        byte[] imageBytes = photo.getImage();

        int width = 1024; // Значение по умолчанию
        int height = 1024; // Значение по умолчанию

        if (resolution != null && !resolution.isEmpty()) {
            try {
                // Пытаемся преобразовать resolution в число
                int res = Integer.parseInt(resolution);
                width = res;
                height = res;
            } catch (NumberFormatException e) {
                // Если resolution не число, используем значения по умолчанию
            }
        }

        byte[] resizedImageBytes = resizeAndCompressImage(imageBytes, width, height, 0.8f);
        if (width<400) {
            return resizedImageBytes;
        }
        return addWatermark(resizedImageBytes, "Studio101.ge");
    }

    private byte[] resizeAndCompressImage(byte[] imageBytes, int maxWidth, int maxHeight, float quality) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
        BufferedImage originalImage = ImageIO.read(bais);

        if (originalImage == null) {
            throw new IOException("Не удалось прочитать изображение");
        }

        // Исправление ориентации перед изменением размера
        originalImage = ImageRoutines.correctOrientation(originalImage, imageBytes);

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // Определяем, какая сторона меньше
        boolean isWidthSmaller = originalWidth < originalHeight;

        // Если resolution задан, используем его для меньшей стороны
        if (maxWidth > 0 && maxHeight > 0) {
            if (isWidthSmaller) {
                // Если ширина меньше, масштабируем по ширине
                double scaleFactor = (double) maxWidth / originalWidth;
                originalHeight = (int) (originalHeight * scaleFactor);
                originalWidth = maxWidth;
            } else {
                // Если высота меньше, масштабируем по высоте
                double scaleFactor = (double) maxHeight / originalHeight;
                originalWidth = (int) (originalWidth * scaleFactor);
                originalHeight = maxHeight;
            }
        }

        // Если изображение уже меньше или равно заданному разрешению, просто сжимаем его
        if (originalWidth <= maxWidth && originalHeight <= maxHeight) {
            return compressImage(originalImage, quality);
        }

        // Создаём новое изображение с изменённым размером
        BufferedImage resizedImage = new BufferedImage(originalWidth, originalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, originalWidth, originalHeight, null);
        g2d.dispose();

        return compressImage(resizedImage, quality);
    }


    // Метод для сжатия изображения (JPEG с уменьшенным качеством)
    private byte[] compressImage(BufferedImage image, float quality) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();

        jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpgWriteParam.setCompressionQuality(quality); // 0.0 = худшее качество, 1.0 = лучшее качество

        ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
        jpgWriter.setOutput(ios);
        jpgWriter.write(null, new javax.imageio.IIOImage(image, null, null), jpgWriteParam);

        ios.close();
        jpgWriter.dispose();
        return baos.toByteArray();
    }

    private byte[] addWatermark(byte[] imageBytes, String watermarkText) throws IOException {
        // Конвертируем байты в BufferedImage
        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
        BufferedImage originalImage = ImageIO.read(bais);
        bais.close();

        if (originalImage == null) {
            throw new IllegalArgumentException("Invalid image format");
        }

        // Создаём новое изображение с тем же размером и типом
        BufferedImage watermarkedImage = new BufferedImage(
                originalImage.getWidth(),
                originalImage.getHeight(),
                BufferedImage.TYPE_INT_RGB // Используем TYPE_INT_RGB для JPEG
        );

        // Рисуем оригинальное изображение на новом изображении
        Graphics2D g2d = watermarkedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, null);

        // Настройки водяного знака
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f); // 20% прозрачности
        g2d.setComposite(alphaComposite);
        g2d.setColor(java.awt.Color.decode("#FFFFFF")); // Белый цвет
        g2d.setFont(new Font("Arial", Font.BOLD, Math.max(originalImage.getWidth() / 10, 100))); // Настраиваем размер шрифта

        // Вычисляем позицию (по центру)
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int x = (originalImage.getWidth() - fontMetrics.stringWidth(watermarkText)) / 2;
        int y = originalImage.getHeight() / 2;

        // Рисуем текст водяного знака
        g2d.drawString(watermarkText, x, y);
        g2d.dispose();

        // Конвертируем BufferedImage обратно в байты (в формате JPEG)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(watermarkedImage, "jpg", baos); // Используем "jpg" вместо "png"
        baos.flush();
        byte[] watermarkedBytes = baos.toByteArray();
        baos.close();

        return watermarkedBytes;
    }


    public void delete(Long id) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Фото с ID " + id + " не найдено"));

        try {
            photoRepository.delete(photo);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении фото: " + e.getMessage(), e);
        }
    }

}
