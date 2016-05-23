package com.github.namikon.blocklimiter.auxiliary;

import java.util.ArrayList;
import java.util.List;

import com.github.namikon.blocklimiter.BlockLimiter;

import eu.usrv.yamcore.auxiliary.IntHelper;
import eu.usrv.yamcore.auxiliary.ItemDescriptor;

public class ItemInfo {
	private ItemDescriptor _mItemDescriptor;

	private List<Integer> _mBannedDimensions;
	private boolean _mGlobalDenied = false;

	public ItemInfo(String pItemConfig)
	{
		_mBannedDimensions = new ArrayList<Integer>();
		InitBlockInfoInstance(pItemConfig);
	}

	public boolean isDenied(ItemDescriptor pID, int pDimensionID) 
	{
		boolean tResult = false;
		BlockLimiter.Logger.debug(String.format("Checking against item %s", _mItemDescriptor.toString()));

		if (_mItemDescriptor.equals(pID))
		{
			BlockLimiter.Logger.debug("Target Item found");
			if(_mBannedDimensions.contains(pDimensionID) || _mGlobalDenied)
				tResult = true;
		}

		BlockLimiter.Logger.debug("Result is " + tResult);
		return tResult;
	}

	public String getInfoString()
	{
		String tInfo = String.format("Item %s ", _mItemDescriptor.toString());
		if (_mGlobalDenied)
			tInfo += "[ALL]";
		else
		{
			tInfo += "[In DIM: ";

			boolean tFirst = true;
			for (Integer i : _mBannedDimensions)
			{
				if (tFirst)
					tFirst = false;
				else
					tInfo += ", ";
				tInfo += String.format("%d", i); 
			}
			tInfo += "]";
		}
		return tInfo;
	}

	private void InitBlockInfoInstance(String pItemConfig)
	{
		BlockLimiter.Logger.info("pItemConfig: " + pItemConfig);

		String[] tBlockInfoArray1 = pItemConfig.split(";");
		_mItemDescriptor = ItemDescriptor.fromString(tBlockInfoArray1[0]);

		if (_mItemDescriptor == null)
		{
			BlockLimiter.Logger.warn("ItemDefinition " + pItemConfig + " is invalid and will be ignored");
			throw new IllegalArgumentException(pItemConfig);
		}

		if (tBlockInfoArray1.length == 1)
		{
			BlockLimiter.Logger.info("New restrictive Item added: " + _mItemDescriptor.toString() + " Item is denied in ALL dimensions");
			_mGlobalDenied = true;
		}
		else
		{
			for (int i = 1; i < tBlockInfoArray1.length; i++)
			{
				if (IntHelper.tryParse(tBlockInfoArray1[i]))
					AddBlacklistedDim(Integer.parseInt(tBlockInfoArray1[i]));
			}

			BlockLimiter.Logger.info("New restrictive Item added: " + _mItemDescriptor.toString() + " Item is denied in " + _mBannedDimensions.size() + " dimension(s)");
		}
	}

	/**
	 * Adds a new dimID to the list of blacklisted dim 
	 * @param pDimID
	 */
	private void AddBlacklistedDim(int pDimID)
	{
		for (Integer i : _mBannedDimensions)
		{
			if (i == pDimID)
				return;
		}
		_mBannedDimensions.add(pDimID);
	}
}
