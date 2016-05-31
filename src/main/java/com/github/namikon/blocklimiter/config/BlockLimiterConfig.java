package com.github.namikon.blocklimiter.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.namikon.blocklimiter.BlockLimiter;
import com.github.namikon.blocklimiter.auxiliary.BlockInfo;
import com.github.namikon.blocklimiter.auxiliary.ItemInfo;

import eu.usrv.yamcore.auxiliary.enums.ItemEqualsCompareMethodEnum;
import eu.usrv.yamcore.config.ConfigManager;

public class BlockLimiterConfig extends ConfigManager {

	public BlockLimiterConfig(File pConfigBaseDirectory,
			String pModCollectionDirectory, String pModID) {
		super(pConfigBaseDirectory, pModCollectionDirectory, pModID);
	}

	public List<BlockInfo> LimitedBlocks = null;
	public List<ItemInfo> LimitedItems = null;
	private String tConfiguredBlocks[];
	private String tConfiguredItems[];
	public String[] RandomDenyMessages = null;
	public String[] RandomItemDenyMessages = null;
	public String SFXOnBlockDeny;
	public String SFXOnItemDeny;
	public boolean DenyCreativeMode;

	/**
	 * PreInit default values and lists
	 */
	@Override
	protected void PreInit()
	{
		LimitedBlocks = new ArrayList<BlockInfo>();
		LimitedItems = new ArrayList<ItemInfo>();
		RandomDenyMessages = new String[] {"You can't place that here", "You are too jelly to place this", "The block doesn't want to be here", "YOU SHALL NOT PLACE (this Block)", "*poof*"};
		RandomItemDenyMessages = new String[] {"You can't use that here", "You are too jelly to use this", "The item seems to be broken...", "YOU SHALL NOT USE (this Item)"};
		SFXOnBlockDeny = "minecraft:ambient.weather.thunder";
		SFXOnItemDeny = "minecraft:ambient.weather.thunder";
		DenyCreativeMode = false;
	}

	protected void Init()
	{
		tConfiguredBlocks = _mainConfig.getStringList("BlockList", "Main", new String[] {}, "Define your Blocks here. Syntax is: [modID]:[BlockID];[DimID];... if you don't add a Dimension (e.g. minecraft:dirt) it will be denied in every dimension");
		tConfiguredItems = _mainConfig.getStringList("ItemList", "Main", new String[] {}, "Define your Items here. Syntax is: [modID]:[ItemID];[DimID];... if you don't add a Dimension (e.g. minecraft:dirt instead of minecraft:dirt;12) it will be denied in every dimension");
		RandomDenyMessages = _mainConfig.getStringList("RejectMessages", "Main", RandomDenyMessages, "Define a few reject messages that are being sent to the player if they try to place a monitored Block");
		RandomItemDenyMessages = _mainConfig.getStringList("ItemRejectMessages", "Main", RandomItemDenyMessages, "Define a few reject messages that are being sent to the player if they try to use a monitored item");
		SFXOnBlockDeny = _mainConfig.getString("PlaySFXOnBlockDeny", "main", SFXOnBlockDeny, "Leave it blank for no sound effect, or put in a valid sound-reference like this: [modID]:[soundeffectID]");
		SFXOnItemDeny = _mainConfig.getString("PlaySFXOnItemDeny", "main", SFXOnItemDeny, "Leave it blank for no sound effect, or put in a valid sound-reference like this: [modID]:[soundeffectID]");
		DenyCreativeMode = _mainConfig.getBoolean("DenyCreativeMode", "main", DenyCreativeMode, "Set this to true to prevent even Server-OPs/Admins from placing forbidden blocks and using forbidden items");
	}

	public boolean Reload()
	{
		_mainConfig.load(); // Reload file
		Init();
		boolean tState = true;
		if (!InitDefinedBlocks())
			tState = false;
		if (!InitDefinedItems())
			tState = false;
		
		return tState;
	}

	/**
	 * Init blockDefs
	 * @param pBlocks
	 */
	private boolean InitDefinedItems()
	{
		boolean tResult = true;
		try
		{
			List<ItemInfo> tNewLimitedItems = new ArrayList<ItemInfo>();	
			for (String tItemDef : tConfiguredItems)
			{
				try
				{
					ItemInfo tII = new ItemInfo(tItemDef);
					tNewLimitedItems.add(tII);
				}
				catch (Exception e)
				{
					tResult = false;
					BlockLimiter.Logger.error("An error occoured while parsing line " + tItemDef + " and therfor it will be ignored");
					e.printStackTrace();
					continue;
				}
			}

			LimitedItems = tNewLimitedItems;
		}
		catch (Exception e)
		{
			BlockLimiter.Logger.error("An error occoured while parsing ItemConfigs. You probably did something wrong did you..?");
			BlockLimiter.Logger.DumpStack(e);			
		}
		return tResult;
	}
	
	/**
	 * Adds a new limited Item to the list of limited items.
	 * 
	 * @param pItemConfig The new limited item. If the item already exists, the dimensions are updated
	 * @return true if the pItemConfig could be parsed, false if not.
	 */
	public boolean IMC_AddLimitedItem(String pItemConfig)
	{
		boolean tResult = true;
		List<ItemInfo> tNewLimitedItems = new ArrayList<ItemInfo>();
		
		try
		{
			// Try to parse received config-line. Will raise an exception if the syntax is wrong
			ItemInfo tII = new ItemInfo(pItemConfig);

			boolean tItemAdded = false;
			// Check if we already know this particular item
			for (ItemInfo iiIter : LimitedItems)
			{
				// If the found item equals the new one, use the new one instead; "Replacing" the existing one
				if (iiIter.getDescriptor().isEqualTo(tII.getDescriptor(), ItemEqualsCompareMethodEnum.Exact))
				{
					tItemAdded = true;
					tNewLimitedItems.add(tII);
				}
				else // if it doesn't equal, just skip
					tNewLimitedItems.add(iiIter);
			}
			
			// Our loop finished. Check if we replaced any existing entries for this item. if not, add it as new one
			if (!tItemAdded)
				tNewLimitedItems.add(tII);
		}
		catch (Exception e)
		{
			tResult = false;
		}
		
		if (tResult)
			LimitedItems = tNewLimitedItems;
		
		return tResult;
	}
	
	/**
	 * Adds a new limited Block to the list of limited blocks.
	 * 
	 * @param pBlockConfig The new limited item. If the item already exists, the dimensions are updated
	 * @return true if pBlockConfig could be parsed, false if not.
	 */
	public boolean IMC_AddLimitedBlock(String pBlockConfig)
	{
		boolean tResult = true;
		List<BlockInfo> tNewLimitedBlocks = new ArrayList<BlockInfo>();
		
		try
		{
			// Try to parse received config-line. Will raise an exception if the syntax is wrong
			BlockInfo tBI = new BlockInfo(pBlockConfig);

			boolean tBlockAdded = false;
			// Check if we already know this particular item
			for (BlockInfo iiIter : LimitedBlocks)
			{
				// If the found block equals the new one, use the new one instead; "Replacing" the existing one
				if (iiIter.getDescriptor().equals(tBI.getDescriptor()))
				{
					tBlockAdded = true;
					tNewLimitedBlocks.add(tBI);
				}
				else // if it doesn't equal, just skip
					tNewLimitedBlocks.add(iiIter);
			}
			
			// Our loop finished. Check if we replaced any existing entries for this block. if not, add it as new one
			if (!tBlockAdded)
				tNewLimitedBlocks.add(tBI);
		}
		catch (Exception e)
		{
			tResult = false;
		}
		
		if (tResult)
			LimitedBlocks = tNewLimitedBlocks;
		
		return tResult;
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
		InitDefinedItems();
	}

}
