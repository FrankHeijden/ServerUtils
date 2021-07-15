package net.frankheijden.serverutils.common.providers;

import java.io.File;
import java.io.InputStream;
import net.frankheijden.serverutils.common.config.ServerUtilsConfig;

public interface ResourceProvider {

    InputStream getResource(String resource);

    ServerUtilsConfig load(InputStream is);

    ServerUtilsConfig load(File file);
}
