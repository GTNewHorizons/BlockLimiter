package com.github.namikon.blocklimiter.events;

import java.util.Random;

import net.minecraftforge.event.world.BlockEvent;

import com.github.namikon.blocklimiter.BlockLimiter;
import com.github.namikon.blocklimiter.auxiliary.BlockInfo;
import com.github.namikon.blocklimiter.config.BlockLimiterConfig;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import eu.usrv.yamcore.auxiliary.PlayerChatHelper;

public class BlockPlaceEvent {
	private BlockLimiterConfig _mConfig = null;
	private Random _mRnd = null;

	public BlockPlaceEvent(BlockLimiterConfig pCfgMan) {
		_mConfig = pCfgMan;
		_mRnd = new Random();
	}

	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onBlockPlace(BlockEvent.PlaceEvent event)
	{
		// Ignore players in Creative-Mode
		if (event.player.capabilities.isCreativeMode && !BlockLimiter.Config.DenyCreativeMode)
			return;

		UniqueIdentifier tBlockDomain = GameRegistry.findUniqueIdentifierFor(event.block);

		BlockLimiter.Logger.debug("BlockPlaceEvent: " + tBlockDomain.modId + ":" + tBlockDomain.name + " in Dim " + event.player.dimension);

		for (BlockInfo tBI : _mConfig.LimitedBlocks)
		{
			if (tBI.isDenied(tBlockDomain, event.player.dimension))
			{
				event.setCanceled(true);

				try // just in case someone messes up with the config file...
				{
					if (BlockLimiter.Config.SFXOnBlockDeny.length() > 0)
						event.world.playSoundAtEntity(event.player, BlockLimiter.Config.SFXOnBlockDeny, 1F, 1F);

					int tMsgIdx = _mRnd.nextInt(BlockLimiter.Config.RandomDenyMessages.length);
					PlayerChatHelper.SendNotifyWarning(event.player, BlockLimiter.Config.RandomDenyMessages[tMsgIdx]);
				}
				catch (Exception e)
				{
					BlockLimiter.Logger.error("Prevented ServerCrash caused by malformed RejectMessage or SoundSetting in the config file");
				}
				return;
			}
		}
	}
}
