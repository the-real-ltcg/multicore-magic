package com.ltcg.multicoremagic.mixin;

import com.ltcg.multicoremagic.ChunkBuilderThreadPool;
import com.ltcg.multicoremagic.MulticoreMagic;
import net.minecraft.TracingExecutor;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Redirect(
        method = "invalidateCompiledGeometry",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/Util;backgroundExecutor()Lnet/minecraft/TracingExecutor;"
        )
    )
    private TracingExecutor multicoremagic$useChunkBuilderExecutor() {
        return ChunkBuilderThreadPool.executorFor(MulticoreMagic.CONFIG);
    }
}
