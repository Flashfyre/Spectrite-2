package com.flashfyre.spectrite.text;

import com.flashfyre.spectrite.mixin.BaseTextAccessor;
import com.flashfyre.spectrite.util.SpectriteUtils;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Language;

import java.util.ArrayList;
import java.util.List;

public class SpectriteText extends BaseText
{
    private final Text baseText;

    private final boolean rotateTextColor;

    private Integer previousColorIndex;

    public SpectriteText(Text baseText, boolean rotateTextColor)
    {
        this.baseText = baseText;
        this.rotateTextColor = rotateTextColor;
        if (!rotateTextColor)
            this.siblings.addAll(getFormattedCharsAsSiblings());
    }

    @Override
    public OrderedText asOrderedText()
    {
        if (rotateTextColor)
        {
            final OrderedText lastOrderedText = ((BaseTextAccessor) this).spectrite$getOrderedText();
            final OrderedText ret = super.asOrderedText();
            final int colorIndex = Math.round((System.currentTimeMillis() >> 7) % 7);
            if (rotateTextColor && previousColorIndex != colorIndex)
            {
                previousColorIndex = colorIndex;
                if (ret == lastOrderedText)
                {
                    final OrderedText newValue = Language.getInstance().reorder(this);
                    ((BaseTextAccessor) this).spectrite$setOrderedText(newValue);
                    return newValue;
                }
            }
            return ret;
        }
        return super.asOrderedText();
    }

    @Override
    public BaseText copy()
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
            ret.add(new LiteralText(String.valueOf(currentChar)).formatted(SpectriteUtils.TEXT_COLORS[(c + colorIndex) % textColorCount]));
        }
        return ret;
    }

    public boolean equals(Object object)
    {
        if (this == object)
            return true;
        if (!(object instanceof SpectriteText spectriteText))
            return false;
        return rotateTextColor == spectriteText.rotateTextColor && baseText.equals(spectriteText.baseText) && super.equals(object);
    }
}
