package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.client.SpectriteClient;
import com.flashfyre.spectrite.entity.SpectriteCompatibleWeaponEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Environment(EnvType.CLIENT)
@Mixin(ProjectileEntityRenderer.class)
public class ProjectileEntityRendererMixin
{
    @ModifyArgs(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"))
    private void spectrite$modifyArgRenderGetEntityCutout(
            Args args, PersistentProjectileEntity projectileEntity, float f, float g, MatrixStack matrixStack,
            VertexConsumerProvider vertexConsumerProvider, int i)
    {
        final SpectriteCompatibleWeaponEntity spectriteCompatibleWeaponEntity = (SpectriteCompatibleWeaponEntity) projectileEntity;
        if (spectriteCompatibleWeaponEntity.isSuperchromatic())
            args.set(0, SpectriteClient.CLIENT_INSTANCE.getHueLayer(args.get(0),
                    spectriteCompatibleWeaponEntity.getSpectriteDamage(),
                    spectriteCompatibleWeaponEntity.isSpectriteCharged()));
    }
}
