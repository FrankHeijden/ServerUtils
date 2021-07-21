package net.frankheijden.serverutils.velocity.reflection;

import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import dev.frankheijden.minecraftreflection.MinecraftReflection;

public class RVelocityConsole {

    private static final MinecraftReflection reflection = MinecraftReflection
            .of("com.velocitypowered.proxy.console.VelocityConsole");

    private RVelocityConsole() {}

    public static void setPermissionFunction(ConsoleCommandSource velocityConsole, PermissionFunction function) {
        reflection.set(velocityConsole, "permissionFunction", function);
    }
}
