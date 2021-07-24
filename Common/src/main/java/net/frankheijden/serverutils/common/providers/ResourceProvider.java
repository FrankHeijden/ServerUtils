package net.frankheijden.serverutils.common.providers;

import java.io.File;
import java.io.InputStream;
import net.frankheijden.serverutils.common.config.ServerUtilsConfig;

public interface ResourceProvider {

    default InputStream getResource(String resource) {
        return getRawResource(resource + getResourceExtension());
    }

    InputStream getRawResource(String resource);

    ServerUtilsConfig load(InputStream is);

    ServerUtilsConfig load(File file);

    String getResourceExtension();
}
