package com.flashfyre.spectrite.text;

import com.flashfyre.spectrite.mixin.MutableTextAccessor;
import com.flashfyre.spectrite.util.SpectriteUtils;
import com.google.common.collect.Lists;
import net.minecraft.text.*;
import net.minecraft.util.Language;

import java.util.ArrayList;
import java.util.List;

public class SpectriteText extends MutableText
{
    private MutableText baseText;

    private final boolean rotateTextColor;

    private Integer previousColorIndex;

    public SpectriteText(MutableText baseText, boolean rotateTextColor)
    {
        super(TextContent.EMPTY, Lists.newArrayList(), Style.EMPTY);
        this.baseText = baseText;
        this.rotateTextColor = rotateTextColor;
        ((MutableTextAccessor) this).getSiblings().addAll(getFormattedCharsAsSiblings());
    }

    @Override
    public OrderedText asOrderedText()
    {
        if (rotateTextColor)
        {
            final OrderedText lastOrderedText = ((MutableTextAccessor) this).spectrite$getOrderedText();
            final OrderedText ret = super.asOrderedText();
            final int colorIndex = Math.round((System.currentTimeMillis() >> 7) % 7);
            if (rotateTextColor && previousColorIndex != colorIndex)
            {
                previousColorIndex = colorIndex;
                if (ret == lastOrderedText)
                {
                    final OrderedText newValue = Language.getInstance().reorder(this);
                    ((MutableTextAccessor) this).spectrite$setOrderedText(newValue);
                    return newValue;
                }
            }
            return ret;
        }
        return super.asOrderedText();
    }

    @Override
    public MutableText copy()
    {
        return new SpectriteText(baseText, rotateTextColor);
    }

    @Override
    public List<Text> getSiblings()
    {
        if (rotateTextColor)
            return getFormattedCharsAsSiblings();
        return super.getSiblings();
    }

    private List<Text> getFormattedCharsAsSiblings()
    {
        final List<Text> ret = new ArrayList<>();
        final char[] chars = baseText.getString().toCharArray();
        final int textColorCount = SpectriteUtils.TEXT_COLORS.length;
        final int colorIndex;
        if (rotateTextColor)
        {
            if (previousColorIndex == null)
                previousColorIndex = Math.round((System.currentTimeMillis() >> 7) % 7);
            colorIndex = previousColorIndex.intValue();
        } else
            colorIndex = 0;
        for (int c = 0; c < chars.length; c++)
        {
            final char currentChar = chars[c];
            ret.add(Text.literal(String.valueOf(currentChar)).formatted(SpectriteUtils.TEXT_COLORS[(c + colorIndex) % textColorCount]));
        }
        return ret;
    }

    public boolean equals(Object object)
    {
        if (this == object)
            return true;
        if (!(object instanceof SpectriteText spectriteTextContent))
            return false;
        return rotateTextColor == spectriteTextContent.rotateTextColor && baseText.equals(spectriteTextContent.baseText) && super.equals(object);
    }
}
