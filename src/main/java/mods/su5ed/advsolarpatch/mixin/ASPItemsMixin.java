package mods.su5ed.advsolarpatch.mixin;

import com.chocohead.advsolar.ASP_Items;
import com.chocohead.advsolar.items.ItemArmourSolarHelmet;
import com.chocohead.advsolar.items.ItemCraftingThings;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

/**
 * 移除 ASP 的双石台阶物品（advanced_solar_panels:double_stone_slab）注册。
 *
 * <p>该物品仅是给原版双石台阶方块包了一层 ItemBlock，放置逻辑存在复制相邻方块的缺陷，
 * 且生态中已有大量模组提供同类方块，没有保留价值。移除注册后旧的 ASM 修复（ItemDoubleSlabVisitor）
 * 同步删除。配方侧由 ConfigsMixin 强制关闭 canCraftDoubleSlabs 兜底。
 */
@Mixin(value = ASP_Items.class, remap = false)
public abstract class ASPItemsMixin {

    /**
     * 重建物品注册流程，跳过 DOUBLE_STONE_SLAB。
     * 逐条注册语句位于同一方法内，无法以注入跳过单条语句；ASP 已停止更新，覆写为最直白可靠的方案。
     */
    @Overwrite(remap = false)
    static void buildItems(Side side) {
        ASP_Items.ADVANCED_SOLAR_HELMET.setInstance(new ItemArmourSolarHelmet(ItemArmourSolarHelmet.SolarHelmetTypes.ADVANCED));
        ASP_Items.HYBRID_SOLAR_HELMET.setInstance(new ItemArmourSolarHelmet(ItemArmourSolarHelmet.SolarHelmetTypes.HYBRID));
        ASP_Items.ULTIMATE_HYBRID_SOLAR_HELMET.setInstance(new ItemArmourSolarHelmet(ItemArmourSolarHelmet.SolarHelmetTypes.ULTIMATE));
        ASP_Items.CRAFTING.setInstance(new ItemCraftingThings());
        // DOUBLE_STONE_SLAB 不再注册
        if (side == Side.CLIENT) {
            advsolarpatch$registerModels();
        }
    }

    /** 与 ASP 原 doModelGuf 一致，但跳过未注册的枚举项（DOUBLE_STONE_SLAB），避免空指针 */
    @Unique
    @SideOnly(Side.CLIENT)
    private static void advsolarpatch$registerModels() {
        for (ASP_Items item : ASP_Items.values()) {
            if (item.getInstance() != null) {
                item.getInstance().registerModels(null);
            }
        }
    }
}
