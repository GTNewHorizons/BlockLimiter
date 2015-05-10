package com.github.namikon.events;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

import com.github.namikon.blocklimiter.auxiliary.BlockInfo;
import com.github.namikon.blocklimiter.auxiliary.LogHelper;
import com.github.namikon.blocklimiter.config.ConfigManager;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

public class BlockPlaceEvent {
	private ConfigManager _mConfig = null;
	
	public BlockPlaceEvent(ConfigManager pCfgMan) {
		_mConfig = pCfgMan;
	}
	
	@SubscribeEvent
	public void onBlockPlace(BlockEvent.PlaceEvent event)
	{
		UniqueIdentifier tBlockDomain = GameRegistry.findUniqueIdentifierFor(event.block);
		
		for (BlockInfo tBI : _mConfig.LimitedBlocks)
		{
			if (!tBI.isDenied(tBlockDomain, event.player.dimension))
			{
				event.setCanceled(true);
				event.player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Sorry you can't use this Block here"));
			}
		}
	}
}
