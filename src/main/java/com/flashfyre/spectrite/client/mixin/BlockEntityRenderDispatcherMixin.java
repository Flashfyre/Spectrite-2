package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.client.util.SpectriteEntityRenderUtils;
import com.flashfyre.spectrite.component.chunk.SuperchromaticChunkComponent;
import com.flashfyre.spectrite.world.Dimensions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRenderDispatcherMixin
{
    @Inject(method = "render(Lnet/minecraft/client/render/block/entity/BlockEntityRenderer;Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderer;render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V"))
    private static void injectRenderSetCurrentRenderingBlockEntitySuperchromatic(BlockEntityRenderer renderer, BlockEntity blockEntity,
                                                                                 float tickDelta, MatrixStack matrices,
                                                                                 VertexConsumerProvider vertexConsumers, CallbackInfo ci)
    {
        SpectriteEntityRenderUtils.CURRENT_RENDERING_BLOCK_ENTITY_SUPERCHROMATIC = blockEntity.getWorld().getRegistryKey() == Dimensions.SUPERCHROMATIC
                || SuperchromaticChunkComponent.KEY.get(
                blockEntity.getWorld().getChunk(blockEntity.getPos()))
                .getSuperchromaticBlocks().contains(blockEntity.getPos());
    }

    @Inject(method = "render(Lnet/minecraft/client/render/block/entity/BlockEntityRenderer;Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V",
            at = @At(value = "TAIL"))
    private static void injectRenderClearCurrentRenderingBlockEntitySuperchromatic(BlockEntityRenderer renderer, BlockEntity blockEntity,
                                                                                   float tickDelta, MatrixStack matrices,
                                                                                   VertexConsumerProvider vertexConsumers, CallbackInfo ci)
    {
        SpectriteEntityRenderUtils.CURRENT_RENDERING_BLOCK_ENTITY_SUPERCHROMATIC = false;
    }
}
