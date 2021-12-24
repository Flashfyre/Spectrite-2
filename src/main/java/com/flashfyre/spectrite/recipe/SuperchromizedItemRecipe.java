package com.flashfyre.spectrite.recipe;

import com.flashfyre.spectrite.item.Items;
import com.flashfyre.spectrite.util.SuperchromaticItemUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SuperchromizedItemRecipe extends SmithingRecipe
{
    public SuperchromizedItemRecipe(Identifier id)
    {
        super(id, Ingredient.EMPTY, Ingredient.EMPTY, ItemStack.EMPTY);
    }

    @Override
    public boolean matches(Inventory inventory, World world)
    {
        ItemStack baseItemStack = inventory.getStack(0);
        ItemStack additionItemStack = inventory.getStack(1);
        if (baseItemStack.isEmpty() || additionItemStack.isEmpty())
            return false;
        return additionItemStack.isOf(Items.SUPERCHROMATIC_ESSENCE)
                && SuperchromaticItemUtils.isSuperchromaticCompatible(baseItemStack) && !SuperchromaticItemUtils.isSuperchromatic(baseItemStack);
    }

    @Override
    public ItemStack craft(Inventory inventory)
    {
        final ItemStack itemStack = inventory.getStack(0).copy();
        final int superchromaticPoints = SuperchromaticItemUtils.getSuperchromaticPoints(itemStack) + 1;
        SuperchromaticItemUtils.setSuperchromaticPoints(itemStack, superchromaticPoints);
        if (superchromaticPoints >= SuperchromaticItemUtils.getRequiredSuperchromaticPoints(itemStack))
            SuperchromaticItemUtils.setSuperchromatic(itemStack, true);
        return itemStack;
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public boolean fits(int width, int height)
    {
        return width * height >= 2;
    }

    @Override
    public boolean isIgnoredInRecipeBook()
    {
        return true;
    }

    @Override
    public ItemStack getOutput()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return RecipeSerializers.SUPERCHROMIZED_ITEM;
    }
}
