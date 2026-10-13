package mods.su5ed.advsolarpatch.mixin;

import com.chocohead.advsolar.items.ItemArmourSolarHelmet;
import ic2.api.item.ElectricItem;
import ic2.api.item.IHazmatLike;
import ic2.core.init.Localization;
import java.util.List;
import mods.su5ed.advsolarpatch.HelmetHooks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 为 ASP 混合/终混太阳能头盔补齐量子系头盔能力（高级头盔不处理，与 IC2 中纳米系定位一致）：
 * 接入 IHazmatLike 辐射防护套装判定、耗电清除负面药水、自动进食罐头、夜视开关，
 * 并在物品信息中显示日/夜间发电功率。功能逻辑统一收敛在 HelmetHooks，mixin 保持薄层。
 */
@Mixin(value = ItemArmourSolarHelmet.class, remap = false)
public abstract class ItemSolarHelmetMixin implements IHazmatLike {

    @Shadow
    @Final
    protected ItemArmourSolarHelmet.SolarHelmetTypes type;

    // 辐射防护接入 IC2 的 IHazmatLike 检查：与其他量子系防具凑齐四件且均有电时，背包内放射物不再施加辐射
    @Override
    public boolean addsProtection(EntityLivingBase entity, EntityEquipmentSlot slot, ItemStack stack) {
        return this.type != ItemArmourSolarHelmet.SolarHelmetTypes.ADVANCED && ElectricItem.manager.getCharge(stack) > 0.0;
    }

    // HEAD 注入使功能不受发电分支的提前返回影响；具体的服务端判定在 HelmetHooks 内完成
    @Inject(method = "onArmorTick", at = @At("HEAD"))
    private void advsolarpatch$onArmorTick(World world, EntityPlayer player, ItemStack stack, CallbackInfo ci) {
        HelmetHooks.onArmorTick(this.type, world, player, stack);
    }

    // 目标类未覆写该方法，此处作为内在方法织入，形成对父类 Item 的覆盖；三档头盔均显示发电信息
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(Localization.translate("advsolarpatch.helmet.genDay", this.type.dayEU));
        tooltip.add(Localization.translate("advsolarpatch.helmet.genNight", this.type.nightEU));
    }
}
