package com.ltcg.multicoremagic;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

public class MulticoreMagicModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            MulticoreMagicConfig config = MulticoreMagic.CONFIG;
            int maxThreads = Runtime.getRuntime().availableProcessors() * 2;

            ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Multicore Magic"))
                .setSavingRunnable(() -> {
                    ChunkBuilderThreadPool.applyThreadCount(config.chunkBuilderThreads);
                    MulticoreMagic.saveConfig();
                });

            ConfigCategory category = builder.getOrCreateCategory(Component.literal("General"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            category.addEntry(entryBuilder.startBooleanToggle(Component.literal("Enabled"), config.enabled)
                .setDefaultValue(true)
                .setTooltip(Component.literal(
                    "Gives chunk mesh building its own dedicated thread pool instead of sharing " +
                    "vanilla's background pool with worldgen/IO. Takes effect on next world join or F3+A."))
                .setSaveConsumer(value -> config.enabled = value)
                .build());

            category.addEntry(entryBuilder.startIntSlider(Component.literal("Chunk builder threads"),
                    config.chunkBuilderThreads, 1, maxThreads)
                .setDefaultValue(MulticoreMagicConfig.defaultThreadCount())
                .setTooltip(Component.literal(
                    "Default matches vanilla's own formula (CPU cores - 1). Raise it on many-core " +
                    "CPUs for faster chunk loading; lower it if you see stutter from thread contention."))
                .setSaveConsumer(value -> config.chunkBuilderThreads = value)
                .build());

            return builder.build();
        };
    }
}
