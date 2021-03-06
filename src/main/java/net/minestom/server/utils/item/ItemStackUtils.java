package net.minestom.server.utils.item;

import net.minestom.server.item.ItemStack;

public class ItemStackUtils {

    /**
     * @param itemStack the ItemStack to return if not null
     * @return {@code itemStack} if not null, {@link ItemStack#getAirItem()} otherwise
     */
    public static ItemStack notNull(ItemStack itemStack) {
        return itemStack == null ? ItemStack.getAirItem() : itemStack;
    }

}
