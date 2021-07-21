package com.flashfyre.spectrite.lootTable;

import com.flashfyre.spectrite.mixin.*;
import net.fabricmc.fabric.api.loot.v1.FabricLootPool;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.mixin.loot.table.LootPoolBuilderHooks;
import net.fabricmc.fabric.mixin.loot.table.LootSupplierBuilderHooks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Pair;

import java.util.*;

import static com.flashfyre.spectrite.item.Items.*;

public class LootTables
{
    private static Map<Item, Pair<Item, Integer>> SPECTRITE_LOOT_ITEMS = new HashMap<>();

    public static void initLootTables()
    {
        addSpectriteLootItem(Items.DIAMOND, SPECTRITE_GEM, 21);
        addSpectriteLootItem(Items.DIAMOND_SWORD, SPECTRITE_SWORD, 189);
        addSpectriteLootItem(Items.DIAMOND_SHOVEL, SPECTRITE_SHOVEL, 147);
        addSpectriteLootItem(Items.DIAMOND_PICKAXE, SPECTRITE_PICKAXE, 189);
        addSpectriteLootItem(Items.DIAMOND_AXE, SPECTRITE_AXE, 147);
        addSpectriteLootItem(Items.DIAMOND_HOE, SPECTRITE_HOE, 147);
        addSpectriteLootItem(Items.DIAMOND_HELMET, SPECTRITE_HELMET, 252);
        addSpectriteLootItem(Items.DIAMOND_CHESTPLATE, SPECTRITE_CHESTPLATE, 252);
        addSpectriteLootItem(Items.DIAMOND_LEGGINGS, SPECTRITE_LEGGINGS, 252);
        addSpectriteLootItem(Items.DIAMOND_BOOTS, SPECTRITE_BOOTS, 252);
        addSpectriteLootItem(Items.ANCIENT_DEBRIS, SPECTRITE_GEM, 12);
        addSpectriteLootItem(Items.NETHERITE_SCRAP, SPECTRITE_GEM, 12);
        addSpectriteLootItem(Items.NETHERITE_INGOT, SPECTRITE_GEM, 7);
        addSpectriteLootItem(Items.NETHERITE_SWORD, SPECTRITE_SWORD, 36);
        addSpectriteLootItem(Items.NETHERITE_SHOVEL, SPECTRITE_SHOVEL, 21);
        addSpectriteLootItem(Items.NETHERITE_PICKAXE, SPECTRITE_PICKAXE, 36);
        addSpectriteLootItem(Items.NETHERITE_AXE, SPECTRITE_AXE, 21);
        addSpectriteLootItem(Items.NETHERITE_HOE, SPECTRITE_HOE, 21);
        addSpectriteLootItem(Items.NETHERITE_HELMET, SPECTRITE_HELMET, 49);
        addSpectriteLootItem(Items.NETHERITE_CHESTPLATE, SPECTRITE_CHESTPLATE, 49);
        addSpectriteLootItem(Items.NETHERITE_LEGGINGS, SPECTRITE_LEGGINGS, 49);
        addSpectriteLootItem(Items.NETHERITE_BOOTS, SPECTRITE_BOOTS, 49);
        addSpectriteLootItem(Items.TRIDENT, SPECTRITE_TRIDENT, 36);

        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) ->
        {
            if (id != null && id.getPath().startsWith("chests/"))
            {
                final List<LootPool.Builder> spectritePools = new ArrayList<>();
                ((LootSupplierBuilderHooks) supplier).getPools().forEach(pool ->
                {
                    final FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().copyFrom(pool);
                    final List<LootPoolEntry> entries = ((FabricLootPool) pool).getEntries();
                    final List<LootPoolEntry> spectriteEntries = ((LootPoolBuilderHooks) poolBuilder).getEntries();
                    spectriteEntries.clear();
                    int spectriteWeight = 0;
                    int totalWeight = 0;
                    for (LootPoolEntry entry : entries)
                    {
                        Pair<Item, Integer> spectriteItemEntry = null;
                        final LeafEntry leafEntry = entry instanceof LeafEntry ? (LeafEntry) entry : null;
                        final ItemEntry itemEntry = leafEntry != null && leafEntry instanceof ItemEntry ? (ItemEntry) leafEntry : null;
                        final int weight = leafEntry != null ? ((LeafEntryAccessor) leafEntry).getWeight() : 0;
                        final int quality = leafEntry != null ? ((LeafEntryAccessor) leafEntry).getQuality() : 0;
                        if (itemEntry != null)
                        {
                            final Item item = ((ItemEntryAccessor) itemEntry).getItem();
                            spectriteItemEntry = SPECTRITE_LOOT_ITEMS.getOrDefault(item, null);
                        }
                        if (spectriteItemEntry != null)
                        {
                            final ItemEntry.Builder spectriteItemEntryBuilder = ItemEntry.builder(spectriteItemEntry.getLeft())
                                    .conditionally(RandomChanceLootCondition.builder(1f / (float) spectriteItemEntry.getRight()))
                                    .weight(weight)
                                    .quality(quality);
                            final List<LootFunction> lootFunctions = Arrays.asList(((LeafEntryAccessor) itemEntry).getFunctions());
                            lootFunctions.forEach(function ->
                            {
                                LootFunction func = function;
                                if (func instanceof SetCountLootFunction setCountLootFunction)
                                {
                                    final LootNumberProvider countRange = ((SetCountLootFunctionAccessor) setCountLootFunction).getCountRange();
                                    if (countRange instanceof ConstantLootNumberProvider constantLootNumberProvider)
                                    {
                                        final float value = (float) Math.floor(Math.sqrt(
                                                ((ConstantLootNumberProviderAccessor) (Object) constantLootNumberProvider).getValue()));
                                        func = SetCountLootFunction.builder(ConstantLootNumberProvider.create(value)).build();
                                    } else if (countRange instanceof UniformLootNumberProvider uniformLootNumberProvider)
                                    {
                                        final LootNumberProvider min = ((UniformLootNumberProviderAccessor) uniformLootNumberProvider).getMin();
                                        final LootNumberProvider max = ((UniformLootNumberProviderAccessor) uniformLootNumberProvider).getMax();
                                        if (min instanceof ConstantLootNumberProvider && max instanceof ConstantLootNumberProvider)
                                        {
                                            final float minValue = (float) Math.floor(Math.sqrt(((ConstantLootNumberProviderAccessor) min).getValue()));
                                            final float maxValue = (float) Math.floor(Math.sqrt(((ConstantLootNumberProviderAccessor) max).getValue()));
                                            func = SetCountLootFunction.builder(UniformLootNumberProvider.create(minValue, maxValue)).build();
                                        }
                                    }
                                }
                                ((LeafEntryBuilderAccessor) spectriteItemEntryBuilder).getFunctions().add(func);
                            });
                            poolBuilder.with(spectriteItemEntryBuilder);
                            spectriteWeight += weight;
                        }
                        totalWeight += weight;
                    }
                    if (spectriteWeight > 0)
                    {
                        poolBuilder.conditionally(RandomChanceLootCondition.builder(spectriteWeight / (float) totalWeight));
                        spectritePools.add(poolBuilder);
                    }
                });
                spectritePools.forEach(sp -> supplier.pool(sp));
            }
        });
    }

    private static void addSpectriteLootItem(Item baseItem, Item spectriteItem, int weightMultiplier)
    {
        SPECTRITE_LOOT_ITEMS.put(baseItem, new Pair<>(spectriteItem, weightMultiplier));
    }
}
