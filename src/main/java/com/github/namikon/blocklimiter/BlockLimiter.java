package com.github.namikon.blocklimiter;

import java.util.List;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import com.github.namikon.blocklimiter.auxiliary.*;
import com.github.namikon.blocklimiter.command.BlockLimiterCommand;
import com.github.namikon.blocklimiter.config.BlockLimiterConfig;
import com.github.namikon.blocklimiter.events.BlockPlaceEvent;

import eu.usrv.yamcore.auxiliary.LogHelper;
import eu.usrv.yamcore.config.ConfigManager;

/**
 * @author Namikon
 *
 */
@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, 
dependencies = 	"required-after:Forge@[10.13.2.1291,);" +
		"required-after:YAMCore@[0.3,);")
public class BlockLimiter {
	public static BlockLimiterConfig Config = null;
	public static LogHelper Logger = new LogHelper("BlockLimiter");
	public static boolean ModInitSuccessful = true;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		try 
		{
			Config = new BlockLimiterConfig(event.getModConfigurationDirectory(), Reference.COLLECTIONNAME, Reference.MODID);
			if (!Config.LoadConfig())
				ModInitSuccessful = false;
		}
	    catch (Exception e)
	    {
	    	Logger.error("Yeeks, I can't load my configuration. What did you do??");
	    	Logger.DumpStack(e);
	    }
	}
	
	@EventHandler
	public void Init(FMLInitializationEvent event)
	{
		if(ModInitSuccessful)
		{
			BlockPlaceEvent tPlaceEvent = new BlockPlaceEvent(Config);
			MinecraftForge.EVENT_BUS.register(tPlaceEvent);
		}
		else
			Logger.warn(Reference.MODID + " will NOT do anything as there where errors due the preInit event. Check the logfile!");
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		
	}
	/** Do some stuff once the server starts
	 * @param pEvent
	 */
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent pEvent)
	{
		pEvent.registerServerCommand(new BlockLimiterCommand());
	}
}
