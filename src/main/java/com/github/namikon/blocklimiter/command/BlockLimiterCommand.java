
package com.github.namikon.blocklimiter.command;


import java.util.ArrayList;
import java.util.List;

import com.github.namikon.blocklimiter.BlockLimiter;
import com.github.namikon.blocklimiter.xmlconfig.BlockLimits.BlockLimit;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import cpw.mods.fml.relauncher.Side;
import eu.usrv.yamcore.auxiliary.PlayerChatHelper;
import net.minecraft.block.Block;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;


public class BlockLimiterCommand implements ICommand
{
  private List aliases;

  public BlockLimiterCommand()
  {
    this.aliases = new ArrayList();
    this.aliases.add( "blimit" );
  }

  @Override
  public int compareTo( Object arg0 )
  {
    return 0;
  }

  @Override
  public String getCommandName()
  {
    return "blimit";
  }

  @Override
  public String getCommandUsage( ICommandSender p_71518_1_ )
  {
    if( isOpOrAdmin( p_71518_1_ ) )
      return "/blimit reload|info|diminfo";
    else
      return "/blimit info";
  }

  @Override
  public List getCommandAliases()
  {

    return this.aliases;
  }

  @Override
  public void processCommand( ICommandSender pCmdSender, String[] pArgs )
  {
    if( pArgs.length < 1 )
    {
      sendHelp( pCmdSender );
      return;
    }

    if( pArgs[0].equalsIgnoreCase( "reload" ) )
    {
      if( !isOpOrAdmin( pCmdSender ) )
      {
        PlayerChatHelper.SendError( pCmdSender, "You need to be op or admin to reload the config" );
        return;
      }
      else
      {
        if( !BlockLimiter.Config.Reload() )
          PlayerChatHelper.SendError( pCmdSender, "Blocks could not be reloaded properly. Please check the console output and fix the config file" );
        else
        {
          PlayerChatHelper.SendInfo( pCmdSender, "Blocklimiter config reloaded. Now monitoring:" );
          PlayerChatHelper.SendInfo( pCmdSender, String.format( " - %d Block(s)", BlockLimiter.blockLimitsHandler.getBlockLimits().size() ) );
          PlayerChatHelper.SendInfo( pCmdSender, String.format( " - %d Item(s)", BlockLimiter.Config.LimitedItems.size() ) );
        }

      }
    }
    else if( pArgs[0].equalsIgnoreCase( "info" ) || pArgs[0].equalsIgnoreCase( "diminfo" ) )
    {
      if( !( pCmdSender instanceof EntityPlayer ) )
      {
        PlayerChatHelper.SendPlain( pCmdSender, "This command must be executed ingame" );
        return;
      }

      EntityPlayer tEp = (EntityPlayer) pCmdSender;
      ItemStack inHand = null;
      if( tEp != null )
      {
        inHand = tEp.getCurrentEquippedItem();
        if( inHand == null )
        {
          PlayerChatHelper.SendPlain( pCmdSender, "Need to have an Item in your hand" );
          return;
        }
      }
      Block tBlockFromHand = Block.getBlockFromItem( inHand.getItem() );
      if( tBlockFromHand == Blocks.air )
      {
        PlayerChatHelper.SendPlain( pCmdSender, "This item seems to be unplaceable" );
        return;
      }
      UniqueIdentifier tUIDFromhand = GameRegistry.findUniqueIdentifierFor( tBlockFromHand );
      ArrayList<String> tPlacedBlocks;

      if( pArgs[0].equalsIgnoreCase( "diminfo" ) )
        tPlacedBlocks = BlockLimiter.instance.BWatcherDB.getBlockReportInDim( tUIDFromhand, tEp.getUniqueID(), tEp.dimension );
      else
        tPlacedBlocks = BlockLimiter.instance.BWatcherDB.getBlockReport( tUIDFromhand, tEp.getUniqueID() );

      PlayerChatHelper.SendInfo( pCmdSender, "== Report for Block %s", tUIDFromhand );
      for( String s : tPlacedBlocks )
      {
        PlayerChatHelper.SendInfo( pCmdSender, s );
      }
      PlayerChatHelper.SendInfo( pCmdSender, "== End of Report" );
      BlockLimit tBLObject = BlockLimiter.blockLimitsHandler.getBlockLimitObjectForUID( tUIDFromhand );
      if( tBLObject != null )
        PlayerChatHelper.SendInfo( pCmdSender, "Limit in your current DimensionID (%d) is: %d", tEp.dimension, tBLObject.getMaxAmountForDimension( tEp.dimension ) );
    }
    else
      PlayerChatHelper.SendError( pCmdSender, "Usage: /blimit reload" );

  }

  private void sendHelp( ICommandSender pCommandSender )
  {
    if( isOpOrAdmin( pCommandSender ) )
      PlayerChatHelper.SendError( pCommandSender, "Usage: /blimit reload|info|diminfo" );
    else
      PlayerChatHelper.SendError( pCommandSender, "Usage: /blimit info|diminfo" );
  }

  @Override
  public boolean canCommandSenderUseCommand( ICommandSender pCommandSender )
  {
    return true;
  }

  /**
   * Check if commandSender is an op or admin
   * 
   * @param pCommandSender
   * @return
   */
  private boolean isOpOrAdmin( ICommandSender pCommandSender )
  {
    if( FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && !FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer() )
      return true;

    if( pCommandSender instanceof EntityPlayerMP )
    {
      EntityPlayerMP tEP = (EntityPlayerMP) pCommandSender;
      return MinecraftServer.getServer().getConfigurationManager().func_152596_g( tEP.getGameProfile() );
    }
    return false;
  }

  @Override
  public List addTabCompletionOptions( ICommandSender p_71516_1_, String[] p_71516_2_ )
  {
    return null;
  }

  @Override
  public boolean isUsernameIndex( String[] p_82358_1_, int p_82358_2_ )
  {
    return false;
  }

}
