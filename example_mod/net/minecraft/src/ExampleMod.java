package net.minecraft.src;

import java.util.List;

public class ExampleMod extends BaseMod {
	public static Achievement eatTheCheese;
	public static Item cheese;
	public static Block cheeseBlock;

	@Override
	public String getName() {
		return "Example Mod";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getAuthor() {
		return "BlueStaggo";
	}

	@Override
	public String getIcon() {
		return "/example_mod/icon.png";
	}

	@Override
	public void load() throws Exception {
		// Create an achievement. Textures are stored in /achievements/example/eat_the_cheese.png
		// and /achievements/example/eat_the_cheese_locked.png depending on progress.
		// Creating the locked achievement image is the same as setting HSL saturation to 0
		// and decreasing HSL lightness by 80.
		eatTheCheese = new Achievement("example_mod/eat_the_cheese", "EAT THE CHEESE!", "Some random cheese fell on your head. Eat it!")
				.setCreativeUnlockable(); // Achievements are not unlockable in creative mode by default.
		ModLoader.registry.addAchievement(eatTheCheese); // Register it to the ModLoader so that it loads properly

		// Create a new item. New IDs can be automatically used with ModLoader.registry.itemId().
		// The "5" in the ItemFood constructor is how much health the food heals.
		cheese = new ItemFood(ModLoader.registry.itemId(), 5)
				.setIconIndex(ModLoader.registry.addItemTexture("/example_mod/items/cheese.png")); // Load in a custom texture into the item texture atlas
		// Add in a tooltip for the inventory
		cheese.setTooltip(new ItemTooltip(ItemTooltip.LEGENDARY_GRAD,   // Legendary gradient (magenta)
				"Cheese", ItemTooltip.LEGENDARY_COL,                    // Legendary color (magenta)
				"Very exquisite cuisine", ItemTooltip.DESCRIPTION_COL,  // Description color (beige)
				cheese)); // Pass in item for automatic info generation (how much health the cheese heals)

		// Create a new block. This cheese block will inherit the sponge texture,
		// but loading custom block textures is done in a very similar fashion
		// to loading custom item textures.
		cheeseBlock = new BlockCheese(ModLoader.registry.blockId(), Block.sponge.blockIndexInTexture, Material.plants)
				.setHardness(1.0F)
				.setStepSound(Block.soundClothFootstep);
		cheeseBlock.setTooltip(new ItemTooltip(ItemTooltip.LEGENDARY_GRAD,
				"Block of Cheese", ItemTooltip.LEGENDARY_COL,
				"SO MUCH CHEESE!", ItemTooltip.DESCRIPTION_COL));
		ModLoader.registry.registerItemBlock(cheeseBlock); // Make sure to register the block item!

		// Create a new recipe. Make a 2x2 square of cheese
		// to craft a cheese block.
		ModLoader.registry.addRecipe(new ItemStack(cheeseBlock),
				"XX",
				"XX",
				'X', cheese);
		ModLoader.registry.addRecipe(new ItemStack(cheese), // Make sure to be able to convert the cheese back!
				"X",
				'X', cheeseBlock);

		// Item tooltips can be overridden. Here, the Bucket
		// of Milk becomes the Bucket of Cheese.
		ItemTooltip.items[Item.bucketMilk.shiftedIndex] = new ItemTooltip("Bucket of Cheese",
				"What kind of cheese is this?", ItemTooltip.DESCRIPTION_COL);
	}

	// Using hooks, custom code can be ran
	// on every tick of the world.
	@Override
	public void onWorldTick(World world) {
		// Drop cheese in singleplayer worlds in 1 in 1000 ticks.
		int cheeseInterval = 1000;
		if (ModLoader.isClient() && world.canDoClientAction() && world.rand.nextInt(cheeseInterval) == 0) {
			for (EntityPlayer player : (List<EntityPlayer>)world.playerEntities) {
				EntityItem item = new EntityItem(world, player.posX, player.posY + 10, player.posZ, new ItemStack(cheese));
				world.spawnEntityInWorld(item);
				world.playSoundAtEntity(item, "random.wand", 1.0F, world.rand.nextFloat() * 0.2F + 0.9F);
				world.achievements.updateProgress(eatTheCheese, 1); // Award the "EAT THE CHEESE!" achievement to whoever got the cheese.
			}
		}
	}

	// Add mod items to the creative inventory using a hook.
	@Override
	public void setupCreativeInventory(CreativeInventoryBuilder inv) {
		inv.addLabel("Example Mod");
		inv.addItem(cheese.shiftedIndex, 0);
		inv.addItem(cheeseBlock.blockID, 0);
	}

	// This is a custom BlockCheese class to give the cheese a small shape.
	// The Block of Cheese is not a full block, so it needs special code.
	private static class BlockCheese extends Block {
		public BlockCheese(int var1, int var2, Material var3) {
			super(var1, var2, var3);
			this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
		}

		@Override
		public boolean isOpaqueCube() {
			return false;
		}

		@Override
		public boolean shouldSideBeRendered(IBlockAccess var1, int var2, int var3, int var4, int var5) {
			return true;
		}
	}
}
