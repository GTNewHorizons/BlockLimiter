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
	
	
	 /**
	 * PreInit default values and lists
	 */
	 @Override
	protected void PreInit()
	{
		LimitedBlocks = new ArrayList<BlockInfo>(); 
	}
	
	protected void Init()
	{
		tConfiguredBlocks = _mainConfig.getStringList("BlockList", "Main", new String[] {}, "Define your Blocks here. Syntax is: [modID]:[BlockID];[DimID];[DimID];[DimID];...");
	}
	
	public boolean Reload()
	{
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
