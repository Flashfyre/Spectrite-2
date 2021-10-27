package com.flashfyre.spectrite.block.entity;

import net.minecraft.block.entity.BlockEntityType;

public class BlockEntities
{
    public static BlockEntityType<SpectriteChestBlockEntity> SPECTRITE_CHEST;

    public static void initBlockEntities()
    {
        /*SPECTRITE_CHEST = Registry.register(Registry.BLOCK_ENTITY_TYPE, Spectrite.getId("spectrite_chest"),
                FabricBlockEntityTypeBuilder.create(SpectriteChestBlockEntity::new, Blocks.SPECTRITE_CHEST).build(null));*/
    }
}
