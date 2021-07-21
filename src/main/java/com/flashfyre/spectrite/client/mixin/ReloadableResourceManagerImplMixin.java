package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.client.SpectriteClient;
import com.flashfyre.spectrite.client.resourcePack.SpectriteResourcePack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.*;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Environment(EnvType.CLIENT)
@Mixin(ReloadableResourceManagerImpl.class)
public abstract class ReloadableResourceManagerImplMixin implements ReloadableResourceManager
{
    @Shadow
    @Final
    private ResourceType type;

    @Shadow
    public abstract void addPack(ResourcePack resourcePack);

    @Inject(method = "reload", at = @At(value = "RETURN", shift = At.Shift.BEFORE))
    private void spectrite$onReload(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs,
                                    CallbackInfoReturnable<ResourceReload> cir)
    {
        if (this.type != ResourceType.CLIENT_RESOURCES)
            return;

        var mod = SpectriteClient.CLIENT_INSTANCE;
        mod.log("Inject generated resource pack.");
        this.addPack(mod.resourcePack = new SpectriteResourcePack(mod));
    }
}