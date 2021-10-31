package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.client.etc.IOrigin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import software.bernie.geckolib3.geo.raw.pojo.Cube;
import software.bernie.geckolib3.geo.raw.pojo.ModelProperties;
import software.bernie.geckolib3.geo.render.built.GeoCube;

@Environment(EnvType.CLIENT)
@Mixin(GeoCube.class)
public class GeoCubeMixin implements IOrigin
{
    private Vec3f origin;

    @Override
    public Vec3f getOrigin()
    {
        return origin;
    }

    @Override
    public void setOrigin(Vec3f origin)
    {
        this.origin = origin;
    }

    @Inject(method = "createFromPojoCube", at = @At(value = "INVOKE",
            target = "Lsoftware/bernie/geckolib3/geo/raw/pojo/Cube;getUv()Lsoftware/bernie/geckolib3/geo/raw/pojo/UvUnion;"),
            locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private static void injectCreateFromPojoCubeOrigin(Cube cubeIn, ModelProperties properties, Double boneInflate, Boolean mirror,
                                                       CallbackInfoReturnable<GeoCube> cir, GeoCube cube)
    {
        final double[] originValues = cubeIn.getOrigin();
        ((IOrigin) cube).setOrigin(new Vec3f((float) originValues[0], (float) originValues[1], (float) originValues[2]));
    }
}
