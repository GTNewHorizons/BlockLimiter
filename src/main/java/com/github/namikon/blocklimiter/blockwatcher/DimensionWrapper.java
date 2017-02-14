
package com.github.namikon.blocklimiter.blockwatcher;


import java.util.ArrayList;


public class DimensionWrapper
{
  private int _mDimensionID;
  private ArrayList<BlockInfoWithOwner> _mPlacedBlocks;

  public DimensionWrapper( int pDimensionID )
  {
    _mDimensionID = pDimensionID;
  }

  public int getDimensionID()
  {
    return _mDimensionID;
  }

  public ArrayList<BlockInfoWithOwner> getBlockList()
  {
    if( _mPlacedBlocks == null )
      _mPlacedBlocks = new ArrayList<BlockInfoWithOwner>();

    return _mPlacedBlocks;
  }
}