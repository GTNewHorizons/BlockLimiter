
package com.github.namikon.blocklimiter.blockwatcher;


import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.github.namikon.blocklimiter.BlockLimiter;

import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import net.minecraftforge.common.util.BlockSnapshot;


public class BlockWatcher
{
  private ArrayList<DimensionWrapper> _mBlockDimensions;

  public BlockWatcher()
  {
    _mBlockDimensions = new ArrayList<DimensionWrapper>();
  }

  public void addPlacedBlock( UUID pPlayer, BlockSnapshot pBlock )
  {
    getOrCreateDimensionWrapper( pBlock.dimId ).getBlockList().add( new BlockInfoWithOwner( pPlayer, pBlock ) );
  }

  public boolean canBreakBlock( UUID pPlayer, int pDimensionID, int pX, int pY, int pZ )
  {
    boolean tResult = true;

    DimensionWrapper dw = getOrCreateDimensionWrapper( pDimensionID );

    if( dw.getBlockList().size() > 0 )
    {
      for( BlockInfoWithOwner biwo : dw.getBlockList() )
      {
        if( biwo.getX() == pX && biwo.getY() == pY && biwo.getZ() == pZ )
        {
          tResult = biwo.getOwner().equals( pPlayer );
          break;
        }
      }
    }

    return tResult;
  }

  public int getPlacedBlockCount( UUID pPlayer, int pDimensionID, UniqueIdentifier pBlockID, int pBlockMeta )
  {
    DimensionWrapper dw = getOrCreateDimensionWrapper( pDimensionID );
    int tCount = 0;

    for( int i = 0; i < dw.getBlockList().size(); i++ )
    {
      if( dw.getBlockList().get( i ).getOwner().equals( pPlayer ) )
      {
        if( dw.getBlockList().get( i ).getBlockIdentifier().equals( pBlockID ) )
        {
          if( dw.getBlockList().get( i ).getBlockMeta() == pBlockMeta )
            tCount++;
        }
      }
    }

    return tCount;
  }

  private DimensionWrapper getOrCreateDimensionWrapper( int pDimensionID )
  {
    DimensionWrapper dw = null;
    for( int i = 0; i < _mBlockDimensions.size(); i++ )
    {
      if( _mBlockDimensions.get( i ).getDimensionID() == pDimensionID )
        dw = _mBlockDimensions.get( i );
    }

    if( dw == null )
    {
      dw = new DimensionWrapper( pDimensionID );
      _mBlockDimensions.add( dw );
    }

    return dw;
  }

  public void AddExistingBlock(int pDimensionID, BlockInfoWithOwner pBlock)
  {
    getOrCreateDimensionWrapper( pDimensionID ).getBlockList().add( pBlock );
  }
  
  public int getPlacedBlockCount( UUID uniqueID, BlockSnapshot blockSnapshot )
  {
    return getPlacedBlockCount( uniqueID, blockSnapshot.dimId, blockSnapshot.blockIdentifier, blockSnapshot.meta );
  }

  public void removeBlock( int pDimensionID, int pX, int pY, int pZ )
  {
    DimensionWrapper dw = getOrCreateDimensionWrapper( pDimensionID );
    int tRemoveIDX = -1;

    if( dw.getBlockList().size() > 0 )
    {
      for( int i = 0; i < dw.getBlockList().size(); i++ )
      {
        BlockInfoWithOwner biwo = dw.getBlockList().get( i );
        if( biwo.getX() == pX && biwo.getY() == pY && biwo.getZ() == pZ )
        {
          tRemoveIDX = i;
          break;
        }
      }
      if( tRemoveIDX > -1 )
      {
        dw.getBlockList().remove( tRemoveIDX );
        BlockLimiter.Logger.debug( String.format( "Block at %d-%d-%d dim %d removed from tracking list", pX, pY, pZ, pDimensionID ) );
      }
    }
  }
}
