package net.frankheijden.serverutils.velocity.reflection;

import com.google.common.collect.Multimap;
import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.Scheduler;
import dev.frankheijden.minecraftreflection.MinecraftReflection;

public class RVelocityScheduler {

    private static final MinecraftReflection reflection = MinecraftReflection
            .of("com.velocitypowered.proxy.scheduler.VelocityScheduler");

    private RVelocityScheduler() {}

    public static Multimap<Object, ScheduledTask> getTasksByPlugin(Scheduler scheduler) {
        return reflection.get(scheduler, "tasksByPlugin");
    }
}
