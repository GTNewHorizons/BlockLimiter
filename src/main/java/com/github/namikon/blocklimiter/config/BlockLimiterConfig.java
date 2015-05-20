package com.github.namikon.blocklimiter.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.namikon.blocklimiter.BlockLimiter;
import com.github.namikon.blocklimiter.auxiliary.BlockInfo;

import eu.usrv.yamcore.auxiliary.LogHelper;
import eu.usrv.yamcore.config.ConfigManager;

public class BlockLimiterConfig extends ConfigManager {

	public BlockLimiterConfig(File pConfigBaseDirectory,
			String pModCollectionDirectory, String pModID) {
		super(pConfigBaseDirectory, pModCollectionDirectory, pModID);
	}

	public List<BlockInfo> LimitedBlocks = null;
	private String tConfiguredBlocks[];
	public String[] RandomDenyMessages = null;
	public String SFXOnBlockDeny;
	public boolean DenyCreativeMode;
	
	 /**
	 * PreInit default values and lists
	 */
	 @Override
	protected void PreInit()
	{
		LimitedBlocks = new ArrayList<BlockInfo>();
		RandomDenyMessages = new String[] {"You can't place that here", "You are too jelly to place this", "The block doesn't want to be here", "YOU SHALL NOT PLACE (this Block)", "*poof*"};
		SFXOnBlockDeny = "minecraft:ambient.weather.thunder";
		DenyCreativeMode = false;
	}
	
	protected void Init()
	{
		tConfiguredBlocks = _mainConfig.getStringList("BlockList", "Main", new String[] {}, "Define your Blocks here. Syntax is: [modID]:[BlockID];[DimID];... if you don't add a Dimension (e.g. minecraft:dirt) it will be denied in every dimension");
		RandomDenyMessages = _mainConfig.getStringList("RejectMessages", "Main", RandomDenyMessages, "Define a few reject messages that are being sent to the player if they try to place a monitored Block");
		SFXOnBlockDeny = _mainConfig.getString("PlaySFXOnBlockDeny", "main", SFXOnBlockDeny, "Leave it blank for no sound effect, or put in a valid sound-reference like this: [modID]:[soundeffectID]");
		DenyCreativeMode = _mainConfig.getBoolean("DenyCreativeMode", "main", DenyCreativeMode, "Set this to true to prevent even Server-OPs/Admins from placing forbidden blocks");
	}
	
	public boolean Reload()
	{
		_mainConfig.load(); // Reload file
		Init();
		return InitDefinedBlocks();
	}
	
	
	/**
	 * Init blockDefs
	 * @param pBlocks
	 */
	private boolean InitDefinedBlocks()
	{
		boolean tResult = true;
		try
		{
			List<BlockInfo> tNewLimitedBlocks = new ArrayList<BlockInfo>();	
			for (String tBlockDef : tConfiguredBlocks)
			{
				try
				{
					BlockInfo tBI = new BlockInfo(tBlockDef);
					tNewLimitedBlocks.add(tBI);
				}
				catch (Exception e)
				{
					tResult = false;
					BlockLimiter.Logger.error("An error occoured while parsing line " + tBlockDef + " and therfor it will be ignored");
					e.printStackTrace();
					continue;
				}
			}
			
			LimitedBlocks = tNewLimitedBlocks;
		}
		catch (Exception e)
		{
			BlockLimiter.Logger.error("An error occoured while parsing BlockConfigs. You probably did something wrong did you..?");
			BlockLimiter.Logger.DumpStack(e);			
		}
		return tResult;
	}

	@Override
	protected void PostInit() {
		InitDefinedBlocks();
	}

}
