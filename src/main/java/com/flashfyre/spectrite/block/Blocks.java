package com.flashfyre.spectrite.block;

import com.flashfyre.spectrite.Spectrite;
import com.flashfyre.spectrite.item.Items;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.Registry;

public class Blocks
{
    public static Block SPECTRITE_ORE = new SpectriteOreBlock(AbstractBlock.Settings.of(Material.STONE)
            .requiresTool()
            .strength(25.0F, 6.0F)
            .luminance(state ->
            {
                return 5;
            }), UniformIntProvider.create(14, 21));

    public static Block DEEPSLATE_SPECTRITE_ORE = new SpectriteOreBlock(AbstractBlock.Settings.copy(SPECTRITE_ORE)
            .mapColor(MapColor.DARK_RED)
            .sounds(BlockSoundGroup.DEEPSLATE),
            UniformIntProvider.create(14, 21));

    public static Block NETHER_SPECTRITE_ORE = new SpectriteOreBlock(AbstractBlock.Settings.copy(SPECTRITE_ORE)
            .mapColor(MapColor.DARK_RED),
            UniformIntProvider.create(14, 21));

    public static Block BLACKSTONE_SPECTRITE_ORE = new SpectriteOreBlock(AbstractBlock.Settings.copy(SPECTRITE_ORE)
            .mapColor(MapColor.BLACK),
            UniformIntProvider.create(14, 21));

    public static Block END_SPECTRITE_ORE = new SpectriteOreBlock(AbstractBlock.Settings.copy(SPECTRITE_ORE)
            .mapColor(MapColor.PALE_YELLOW),
            UniformIntProvider.create(14, 21));

    public static Block SPECTRITE_BLOCK = new SimpleSpectriteBlock(AbstractBlock.Settings.of(Material.METAL, MapColor.PURPLE)
            .requiresTool()
            .strength(50.0F, 9.0F)
            .sounds(BlockSoundGroup.METAL)
            .luminance(state ->
            {
                return 10;
            }));

    public static Block SUPERCHROMATIC_CHORUS_PLANT = new SuperchromaticChorusPlantBlock(
            AbstractBlock.Settings.of(Material.PLANT, MapColor.PURPLE).strength(0.4f).sounds(BlockSoundGroup.WOOD).nonOpaque());

    public static Block SUPERCHROMATIC_CHORUS_FLOWER = new SuperchromaticChorusFlowerBlock((SuperchromaticChorusPlantBlock) SUPERCHROMATIC_CHORUS_PLANT,
            AbstractBlock.Settings.of(Material.PLANT, MapColor.PURPLE).ticksRandomly().strength(0.4f).sounds(BlockSoundGroup.WOOD).nonOpaque());

    /*public static Block SPECTRITE_CHEST = new SpectriteChestBlock(AbstractBlock.Settings.of(Material.METAL, MapColor.PURPLE)
            .requiresTool()
            .strength(50.0F, 9.0F)
            .sounds(BlockSoundGroup.METAL)
            .luminance(state ->
            {
                return 10;
            }), () -> BlockEntities.SPECTRITE_CHEST);*/

    public static Block SUPERCHROMATIC_PORTAL;

    public static void initBlocks()
    {
        registerBlock("spectrite_ore", SPECTRITE_ORE, new FabricItemSettings());
        registerBlock("spectrite_ore_deepslate", DEEPSLATE_SPECTRITE_ORE, new FabricItemSettings());
        registerBlock("spectrite_ore_nether", NETHER_SPECTRITE_ORE, new FabricItemSettings());
        registerBlock("spectrite_ore_blackstone", BLACKSTONE_SPECTRITE_ORE, new FabricItemSettings());
        registerBlock("spectrite_ore_end", END_SPECTRITE_ORE, new FabricItemSettings());
        registerBlock("spectrite_block", SPECTRITE_BLOCK, new FabricItemSettings());
        //registerBlock("spectrite_chest", SPECTRITE_CHEST, new FabricItemSettings().group(ItemGroup.DECORATIONS));
        registerBlock("superchromatic_chorus_plant", SUPERCHROMATIC_CHORUS_PLANT, new FabricItemSettings());
        registerBlock("superchromatic_chorus_flower", SUPERCHROMATIC_CHORUS_FLOWER, new FabricItemSettings());
    }

    private static void registerBlock(String name, Block block, FabricItemSettings itemSettings)
    {
        final Identifier identifier = Spectrite.getId(name);
        Registry.register(Registry.BLOCK, identifier, block);
        if (itemSettings != null)
            Items.registerBlockItem(identifier, block, itemSettings.group(ItemGroup.BUILDING_BLOCKS));
    }
}
