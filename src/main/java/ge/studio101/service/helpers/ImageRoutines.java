package ge.studio101.service.helpers;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ImageRoutines {
    public static BufferedImage correctOrientation(BufferedImage image, byte[] imageBytes) throws IOException {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(imageBytes));
            Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (directory != null && directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
                int orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
                return rotateImage(image, orientation);
            }
        } catch (Exception e) {
            System.err.println("Ошибка при чтении EXIF: " + e.getMessage());
        }
        return image;
    }

    // Метод для поворота изображения
    private static BufferedImage rotateImage(BufferedImage image, int orientation) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage rotatedImage;

        AffineTransform transform = new AffineTransform();

        switch (orientation) {
            case 6: // 90 градусов по часовой стрелке
                rotatedImage = new BufferedImage(height, width, image.getType());
                transform.translate(height, 0);
                transform.rotate(Math.toRadians(90));
                break;
            case 3: // 180 градусов
                rotatedImage = new BufferedImage(width, height, image.getType());
                transform.translate(width, height);
                transform.rotate(Math.toRadians(180));
                break;
            case 8: // 270 градусов (90 против часовой)
                rotatedImage = new BufferedImage(height, width, image.getType());
                transform.translate(0, width);
                transform.rotate(Math.toRadians(-90));
                break;
            default:
                return image; // Нет необходимости в повороте
        }

        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        op.filter(image, rotatedImage);
        return rotatedImage;
    }
}
