package com.flashfyre.spectrite.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.nio.channels.WritableByteChannel;

@Environment(EnvType.CLIENT)
@Mixin(NativeImage.class)
public interface NativeImageAccessor
{
    @Invoker("write")
    boolean invokeWrite(WritableByteChannel writableByteChannel);
}