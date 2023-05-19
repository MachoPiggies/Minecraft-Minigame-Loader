package com.machopiggies.gameloaderapi.util;

import com.machopiggies.gameloaderapi.util.objects.GlowEnchant;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;

public class ItemBuilder {
    private ItemStack stack;
    private ItemMeta meta;
    private List<String> lore;

    private static final GlowEnchant glowEnchant = new GlowEnchant();

    public ItemBuilder(Material material) {
        stack = new ItemStack(material);
        meta = stack.getItemMeta();
        lore = new ArrayList<>();
    }

    public ItemBuilder(ItemStack stack) {
        this.stack = stack;
        meta = stack.getItemMeta();
        if (meta.hasLore()) {
            lore = meta.getLore();
        } else {
            lore = new ArrayList<>();
        }
    }

    // TYPE //

    public Material getType() {
        return stack.getType();
    }

    @Deprecated
    public int getTypeId() {
        return stack.getTypeId();
    }

    public ItemBuilder setType(Material type) {
        stack.setType(type);
        return this;
    }

    @Deprecated
    public ItemBuilder setTypeId(int i) {
        stack.setTypeId(i);
        return this;
    }

    // AMOUNTS //

    public int getAmount() {
        return stack.getAmount();
    }

    public ItemBuilder setAmount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    public int getMaxStackSize() {
        return stack.getMaxStackSize();
    }

    // DURABILITY //

    public short getDurability() {
        return stack.getDurability();
    }

    public ItemBuilder setDurability(short durability) {
        stack.setDurability(durability);
        return this;
    }

    // ENCHANTS //

    public Map<Enchantment, Integer> getEnchants() {
        return stack.getEnchantments();
    }

    public boolean containsEnchantment(Enchantment enchant) {
        return stack.containsEnchantment(enchant);
    }

    public int getEnchantmentLevel(Enchantment enchant) {
        return stack.getEnchantmentLevel(enchant);
    }

    public ItemBuilder addEnchantment(Enchantment enchant, int level) {
        stack.addEnchantment(enchant, level);
        return this;
    }

    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchants) {
        stack.addEnchantments(enchants);
        return this;
    }

    public ItemBuilder addUnsafeEnchantment(Enchantment enchant, int level) {
        stack.addUnsafeEnchantment(enchant, level);
        return this;
    }

    public ItemBuilder addUnsafeEnchantments(Map<Enchantment, Integer> enchants) {
        stack.addUnsafeEnchantments(enchants);
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment enchant) {
        stack.removeEnchantment(enchant);
        return this;
    }

    // MISC //

    public MaterialData getData() {
        return stack.getData();
    }

    public boolean isSimilar(ItemStack stack) {
        ItemStack local = stack.clone();
        local.setItemMeta(meta);
        return local.isSimilar(stack);
    }

    public boolean hasItemMeta() {
        return true;
    }

    // NAMING //

    public boolean hasName() {
        return meta.hasDisplayName();
    }

    public String getName() {
        return meta.getDisplayName();
    }

    public ItemBuilder setDisplayName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    // LORE //

    public List<String> getLore() {
        return lore;
    }

    public String getLoreLine(int index) {
        if (lore.size() > index) {
            return lore.get(index);
        } else {
            throw new ArrayIndexOutOfBoundsException("Index: " + index + ", Size: " + lore.size());
        }
    }

    public ItemBuilder addLore(List<String> list) {
        lore.addAll(list);
        return this;
    }

    public ItemBuilder addLore(boolean bool, List<String> list) {
        if (bool) {
            lore.addAll(list);
        }
        return this;
    }

    public ItemBuilder addLore(String... list) {
        lore.addAll(Arrays.asList(list));
        return this;
    }

    public ItemBuilder addLore(boolean bool, String... list) {
        if (bool) {
            lore.addAll(Arrays.asList(list));
        }
        return this;
    }

    public ItemBuilder addLore(List<String> lines, LoreFunction list) {
        if (list == null) return this;
        for (String line : lines) {
            lore.addAll(Arrays.asList(list.parseLore(line)));
        }
        return this;
    }

    public ItemBuilder addLore(String[] lines, LoreFunction list) {
        if (list == null) return this;
        for (String line : lines) {
            lore.addAll(Arrays.asList(list.parseLore(line)));
        }
        return this;
    }

    public ItemBuilder addLore(boolean bool, List<String> lines, LoreFunction list) {
        if (!bool) return this;
        return addLore(lines, list);
    }

    public ItemBuilder addLoreLine(int index, String string) {
        if (lore.size() > index) {
            lore.add(index, string);
            return this;
        } else {
            throw new ArrayIndexOutOfBoundsException("Index: " + index + ", Size: " + lore.size());
        }
    }

    public ItemBuilder setLore(List<String> list) {
        lore = list;
        return this;
    }

    public ItemBuilder setLore(boolean bool, List<String> list) {
        if (bool) {
            lore = list;
        }
        return this;
    }

    public ItemBuilder setLore(String... list) {
        lore = new ArrayList<>(Arrays.asList(list));
        return this;
    }

    public ItemBuilder setLore(boolean bool, String... list) {
        if (bool) {
            lore = new ArrayList<>(Arrays.asList(list));
        }
        return this;
    }

    public ItemBuilder setLoreLine(int index, String string) {
        if (lore.size() > index) {
            lore.set(index, string);
            return this;
        } else {
            throw new ArrayIndexOutOfBoundsException("Index: " + index + ", Size: " + lore.size());
        }
    }

    public ItemBuilder removeLoreLine(int index) {
        if (lore.size() > index) {
            lore.remove(index);
            return this;
        } else {
            throw new ArrayIndexOutOfBoundsException("Index: " + index + ", Size: " + lore.size());
        }
    }

    // GLOW //

    public ItemBuilder addGlow() {
        if (stack.getEnchantments().size() <= 0) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            stack.addUnsafeEnchantment(glowEnchant, 1);
        }
        return this;
    }

    public ItemBuilder addGlow(boolean bool) {
        if (bool) {
            if (stack.getEnchantments().size() <= 0) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                stack.addUnsafeEnchantment(glowEnchant, 1);
            }
        }
        return this;
    }

    public ItemBuilder removeGlow() {
        if (stack.getEnchantments().size() <= 1 && (stack.containsEnchantment(Enchantment.PROTECTION_PROJECTILE) && stack.getEnchantmentLevel(Enchantment.PROTECTION_PROJECTILE) == 1)) {
            stack.removeEnchantment(glowEnchant);
            meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    // UNBREAKABLE //

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        meta.spigot().setUnbreakable(unbreakable);
        return this;
    }

    public boolean isUnbreakable() {
        return meta.spigot().isUnbreakable();
    }

    // FLAGS //

    public Set<ItemFlag> getItemFlags() {
        return meta.getItemFlags();
    }

    public ItemBuilder addItemFlags(ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder setItemFlags(ItemFlag... flags) {
        meta.removeItemFlags(ItemFlag.values());
        meta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder removeItemFlags(ItemFlag... flags) {
        meta.removeItemFlags(flags);
        return this;
    }

    // LEGACY //

    public ItemBuilder amount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    public ItemBuilder durability(short durability) {
        stack.setDurability(durability);
        return this;
    }

    public ItemBuilder name(String name) {
        return applyMetaChange(itemMeta -> itemMeta.setDisplayName(name));
    }

    public ItemBuilder lore(String... lore) {
        return lore(Arrays.asList(lore));
    }

    public ItemBuilder lore(List<String> lore) {
        return applyMetaChange(itemMeta -> itemMeta.setLore(lore));
    }

    public ItemBuilder flags(ItemFlag... flags) {
        return applyMetaChange(itemMeta -> itemMeta.addItemFlags(flags));
    }

    public ItemBuilder glow(boolean opt) {
        GlowEnchant glow = new GlowEnchant();

        if (!GlowEnchant.registered) {
            try {
                Field f = Enchantment.class.getDeclaredField("acceptingNew");
                f.setAccessible(true);
                f.set(null, true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Enchantment.registerEnchantment(glow);
            } catch (IllegalArgumentException ignored) { }
            GlowEnchant.registered = true;
        }
        if (opt) {
            return addUnsafeEnchantment(glow, 0);
        } else {
            return removeEnchantment(glow);
        }
    }

    public ItemBuilder applyDataChange(Consumer<MaterialData> dataConsumer) {
        MaterialData data = stack.getData();
        dataConsumer.accept(data);
        stack.setData(data);
        return this;
    }

    public ItemBuilder applyMetaChange(Consumer<ItemMeta> metaConsumer) {
        metaConsumer.accept(meta);
        stack.setItemMeta(meta);
        return this;
    }

    // BUILD //

    public ItemStack build() {
        ItemStack local = stack.clone();
        ItemMeta localM = meta.clone();
        localM.setLore(lore);
        local.setItemMeta(localM);
        local.addUnsafeEnchantments(stack.getEnchantments());
        return local;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ItemBuilder) {
            ItemBuilder builder = (ItemBuilder) obj;
            return stack.equals(builder.stack) && meta.equals(builder.meta) && lore.equals(builder.lore);
        } else {
            return super.equals(obj);
        }
    }

    @Override
    public String toString() {
        return build().toString();
    }

    @Override
    public Object clone() {
        try {
            ItemBuilder builder = new ItemBuilder(Material.AIR);
            builder.stack = stack;
            builder.meta = meta;
            builder.lore = lore;
            return builder;
        } catch (Exception e) {
            try {
                return super.clone();
            } catch (CloneNotSupportedException ignored) { }
        }
        return null;
    }

    public static class PlayerSkull {
        private ItemStack stack;
        private String playerName;

        public PlayerSkull(OfflinePlayer player) {
            this(player.getName());
        }

        public PlayerSkull(String playerName) {
            stack = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
            SkullMeta meta = (SkullMeta) stack.getItemMeta();
            meta.setOwner(playerName);
            stack.setItemMeta(meta);
        }

        private PlayerSkull(ItemStack stack) {
            this.stack = stack;
        }

        public ItemStack getStack() {
            return stack;
        }

        public ItemBuilder getStackAsItemBuilder() {
            return new ItemBuilder(stack);
        }

        public String getPlayerName() {
            return playerName;
        }

        public void setPlayerName(String playerName) {
            this.playerName = playerName;

            stack = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
            SkullMeta meta = (SkullMeta) stack.getItemMeta();
            meta.setOwner(playerName);
            stack.setItemMeta(meta);
        }
    }

    @FunctionalInterface
    public interface LoreFunction {
        String[] parseLore(String string);
    }
}