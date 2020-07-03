package net.frankheijden.serverutils.common.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

public class FileUtils {

    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:77.0)"
            + " Gecko/20100101"
            + " Firefox/77.0";

    /**
     * Downloads file from a URL to a file location.
     * @param urlString The url to download.
     * @param target The location to save the output to.
     * @throws IOException If an I/O exception occurs.
     */
    public static void download(String urlString, File target) throws IOException {
        try (InputStream is = stream(urlString)) {
            if (is == null) return;
            try (ReadableByteChannel rbc = Channels.newChannel(is);
                 FileOutputStream fos = new FileOutputStream(target)) {
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            }
        }
    }

    /**
     * Opens a stream to an url.
     * @param url The url to stream.
     * @return An InputStream of the url.
     * @throws IOException If an I/O exception occurs.
     */
    public static InputStream stream(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setConnectTimeout(10000);
        int res = conn.getResponseCode();
        return (res >= 200 && res <= 299) ? conn.getInputStream() : conn.getErrorStream();
    }

    /**
     * Reads all bytes from a reader and appends them to a string result.
     * @param reader The reader to read from.
     * @return All byte characters.
     * @throws IOException If an I/O exception occurs.
     */
    public static String readAll(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = reader.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    /**
     * Reads an url and converts it into a JsonElement.
     * @param url The url to read from.
     * @return The JsonElement.
     * @throws IOException If an I/O exception occurs.
     */
    public static JsonElement readJsonFromUrl(String url) throws IOException {
        try (InputStream is = stream(url)) {
            if (is == null) return null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(reader);
            return new JsonParser().parse(jsonText);
        }
    }
}
