
package com.github.namikon.blocklimiter;


import net.minecraftforge.common.MinecraftForge;

import com.github.namikon.blocklimiter.auxiliary.Reference;
import com.github.namikon.blocklimiter.blockwatcher.BlockWatcher;
import com.github.namikon.blocklimiter.command.BlockLimiterCommand;
import com.github.namikon.blocklimiter.config.BlockLimiterConfig;
import com.github.namikon.blocklimiter.db.BLDataSource;
import com.github.namikon.blocklimiter.db.BLSchema;
import com.github.namikon.blocklimiter.events.BlockEvents;
import com.github.namikon.blocklimiter.events.ItemUseEvent;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import eu.usrv.yamcore.auxiliary.LogHelper;


@Mod( modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, dependencies = "required-after:Forge@[10.13.4.1614,);" + "required-after:YAMCore@[0.5.72-SNAPSHOT,);" )
public class BlockLimiter
{
  public static BlockLimiterConfig Config = null;
  public static LogHelper Logger = new LogHelper( "BlockLimiter" );
  public static boolean ModInitSuccessful = true;
  public BlockWatcher BWatcher;
  public BLDataSource BWatcherDB;
  
  @Instance( Reference.MODID )
  public static BlockLimiter instance;
  
  @EventHandler
  public void preInit( FMLPreInitializationEvent event )
  {
    try
    {
      Config = new BlockLimiterConfig( event.getModConfigurationDirectory(), Reference.COLLECTIONNAME, Reference.MODID );
    }
    catch( Exception e )
    {
      Logger.error( "Yeeks, I can't load my configuration. What did you do?" );
      Logger.DumpStack( e );
    }
  }

  @EventHandler
  public void Init( FMLInitializationEvent event )
  {
    BWatcher = new BlockWatcher();
  }

  @EventHandler
  public void postInit( FMLPostInitializationEvent event )
  {
    if( Config.LoadConfig() )
    {
      BlockEvents tPlaceEvent = new BlockEvents( Config );
      ItemUseEvent tUseItemEvent = new ItemUseEvent( Config );
      MinecraftForge.EVENT_BUS.register( tPlaceEvent );
      MinecraftForge.EVENT_BUS.register( tUseItemEvent );
      
      BWatcherDB = new BLDataSource( Logger, new BLSchema(), "127.0.0.1", "root", "root", "blocklimitertest" );
    }
    else
      Logger.warn( Reference.MODID + " will NOT do anything as there where errors in the postInit event. Check the logfile!" );
  }

  /**
   * Do some stuff once the server starts
   * 
   * @param pEvent
   */

  @EventHandler
  public void serverLoad( FMLServerStartingEvent pEvent )
  {
    pEvent.registerServerCommand( new BlockLimiterCommand() );
  }
}
