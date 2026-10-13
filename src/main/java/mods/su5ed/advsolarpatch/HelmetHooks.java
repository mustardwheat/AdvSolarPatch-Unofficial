package mods.su5ed.advsolarpatch;

import com.chocohead.advsolar.items.ItemArmourSolarHelmet;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import ic2.core.IC2Potion;
import ic2.core.init.Localization;
import ic2.core.item.ItemTinCan;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * ASP 混合/终混太阳能头盔的量子系功能实现，行为与 SSP 太阳能头盔（com.denfop.ssp）保持一致：
 * 耗电清除负面药水、饥饿时自动进食灌装食物、Alt+模式切换键开关夜视。
 * 夜视在亮处仅关闭效果，不像原版量子头盔那样附加致盲。
 * 整体仅在服务端执行（按键状态经 IC2 网络同步至服务端，与 SSP 头盔的按键方案相同）。
 */
public final class HelmetHooks {

    /** 各负面药水对应的清除耗电量（EU）；辐射与原版量子头盔一致为 10000，倍率随药水等级放大 */
    private static final Map<Potion, Integer> POTION_REMOVAL_COST = new IdentityHashMap<>();

    /** 水下补气与进食的单次耗电量（EU），与 SSP 头盔一致 */
    private static final double USE_COST = 1000.0;

    static {
        POTION_REMOVAL_COST.put(IC2Potion.radiation, 10000);
        POTION_REMOVAL_COST.put(MobEffects.POISON, 400);
        POTION_REMOVAL_COST.put(MobEffects.WITHER, 500);
        POTION_REMOVAL_COST.put(MobEffects.SLOWNESS, 300);
        POTION_REMOVAL_COST.put(MobEffects.NAUSEA, 1000);
        POTION_REMOVAL_COST.put(MobEffects.HUNGER, 300);
        POTION_REMOVAL_COST.put(MobEffects.WEAKNESS, 400);
    }

    private HelmetHooks() {
    }

    public static void onArmorTick(ItemArmourSolarHelmet.SolarHelmetTypes type, World world, EntityPlayer player, ItemStack stack) {
        // 高级太阳能头盔不属于量子系防具，不处理
        if (type == ItemArmourSolarHelmet.SolarHelmetTypes.ADVANCED || !IC2.platform.isSimulating()) {
            return;
        }

        // 耗电清除负面药水效果
        for (PotionEffect effect : new LinkedList<>(player.getActivePotionEffects())) {
            Potion potion = effect.getPotion();
            Integer cost = POTION_REMOVAL_COST.get(potion);
            if (cost != null) {
                int total = cost * Math.max(1, effect.getAmplifier() + 1);
                if (ElectricItem.manager.canUse(stack, total)) {
                    ElectricItem.manager.use(stack, total, null);
                    IC2.platform.removePotion(player, potion);
                }
            }
        }

        // 自动进食：饥饿时耗电食用背包中的灌装食物
        if (ElectricItem.manager.canUse(stack, USE_COST) && player.getFoodStats().needFood()) {
            int slot = -1;
            for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
                ItemStack slotStack = player.inventory.mainInventory.get(i);
                if (!StackUtil.isEmpty(slotStack) && slotStack.getItem() == ItemName.filled_tin_can.getInstance()) {
                    slot = i;
                    break;
                }
            }
            if (slot > -1) {
                ItemStack canStack = player.inventory.mainInventory.get(slot);
                ItemTinCan can = (ItemTinCan) canStack.getItem();
                ActionResult<ItemStack> result = can.onEaten(player, canStack);
                if (StackUtil.isEmpty(result.getResult())) {
                    player.inventory.mainInventory.set(slot, StackUtil.emptyStack);
                }
                if (result.getType() == EnumActionResult.SUCCESS) {
                    ElectricItem.manager.use(stack, USE_COST, null);
                }
            }
        }

        // 夜视开关：Alt + 模式切换键，toggleTimer 防抖
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        byte toggleTimer = nbt.getByte("toggleTimer");
        boolean nightvision = nbt.getBoolean("Nightvision");
        if (IC2.keyboard.isAltKeyDown(player) && IC2.keyboard.isModeSwitchKeyDown(player) && toggleTimer == 0) {
            toggleTimer = 10;
            nightvision = !nightvision;
            nbt.setBoolean("Nightvision", nightvision);
            IC2.platform.messagePlayer(player, nightvision ? "Nightvision enabled." : "Nightvision disabled.", new Object[0]);
        }
        if (toggleTimer > 0) {
            nbt.setByte("toggleTimer", --toggleTimer);
        }

        // 夜视施加：亮处仅关闭夜视，不产生致盲
        if (nightvision && ElectricItem.manager.use(stack, 1.0, player)) {
            BlockPos pos = new BlockPos((int) Math.floor(player.posX), (int) Math.floor(player.posY), (int) Math.floor(player.posZ));
            int skylight = world.getLightFromNeighbors(pos);
            if (skylight > 8) {
                IC2.platform.removePotion(player, MobEffects.NIGHT_VISION);
            } else {
                player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, true, true));
            }
        }
    }
}
