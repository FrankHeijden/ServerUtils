package net.frankheijden.serverutils.velocity.entities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import net.frankheijden.serverutils.common.config.ServerUtilsConfig;
import net.frankheijden.serverutils.common.providers.ResourceProvider;
import net.frankheijden.serverutils.velocity.ServerUtils;

public class VelocityResourceProvider implements ResourceProvider {

    private final ServerUtils plugin;

    public VelocityResourceProvider(ServerUtils plugin) {
        this.plugin = plugin;
    }

    @Override
    public InputStream getRawResource(String resource) {
        return plugin.getClass().getClassLoader().getResourceAsStream(resource);
    }

    @Override
    public ServerUtilsConfig load(InputStream is) {
        try {
            Path tmpFile = Files.createTempFile(null, null);
            Files.copy(is, tmpFile, StandardCopyOption.REPLACE_EXISTING);

            VelocityTomlConfig config = new VelocityTomlConfig(tmpFile.toFile());
            Files.delete(tmpFile);

            return config;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public ServerUtilsConfig load(File file) {
        return new VelocityTomlConfig(file);
    }

    @Override
    public String getResourceExtension() {
        return ".toml";
    }
}
