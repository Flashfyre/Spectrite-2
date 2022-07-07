package com.flashfyre.spectrite.mixin;

import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(MutableText.class)
public interface MutableTextAccessor
{
    @Accessor("siblings")
    List<Text> getSiblings();

    @Accessor("ordered")
    OrderedText spectrite$getOrderedText();

    @Accessor("ordered")
    void spectrite$setOrderedText(OrderedText orderedText);
}
