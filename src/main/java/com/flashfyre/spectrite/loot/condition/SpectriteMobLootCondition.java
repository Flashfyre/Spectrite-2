package com.flashfyre.spectrite.loot.condition;

import com.flashfyre.spectrite.entity.SpectriteCompatibleMobEntity;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.entity.Entity;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.JsonSerializer;

import java.util.Set;

public class SpectriteMobLootCondition implements LootCondition
{
    static final SpectriteMobLootCondition INSTANCE = new SpectriteMobLootCondition();

    private SpectriteMobLootCondition()
    {
    }

    public LootConditionType getType()
    {
        return LootConditionTypes.SPECTRITE_MOB;
    }

    public Set<LootContextParameter<?>> getRequiredParameters()
    {
        return ImmutableSet.of(LootContextParameters.THIS_ENTITY);
    }

    public boolean test(LootContext lootContext)
    {
        final Entity entity = lootContext.get(LootContextParameters.THIS_ENTITY);
        return entity instanceof SpectriteCompatibleMobEntity spectriteCompatibleMobEntity
                && spectriteCompatibleMobEntity.isSuperchromatic();
    }

    public static LootCondition.Builder builder()
    {
        return () -> INSTANCE;
    }

    public static class Serializer implements JsonSerializer<SpectriteMobLootCondition>
    {
        public void toJson(JsonObject jsonObject, SpectriteMobLootCondition spectriteMobLootCondition, JsonSerializationContext jsonSerializationContext)
        {
        }

        public SpectriteMobLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext)
        {
            return SpectriteMobLootCondition.INSTANCE;
        }
    }
}
