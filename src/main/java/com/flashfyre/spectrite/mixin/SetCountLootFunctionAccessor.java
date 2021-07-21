package com.flashfyre.spectrite.mixin;

import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.LootNumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SetCountLootFunction.class)
public interface SetCountLootFunctionAccessor
{
    @Accessor("countRange")
    LootNumberProvider getCountRange();
}
