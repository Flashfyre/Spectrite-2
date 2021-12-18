package com.flashfyre.spectrite.entity;

import com.flashfyre.spectrite.Spectrite;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.registry.Registry;

public class EntityTypes
{
    public static EntityType<SpectriteGolemEntity> SPECTRITE_GOLEM = FabricEntityTypeBuilder.create(SpawnGroup.MISC, SpectriteGolemEntity::new)
            .dimensions(EntityDimensions.fixed(1.4f, 2.7f)).trackRangeBlocks(10).build();

    public static EntityType<SpectriteBombEntity> SPECTRITE_BOMB = FabricEntityTypeBuilder.<SpectriteBombEntity>create(SpawnGroup.MISC, SpectriteBombEntity::new)
            .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).trackRangeBlocks(4).trackedUpdateRate(10).build();

    public static void initEntityTypes()
    {
        registerEntity("spectrite_golem", SPECTRITE_GOLEM);
        FabricDefaultAttributeRegistry.register(SPECTRITE_GOLEM, IronGolemEntity.createIronGolemAttributes());

        registerEntity("spectrite_bomb", SPECTRITE_BOMB);
    }

    private static void registerEntity(String name, EntityType<? extends Entity> entity)
    {
        Registry.register(Registry.ENTITY_TYPE, Spectrite.getId(name), entity);
    }
}
