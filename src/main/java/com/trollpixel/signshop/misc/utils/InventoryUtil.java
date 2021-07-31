package com.trollpixel.signshop.misc.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InventoryUtil {

    public static String serializeContents(ItemStack[] contents) {
        if (contents == null) {
            return null;
        }

        BukkitObjectOutputStream dataOutput = null;

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(contents.length);

            for (int i = 0; i < contents.length; i++) {
                dataOutput.writeInt(i);
                dataOutput.writeObject(contents[i]);
            }

            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dataOutput != null) {
                try {
                    dataOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static ItemStack[] deserializeContents(String str) {

        if (str == null || str.isEmpty()) {
            return null;
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(str));

        try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            int size = dataInput.readInt();

            ItemStack[] contents = new ItemStack[size];

            for (int i = 0; i < size; i++) {
                try {
                    contents[dataInput.readInt()] = (ItemStack) dataInput.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            return contents;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static Inventory copy(Inventory inventory) {
        Inventory inv = Bukkit.createInventory(null, inventory.getSize(), inventory.getTitle());

        ItemStack[] orginal = inventory.getContents();
        ItemStack[] clone = new ItemStack[orginal.length];

        for (int i = 0; i < orginal.length; i++) {
            if (orginal[i] != null) {
                clone[i] = orginal[i].clone();
            }
        }

        inv.setContents(clone);

        return inv;
    }

    public static boolean fits(Inventory inventory, ItemStack... stacks) {
        Inventory clonedInventory = InventoryUtil.copy(inventory);

        ItemStack[] clone = new ItemStack[stacks.length];

        for (int i = 0; i < stacks.length; i++) {
            if (stacks[i] != null) {
                clone[i] = stacks[i].clone();
            }
        }

        return clonedInventory.addItem(clone).isEmpty();
    }

    public static int removeItems(Inventory inventory, ItemStack item, int amount) {
        int removedItems = 0;

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack targetItem = inventory.getItem(slot);

            if (targetItem == null || !isSimilar(item, targetItem)) {
                continue;
            }

            if (amount == -1) {
                removedItems += targetItem.getAmount();

                inventory.setItem(slot, null);
                continue;
            }

            if (amount < targetItem.getAmount()) {
                removedItems += amount;

                targetItem.setAmount(targetItem.getAmount() - amount);
                break;
            }

            removedItems += targetItem.getAmount();
            amount -= targetItem.getAmount();
            inventory.setItem(slot, null);
        }

        return removedItems;
    }

    public static int countItems(Inventory inventory, ItemStack item) {
        int sum = 0;

        for (ItemStack itemTarget : inventory.getContents()) {
            if (isSimilar(itemTarget, item)) {
                sum += itemTarget.getAmount();
            }
        }

        return sum;
    }

    public static Map<Integer, ItemStack> addAllItems(final Inventory inventory, boolean simulate, final ItemStack... items) {
        final Inventory fakeInventory = Bukkit.getServer().createInventory(null, inventory.getType());
        fakeInventory.setContents(inventory.getContents());
        Map<Integer, ItemStack> overFlow = addOversizedItems(fakeInventory, items);
        if (overFlow.isEmpty()) {
            if (!simulate) {
                addOversizedItems(inventory, items);
            }
            return null;
        }
        return addOversizedItems(fakeInventory, items);
    }

    private static Map<Integer, ItemStack> addOversizedItems(final Inventory inventory, final ItemStack... items) {
        final Map<Integer, ItemStack> leftover = new HashMap<>();

        final ItemStack[] combined = new ItemStack[items.length];
        for (ItemStack item : items) {
            if (item == null || item.getAmount() < 1) {
                continue;
            }
            for (int j = 0; j < combined.length; j++) {
                if (combined[j] == null) {
                    combined[j] = item.clone();
                    break;
                }
                if (isSimilar(combined[j], item)) {
                    combined[j].setAmount(combined[j].getAmount() + item.getAmount());
                    break;
                }
            }
        }

        for (int i = 0; i < combined.length; i++) {
            final ItemStack item = combined[i];
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            while (true) {
                final int maxAmount = Math.max(0, item.getType().getMaxStackSize());
                final int firstPartial = firstPartial(inventory, item, maxAmount);

                if (firstPartial == -1) {
                    final int firstFree = inventory.firstEmpty();

                    if (firstFree == -1) {
                        leftover.put(i, item);
                        break;
                    } else {
                        if (item.getAmount() > maxAmount) {
                            final ItemStack stack = item.clone();
                            stack.setAmount(maxAmount);
                            inventory.setItem(firstFree, stack);
                            item.setAmount(item.getAmount() - maxAmount);
                        } else {
                            inventory.setItem(firstFree, item);
                            break;
                        }
                    }
                } else {
                    final ItemStack partialItem = inventory.getItem(firstPartial);

                    final int amount = item.getAmount();
                    final int partialAmount = partialItem.getAmount();

                    if (amount + partialAmount <= maxAmount) {
                        partialItem.setAmount(amount + partialAmount);
                        break;
                    }

                    partialItem.setAmount(maxAmount);
                    item.setAmount(amount + partialAmount - maxAmount);
                }
            }
        }
        return leftover;
    }

    private static int firstPartial(final Inventory inventory, final ItemStack item, final int maxAmount) {
        if (item == null) {
            return -1;
        }
        final ItemStack[] stacks = inventory.getContents();
        for (int i = 0; i < stacks.length; i++) {
            final ItemStack cItem = stacks[i];
            if (cItem != null && cItem.getAmount() < maxAmount && isSimilar(cItem, item)) {
                return i;
            }
        }
        return -1;
    }

    private static boolean isSimilar(ItemStack source, ItemStack anotherStack) {
        if (source == null || anotherStack == null) {
            return false;
        }

        if (source.getTypeId() != anotherStack.getTypeId() ||
                source.getDurability() != anotherStack.getDurability()) {
            return false;
        }

        boolean sourceHasMeta = source.hasItemMeta();
        boolean anotherStackHasMeta = anotherStack.hasItemMeta();

        if (!sourceHasMeta && !anotherStackHasMeta) {
            return true;
        }

        return Bukkit.getItemFactory().equals(source.getItemMeta(), anotherStack.getItemMeta());
    }

}
