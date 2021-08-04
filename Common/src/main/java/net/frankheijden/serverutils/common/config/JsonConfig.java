package net.frankheijden.serverutils.common.config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.frankheijden.serverutils.common.providers.ResourceProvider;

public class JsonConfig implements ServerUtilsConfig {

    protected static final Gson gson = new Gson();

    private final JsonObject config;
    private File file = null;

    public JsonConfig(File file) throws IOException {
        this.config = gson.fromJson(Files.newBufferedReader(file.toPath()), JsonObject.class);
        this.file = file;
    }

    public JsonConfig(JsonObject config) {
        this.config = config;
    }

    /**
     * Loads a resource from the jar file.
     */
    public static JsonConfig load(ResourceProvider provider, ServerUtilsPlugin.Platform platform, String resourceName) {
        // Create the platform JsonConfig by merging the platformConfig with the generalConfig
        JsonConfig generalConfig = new JsonConfig(JsonConfig.gson.fromJson(
                new InputStreamReader(provider.getRawResource(resourceName + ".json")),
                JsonObject.class
        ));

        String platformResource = platform.name().toLowerCase(Locale.ENGLISH) + '-' + resourceName;
        JsonConfig platformConfig = new JsonConfig(JsonConfig.gson.fromJson(
                new InputStreamReader(provider.getRawResource(platformResource + ".json")),
                JsonObject.class
        ));
        ServerUtilsConfig.addDefaults(platformConfig, generalConfig);

        return generalConfig;
    }

    public JsonObject getConfig() {
        return config;
    }

    /**
     * Retrieves the JsonElement at given path.
     */
    public JsonElement getJsonElement(String path) {
        JsonElement result = config;

        for (String memberName : path.split("\\.")) {
            if (result == null) return null;
            result = result.getAsJsonObject().get(memberName);
        }

        return result;
    }

    /**
     * Parses a json element as a java object. Supported constructs are:
     *  - Primitives (bool, number, string).
     *  - Simple string lists.
     */
    public static Object toObjectValue(JsonElement jsonElement) {
        if (jsonElement == null) return null;
        if (jsonElement.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = (JsonPrimitive) jsonElement;
            if (jsonPrimitive.isBoolean()) {
                return jsonPrimitive.getAsBoolean();
            } else if (jsonPrimitive.isNumber()) {
                double d = jsonPrimitive.getAsDouble();
                if (d == Math.rint(d)) {
                    return (int) d;
                } else {
                    return d;
                }
            } else if (jsonPrimitive.isString()) {
                return jsonPrimitive.getAsString();
            } else {
                throw new IllegalStateException("Not a JSON Primitive: " + jsonPrimitive);
            }
        } else if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = (JsonArray) jsonElement;
            List<String> stringList = new ArrayList<>(jsonArray.size());
            for (JsonElement childJsonElement : jsonArray) {
                stringList.add(toObjectValue(childJsonElement).toString());
            }
            return stringList;
        }
        return null;
    }

    @Override
    public Object get(String path) {
        JsonElement result = getJsonElement(path);

        if (result != null && result.isJsonObject()) {
            return new JsonConfig(result.getAsJsonObject());
        }
        return result;
    }

    @Override
    public List<String> getStringList(String path) {
        Object obj = get(path);
        if (!(obj instanceof JsonArray)) throw new IllegalStateException("Not a JSON Array: " + obj);

        JsonArray jsonArray = (JsonArray) obj;
        List<String> list = new ArrayList<>(jsonArray.size());
        for (JsonElement jsonElement : jsonArray) {
            list.add(jsonElement.getAsString());
        }
        return list;
    }

    @Override
    public Map<String, Object> getMap(String path) {
        return gson.fromJson(getJsonElement(path), new TypeToken<Map<String, Object>>() {}.getType());
    }

    @Override
    public void setUnsafe(String path, Object value) {
        int lastDotIndex = path.lastIndexOf('.');

        String memberName = path;
        JsonObject jsonObject = config.getAsJsonObject();
        if (lastDotIndex != -1) {
            memberName = path.substring(lastDotIndex + 1);

            for (String pathSection : path.substring(0, lastDotIndex).split("\\.")) {
                JsonElement childMember = jsonObject.get(pathSection);
                if (childMember == null) {
                    childMember = new JsonObject();
                }

                jsonObject.add(pathSection, childMember);
                jsonObject = childMember.getAsJsonObject();
            }
        }
        jsonObject.add(memberName, gson.toJsonTree(value));
    }

    @Override
    public void remove(String path) {
        int lastDotIndex = path.lastIndexOf('.');

        JsonObject object;
        if (lastDotIndex == -1) {
            object = config;
        } else {
            Object obj = get(path.substring(0, lastDotIndex));
            if (!(obj instanceof JsonConfig)) return;
            object = ((JsonConfig) obj).config;
        }

        object.remove(path.substring(lastDotIndex + 1));
    }

    @Override
    public String getString(String path) {
        JsonElement element = getJsonElement(path);
        if (element == null) return null;
        return element.getAsString();
    }

    @Override
    public boolean getBoolean(String path) {
        JsonElement element = getJsonElement(path);
        if (element == null) return false;
        return element.getAsBoolean();
    }

    @Override
    public int getInt(String path) {
        JsonElement element = getJsonElement(path);
        if (element == null) return -1;
        return element.getAsNumber().intValue();
    }

    @Override
    public Collection<? extends String> getKeys() {
        return config.keySet();
    }

    @Override
    public void save() throws IOException {
        Files.write(
                file.toPath(),
                gson.toJson(config).getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }
}
