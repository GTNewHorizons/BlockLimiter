package com.github.namikon.blocklimiter.events;

import java.util.Random;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import com.github.namikon.blocklimiter.BlockLimiter;
import com.github.namikon.blocklimiter.auxiliary.ItemInfo;
import com.github.namikon.blocklimiter.config.BlockLimiterConfig;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import eu.usrv.yamcore.auxiliary.ItemDescriptor;
import eu.usrv.yamcore.auxiliary.PlayerChatHelper;

public class ItemUseEvent {
	private BlockLimiterConfig _mConfig = null;
	private Random _mRnd = null;
	
	public ItemUseEvent(BlockLimiterConfig pCfgMan) {
		_mConfig = pCfgMan;
		_mRnd = new Random();
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onInteractEvent(PlayerInteractEvent pEvent)
	{
		if(pEvent.entityPlayer.worldObj.isRemote)
			return;
		
		if(pEvent.action == Action.LEFT_CLICK_BLOCK || !pEvent.isCancelable())
			return;
		
		// Ignore players in Creative-Mode
		if (pEvent.entityPlayer.capabilities.isCreativeMode && !BlockLimiter.Config.DenyCreativeMode)
			return;
		
		if(pEvent.entityPlayer != null && pEvent.entityPlayer.getCurrentEquippedItem() != null)
		{
			ItemDescriptor tID = ItemDescriptor.fromStack(pEvent.entityPlayer.getCurrentEquippedItem());

			for (ItemInfo tII : _mConfig.LimitedItems)
			{
				if (tII.isDenied(tID, pEvent.entityPlayer.dimension))
				{
					pEvent.setCanceled(true);

					try // just in case someone messes up with the config file...
					{
						if (BlockLimiter.Config.SFXOnItemDeny.length() > 0)
							pEvent.entityPlayer.worldObj.playSoundAtEntity(pEvent.entityPlayer, BlockLimiter.Config.SFXOnItemDeny, 1F, 1F);

						int tMsgIdx = _mRnd.nextInt(BlockLimiter.Config.RandomItemDenyMessages.length);
						PlayerChatHelper.SendNotifyWarning(pEvent.entityPlayer, BlockLimiter.Config.RandomItemDenyMessages[tMsgIdx]);
					}
					catch (Exception e)
					{
						BlockLimiter.Logger.error("Prevented ServerCrash caused by malformed RejectMessage or SoundSetting in the config file");
					}			

				}
			}
		}
	}
}
