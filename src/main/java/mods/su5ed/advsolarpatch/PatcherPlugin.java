package mods.su5ed.advsolarpatch;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions("mods.su5ed.advsolarpatch.asm")
public class PatcherPlugin implements IFMLLoadingPlugin {

    /**
     * 需委托给父加载器的 Mixin 相关包前缀。
     * 前七项与 Mixin 0.8.5 内置 LaunchWrapper 引导注册的排除项一致；
     * org.objectweb.asm. 固定走 LaunchWrapper 自带的 ASM，避免不同 jar 中的 ASM 副本分裂。
     */
    private static final String[] MIXIN_CLASS_LOADER_EXCLUSIONS = {
            "org.spongepowered.asm.launch.",
            "org.spongepowered.asm.service.",
            "org.spongepowered.asm.mixin.",
            "org.spongepowered.asm.logging.",
            "org.spongepowered.asm.util.",
            "org.spongepowered.asm.lib.",
            "org.spongepowered.tools.",
            "org.objectweb.asm."
    };

    public PatcherPlugin() {
        // 1.12.2 没有官方 Mixin 前置；本模组将 Mixin 0.8.5 内嵌进产物 jar，此处自行引导。
        // Mixin 的 LaunchWrapper 引导会把 org.spongepowered 包注册为类加载器排除项（findClass 委托给父加载器），
        // 而本 jar 位于 LaunchClassLoader 的 mod 类路径，父加载器默认不可见；不先注入会在引导中途抛出 NoClassDefFoundError，
        // 且先加载与后加载的 Mixin 类分属两个加载器会造成类身份分裂。
        injectSelfIntoAppClassLoader();
        // 必须在任何 org.spongepowered 类加载之前注册排除项，使 Mixin 类从第一步起统一由父加载器加载
        registerMixinClassLoaderExclusions();
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.advsolarpatch.json");
    }

    /**
     * 通过反射 URLClassLoader.addURL 把本模组 jar 注入系统类加载器，使父加载器可见内嵌的 Mixin 类。
     * 这是 1.12.2 上内嵌 Mixin 的不得已手段（合规的反射例外）；1.12.2 运行时为 Java 8，系统类加载器必为 URLClassLoader。
     */
    private static void injectSelfIntoAppClassLoader() {
        URL self = PatcherPlugin.class.getProtectionDomain().getCodeSource().getLocation();
        if (!(ClassLoader.getSystemClassLoader() instanceof URLClassLoader)) {
            throw new IllegalStateException("系统类加载器不是 URLClassLoader，无法注入 AdvSolarPatch jar 以引导内嵌 Mixin");
        }
        URLClassLoader appClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        try {
            // 开发环境重复实例化插件等场景下 jar 可能已在系统类路径中，避免重复注入
            if (!Arrays.asList(appClassLoader.getURLs()).contains(self)) {
                Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                addURL.setAccessible(true);
                addURL.invoke(appClassLoader, self);
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("无法将 AdvSolarPatch jar 注入系统类加载器以引导内嵌 Mixin", e);
        }
    }

    /** 注册 Mixin 相关包的类加载器排除项；目标集合去重，重复注册幂等 */
    private static void registerMixinClassLoaderExclusions() {
        for (String exclusion : MIXIN_CLASS_LOADER_EXCLUSIONS) {
            Launch.classLoader.addClassLoaderExclusion(exclusion);
        }
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
