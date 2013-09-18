package net.minekingdom.gladiator;

public enum MerchantItem {
	ENCHANTMENT_TABLE (10),
	BOOKSHELF (2),
	WORKBENCH (10),
	CAULDRON_ITEM (10),
	GRILLED_PORK (3),
	GLASS_BOTTLE (3),
	GOLDEN_APPLE (25);
	
	private final int price;
	
	private MerchantItem(int price) {
		this.price = price;
	}
	
	public int getPrice() {
		return price;
	}
}