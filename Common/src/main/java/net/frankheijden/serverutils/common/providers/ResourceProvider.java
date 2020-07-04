package net.frankheijden.serverutils.common.providers;

import net.frankheijden.serverutils.common.config.YamlConfig;

import java.io.File;
import java.io.InputStream;

public interface ResourceProvider {

    InputStream getResource(String resource);

    YamlConfig load(InputStream is);

    YamlConfig load(File file);
}
