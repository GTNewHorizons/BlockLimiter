package com.github.namikon.blocklimiter.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.LogManager;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import com.github.namikon.blocklimiter.auxiliary.BlockInfo;
import com.github.namikon.blocklimiter.auxiliary.LogHelper;
import com.github.namikon.blocklimiter.auxiliary.Reference;

import net.minecraftforge.common.config.Configuration;

import org.apache.commons.io.FileUtils;

/**
 * config class to read/setup config files and folders
 * @author Namikon
 */
public class ConfigManager {
	private File _mainconfigDir = null;
	
	private Configuration _mainConfig = null;
	FMLPreInitializationEvent _event = null;
	 
	public List<BlockInfo> LimitedBlocks = null;
	
	 public boolean DoDebugMessages = false;
	 
	 public ConfigManager(FMLPreInitializationEvent pEvent) {
		 _event = pEvent;
		 PreInit();
	 }
	 
	 
	 /**
	 * PreInit default values and lists
	 */
	private void PreInit()
	 {
		LimitedBlocks = new ArrayList<BlockInfo>(); 
	 }
	 
	 /**
	  * Load/init the config file
	 * @return true/false if the load/init was successful or not
	 */
	public boolean LoadConfig()
	 {
		 try
		 {
			 if (_mainConfig == null)
			 {
				 LogHelper.error("Y u no call InitConfigDirs first?");
				 return false;
			 }
				 
			 _mainConfig.load();
			 
			 String tConfiguredBlocks[] = _mainConfig.getStringList("BlockList", "Main", new String[] {}, "Define your Blocks here. Syntax is: [modID]:[BlockID];[DimID];[DimID];[DimID];...");
			 DoDebugMessages = _mainConfig.getBoolean("DoDebugMessages", "Debug", false, "Enable debug output to fml-client-latest.log");
			 
			 LogHelper.setDebugOutput(DoDebugMessages);
			 _mainConfig.save();

			 InitDefinedBlocks(tConfiguredBlocks);
			 
			 return true;
		 }
		 catch (Exception e)
		 {
			 LogHelper.error("Unable to init config file");
			 LogHelper.DumpStack(e);
			 return false;
		 }
	 }
	 
	 
	/**
	 * Init blockDefs
	 * @param pBlocks
	 */
	private void InitDefinedBlocks(String pBlocks[])
	{
		try
		{
			for (String tBlockDef : pBlocks)
			{
				try
				{
					BlockInfo tBI = new BlockInfo(tBlockDef);
					LimitedBlocks.add(tBI);
				}
				catch (Exception e)
				{
					LogHelper.error("An error occoured while parsing line " + tBlockDef + " and therfor it will be ignored");
					LogHelper.DumpStack(e);
					continue;
				}
			}
		}
		catch (Exception e)
		{
			LogHelper.error("An error occoured while parsing BlockConfigs. You probably did something wrong did you..?");
			LogHelper.DumpStack(e);			
		}
	}
 
	 /**
	 * Search for required config-directory / file and create them if they can't be found 
	 */
	public void InitConfigDirs()
	 {
		 LogHelper.info("Checking/creating config folders");
		 
		 File file = _event.getSuggestedConfigurationFile();
		 String cfgDir = file.getParent();
		 
		 _mainconfigDir = new File(cfgDir + "\\" + Reference.COLLECTIONNAME);
	 
	    if(!_mainconfigDir.exists()) {
	    	LogHelper.info("Config folder not found. Creating...");
	    	_mainconfigDir.mkdir();
	    }
	    
	    File tRealConfigFile = new File(_mainconfigDir + "\\" + Reference.MODID + ".cfg");
	    
	    _mainConfig = new Configuration(tRealConfigFile);
	 }
}