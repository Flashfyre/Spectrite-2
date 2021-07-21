package com.flashfyre.spectrite.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.SpriteAtlasTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(SpriteAtlasTexture.Data.class)
public interface SpriteAtlasTextureDataAccessor
{
    @Accessor("width")
    int getWidth();

    @Accessor("height")
    int getHeight();
}
