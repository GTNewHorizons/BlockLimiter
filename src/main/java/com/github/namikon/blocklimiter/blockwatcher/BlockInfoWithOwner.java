
package com.github.namikon.blocklimiter.blockwatcher;


import java.util.UUID;

import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import net.minecraftforge.common.util.BlockSnapshot;


public class BlockInfoWithOwner
{
  private int _mDBID;
  private UniqueIdentifier _mBlockID;
  private int _mBlockMeta;
  private int _mLocationX;
  private int _mLocationY;
  private int _mLocationZ;
  private UUID _mOwner;
  private long _mPlaced;

  private BlockInfoWithOwner()
  {
    _mDBID = -1;
    _mBlockID = null;
    _mBlockMeta = -1;
    _mOwner = null;
    _mLocationX = -1;
    _mLocationY = -1;
    _mLocationZ = -1;
    _mPlaced = -1;
  }
  
  public BlockInfoWithOwner( UUID pPlayer )
  {
    this();
    _mOwner = pPlayer;
    _mPlaced = System.currentTimeMillis() / 1000L;
  }

  public BlockInfoWithOwner( int pDatabaseID, UUID pPlayer )
  {
    this();
    _mDBID = pDatabaseID;
    _mOwner = pPlayer;
  }

  public void setDBID( int pDBID )
  {
    if( _mDBID != -1 )
      return;

    _mDBID = pDBID;
  }

  public void setBlockID( UniqueIdentifier pBlockID, int pBlockMeta )
  {
    if( _mBlockID != null )
      return;

    _mBlockID = pBlockID;
    _mBlockMeta = pBlockMeta;
  }

  public void setPlaced( long pTimeStamp )
  {
    if( _mPlaced != -1 )
      return;

    _mPlaced = pTimeStamp;
  }

  public void setLocation( int pX, int pY, int pZ )
  {
    if( _mLocationX != -1 || _mLocationY != -1 || _mLocationZ != -1 )
      return;

    _mLocationX = pX;
    _mLocationY = pY;
    _mLocationZ = pZ;
  }

  public int getDBID()
  {
    return _mDBID;
  }

  public UniqueIdentifier getBlockIdentifier()
  {
    return _mBlockID;
  }

  public int getBlockMeta()
  {
    return _mBlockMeta;
  }

  public int getX()
  {
    return _mLocationX;
  }

  public int getY()
  {
    return _mLocationY;
  }

  public int getZ()
  {
    return _mLocationZ;
  }

  public UUID getOwner()
  {
    return _mOwner;
  }

  public long getPlacedTime()
  {
    return _mPlaced;
  }

}
