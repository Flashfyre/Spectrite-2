package com.flashfyre.spectrite.item.material;

import com.flashfyre.spectrite.item.Items;
import com.google.common.primitives.Ints;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Lazy;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.minecraft.item.ArmorMaterials.IRON;

public enum ArmorMaterials implements ArmorMaterial
{
    SPECTRITE("spectrite", 45, new int[]
            {4, 7, 9, 4}, 20, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 4.0F, 0.15F, () ->
    {
        return Ingredient.ofItems(Items.SPECTRITE_GEM);
    }),
    DEPLETED_SPECTRITE("depleted_spectrite", 45,
            Ints.toArray(Arrays.stream(EquipmentSlot.values()).filter(es -> es.getType() == EquipmentSlot.Type.ARMOR)
                    .map(es -> IRON.getProtectionAmount(es)).collect(Collectors.toList())),
            20, IRON.getEquipSound(), IRON.getToughness(), IRON.getKnockbackResistance(), () ->
    {
        return Ingredient.ofItems(Items.SPECTRITE_GEM);
    });

    private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
    private final String name;
    private final int durabilityMultiplier;
    private final int[] protectionAmounts;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final Lazy<Ingredient> repairIngredientSupplier;

    ArmorMaterials(String name, int durabilityMultiplier, int[] protectionAmounts, int enchantability,
                   SoundEvent equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredientSupplier)
    {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionAmounts = protectionAmounts;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredientSupplier = new Lazy(repairIngredientSupplier);
    }

    public int getDurability(EquipmentSlot slot)
    {
        return BASE_DURABILITY[slot.getEntitySlotId()] * this.durabilityMultiplier;
    }

    public int getProtectionAmount(EquipmentSlot slot)
    {
        return this.protectionAmounts[slot.getEntitySlotId()];
    }

    public int getEnchantability()
    {
        return this.enchantability;
    }

    public SoundEvent getEquipSound()
    {
        return this.equipSound;
    }

    public Ingredient getRepairIngredient()
    {
        return null;
    }

    public String getName()
    {
        return this.name;
    }

    public float getToughness()
    {
        return this.toughness;
    }

    public float getKnockbackResistance()
    {
        return this.knockbackResistance;
    }
}
