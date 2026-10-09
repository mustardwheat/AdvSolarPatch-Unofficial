package mods.su5ed.advsolarpatch.mixin;

import com.chocohead.advsolar.tiles.TileEntitySolarPanel;
import ic2.core.init.Localization;
import mods.su5ed.advsolarpatch.MaxOutputConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 将 ASP 太阳能的最大输出上限改为读取 MaxOutputConfig 中的配置值。
 *
 * <p>ASP 没有独立的 maxOutput 字段：实际输出由 getOfferedEnergy 按 tierPower
 * （构造器中以 EnergyNet.getPowerFromTier(tier) 算出）封顶，GUI 的 Max Output 文本
 * 在 getMaxOutput 中按同一换算即时生成。两个方法均以 HEAD 注入整体替换返回值。
 * ASP 的类在生产环境不会被混淆，关闭 remap（无需 refmap）。
 */
@Mixin(value = TileEntitySolarPanel.class, remap = false)
public abstract class TileEntitySolarPanelMixin {

    @Shadow
    public int storage;

    /**
     * 实际输出上限读取点：原实现为 min(storage, tierPower)。
     */
    @Inject(method = "getOfferedEnergy", at = @At("HEAD"), cancellable = true)
    private void advsolarpatch$capOfferedEnergy(CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(Math.min(this.storage, MaxOutputConfig.getMaxOutput((TileEntitySolarPanel) (Object) this)));
    }

    /**
     * GUI 展示的 Max Output 文本，与配置值保持一致；格式与 ASP 原实现相同。
     */
    @Inject(method = "getMaxOutput", at = @At("HEAD"), cancellable = true)
    private void advsolarpatch$capMaxOutputText(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(String.format(
                "%s %.0f %s",
                Localization.translate("advanced_solar_panels.gui.maxOutput"),
                MaxOutputConfig.getMaxOutput((TileEntitySolarPanel) (Object) this),
                Localization.translate("ic2.generic.text.EUt")));
    }
}
