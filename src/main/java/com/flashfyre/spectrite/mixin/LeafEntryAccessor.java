package com.flashfyre.spectrite.mixin;

import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.LootFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LeafEntry.class)
public interface LeafEntryAccessor
{
    @Accessor("weight")
    int getWeight();

    @Accessor("quality")
    int getQuality();

    @Accessor("functions")
    LootFunction[] getFunctions();
}
