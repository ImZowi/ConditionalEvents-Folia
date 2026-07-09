package ce.ajneb97.libs.repairevent;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public class RepairEvent extends PlayerEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancel = false;
	private ItemStack item;
	private RepairType repairType;
	private String renameText;

	public RepairEvent(Player player, ItemStack item, RepairType repairType, String renameText) {
		super(player);
		this.item = item;
		this.repairType = repairType;
		this.renameText = renameText;
	}

	public ItemStack getItem() {
		return item;
	}

	public RepairType getRepairType() {
		return repairType;
	}

	public String getRenameText() {
		return renameText;
	}

	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
	    return handlers;
	}

	@Override
	public boolean isCancelled() {
		return this.cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	public enum RepairType {
		ANVIL,
		GRINDSTONE,
		MENDING,
		INVENTORY
	}
}
