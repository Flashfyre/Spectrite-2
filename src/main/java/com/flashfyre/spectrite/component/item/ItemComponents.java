package com.flashfyre.spectrite.component.item;

import com.flashfyre.spectrite.Spectrite;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ToolItem;

public final class ItemComponents implements ItemComponentInitializer
{
    public static final ComponentKey<SuperchromaticItemComponent> SUPERCHROMATIC_ITEM =
            ComponentRegistryV3.INSTANCE.getOrCreate(Spectrite.getId("superchromatic_item"), SuperchromaticItemComponent.class);

    @Override
    public void registerItemComponentFactories(ItemComponentFactoryRegistry registry)
    {
        registry.register(item -> item instanceof ToolItem || item instanceof BowItem,
                SUPERCHROMATIC_ITEM, itemstack -> new SuperchromaticItemComponent(itemstack, SUPERCHROMATIC_ITEM, true));

        registry.register(item -> item instanceof ArmorItem, SUPERCHROMATIC_ITEM,
                itemstack -> new SuperchromaticItemComponent(itemstack, SUPERCHROMATIC_ITEM, false));
    }
}
