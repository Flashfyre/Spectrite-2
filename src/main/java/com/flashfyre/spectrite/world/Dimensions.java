package com.flashfyre.spectrite.world;

import com.flashfyre.spectrite.Spectrite;
import com.flashfyre.spectrite.block.Blocks;
import com.flashfyre.spectrite.item.Items;
import net.kyrptonaught.customportalapi.CustomPortalApiRegistry;
import net.kyrptonaught.customportalapi.api.CustomPortalBuilder;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class Dimensions
{
    public static final RegistryKey<World> SUPERCHROMATIC = RegistryKey.of(Registry.WORLD_KEY, Spectrite.getId("superchromatic"));

    public static void initDimensions()
    {
        CustomPortalBuilder.beginPortal()
                .frameBlock(Blocks.SPECTRITE_BLOCK)
                .destDimID(SUPERCHROMATIC.getValue())
                .lightWithItem(Items.SUPERCHROMATIC_ELIXIR)
                .tintColor(128, 128, 128)
                .registerPortal();
        Blocks.SUPERCHROMATIC_PORTAL = CustomPortalApiRegistry.getPortalLinkFromBase(Blocks.SPECTRITE_BLOCK).getPortalBlock();
    }
}
