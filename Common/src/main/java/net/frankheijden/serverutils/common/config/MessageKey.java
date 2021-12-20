package net.frankheijden.serverutils.common.config;

import java.util.Locale;

public enum MessageKey implements PlaceholderConfigKey {

    RELOAD("reload", false),
    LOADPLUGIN("loadplugin"),
    UNLOADPLUGIN("unloadplugin"),
    SERVERUTILS_UPDATER("serverutils-updater", false),
    RELOADPLUGIN_SUCCESS("reloadplugin.success"),
    RELOADPLUGIN_SERVERUTILS("reloadplugin.serverutils"),
    GENERIC_PREFIX("generic.prefix", false),
    GENERIC_ERROR("generic.error", false),
    GENERIC_NOT_EXISTS("generic.not-exists"),
    GENERIC_NOT_ENABLED("generic.not-enabled"),
    GENERIC_ALREADY_LOADED("generic.already-loaded"),
    GENERIC_ALREADY_ENABLED("generic.already-enabled"),
    GENERIC_ALREADY_DISABLED("generic.already-disabled"),
    GENERIC_INVALID_PLUGIN("generic.invalid-plugin"),
    GENERIC_INVALID_DESCRIPTION("generic.invalid-description"),
    GENERIC_UNKNOWN_DEPENDENCY("generic.unknown-dependency"),
    GENERIC_FILE_DELETED("generic.file-deleted"),
    GENERIC_PROTECTED_PLUGIN("generic.protected-plugin"),
    DEPENDING_PLUGINS_PREFIX("depending-plugins.prefix"),
    DEPENDING_PLUGINS_FORMAT("depending-plugins.format"),
    DEPENDING_PLUGINS_SEPARATOR("depending-plugins.separator", false),
    DEPENDING_PLUGINS_LAST_SEPARATOR("depending-plugins.last-separator", false),
    DEPENDING_PLUGINS_OVERRIDE("depending-plugins.override"),
    WATCHPLUGIN_START("watchplugin.start"),
    WATCHPLUGIN_CHANGE("watchplugin.change", false),
    WATCHPLUGIN_STOPPED("watchplugin.stopped"),
    WATCHPLUGIN_FILE_DELETED("watchplugin.file-deleted"),
    WATCHPLUGIN_DELETED_FILE_IS_CREATED("watchplugin.deleted-file-is-created"),
    WATCHPLUGIN_ALREADY_WATCHING("watchplugin.already-watching"),
    WATCHPLUGIN_NOT_WATCHING("watchplugin.not-watching"),
    UPDATE_AVAILABLE("update.available"),
    UPDATE_DOWNLOADING("update.downloading"),
    UPDATE_DOWNLOAD_FAILED("update.download-failed"),
    UPDATE_DOWNLOAD_SUCCESS("update.download-success", false),
    HELP_HEADER("help.header", false),
    HELP_FORMAT("help.format"),
    HELP_FOOTER("help.footer", false),
    PLUGINS_HEADER("plugins.header", false),
    PLUGINS_PREFIX("plugins.prefix"),
    PLUGINS_FORMAT("plugins.format"),
    PLUGINS_FORMAT_DISABLED("plugins.format-disabled"),
    PLUGINS_SEPARATOR("plugins.separator", false),
    PLUGINS_LAST_SEPARATOR("plugins.last-separator", false),
    PLUGINS_VERSION("plugins.version"),
    PLUGINS_FOOTER("plugins.footer", false),
    PLUGININFO_HEADER("plugininfo.header", false),
    PLUGININFO_FORMAT("plugininfo.format"),
    PLUGININFO_LIST_FORMAT("plugininfo.list-format"),
    PLUGININFO_LIST_SEPARATOR("plugininfo.list-separator", false),
    PLUGININFO_LIST_LAST_SEPARATOR("plugininfo.list-last-separator", false),
    PLUGININFO_FOOTER("plugininfo.footer", false),
    COMMANDINFO_HEADER("commandinfo.header", false),
    COMMANDINFO_FORMAT("commandinfo.format"),
    COMMANDINFO_LIST_FORMAT("commandinfo.list-format"),
    COMMANDINFO_LIST_SEPARATOR("commandinfo.list-separator", false),
    COMMANDINFO_LIST_LAST_SEPARATOR("commandinfo.list-last-separator", false),
    COMMANDINFO_FOOTER("commandinfo.footer", false),
    COMMANDINFO_NOT_EXISTS("commandinfo.not-exists", false),
    ;

    private final String path;
    private final boolean hasPlaceholders;

    MessageKey(String path) {
        this(path, true);
    }

    MessageKey(String path, boolean hasPlaceholders) {
        this.path = path;
        this.hasPlaceholders = hasPlaceholders;
    }

    public static MessageKey fromPath(String path) {
        return MessageKey.valueOf(path.replaceAll("\\.|-", "_").toUpperCase(Locale.ENGLISH));
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public boolean hasPlaceholders() {
        return hasPlaceholders;
    }
}
