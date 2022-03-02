package net.frankheijden.serverutils.bukkit.reflection;

import com.mojang.brigadier.tree.CommandNode;
import dev.frankheijden.minecraftreflection.ClassObject;
import dev.frankheijden.minecraftreflection.MinecraftReflection;

public class RCommandNode {

    private static final MinecraftReflection reflection;

    static {
        reflection = MinecraftReflection.of(CommandNode.class);
    }

    public RCommandNode() {}

    public static void removeCommand(Object node, String name) {
        reflection.invoke(node, "removeCommand", name);
    }

    public static String getName(Object node) {
        return reflection.invoke(node, "getName");
    }

    public static void addChild(Object parent, Object child) {
        reflection.invoke(parent, "addChild", ClassObject.of(CommandNode.class, child));
    }
}
