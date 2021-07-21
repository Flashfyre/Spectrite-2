package com.flashfyre.spectrite.mixin;

import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.LootFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LeafEntry.Builder.class)
public interface LeafEntryBuilderAccessor
{
    @Accessor("functions")
    List<LootFunction> getFunctions();
}
