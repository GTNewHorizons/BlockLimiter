package com.github.namikon.blocklimiter;

import java.util.List;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import com.github.namikon.blocklimiter.auxiliary.*;
import com.github.namikon.blocklimiter.config.ConfigManager;
import com.github.namikon.events.BlockPlaceEvent;

/**
 * @author Namikon
 *
 */
@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION)
public class BlockLimiter {
	private static ConfigManager _cfgManager = null;
	public static boolean ModInitSuccessful = true;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		try 
		{
			_cfgManager = new ConfigManager(event);
			_cfgManager.InitConfigDirs();
			if (!_cfgManager.LoadConfig())
				ModInitSuccessful = false;
		}
	    catch (Exception e)
	    {
	    	LogHelper.error("Yeeks, I can't load my configuration. What did you do??");
	    	LogHelper.DumpStack(e);
	    }
	}
	
	@EventHandler
	public void Init(FMLInitializationEvent event)
	{
		if(ModInitSuccessful)
		{
			BlockPlaceEvent tPlaceEvent = new BlockPlaceEvent(_cfgManager);
			MinecraftForge.EVENT_BUS.register(tPlaceEvent);
		}
		else
			LogHelper.warn(Reference.MODID + " will NOT do anything as there where errors due the preInit event. Check the logfile!");
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		
	}
}
