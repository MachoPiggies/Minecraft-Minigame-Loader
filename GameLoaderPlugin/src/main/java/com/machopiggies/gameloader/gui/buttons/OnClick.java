package com.machopiggies.gameloader.gui.buttons;

import com.machopiggies.gameloader.gui.MenuInterface;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface OnClick {
    void onClick(MenuInterface i, InventoryClickEvent e);
}
