package at.ac.tuwien.sepm.groupphase.backend.util;

import at.ac.tuwien.sepm.groupphase.backend.exception.UnexpectedException;
import org.h2.util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.net.URL;

/**
 * This class has basic Image methods.
 * Like compress and decompress byte[].
 */
public class ImageUtility {

    public static byte[] randomJpeg(int width, int height, Category category) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            IOUtils.copyAndClose(
                new URL("https://placeimg.com/" + width + "/" + height + "/" + (category == null ? "all" : category.value)).openStream(),
                outputStream
            );
        } catch (Exception e) {
            throw new UnexpectedException("Error while loading random JPEG.", e);
        }

        return outputStream.toByteArray();
    }

    public static byte[] randomJpeg(int width, int height) {
        return randomJpeg(width, height, null);
    }

    enum Category {
        ANIMALS("animals"),
        ARCHITECTURE("architecture"),
        NATURE("nature"),
        PEOPLE("people"),
        TECH("tech");

        private final String value;

        Category(String value) {
            this.value = value;
        }
    }
}
