package com.trollpixel.signshop.model;

import com.trollpixel.signshop.location.SerializedLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Getter
public class ChestShop {

    private final SerializedLocation serializedLocation;

    @Setter
    private ItemStack itemStack;

    @Setter
    private String itemName;

    @Setter
    private int amount;

    @Setter
    private double sellPrice;

    @Setter
    private double buyPrice;

}
