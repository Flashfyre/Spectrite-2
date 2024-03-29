package com.flashfyre.spectrite.loot;

import com.flashfyre.spectrite.loot.condition.SuperchromaticMobLootCondition;
import com.flashfyre.spectrite.mixin.*;
import com.flashfyre.spectrite.util.SuperchromaticEntityUtils;
import net.fabricmc.fabric.api.loot.v2.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.*;

import static com.flashfyre.spectrite.item.Items.*;

public class LootTables
{
    private static Map<Item, Pair<Item, Integer>> SPECTRITE_LOOT_ITEMS = new HashMap<>();
    private static Set<Item> SPECTRITE_COUNT_OVERRIDE_ITEMS = new HashSet<>();

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
        addSpectriteLootItem(Items.GOLDEN_APPLE, SUPERCHROMATIC_APPLE, 49);
        addSpectriteLootItem(Items.GOLDEN_CARROT, SUPERCHROMATIC_CARROT, 49);
        addSpectriteLootItem(Items.DIAMOND_HORSE_ARMOR, SPECTRITE_HORSE_ARMOR, 21);

        SPECTRITE_COUNT_OVERRIDE_ITEMS.add(SUPERCHROMATIC_APPLE);
        SPECTRITE_COUNT_OVERRIDE_ITEMS.add(SUPERCHROMATIC_CARROT);

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) ->
        {
            if (id != null && id.getPath().startsWith("chests/"))
            {
                final List<FabricLootPoolBuilder> spectritePools = new ArrayList<>();
                ((LootTableBuilderAccessor) tableBuilder).getPools().forEach(pool ->
                {
                    final FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.copyOf(pool);
                    final List<LootPoolEntry> spectriteEntries = ((LootPoolBuilderAccessor) poolBuilder).getEntries();
                    spectriteEntries.clear();
                    int spectriteWeight = 0;
                    int totalWeight = 0;
                    for (LootPoolEntry entry : pool.entries)
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
                            final Item spectriteItem = spectriteItemEntry.getLeft();
                            final ItemEntry.Builder spectriteItemEntryBuilder = ItemEntry.builder(spectriteItem)
                                    .conditionally(RandomChanceLootCondition.builder(1f / (float) spectriteItemEntry.getRight()))
                                    .weight(weight)
                                    .quality(quality);
                            final List<LootFunction> lootFunctions = Arrays.asList(((LeafEntryAccessor) itemEntry).getFunctions());
                            lootFunctions.forEach(function ->
                            {
                                LootFunction func = function;
                                if (func instanceof SetCountLootFunction setCountLootFunction)
                                {
                                    if (!SPECTRITE_COUNT_OVERRIDE_ITEMS.contains(spectriteItem))
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
                                    } else
                                        func = SetCountLootFunction.builder(ConstantLootNumberProvider.create(1f)).build();
                                }
                                ((LeafEntryBuilderAccessor) spectriteItemEntryBuilder).getFunctions().add(func);
                            });
                            poolBuilder.with(spectriteItemEntryBuilder.build());
                            spectriteWeight += weight;
                        }
                        totalWeight += weight;
                    }
                    if (spectriteWeight > 0)
                    {
                        poolBuilder.conditionally(RandomChanceLootCondition.builder(spectriteWeight / (float) totalWeight).build());
                        spectritePools.add(poolBuilder);
                    }
                });
                spectritePools.forEach(sp -> tableBuilder.pool(((LootPool.Builder) sp).build()));
            } else if (id.getPath().startsWith("entities/"))
            {
                String entityName = id.getPath().substring(9);
                if (entityName.contains("/"))
                    entityName = entityName.substring(0, entityName.indexOf('/'));

                final Identifier entityId = new Identifier(id.getNamespace(), entityName);
                final Map.Entry<Integer, Integer> essenceRange = SuperchromaticEntityUtils.getSuperchromaticEssenceRange(entityId);

                final LootPool superchromaticEssencePool = LootPool.builder()
                        .with(ItemEntry.builder(SUPERCHROMATIC_ESSENCE)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(essenceRange.getKey(), essenceRange.getValue())))
                                .conditionally(SuperchromaticMobLootCondition.builder())
                                .conditionally(KilledByPlayerLootCondition.builder()))
                        .build();
                tableBuilder.pool(superchromaticEssencePool);

                switch (entityName)
                {
                    case "witch":
                        tableBuilder.pool(LootPool.builder()
                                .with(ItemEntry.builder(SUPERCHROMATIC_ELIXIR)
                                        .conditionally(SuperchromaticMobLootCondition.builder())
                                        .conditionally(KilledByPlayerLootCondition.builder())
                                        .conditionally(RandomChanceLootCondition.builder(1f / 7f))));
                        break;
                    case "enderman":
                    case "wither":
                        final boolean isEnderman = "enderman".equals(entityName);
                        tableBuilder.pool(LootPool.builder()
                                .with(ItemEntry.builder(isEnderman ? SUPERCHROMATIC_ENDER_PEARL : SUPERCHROMATIC_NETHER_STAR)
                                        .conditionally(SuperchromaticMobLootCondition.builder())
                                        .conditionally(KilledByPlayerLootCondition.builder())
                                        .conditionally(RandomChanceLootCondition.builder(1f / 7f))));
                        break;
                }
            }
        });
    }

    private static void addSpectriteLootItem(Item baseItem, Item spectriteItem, int weightMultiplier)
    {
        SPECTRITE_LOOT_ITEMS.put(baseItem, new Pair<>(spectriteItem, weightMultiplier));
    }
}
