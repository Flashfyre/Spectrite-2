package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.client.SpectriteClient;
import com.flashfyre.spectrite.client.resourcePack.SpectriteResourcePack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ReloadableResourceManagerImpl.class)
public abstract class ReloadableResourceManagerImplMixin implements ResourceManager
{
    @Shadow
    @Final
    private ResourceType type;

    @ModifyArg(method = "reload", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/resource/LifecycledResourceManagerImpl;<init>(Lnet/minecraft/resource/ResourceType;Ljava/util/List;)V"), index = 1)
    private List<ResourcePack> spectrite$onReload(List<ResourcePack> packs)
    {
        if (this.type != ResourceType.CLIENT_RESOURCES)
            return packs;

        SpectriteClient mod = SpectriteClient.CLIENT_INSTANCE;
        List<ResourcePack> list = new ArrayList<>(packs);
        list.add(mod.resourcePack = new SpectriteResourcePack(mod));

        return list;
    }
}