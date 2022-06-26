package at.ac.tuwien.sepm.groupphase.backend.util;

import at.ac.tuwien.sepm.groupphase.backend.exception.UnexpectedException;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class CompressionUtility {

    public static byte[] compress(byte[] data) {
        if (data == null) {
            throw new UnexpectedException("Empty data cannot be compressed.");
        }

        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4 * 1024];
        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }
        try {
            outputStream.close();
        } catch (Exception e) {
            throw new UnexpectedException("Error occurred while compressing data.", e);
        }
        return outputStream.toByteArray();
    }

    public static byte[] decompress(byte[] data) {
        if (data == null) {
            throw new UnexpectedException("Empty data cannot be decompressed.");
        }

        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4 * 1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
        } catch (Exception e) {
            throw new UnexpectedException("Error occurred while decompressing data.", e);
        }
        return outputStream.toByteArray();
    }

}
