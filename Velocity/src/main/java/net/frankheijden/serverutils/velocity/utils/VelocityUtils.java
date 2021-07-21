package net.frankheijden.serverutils.velocity.utils;

import com.velocitypowered.api.command.CommandSource;
import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.velocity.entities.VelocityCommandSender;

public class VelocityUtils {

    public static ServerCommandSender wrap(CommandSource source) {
        return new VelocityCommandSender(source);
    }
}
