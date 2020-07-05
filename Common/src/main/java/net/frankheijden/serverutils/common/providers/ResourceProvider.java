package net.frankheijden.serverutils.common.providers;

import java.io.File;
import java.io.InputStream;

import net.frankheijden.serverutils.common.config.YamlConfig;

public interface ResourceProvider {

    InputStream getResource(String resource);

    YamlConfig load(InputStream is);

    YamlConfig load(File file);
}
