package com.flashfyre.spectrite.component;

import com.flashfyre.spectrite.Spectrite;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;

public final class Components implements EntityComponentInitializer
{
    public static final ComponentKey<SpectriteEntityComponent> SPECTRITE_ENTITY =
            ComponentRegistryV3.INSTANCE.getOrCreate(Spectrite.getId("spectrite_entity"), SpectriteEntityComponent.class);

    public static final ComponentKey<SpectriteWeaponEntityAttributesComponent> SPECTRITE_WEAPON_ENTITY_ATTRIBUTES =
            ComponentRegistryV3.INSTANCE.getOrCreate(Spectrite.getId("spectrite_weapon_entity_attributes"), SpectriteWeaponEntityAttributesComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry)
    {
        registry.registerFor(MobEntity.class, SPECTRITE_ENTITY, entity -> new SpectriteEntityComponent());
        registry.registerFor(PersistentProjectileEntity.class, SPECTRITE_ENTITY, entity -> new SpectriteEntityComponent());
        registry.registerFor(PersistentProjectileEntity.class, SPECTRITE_WEAPON_ENTITY_ATTRIBUTES, entity -> new SpectriteWeaponEntityAttributesComponent());
        registry.registerFor(WitherSkullEntity.class, SPECTRITE_ENTITY, entity -> new SpectriteEntityComponent());
        registry.registerFor(WitherSkullEntity.class, SPECTRITE_WEAPON_ENTITY_ATTRIBUTES, entity -> new SpectriteWeaponEntityAttributesComponent());
    }
}
