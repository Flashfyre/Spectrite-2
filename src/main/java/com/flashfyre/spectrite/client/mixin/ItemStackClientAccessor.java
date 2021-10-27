package com.flashfyre.spectrite.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public interface ItemStackClientAccessor
{
    @Invoker("getHideFlags")
    int invokeGetHideFlags();
}
