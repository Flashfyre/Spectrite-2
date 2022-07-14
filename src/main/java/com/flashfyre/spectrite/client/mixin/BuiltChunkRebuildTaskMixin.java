package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.client.SpectriteClient;
import com.flashfyre.spectrite.world.Dimensions;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(ChunkBuilder.BuiltChunk.RebuildTask.class)
public class BuiltChunkRebuildTaskMixin
{
    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/RenderLayers;getFluidLayer(Lnet/minecraft/fluid/FluidState;)Lnet/minecraft/client/render/RenderLayer;"))
    public RenderLayer renderOverrideSuperchromaticFluidRenderLayer(RenderLayer renderLayer)
    {
        if (MinecraftClient.getInstance().world.getRegistryKey() == Dimensions.SUPERCHROMATIC)
            return SpectriteClient.CLIENT_INSTANCE.getSuperchromaticBlockLayer(renderLayer);
        return renderLayer;
    }
}
