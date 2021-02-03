package net.frankheijden.serverutils.common.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.codec.digest.DigestUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileUtils {

    private FileUtils() {}

    /**
     * Parses an InputStream into a JsonElement.
     */
    public static JsonElement parseJson(InputStream in) throws IOException {
        if (in == null) return null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            return new JsonParser().parse(reader);
        }
    }

    /**
     * Saves an InputStream to a file.
     */
    public static boolean saveResource(InputStream in, File target) throws IOException {
        if (target.exists()) return false;
        Files.copy(in, target.toPath());
        return true;
    }

    /**
     * Get the Hash of a file.
     *
     * @param file The file
     * @return The file's hash
     */
    public static String getHash(File file) {
        try {
            return DigestUtils.md5Hex(new FileInputStream(file));
        } catch (IOException e) {
            // Shouldn't happen
            return null;
        }
    }
}
