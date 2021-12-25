package com.flashfyre.spectrite.client.mixin;

import net.minecraft.text.StringVisitable;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(TranslatableText.class)
public interface TranslatableTextAccessor
{
    @Invoker("updateTranslations")
    void invokeUpdateTranslations();

    @Accessor("translations")
    List<StringVisitable> getTranslations();
}
