package com.trollpixel.signshop.inventories;

import lombok.Getter;

public class Container {

    @Getter
    protected ICustomInventory parent;

    public void init(ICustomInventory parent) {
        this.parent = parent;
    }

}