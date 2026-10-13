package mods.su5ed.advsolarpatch;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * AdvSolarPatch 的 coremod 插件。
 * 职责仅剩承载 ClassTransformer（IC2 2.8.191+ 的 InvSlot 构造器描述符改写，JVM 校验器不允许
 * 重定向 super() 构造调用，无法用 Mixin 表达）；Mixin 配置的引导交由 MixinBooter 完成，
 * 本模组不再内嵌与自行引导 Mixin。
 */
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions("mods.su5ed.advsolarpatch.asm")
public class PatcherPlugin implements IFMLLoadingPlugin {

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
