package com.github.namikon.blocklimiter;

import net.minecraftforge.common.MinecraftForge;

import com.github.namikon.blocklimiter.auxiliary.Reference;
import com.github.namikon.blocklimiter.command.BlockLimiterCommand;
import com.github.namikon.blocklimiter.config.BlockLimiterConfig;
import com.github.namikon.blocklimiter.events.BlockPlaceEvent;
import com.github.namikon.blocklimiter.events.ItemUseEvent;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import eu.usrv.yamcore.auxiliary.LogHelper;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, 
dependencies = 	"required-after:Forge@[10.13.2.1291,);" +
		"required-after:YAMCore@[0.5.65,);")
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
		}
		catch (Exception e)
		{
			Logger.error("Yeeks, I can't load my configuration. What did you do?");
			Logger.DumpStack(e);
		}
	}

    @EventHandler
    public void imcCallback(FMLInterModComms.IMCEvent event)
    {
        for (final FMLInterModComms.IMCMessage imcMessage : event.getMessages())
        {
            if (imcMessage.key.equalsIgnoreCase("disallow-item"))
            {
                if (imcMessage.isStringMessage())
                {
                    Logger.info("Received DISALLOW_ITEM IMC from [" + imcMessage.getSender() + "]: " + imcMessage.getStringValue());
                    if (!Config.IMC_AddLimitedItem(imcMessage.getStringValue()))
                    	Logger.warn("Unable to add Item [" + imcMessage.getStringValue() + "] as limited item. Ignoring IMC");
                }
            }
            if (imcMessage.key.equalsIgnoreCase("disallow-block"))
            {
                if (imcMessage.isStringMessage())
                {
                	Logger.info("Received DISALLOW_BLOCK IMC from [" + imcMessage.getSender() + "]: " + imcMessage.getStringValue());
                	if (!Config.IMC_AddLimitedBlock(imcMessage.getStringValue()))
                		Logger.warn("Unable to add Block [" + imcMessage.getStringValue() + "] as limited block. Ignoring IMC");
                }
            }
        }
    }
	
	@EventHandler
	public void Init(FMLInitializationEvent event)
	{
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		if (Config.LoadConfig())
		{
			BlockPlaceEvent tPlaceEvent = new BlockPlaceEvent(Config);
			ItemUseEvent tUseItemEvent = new ItemUseEvent(Config);
			MinecraftForge.EVENT_BUS.register(tPlaceEvent);
			MinecraftForge.EVENT_BUS.register(tUseItemEvent);
		}
		else
			Logger.warn(Reference.MODID + " will NOT do anything as there where errors in the postInit event. Check the logfile!");
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
