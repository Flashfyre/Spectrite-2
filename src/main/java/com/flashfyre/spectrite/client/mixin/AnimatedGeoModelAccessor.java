package com.flashfyre.spectrite.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

@Environment(EnvType.CLIENT)
@Mixin(AnimatedGeoModel.class)
public interface AnimatedGeoModelAccessor
{
    @Accessor("currentModel")
    GeoModel getCurrentModel();
}
