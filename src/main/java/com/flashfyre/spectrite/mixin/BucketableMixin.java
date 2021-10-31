package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.entity.SpectriteCompatibleMobEntity;
import com.flashfyre.spectrite.util.SpectriteEntityUtils;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Bucketable.class)
public interface BucketableMixin
{
    @Inject(method = "copyDataToStack(Lnet/minecraft/entity/mob/MobEntity;Lnet/minecraft/item/ItemStack;)V", at = @At("TAIL"))
    private static void injectCopyDataToStackSuperchromatic(MobEntity entity, ItemStack stack, CallbackInfo ci)
    {
        if (entity instanceof SpectriteCompatibleMobEntity spectriteCompatibleMobEntity)
            stack.getNbt().putBoolean("Superchromatic", spectriteCompatibleMobEntity.isSuperchromatic());
    }

    @Inject(method = "copyDataFromNbt(Lnet/minecraft/entity/mob/MobEntity;Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    private static void injectCopyDataFromNbtSuperchromatic(MobEntity entity, NbtCompound nbt, CallbackInfo ci)
    {
        if (nbt.contains("Superchromatic") && entity instanceof SpectriteCompatibleMobEntity)
            SpectriteEntityUtils.setSuperchromatic(entity, nbt.getBoolean("Superchromatic"));
    }
}
