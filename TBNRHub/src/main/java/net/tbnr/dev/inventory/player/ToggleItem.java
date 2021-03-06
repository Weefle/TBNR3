package net.tbnr.dev.inventory.player;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.cogzmc.core.effect.inventory.ControlledInventoryButton;
import net.cogzmc.core.player.CPlayer;
import net.tbnr.dev.TBNRHub;
import net.tbnr.dev.inventory.SettingUtils;
import net.tbnr.dev.setting.PlayerSetting;
import net.tbnr.dev.setting.PlayerSettingsManager;
import net.tbnr.dev.setting.SettingChangeException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@EqualsAndHashCode(callSuper = true)
@Data
@Getter(AccessLevel.NONE)
public class ToggleItem extends ControlledInventoryButton {
    protected final PlayerSetting setting;

    @Override
    protected ItemStack getStack(CPlayer player) {
        boolean state = TBNRHub.getInstance().getSettingsManager().getStateFor(setting, player);
        ItemStack itemStack = new ItemStack(Material.INK_SACK);
        itemStack.setDurability(state ? (short) 10 : (short) 8);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName((state ?
                (ChatColor.GREEN.toString() + ChatColor.BOLD + "ON") :
                (ChatColor.RED.toString() + ChatColor.BOLD + "OFF"))
                + " " + ChatColor.GRAY + setting.getName());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    protected boolean canUse(CPlayer player) {return setting.getPermission() == null || player.hasPermission(setting.getPermission());}

    @Override
    protected final void onUse(CPlayer player) {
        if (!canUse(player)) {
            SettingUtils.onSettingDeny(player);
            return;
        }
        PlayerSettingsManager settingsManager = TBNRHub.getInstance().getSettingsManager();
        try {
            settingsManager.toggleStateFor(setting, player);
        } catch (SettingChangeException e) {
            SettingUtils.onSettingDeny(player);
            return;
        }
        SettingUtils.onSettingToggle(player, setting);
    }
}
