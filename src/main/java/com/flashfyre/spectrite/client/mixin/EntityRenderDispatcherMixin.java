package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.client.util.SpectriteEntityRenderUtils;
import com.flashfyre.spectrite.entity.SpectriteCompatibleEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin
{
    @Inject(method = "render", at = @At(value = "HEAD"))
    private <E extends Entity> void injectRenderSetCurrentRenderingSpectriteCompatibleEntity(E entity, double x, double y, double z, float yaw,
                                                                                             float tickDelta, MatrixStack matrices,
                                                                                             VertexConsumerProvider vertexConsumers, int light,
                                                                                             CallbackInfo ci)
    {
        SpectriteEntityRenderUtils.CURRENT_RENDERING_ENTITY_ID = EntityType.getId(entity.getType());
        SpectriteEntityRenderUtils.CURRENT_RENDERING_ENTITY_SPECTRITE_COMPATIBLE = entity instanceof SpectriteCompatibleEntity
                || entity instanceof PlayerEntity
                || entity instanceof ArmorStandEntity;
        SpectriteEntityRenderUtils.CURRENT_RENDERING_ENTITY_MODEL_CLASS_NAME = null;
    }

    @Inject(method = "render", at = @At(value = "RETURN"))
    private <E extends Entity> void injectRenderClearCurrentRenderingSpectriteCompatibleEntity(E entity, double x, double y, double z, float yaw,
                                                                                               float tickDelta, MatrixStack matrices,
                                                                                               VertexConsumerProvider vertexConsumers, int light,
                                                                                               CallbackInfo ci)
    {
        SpectriteEntityRenderUtils.CURRENT_RENDERING_ENTITY_ID = null;
        SpectriteEntityRenderUtils.CURRENT_RENDERING_ENTITY_SPECTRITE_COMPATIBLE = false;
        SpectriteEntityRenderUtils.CURRENT_RENDERING_ENTITY_MODEL_CLASS_NAME = null;
    }
}
