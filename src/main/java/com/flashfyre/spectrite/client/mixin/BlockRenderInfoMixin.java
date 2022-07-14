package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.block.Blocks;
import com.flashfyre.spectrite.client.SpectriteClient;
import com.flashfyre.spectrite.component.chunk.SuperchromaticChunkComponent;
import com.flashfyre.spectrite.world.Dimensions;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(BlockRenderInfo.class)
public abstract class BlockRenderInfoMixin
{
    @Shadow
    public BlockPos blockPos;

    @Shadow
    public BlockState blockState;

    @ModifyExpressionValue(method = "prepareForBlock", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/RenderLayers;getBlockLayer(Lnet/minecraft/block/BlockState;)Lnet/minecraft/client/render/RenderLayer;"))
    public RenderLayer spectrite$overridePrepareForBlockGetRenderLayer(RenderLayer renderLayer)
    {
        if (((MinecraftClient.getInstance().world.getRegistryKey() == Dimensions.SUPERCHROMATIC
                || SuperchromaticChunkComponent.KEY.get(MinecraftClient.getInstance().world.getChunk(this.blockPos)).getSuperchromaticBlocks().contains(blockPos))
                && !blockState.isOf(Blocks.SPECTRITE_BLOCK))
                || this.blockState.isOf(Blocks.SUPERCHROMATIC_PORTAL))
            return SpectriteClient.CLIENT_INSTANCE.getSuperchromaticBlockLayer(renderLayer);
        return renderLayer;
    }
}
