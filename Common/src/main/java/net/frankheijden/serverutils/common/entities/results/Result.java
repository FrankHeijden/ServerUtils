package net.frankheijden.serverutils.common.entities.results;

import net.frankheijden.serverutils.common.config.MessageKey;

/**
 * An enum containing possible results.
 */
public enum Result implements AbstractResult {
    NOT_EXISTS(MessageKey.GENERIC_NOT_EXISTS),
    NOT_ENABLED(MessageKey.GENERIC_NOT_ENABLED),
    ALREADY_LOADED(MessageKey.GENERIC_ALREADY_LOADED),
    ALREADY_ENABLED(MessageKey.GENERIC_ALREADY_ENABLED),
    ALREADY_DISABLED(MessageKey.GENERIC_ALREADY_DISABLED),
    FILE_DELETED(MessageKey.GENERIC_FILE_DELETED),
    INVALID_DESCRIPTION(MessageKey.GENERIC_INVALID_DESCRIPTION),
    INVALID_PLUGIN(MessageKey.GENERIC_INVALID_PLUGIN),
    UNKNOWN_DEPENDENCY(MessageKey.GENERIC_UNKNOWN_DEPENDENCY),
    ERROR(MessageKey.GENERIC_ERROR),
    SUCCESS(null),
    ;

    private final MessageKey key;

    Result(MessageKey key) {
        this.key = key;
    }

    public MessageKey getKey() {
        return key;
    }
}
