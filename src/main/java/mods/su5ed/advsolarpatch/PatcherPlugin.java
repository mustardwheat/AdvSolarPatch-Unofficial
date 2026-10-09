package mods.su5ed.advsolarpatch;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions("mods.su5ed.advsolarpatch.asm")
public class PatcherPlugin implements IFMLLoadingPlugin {

    public PatcherPlugin() {
        // 1.12.2 没有官方 Mixin 前置；本模组将 Mixin 0.8.5 内嵌进产物 jar，此处按经典顺序自行引导
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.advsolarpatch.json");
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
