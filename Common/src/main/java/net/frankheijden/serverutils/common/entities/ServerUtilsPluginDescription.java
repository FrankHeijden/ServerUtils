package net.frankheijden.serverutils.common.entities;

import java.io.File;
import java.util.Set;

public interface ServerUtilsPluginDescription {

    String getId();

    String getName();

    String getVersion();

    String getAuthor();

    File getFile();

    Set<String> getDependencies();
}
