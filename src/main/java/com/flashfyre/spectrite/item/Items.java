package com.flashfyre.spectrite.item;

import com.flashfyre.spectrite.Spectrite;
import com.flashfyre.spectrite.item.material.ArmorMaterials;
import com.flashfyre.spectrite.item.material.ToolMaterials;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Items
{
    private static final SpectriteDamageableDamageHandler spectriteDamageableDamageHandler = new SpectriteDamageableDamageHandler();

    public static Map<Item, Item> DEPLETED_DAMAGEABLE_ITEMS_MAP = new HashMap<>();

    public static List<Item> SPECTRITE_CHARGEABLE_ITEMS = new ArrayList<>();

    public static final Item SPECTRITE_GEM = new SimpleSpectriteItem(itemSettings().group(ItemGroup.MATERIALS));

    public static final Item SPECTRITE_ROD = new SimpleSpectriteItem(itemSettings().group(ItemGroup.MATERIALS));

    public static final Item SPECTRITE_BOW = new SpectriteBowItem(false,
            itemSettings().customDamage(spectriteDamageableDamageHandler).maxDamage(854).group(ItemGroup.COMBAT));

    public static final Item DEPLETED_SPECTRITE_BOW = new SpectriteBowItem(true,
            itemSettings().customDamage(spectriteDamageableDamageHandler).maxDamage(854));

    public static final Item SPECTRITE_SWORD = new SpectriteSwordItem(ToolMaterials.SPECTRITE, 3, -2.4f, false,
            itemSettings().customDamage(spectriteDamageableDamageHandler).group(ItemGroup.COMBAT));

    public static final Item DEPLETED_SPECTRITE_SWORD = new SpectriteSwordItem(ToolMaterials.DEPLETED_SPECTRITE, 3, -2.4f, true,
            itemSettings().customDamage(spectriteDamageableDamageHandler));

    public static final Item SPECTRITE_SHOVEL = new SpectriteShovelItem(ToolMaterials.SPECTRITE, 1.5F, -3.0F, false,
            itemSettings().customDamage(spectriteDamageableDamageHandler).group(ItemGroup.TOOLS));

    public static final Item DEPLETED_SPECTRITE_SHOVEL = new SpectriteShovelItem(ToolMaterials.DEPLETED_SPECTRITE, 1.5F, -3.0F, true,
            itemSettings().customDamage(spectriteDamageableDamageHandler));

    public static final Item SPECTRITE_PICKAXE = new SpectritePickaxeItem(ToolMaterials.SPECTRITE, 1, -2.8f, false,
            itemSettings().customDamage(spectriteDamageableDamageHandler).group(ItemGroup.TOOLS));

    public static final Item DEPLETED_SPECTRITE_PICKAXE = new SpectritePickaxeItem(ToolMaterials.DEPLETED_SPECTRITE, 1, -2.8f, true,
            itemSettings().customDamage(spectriteDamageableDamageHandler));

    public static final Item SPECTRITE_AXE = new SpectriteAxeItem(ToolMaterials.SPECTRITE, 5.0F, -3.0F, false,
            itemSettings().customDamage(spectriteDamageableDamageHandler).group(ItemGroup.TOOLS));

    public static final Item DEPLETED_SPECTRITE_AXE = new SpectriteAxeItem(ToolMaterials.DEPLETED_SPECTRITE, 5.0F, -3.0F, true,
            itemSettings().customDamage(spectriteDamageableDamageHandler));

    public static final Item SPECTRITE_HOE = new SpectriteHoeItem(ToolMaterials.SPECTRITE, -5, -1.0F, false,
            itemSettings().customDamage(spectriteDamageableDamageHandler).group(ItemGroup.TOOLS));

    public static final Item DEPLETED_SPECTRITE_HOE = new SpectriteHoeItem(ToolMaterials.DEPLETED_SPECTRITE, -2, 0.0F, true,
            itemSettings().customDamage(spectriteDamageableDamageHandler));

    public static final Item SPECTRITE_HELMET = new SpectriteArmorItem(ArmorMaterials.SPECTRITE, EquipmentSlot.HEAD, false,
            itemSettings().customDamage(spectriteDamageableDamageHandler).group(ItemGroup.COMBAT));

    public static final Item DEPLETED_SPECTRITE_HELMET = new SpectriteArmorItem(ArmorMaterials.DEPLETED_SPECTRITE, EquipmentSlot.HEAD, true,
            itemSettings().customDamage(spectriteDamageableDamageHandler));

    public static final Item SPECTRITE_CHESTPLATE = new SpectriteArmorItem(ArmorMaterials.SPECTRITE, EquipmentSlot.CHEST, false,
            itemSettings().customDamage(spectriteDamageableDamageHandler).group(ItemGroup.COMBAT));

    public static final Item DEPLETED_SPECTRITE_CHESTPLATE = new SpectriteArmorItem(ArmorMaterials.DEPLETED_SPECTRITE, EquipmentSlot.CHEST, true,
            itemSettings().customDamage(spectriteDamageableDamageHandler));

    public static final Item SPECTRITE_LEGGINGS = new SpectriteArmorItem(ArmorMaterials.SPECTRITE, EquipmentSlot.LEGS, false,
            itemSettings().customDamage(spectriteDamageableDamageHandler).group(ItemGroup.COMBAT));

    public static final Item DEPLETED_SPECTRITE_LEGGINGS = new SpectriteArmorItem(ArmorMaterials.DEPLETED_SPECTRITE, EquipmentSlot.LEGS, true,
            itemSettings().customDamage(spectriteDamageableDamageHandler));

    public static final Item SPECTRITE_BOOTS = new SpectriteArmorItem(ArmorMaterials.SPECTRITE, EquipmentSlot.FEET, false,
            itemSettings().customDamage(spectriteDamageableDamageHandler).group(ItemGroup.COMBAT));

    public static final Item DEPLETED_SPECTRITE_BOOTS = new SpectriteArmorItem(ArmorMaterials.DEPLETED_SPECTRITE, EquipmentSlot.FEET, true,
            itemSettings().customDamage(spectriteDamageableDamageHandler));

    public static final Item SPECTRITE_SHIELD = new SpectriteShieldItem(false,
            itemSettings().customDamage(spectriteDamageableDamageHandler).maxDamage(880).group(ItemGroup.COMBAT));

    public static final Item DEPLETED_SPECTRITE_SHIELD = new SpectriteShieldItem(true,
            itemSettings().customDamage(spectriteDamageableDamageHandler).maxDamage(880));

    public static final Item SPECTRITE_TRIDENT = new SpectriteTridentItem(false,
            itemSettings().customDamage(spectriteDamageableDamageHandler).maxDamage(462).group(ItemGroup.COMBAT));

    public static final Item DEPLETED_SPECTRITE_TRIDENT = new SpectriteTridentItem(true,
            itemSettings().customDamage(spectriteDamageableDamageHandler).maxDamage(462));

    public static final Item SUPERCHROMATIC_APPLE = new SimpleSpectriteItem(itemSettings().group(ItemGroup.FOOD).food(FoodComponents.GOLDEN_APPLE));

    public static final Item SUPERCHROMATIC_CARROT = new SimpleSpectriteItem(itemSettings().group(ItemGroup.FOOD).food(FoodComponents.GOLDEN_CARROT));

    public static final Item SUPERCHROMATIC_AXOLOTL_BUCKET = new SuperchromaticAxolotlBucketItem(Fluids.WATER, itemSettings().maxCount(1).group(ItemGroup.MISC));

    public static final Item SUPERCHROMATIC_ENDER_PEARL = new SuperchromaticEnderPearlItem(itemSettings().maxCount(16).group(ItemGroup.MISC));

    public static final Item SUPERCHROMATIC_NETHER_STAR = new SuperchromaticNetherStarItem(itemSettings().group(ItemGroup.MATERIALS));

    public static final Item SPECTRITE_BOMB = new SpectriteBombItem(itemSettings().group(ItemGroup.MISC));

    public static final Item SPECTRITE_HORSE_ARMOR = new SpectriteHorseArmorItem(17, itemSettings().group(ItemGroup.MISC));

    public static void initItems()
    {
        registerItem("spectrite_gem", SPECTRITE_GEM);
        registerItem("spectrite_rod", SPECTRITE_ROD);
        registerDamageableItem("spectrite_bow", SPECTRITE_BOW, DEPLETED_SPECTRITE_BOW);
        registerDamageableItem("spectrite_sword", SPECTRITE_SWORD, DEPLETED_SPECTRITE_SWORD);
        registerDamageableItem("spectrite_shovel", SPECTRITE_SHOVEL, DEPLETED_SPECTRITE_SHOVEL);
        registerDamageableItem("spectrite_pickaxe", SPECTRITE_PICKAXE, DEPLETED_SPECTRITE_PICKAXE);
        registerDamageableItem("spectrite_axe", SPECTRITE_AXE, DEPLETED_SPECTRITE_AXE);
        registerDamageableItem("spectrite_hoe", SPECTRITE_HOE, DEPLETED_SPECTRITE_HOE);
        registerDamageableItem("spectrite_helmet", SPECTRITE_HELMET, DEPLETED_SPECTRITE_HELMET);
        registerDamageableItem("spectrite_chestplate", SPECTRITE_CHESTPLATE, DEPLETED_SPECTRITE_CHESTPLATE);
        registerDamageableItem("spectrite_leggings", SPECTRITE_LEGGINGS, DEPLETED_SPECTRITE_LEGGINGS);
        registerDamageableItem("spectrite_boots", SPECTRITE_BOOTS, DEPLETED_SPECTRITE_BOOTS);
        registerDamageableItem("spectrite_shield", SPECTRITE_SHIELD, DEPLETED_SPECTRITE_SHIELD);
        registerDamageableItem("spectrite_trident", SPECTRITE_TRIDENT, DEPLETED_SPECTRITE_TRIDENT);
        registerItem("superchromatic_apple", SUPERCHROMATIC_APPLE);
        registerItem("superchromatic_carrot", SUPERCHROMATIC_CARROT);
        registerItem("superchromatic_axolotl_bucket", SUPERCHROMATIC_AXOLOTL_BUCKET);
        registerItem("superchromatic_ender_pearl", SUPERCHROMATIC_ENDER_PEARL);
        registerItem("superchromatic_nether_star", SUPERCHROMATIC_NETHER_STAR);
        registerItem("spectrite_bomb", SPECTRITE_BOMB);
        registerItem("spectrite_horse_armor", SPECTRITE_HORSE_ARMOR);
    }

    private static FabricItemSettings itemSettings()
    {
        return new FabricItemSettings().rarity(Rarity.EPIC);
    }

    public static void registerBlockItem(Identifier identifier, Block block, FabricItemSettings settings)
    {
        Registry.register(Registry.ITEM, identifier, new BlockItem(block, settings));
    }

    private static void registerItem(String name, Item item)
    {
        Registry.register(Registry.ITEM, Spectrite.getId(name), item);
    }

    private static void registerDamageableItem(String name, Item item, Item depletedItem)
    {
        registerItem(name, item);
        registerItem("depleted_" + name, depletedItem);
        DEPLETED_DAMAGEABLE_ITEMS_MAP.put(item, depletedItem);
        if (item instanceof SpectriteChargeableItem)
            SPECTRITE_CHARGEABLE_ITEMS.add(item);
    }
}