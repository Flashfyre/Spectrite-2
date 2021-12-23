package com.flashfyre.spectrite.recipe;

import com.flashfyre.spectrite.Spectrite;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.registry.Registry;

public class RecipeSerializers
{
    public static final SpecialRecipeSerializer<SuperchromizedItemRecipe> SUPERCHROMIZED_ITEM
            = new SpecialRecipeSerializer<>(SuperchromizedItemRecipe::new);

    public static void initRecipeSerializers()
    {
        registerRecipeSerializer("superchromized_item", SUPERCHROMIZED_ITEM);
    }

    private static void registerRecipeSerializer(String name, RecipeSerializer<?> recipeSerializer)
    {
        Registry.register(Registry.RECIPE_SERIALIZER, Spectrite.getId(name), recipeSerializer);
    }
}
