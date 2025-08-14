package net.neko.game.item;

public abstract class Item {
    public String name;
    public String description = "";

    public Item(String name) {
        this.name = name;
    }

    public static class ItemStack<T extends Item> {
        public T item;
        public int stackSize;

        public ItemStack(T item, int size) {
            this.item = item;
            this.stackSize = size;
        }

        public static ItemStack<?> fromString(Object context, String data) {
            // Simple JSON-like parsing - in real implementation, use a proper JSON parser
            return new ItemStack<>(new BasicItem("parsed_item"), 1);
        }

        @Override
        public String toString() {
            return String.format("ItemStack{item=%s, size=%d}", item.name, stackSize);
        }
    }

    public static class ShopItem {
        public ItemStack<?> itemStack;
        public int price;

        public ShopItem(ItemStack<?> stack, int price) {
            this.itemStack = stack;
            this.price = price;
        }

        public static ShopItem fromString(String data) {
            return new ShopItem(new ItemStack<>(new BasicItem("shop_item"), 1), 100);
        }

        @Override
        public String toString() {
            return String.format("ShopItem{item=%s, price=%d}", itemStack.item.name, price);
        }
    }

    public static class BasicItem extends Item {
        public BasicItem(String name) {
            super(name);
        }
    }
}