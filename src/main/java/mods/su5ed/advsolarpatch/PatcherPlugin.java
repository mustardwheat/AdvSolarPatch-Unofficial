package mods.su5ed.advsolarpatch;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * AdvSolarPatch 的 coremod 插件。
 * 职责仅剩承载 ClassTransformer（IC2 2.8.191+ 的 InvSlot 构造器描述符改写，JVM 校验器不允许
 * 重定向 super() 构造调用，无法用 Mixin 表达）。Mixin 由 MixinBooter 提供：
 * 本类实现 IEarlyMixinLoader 向 MixinBooter 注册配置（开发与生产环境均生效），
 * 产物 jar 的 manifest MixinConfigs 属性作为兜底；本模组不再内嵌与自行引导 Mixin。
 */
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions("mods.su5ed.advsolarpatch.asm")
public class PatcherPlugin implements IFMLLoadingPlugin, IEarlyMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList("mixins.advsolarpatch.json");
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[] { ClassTransformer.class.getName() };
    }

    @Override
    public String getModContainerClass() {
        return PluginModContainer.class.getName();
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
