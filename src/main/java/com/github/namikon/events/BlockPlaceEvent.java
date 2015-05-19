package com.github.namikon.events;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;

import com.github.namikon.blocklimiter.auxiliary.BlockInfo;
import com.github.namikon.blocklimiter.config.BlockLimiterConfig;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import eu.usrv.yamcore.auxiliary.PlayerChatHelper;
import eu.usrv.yamcore.client.Notification;
import eu.usrv.yamcore.client.NotificationTickHandler;

public class BlockPlaceEvent {
	private BlockLimiterConfig _mConfig = null;
	
	public BlockPlaceEvent(BlockLimiterConfig pCfgMan) {
		_mConfig = pCfgMan;
	}
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onBlockPlace(BlockEvent.PlaceEvent event)
	{
		UniqueIdentifier tBlockDomain = GameRegistry.findUniqueIdentifierFor(event.block);
		
		for (BlockInfo tBI : _mConfig.LimitedBlocks)
		{
			if (!tBI.isDenied(tBlockDomain, event.player.dimension))
			{
				event.setCanceled(true);
				PlayerChatHelper.SendNotifyWarning(event.player, "You can't place that here");
				return;
			}
		}
	}
}
