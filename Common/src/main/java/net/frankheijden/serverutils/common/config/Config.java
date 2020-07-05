package net.frankheijden.serverutils.common.config;

/**
 * The general common config class.
 */
public class Config extends YamlResource {

    private static Config instance;

    /**
     * Constructs a new Config with the config file name and the resource name from the jar.
     * @param fileName The file name in the data folder.
     * @param resource The resource name in the jar file.
     */
    public Config(String fileName, String resource) {
        super(fileName, resource);
        instance = this;
    }

    /**
     * Retrieves the current instance of the Config.
     * @return The current instance.
     */
    public static Config getInstance() {
        return instance;
    }
}
