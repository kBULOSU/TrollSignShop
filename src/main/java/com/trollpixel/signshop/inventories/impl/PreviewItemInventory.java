package com.trollpixel.signshop.inventories.impl;

import com.trollpixel.signshop.inventories.CustomInventory;
import com.trollpixel.signshop.model.ChestShop;

public class PreviewItemInventory extends CustomInventory {

    public PreviewItemInventory(ChestShop chestShop) {
        super(3 * 9, "Pré-visualização");

        this.setItem(
                13,
                chestShop.getItemStack()
        );
    }
}