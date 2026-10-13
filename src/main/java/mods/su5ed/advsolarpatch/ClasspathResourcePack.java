package mods.su5ed.advsolarpatch;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.ModContainer;

/**
 * 以类加载器为后端的资源包，仅服务 advsolarpatch 域。
 *
 * <p>本模组的 ModContainer 是 DummyModContainer（coremod 附带的容器），FML 不会为它绑定
 * 文件系统资源包，导致 assets/advsolarpatch/lang 下的语言文件不进 LanguageManager。
 * 本类直接从类加载器读取资源：开发环境覆盖 classes 与 resources 两个输出目录，
 * 生产环境即模组 jar 本身，两种环境行为一致。仅在客户端被实例化（由 getCustomResourcePackClass 触发）。
 */
public class ClasspathResourcePack implements IResourcePack {

    /** 本包服务的资源域，与模组 id 一致 */
    private static final String DOMAIN = "advsolarpatch";

    /** 1.12.2 对应的资源包格式号，避免被 FML 包装为 LegacyV2Adapter */
    private static final int PACK_FORMAT = 3;

    // FML 以 getConstructor(ModContainer.class) 精确按参数类型反射实例化，构造器签名必须保持为 ModContainer
    public ClasspathResourcePack(ModContainer container) {
        // 无需持有容器状态
    }

    private static String toPath(ResourceLocation location) {
        return "assets/" + location.getNamespace() + '/' + location.getPath();
    }

    @Override
    public InputStream getInputStream(ResourceLocation location) throws IOException {
        InputStream stream = ClasspathResourcePack.class.getClassLoader().getResourceAsStream(toPath(location));
        if (stream == null) {
            throw new FileNotFoundException(toPath(location));
        }
        return stream;
    }

    @Override
    public boolean resourceExists(ResourceLocation location) {
        return ClasspathResourcePack.class.getClassLoader().getResource(toPath(location)) != null;
    }

    @Override
    public Set<String> getResourceDomains() {
        return Collections.singleton(DOMAIN);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer serializer, String sectionName) {
        // 仅提供 pack 段；language 等其余段不存在，返回 null（LanguageManager 会对错误类型告警）
        if (!"pack".equals(sectionName)) {
            return null;
        }
        return (T) new PackMetadataSection(new TextComponentString("AdvSolarPatch resources"), PACK_FORMAT);
    }

    @Override
    public BufferedImage getPackImage() throws IOException {
        InputStream stream = ClasspathResourcePack.class.getClassLoader().getResourceAsStream("pack.png");
        if (stream == null) {
            throw new FileNotFoundException("pack.png");
        }
        return javax.imageio.ImageIO.read(stream);
    }

    @Override
    public String getPackName() {
        return "AdvSolarPatch";
    }
}
