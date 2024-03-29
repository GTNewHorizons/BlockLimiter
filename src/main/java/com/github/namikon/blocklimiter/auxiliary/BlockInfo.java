package com.github.namikon.blocklimiter.auxiliary;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;

import com.github.namikon.blocklimiter.BlockLimiter;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import eu.usrv.yamcore.auxiliary.IntHelper;

public class BlockInfo {

    private String _mFQBN;
    private String _mModID;
    private String _mBlockName;

    private List<Integer> _mBannedDimensions;
    private boolean _mGlobalDenied = false;

    public BlockInfo(String pBlockConfig) {
        _mBannedDimensions = new ArrayList<Integer>();
        InitBlockInfoInstance(pBlockConfig);
    }

    public boolean isDenied(UniqueIdentifier pUID, int pDimensionID) {
        boolean tResult = false;
        BlockLimiter.Logger.debug(String.format("Checking against block %s:%s", _mModID, _mBlockName));

        if (pUID.modId.equalsIgnoreCase(_mModID) && pUID.name.equalsIgnoreCase(_mBlockName)) {
            BlockLimiter.Logger.debug("Target Block found, ModID and Name match");
            if (_mBannedDimensions.contains(pDimensionID) || _mGlobalDenied) tResult = true;
        }

        BlockLimiter.Logger.debug("Result is " + tResult);
        return tResult;
    }

    public String getInfoString() {
        String tInfo = String.format("Block %s:%s ", _mModID, _mBlockName);
        if (_mGlobalDenied) tInfo += "[ALL]";
        else {
            tInfo += "[In DIM: ";

            boolean tFirst = true;
            for (Integer i : _mBannedDimensions) {
                if (tFirst) tFirst = false;
                else tInfo += ", ";
                tInfo += String.format("%d", i);
            }
            tInfo += "]";
        }
        return tInfo;
    }

    private void InitBlockInfoInstance(String pBlockConfig) {
        BlockLimiter.Logger.debug("pBlockConfig: " + pBlockConfig);

        String[] tBlockInfoArray1 = pBlockConfig.split(";");
        _mFQBN = tBlockInfoArray1[0];

        String[] tBlockInfoArray2 = _mFQBN.split(":");

        if (tBlockInfoArray2.length < 2) {
            BlockLimiter.Logger.warn("BlockDefinition " + pBlockConfig + " is invalid and will be ignored");
            throw new IllegalArgumentException(pBlockConfig);
        }

        _mModID = tBlockInfoArray2[0];
        _mBlockName = tBlockInfoArray2[1];

        Block tTestBlock = GameRegistry.findBlock(_mModID, _mBlockName);
        if (tTestBlock == null) {
            BlockLimiter.Logger.warn("The Block " + _mFQBN + " can't be found in the Gameregistry and will be ignored");
            throw new IllegalArgumentException(pBlockConfig);
        }

        if (tBlockInfoArray1.length == 1) {
            BlockLimiter.Logger.debug(
                    "New restrictive Block added: " + tTestBlock.getUnlocalizedName()
                            + " Block is denied in ALL dimensions");
            _mGlobalDenied = true;
        } else {
            for (int i = 1; i < tBlockInfoArray1.length; i++) {
                if (IntHelper.tryParse(tBlockInfoArray1[i])) AddBlacklistedDim(Integer.parseInt(tBlockInfoArray1[i]));
            }

            BlockLimiter.Logger.debug(
                    "New restrictive Block added: " + tTestBlock.getUnlocalizedName()
                            + " Block is denied in "
                            + _mBannedDimensions.size()
                            + " dimension(s)");
        }
    }

    /**
     * Adds a new dimID to the list of blacklisted dim
     * 
     * @param pDimID
     */
    private void AddBlacklistedDim(int pDimID) {
        for (Integer i : _mBannedDimensions) {
            if (i == pDimID) return;
        }
        _mBannedDimensions.add(pDimID);
    }
}
