package org.abp.systems.cls;

import java.util.ArrayList;

import org.abp.core.Plugin;
import org.abp.data.LocationUtil;
import org.abp.logging.PlayerLogger;
import org.abp.systems.cs.ClanSystem;
import org.abp.systems.cs.ClanSystem.Clan;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CLSListener implements Listener {

	private static final String PLAYER_LOCK_HEADER = "[PlayerLock]";
	private static final String CLAN_LOCK_HEADER = "[ClanLock]";
	
	private Plugin plugin;
	private ClanSystem clanSystem;
	
	CLSListener(Plugin plugin, ClanSystem clanSystem) {
		this.plugin = plugin;
		this.clanSystem = clanSystem;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		PlayerLogger playerLogger = new PlayerLogger(player, plugin.getChatTheme());
				
		if(!player.isOp()) {
			Block clickedBlock = event.getClickedBlock();
			
			if(clickedBlock != null) {
				if(isChest(clickedBlock)) {
					if(!isChestOwner(player, clickedBlock)) {
						playerLogger.log("§eYou are not owner of this chest");
						event.setCancelled(true);
					}
				} else {
					if(clickedBlock.getBlockData() instanceof org.bukkit.block.data.type.Sign || 
							clickedBlock.getBlockData() instanceof org.bukkit.block.data.type.WallSign) {
						Sign sign = (Sign) clickedBlock.getState();
						Owner owner = createOwnerFromSign(sign);
						
						if(owner != null) {
							Block attachedBlock = getSignAttachedBlock(clickedBlock);
							
							if(attachedBlock != null && isChest(attachedBlock) && !isChestOwner(player, attachedBlock)) {
								playerLogger.log("§eYou are not owner of this chest");
								event.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		PlayerLogger playerLogger = new PlayerLogger(player, plugin.getChatTheme());
		Sign sign = (Sign) event.getBlock().getState();
		Owner owner = createOwnerFromSign(sign);
		
		if(owner != null) {
			Block attachedBlock = getSignAttachedBlock(event.getBlock());
				
			if((attachedBlock != null && isChest(attachedBlock)) && !isChestOwner(player, attachedBlock)) {
				playerLogger.log("§eYou are not allowed to edit this lock");
				event.setCancelled(true);
			}
		}
	}
	
	private boolean isChestOwner(Player player, Block chestBlock) {
		Location location = chestBlock.getLocation();
		
		Block px = new Location(location.getWorld(), location.getBlockX() + 1, location.getBlockY(),
				location.getBlockZ()).getBlock();
		Block nx = new Location(location.getWorld(), location.getBlockX() - 1, location.getBlockY(),
				location.getBlockZ()).getBlock();
		Block pz = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(),
				location.getBlockZ() + 1).getBlock();
		Block nz = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(),
				location.getBlockZ() - 1).getBlock();
		
		int totalOwners = 0;
		for(Block block : new Block[] {chestBlock, px, nx, pz ,nz}) {
			if(isChest(block)) {
				ArrayList<Owner> owners = getSingleChestOwners(player, block);
				
				for(Owner owner : owners)
					if(owner.isPartOwner(player))
						return true;
				
				totalOwners += owners.size();
			}
		}
		
		return totalOwners == 0;
	}
	
	private ArrayList<Owner> getSingleChestOwners(Player player, Block chestBlock) {
		Location location = chestBlock.getLocation();
		
		Block px = new Location(location.getWorld(), location.getBlockX() + 1, location.getBlockY(),
				location.getBlockZ()).getBlock();
		Block nx = new Location(location.getWorld(), location.getBlockX() - 1, location.getBlockY(),
				location.getBlockZ()).getBlock();
		Block py = new Location(location.getWorld(), location.getBlockX(), location.getBlockY() + 1,
				location.getBlockZ()).getBlock();
		Block pz = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(),
				location.getBlockZ() + 1).getBlock();
		Block nz = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(),
				location.getBlockZ() - 1).getBlock();
		
		ArrayList<Owner> owners = new ArrayList<>();
		
		for(Block block : new Block[] {px, nx, py, pz, nz}) {
			Block attached = getSignAttachedBlock(block);
			
			if(attached != null && LocationUtil.areEqual(attached.getLocation(), location)) {
				Sign sign = (Sign) block.getState();
				Owner owner = createOwnerFromSign(sign);
				
				if(owner != null)
					owners.add(owner);
			}
		}
		
		return owners;
	}

	private Owner createOwnerFromSign(Sign sign) {
		if(sign.getLine(0).equalsIgnoreCase(PLAYER_LOCK_HEADER))
			return new Owner(sign.getLine(1));
		else if(sign.getLine(0).equalsIgnoreCase(CLAN_LOCK_HEADER)) {
			Clan clan = clanSystem.getClan(sign.getLine(1));

			if(clan != null)
				return new Owner(clan);
		}
		
		return null;
	}
	
	private static boolean isChest(Block block) {
		return block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST;
	}
	
	private static Block getSignAttachedBlock(Block signBlock) {
		if(signBlock.getBlockData() instanceof org.bukkit.block.data.type.Sign || 
				signBlock.getBlockData() instanceof org.bukkit.block.data.type.WallSign) {
			if(signBlock.getBlockData() instanceof Directional) {
				Directional directional = (Directional) signBlock.getBlockData();
				return signBlock.getRelative(directional.getFacing().getOppositeFace());
			} else
				return signBlock.getRelative(BlockFace.DOWN);
		}
		
		return null;
	}
	
	private static class Owner {
		
		private Clan clan;
		private String playerName;
		
		private Owner(Clan clan) {
			this.clan = clan;
		}
		
		private Owner(String playerName) {
			this.playerName = playerName;
		}
		
		public boolean isPartOwner(Player player) {
			if(clan != null)
				return clan.isMember(player.getUniqueId().toString());
			if(playerName != null)
				return playerName.equals(player.getName());
			return false;
		}
	}
}
