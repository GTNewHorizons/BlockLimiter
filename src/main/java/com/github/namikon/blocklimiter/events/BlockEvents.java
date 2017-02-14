
package com.github.namikon.blocklimiter.events;


import java.util.Random;

import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent;

import com.github.namikon.blocklimiter.BlockLimiter;
import com.github.namikon.blocklimiter.auxiliary.BlockInfo;
import com.github.namikon.blocklimiter.config.BlockLimiterConfig;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import eu.usrv.yamcore.auxiliary.PlayerChatHelper;


public class BlockEvents
{
  private BlockLimiterConfig _mConfig = null;
  private Random _mRnd = null;

  public BlockEvents( BlockLimiterConfig pCfgMan )
  {
    _mConfig = pCfgMan;
    _mRnd = new Random();
  }

  @SubscribeEvent( priority = EventPriority.LOWEST, receiveCanceled = false )
  public void onBlockBreak( BlockEvent.BreakEvent event )
  {
    UniqueIdentifier tBlockDomain = GameRegistry.findUniqueIdentifierFor( event.block );

    BlockLimiter.Logger.debug( "BlockBreakEvent: " + tBlockDomain.modId + ":" + tBlockDomain.name + " in Dim " + event.getPlayer().dimension );

    // Instead of looping the database and its millions of records,
    // we loop the configured blocks and check only for those who are actually limited.
    // Should save a decent chunk of CPU time
    for( BlockInfo tBI : _mConfig.LimitedBlocks )
    {
      if( tBI.matches( tBlockDomain ) )
      {
        if( BlockLimiter.instance.BWatcher.canBreakBlock( event.getPlayer().getUniqueID(), event.getPlayer().dimension, event.x, event.y, event.z ) )
        {
          BlockLimiter.instance.BWatcher.removeBlock( event.getPlayer().dimension, event.x, event.y, event.z );
        }
        else
        {
          if( !( event.getPlayer() instanceof FakePlayer ) )
            PlayerChatHelper.SendError( event.getPlayer(), "This Block is protected and cannot be removed by you" );
          event.setCanceled( true );
        }
      }
    }
  }

  @SubscribeEvent( priority = EventPriority.LOWEST, receiveCanceled = false )
  public void onBlockPlace( BlockEvent.PlaceEvent event )
  {
    // Ignore players in Creative-Mode
    if( event.player.capabilities.isCreativeMode && !BlockLimiter.Config.DenyCreativeMode )
      return;

    UniqueIdentifier tBlockDomain = GameRegistry.findUniqueIdentifierFor( event.block );

    BlockLimiter.Logger.debug( "BlockPlaceEvent: " + tBlockDomain.modId + ":" + tBlockDomain.name + " in Dim " + event.player.dimension );

    for( BlockInfo tBI : _mConfig.LimitedBlocks )
    {
      if( tBI.matches( tBlockDomain ) )
      {
        int tMaxForDim = tBI.getMaxAmountForDimension( event.player.dimension );
        if( tMaxForDim == -1 )
        {
          event.setCanceled( true );

          try
          // just in case someone messes up with the config file...
          {
            if( BlockLimiter.Config.SFXOnBlockDeny.length() > 0 )
              event.world.playSoundAtEntity( event.player, BlockLimiter.Config.SFXOnBlockDeny, 1F, 1F );

            int tMsgIdx = _mRnd.nextInt( BlockLimiter.Config.RandomDenyMessages.length );
            PlayerChatHelper.SendNotifyWarning( event.player, BlockLimiter.Config.RandomDenyMessages[tMsgIdx] );
          }
          catch( Exception e )
          {
            BlockLimiter.Logger.error( "Prevented ServerCrash caused by malformed RejectMessage or SoundSetting in the config file" );
          }
        }
        else if( tMaxForDim > 0 )
        {
          int tPlacedCount = BlockLimiter.instance.BWatcher.getPlacedBlockCount( event.player.getUniqueID(), event.player.dimension, tBlockDomain, event.blockMetadata );
          if( tPlacedCount == 0 )
          {
            BlockLimiter.Logger.info( String.format( "Block placed: %s", tBlockDomain.toString() ) );
            PlayerChatHelper.SendNotifyNormal( event.player, "This Block is limited." );
            PlayerChatHelper.SendNotifyNormal( event.player, "You can place %d more of it in this Dimension", tMaxForDim - 1 );
          }

          if( tPlacedCount >= tMaxForDim )
          {
            event.setCanceled( true );
            PlayerChatHelper.SendNotifyWarning( event.player, "You can't place more of this!" );
          }
          // Warn if only 10 blocks of this type remain
          else if( ( tMaxForDim - 10 ) <= tPlacedCount )
          {
            PlayerChatHelper.SendNotifyWarning( event.player, "You can place %d more of this", ( tMaxForDim - tPlacedCount - 1 ) );
          }

          if( !event.isCanceled() )
            BlockLimiter.instance.BWatcher.addPlacedBlock( event.player.getUniqueID(), tBlockDomain, event.blockMetadata, event.player.dimension, event.x, event.y, event.z );
        }
      }
    }
  }
}
