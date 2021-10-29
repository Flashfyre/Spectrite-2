package com.flashfyre.spectrite.loot.condition;

import com.flashfyre.spectrite.Spectrite;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.util.registry.Registry;

public class LootConditionTypes
{
    public static final LootConditionType SPECTRITE_MOB = new LootConditionType(new SuperchromaticMobLootCondition.Serializer());

    public static void initLootConditionTypes()
    {
        Registry.register(Registry.LOOT_CONDITION_TYPE, Spectrite.getId("spectrite_mob"), SPECTRITE_MOB);
    }
}
