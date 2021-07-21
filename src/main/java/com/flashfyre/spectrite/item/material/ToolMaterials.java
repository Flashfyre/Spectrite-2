package com.flashfyre.spectrite.item.material;

import com.flashfyre.spectrite.item.Items;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Lazy;

import java.util.function.Supplier;

import static net.minecraft.item.ToolMaterials.IRON;

public enum ToolMaterials implements ToolMaterial
{
    SPECTRITE(MiningLevels.NETHERITE, 923, 10.0F, 5.0F, 20, () ->
    {
        return Ingredient.ofItems(Items.SPECTRITE_GEM);
    }),
    DEPLETED_SPECTRITE(IRON.getMiningLevel(), 923, IRON.getMiningSpeedMultiplier(),
            IRON.getAttackDamage(), 20, () ->
    {
        return Ingredient.ofItems(Items.SPECTRITE_GEM);
    });

    private final int miningLevel;
    private final int itemDurability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final Lazy<Ingredient> repairIngredient;

    ToolMaterials(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability, Supplier<Ingredient> repairIngredient)
    {
        this.miningLevel = miningLevel;
        this.itemDurability = itemDurability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredient = new Lazy(repairIngredient);
    }

    public int getDurability()
    {
        return this.itemDurability;
    }

    public float getMiningSpeedMultiplier()
    {
        return this.miningSpeed;
    }

    public float getAttackDamage()
    {
        return this.attackDamage;
    }

    public int getMiningLevel()
    {
        return this.miningLevel;
    }

    public int getEnchantability()
    {
        return this.enchantability;
    }

    public Ingredient getRepairIngredient()
    {
        return this.repairIngredient.get();
    }
}
