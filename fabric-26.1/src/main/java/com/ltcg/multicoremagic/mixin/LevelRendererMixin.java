package com.ltcg.multicoremagic.mixin;

import com.ltcg.multicoremagic.ChunkBuilderThreadPool;
import com.ltcg.multicoremagic.MulticoreMagic;
import net.minecraft.TracingExecutor;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * On 26.1.2 this same SectionRenderDispatcher-construction logic lives in allChanged() (renamed
 * to invalidateCompiledGeometry(...) on 26.2 with an expanded signature) - verified via javap
 * against the real deobfuscated client jars for both versions, not assumed.
 */
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Redirect(
        method = "allChanged",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/Util;backgroundExecutor()Lnet/minecraft/TracingExecutor;"
        )
    )
    private TracingExecutor multicoremagic$useChunkBuilderExecutor() {
        return ChunkBuilderThreadPool.executorFor(MulticoreMagic.CONFIG);
    }
}
