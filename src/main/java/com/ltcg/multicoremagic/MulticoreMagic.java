package com.ltcg.multicoremagic;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class MulticoreMagic implements ClientModInitializer {
    public static MulticoreMagicConfig CONFIG;
    private static java.nio.file.Path configPath;

    @Override
    public void onInitializeClient() {
        configPath = FabricLoader.getInstance().getConfigDir().resolve("multicoremagic.json");
        CONFIG = MulticoreMagicConfig.load(configPath);
        ChunkBuilderThreadPool.applyThreadCount(CONFIG.chunkBuilderThreads);

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(literal("multicoremagic")
            .then(literal("on").executes(ctx -> setEnabled(ctx, true)))
            .then(literal("off").executes(ctx -> setEnabled(ctx, false)))
            .then(literal("threads")
                .then(argument("count", IntegerArgumentType.integer(1))
                    .executes(ctx -> setThreads(ctx, IntegerArgumentType.getInteger(ctx, "count")))))
            .then(literal("status").executes(MulticoreMagic::status))
            .executes(MulticoreMagic::status)));
    }

    public static void saveConfig() {
        CONFIG.save(configPath);
    }

    private static int setEnabled(CommandContext<FabricClientCommandSource> ctx, boolean enabled) {
        CONFIG.enabled = enabled;
        saveConfig();
        feedback(ctx, (enabled ? "Enabled" : "Disabled")
            + " the dedicated chunk builder pool. Takes effect next time chunks reload (rejoin the world or F3+A).");
        return 1;
    }

    private static int setThreads(CommandContext<FabricClientCommandSource> ctx, int count) {
        CONFIG.chunkBuilderThreads = count;
        ChunkBuilderThreadPool.applyThreadCount(count);
        saveConfig();
        feedback(ctx, "Chunk builder now using " + count + " threads (applies immediately).");
        return 1;
    }

    private static int status(CommandContext<FabricClientCommandSource> ctx) {
        feedback(ctx, String.format(
            "Multicore Magic: %s | threads=%d active=%d queued=%d",
            CONFIG.enabled ? "enabled" : "disabled (vanilla shared pool)",
            ChunkBuilderThreadPool.currentThreadCount(),
            ChunkBuilderThreadPool.activeThreads(),
            ChunkBuilderThreadPool.queuedTasks()
        ));
        return 1;
    }

    private static void feedback(CommandContext<FabricClientCommandSource> ctx, String message) {
        ctx.getSource().sendFeedback(Component.literal(message));
    }
}
