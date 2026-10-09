package mods.su5ed.advsolarpatch;

import com.chocohead.advsolar.tiles.TileEntityAdvancedSolar;
import com.chocohead.advsolar.tiles.TileEntityHybridSolar;
import com.chocohead.advsolar.tiles.TileEntityQuantumSolar;
import com.chocohead.advsolar.tiles.TileEntitySolarPanel;
import com.chocohead.advsolar.tiles.TileEntityUltimateHybridSolar;
import ic2.api.energy.EnergyNet;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * ASP 四种太阳能的最大输出（MaxOutput）配置。
 *
 * <p>配置项写入 ASP 自身的配置文件（config/advanced_solar_panels.cfg）的 solars 组，键名沿用该文件既有风格
 * （含 Hybryd 的历史拼写）；默认值与 ASP 硬编码原值一致，即 EnergyNet.getPowerFromTier(tier) 的结果
 * （tier 1 对应 32，tier 2 对应 128，tier 3 对应 512，tier 5 对应 8192）。
 */
public final class MaxOutputConfig {

    private static final String SOLARS = "solars";

    /** 高级太阳能默认最大输出（EU/t），对应 tier 1 */
    private static final int DEFAULT_ADVANCED_MAX_OUTPUT = 32;
    /** 混合太阳能默认最大输出（EU/t），对应 tier 2 */
    private static final int DEFAULT_HYBRID_MAX_OUTPUT = 128;
    /** 终极混合太阳能默认最大输出（EU/t），对应 tier 3 */
    private static final int DEFAULT_ULTIMATE_HYBRID_MAX_OUTPUT = 512;
    /** 量子太阳能默认最大输出（EU/t），对应 tier 5 */
    private static final int DEFAULT_QUANTUM_MAX_OUTPUT = 8192;

    private static double advancedMaxOutput = DEFAULT_ADVANCED_MAX_OUTPUT;
    private static double hybridMaxOutput = DEFAULT_HYBRID_MAX_OUTPUT;
    private static double ultimateHybridMaxOutput = DEFAULT_ULTIMATE_HYBRID_MAX_OUTPUT;
    private static double quantumMaxOutput = DEFAULT_QUANTUM_MAX_OUTPUT;

    private MaxOutputConfig() {
    }

    /**
     * 从 ASP 的配置文件追加读取四个 MaxOutput 键，缺失时以默认值写入。
     * 由 ConfigsMixin 在 ASP 的 Configs.loadConfig 返回处调用，此时 ASP 已完成首次保存。
     * Forge 的 Configuration 构造器会自动执行 load，无需显式调用。
     */
    public static void load(File configFile) {
        Configuration config = new Configuration(configFile);
        try {
            advancedMaxOutput = config.get(SOLARS, "AdvancedSPMaxOutput", DEFAULT_ADVANCED_MAX_OUTPUT).getInt(DEFAULT_ADVANCED_MAX_OUTPUT);
            hybridMaxOutput = config.get(SOLARS, "HybrydSPMaxOutput", DEFAULT_HYBRID_MAX_OUTPUT).getInt(DEFAULT_HYBRID_MAX_OUTPUT);
            ultimateHybridMaxOutput = config.get(SOLARS, "UltimateHSPMaxOutput", DEFAULT_ULTIMATE_HYBRID_MAX_OUTPUT).getInt(DEFAULT_ULTIMATE_HYBRID_MAX_OUTPUT);
            quantumMaxOutput = config.get(SOLARS, "QuantumSPMaxOutput", DEFAULT_QUANTUM_MAX_OUTPUT).getInt(DEFAULT_QUANTUM_MAX_OUTPUT);
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    /**
     * 返回给定太阳能实例的最大输出上限（EU/t）。
     * 未知子类回退为 ASP 原始行为（按 tier 换算），保证行为不退化。
     */
    public static double getMaxOutput(TileEntitySolarPanel panel) {
        if (panel instanceof TileEntityQuantumSolar) {
            return quantumMaxOutput;
        }
        if (panel instanceof TileEntityUltimateHybridSolar) {
            return ultimateHybridMaxOutput;
        }
        if (panel instanceof TileEntityHybridSolar) {
            return hybridMaxOutput;
        }
        if (panel instanceof TileEntityAdvancedSolar) {
            return advancedMaxOutput;
        }
        return EnergyNet.instance.getPowerFromTier(panel.getSourceTier());
    }
}
