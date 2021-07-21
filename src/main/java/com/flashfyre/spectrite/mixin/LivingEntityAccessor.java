package com.flashfyre.spectrite.mixin;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor
{
    @Invoker("setArmorInSlot")
    void invokeSetArmorInSlot(EquipmentSlot slot, ItemStack armor);
}
