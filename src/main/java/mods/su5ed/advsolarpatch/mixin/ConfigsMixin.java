package mods.su5ed.advsolarpatch.mixin;

import mods.su5ed.advsolarpatch.MaxOutputConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

/**
 * 在 ASP 的 Configs.loadConfig 返回处追加读取 MaxOutput 配置项。
 * 目标类 Configs 为包私有，无法直接引用，故以字符串指定目标；
 * ASP 的类在生产环境不会被混淆，关闭 remap（无需 refmap）。
 */
@Mixin(targets = "com.chocohead.advsolar.Configs", remap = false)
public abstract class ConfigsMixin {

    @Inject(method = "loadConfig", at = @At("RETURN"))
    private static void advsolarpatch$loadMaxOutput(File config, boolean client, CallbackInfo ci) {
        MaxOutputConfig.load(config);
    }
}
