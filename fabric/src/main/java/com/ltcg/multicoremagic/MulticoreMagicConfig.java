package com.ltcg.multicoremagic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MulticoreMagicConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /** Vanilla's own formula for its shared background pool: cores - 1. Used as our default too. */
    public static int defaultThreadCount() {
        return Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
    }

    public boolean enabled = true;
    public int chunkBuilderThreads = defaultThreadCount();

    public static MulticoreMagicConfig load(Path path) {
        if (Files.exists(path)) {
            try (var reader = Files.newBufferedReader(path)) {
                MulticoreMagicConfig loaded = GSON.fromJson(reader, MulticoreMagicConfig.class);
                if (loaded != null) {
                    return loaded.sanitize();
                }
            } catch (IOException e) {
                System.err.println("[MulticoreMagic] Failed to read config, using defaults: " + e.getMessage());
            }
        }
        MulticoreMagicConfig defaults = new MulticoreMagicConfig();
        defaults.save(path);
        return defaults;
    }

    public void save(Path path) {
        try {
            Files.createDirectories(path.getParent());
            try (var writer = Files.newBufferedWriter(path)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            System.err.println("[MulticoreMagic] Failed to save config: " + e.getMessage());
        }
    }

    private MulticoreMagicConfig sanitize() {
        int maxAllowed = Runtime.getRuntime().availableProcessors() * 2;
        if (chunkBuilderThreads < 1) {
            chunkBuilderThreads = 1;
        }
        if (chunkBuilderThreads > maxAllowed) {
            chunkBuilderThreads = maxAllowed;
        }
        return this;
    }
}
