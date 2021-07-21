package com.flashfyre.spectrite.mixin;

import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(UniformLootNumberProvider.class)
public interface UniformLootNumberProviderAccessor
{
    @Accessor("min")
    LootNumberProvider getMin();

    @Accessor("max")
    LootNumberProvider getMax();
}
