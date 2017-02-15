
package com.github.namikon.blocklimiter.xmlconfig;


import com.github.namikon.blocklimiter.xmlconfig.BlockLimits.BlockLimit;
import com.github.namikon.blocklimiter.xmlconfig.BlockLimits.BlockLimit.Limit;


public class BlockLimitsFactory
{
  public BlockLimit createBlockLimit( String pIdentifier, boolean pInvertLogic, String pCustomDenyString )
  {
    BlockLimit tLimit = new BlockLimit();
    tLimit.mIdentifier = pIdentifier;
    tLimit.mInvertAllowLogic = pInvertLogic;
    tLimit.mCustomDeny = pCustomDenyString;

    return tLimit;
  }

  public Limit createLimit( int pMaxPP, String pCustomDenyString, int pTargetDim, boolean pIsServerWide )
  {
    Limit tObject = new Limit();
    tObject.mAmountPerPlayer = pMaxPP;
    tObject.mCustomDeny = pCustomDenyString;
    tObject.mDimensionID = pTargetDim;
    tObject.mIsServerWide = pIsServerWide;

    return tObject;
  }
}
