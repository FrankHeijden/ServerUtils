package net.frankheijden.serverutils.velocity.reflection;

import com.mojang.brigadier.CommandDispatcher;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.frankheijden.minecraftreflection.MinecraftReflection;
import dev.frankheijden.minecraftreflection.Reflection;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import net.frankheijden.serverutils.velocity.utils.ReflectionUtils;

public class RVelocityCommandManager {

    private static final MinecraftReflection reflection = MinecraftReflection
            .of("com.velocitypowered.proxy.command.VelocityCommandManager");

    private RVelocityCommandManager() {}

    public static CommandDispatcher<CommandSource> getDispatcher(CommandManager manager) {
        return reflection.get(manager, "dispatcher");
    }

    /**
     * Proxies the registrars.
     */
    @SuppressWarnings("rawtypes")
    public static void proxyRegistrars(
            ProxyServer proxy,
            ClassLoader loader,
            BiConsumer<PluginContainer, CommandMeta> registrationConsumer
    ) {
        List<Object> proxiedRegistrars = new ArrayList<>();

        Class<?> commandRegistrarClass;
        try {
            commandRegistrarClass = Class.forName("com.velocitypowered.proxy.command.registrar.CommandRegistrar");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return;
        }

        for (Object registrar : (List) reflection.get(proxy.getCommandManager(), "registrars")) {
            proxiedRegistrars.add(Proxy.newProxyInstance(
                    loader,
                    new Class[]{ commandRegistrarClass },
                    new CommandRegistrarInvocationHandler(
                            proxy,
                            registrar,
                            registrationConsumer
                    )
            ));
        }

        Field registrarsField = Reflection.getAccessibleField(reflection.getClazz(), "registrars");
        ReflectionUtils.doPrivilegedWithUnsafe(unsafe -> {
            long offset = unsafe.objectFieldOffset(registrarsField);
            unsafe.putObject(proxy.getCommandManager(), offset, proxiedRegistrars);
        });
    }

    public static final class CommandRegistrarInvocationHandler implements InvocationHandler {

        private final ProxyServer proxy;
        private final Object commandRegistrar;
        private final BiConsumer<PluginContainer, CommandMeta> registrationConsumer;

        /**
         * Constructs  a new {@link CommandRegistrarInvocationHandler}.
         */
        public CommandRegistrarInvocationHandler(
                ProxyServer proxy,
                Object commandRegistrar,
                BiConsumer<PluginContainer, CommandMeta> registrationConsumer
        ) {
            this.proxy = proxy;
            this.commandRegistrar = commandRegistrar;
            this.registrationConsumer = registrationConsumer;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object obj = method.invoke(commandRegistrar, args);
            if (method.getName().equals("register")) {
                handleRegisterMethod((CommandMeta) args[0], (Command) args[1]);
            }
            return obj;
        }

        private void handleRegisterMethod(CommandMeta commandMeta, Command command) {
            ClassLoader classLoader = command.getClass().getClassLoader();

            for (PluginContainer container : proxy.getPluginManager().getPlugins()) {
                if (container.getInstance().filter(i -> i.getClass().getClassLoader() == classLoader).isPresent()) {
                    registrationConsumer.accept(container, commandMeta);
                    break;
                }
            }
        }
    }
}
