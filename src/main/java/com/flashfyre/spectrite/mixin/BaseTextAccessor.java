package com.flashfyre.spectrite.mixin;

import net.minecraft.text.BaseText;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BaseText.class)
public interface BaseTextAccessor
{
    @Accessor("orderedText")
    OrderedText spectrite$getOrderedText();

    @Accessor("orderedText")
    void spectrite$setOrderedText(OrderedText orderedText);
}
