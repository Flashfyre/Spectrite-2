package com.flashfyre.spectrite.block.entity;

import com.flashfyre.spectrite.Spectrite;
import com.flashfyre.spectrite.block.Blocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class BlockEntities
{
    public static BlockEntityType<SpectriteChestBlockEntity> SPECTRITE_CHEST;

    public static void initBlockEntities()
    {
        SPECTRITE_CHEST = Registry.register(Registry.BLOCK_ENTITY_TYPE, Spectrite.getId("spectrite_chest"),
                FabricBlockEntityTypeBuilder.create(SpectriteChestBlockEntity::new, Blocks.SPECTRITE_CHEST).build(null));
    }
}
