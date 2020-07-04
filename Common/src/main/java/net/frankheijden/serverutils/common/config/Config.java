package net.frankheijden.serverutils.common.config;

public class Config extends YamlResource {

    private static Config instance;

    public Config(String fileName, String resource) {
        super(fileName, resource);
        instance = this;
    }

    public static Config getInstance() {
        return instance;
    }
}
