package com.objectvolatile.megacorev2.gui;

public class SlotItemFieldPair {

    private String slotField;
    private String itemField;

    public SlotItemFieldPair(String slotField, String itemField) {
        this.slotField = slotField;
        this.itemField = itemField;
    }

    public String slotField() {
        return slotField;
    }

    public String itemField() {
        return itemField;
    }

    public void updateItemField(String itemField) {
        this.itemField = itemField;
    }
}