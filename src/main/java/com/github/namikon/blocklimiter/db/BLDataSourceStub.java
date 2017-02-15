
package com.github.namikon.blocklimiter.db;


import java.util.ArrayList;
import java.util.UUID;

import com.github.namikon.blocklimiter.blockwatcher.BlockInfoWithOwner;

import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;


public class BLDataSourceStub implements IBLDataSource
{

  @Override
  public boolean saveBlock( int pDimensionID, BlockInfoWithOwner pBIWO )
  {
    return true;
  }

  @Override
  public boolean deleteBlock( int pDatabaseID )
  {
    return true;
  }
  
  @Override
  public boolean isDummy()
  {
    return true;
  }

  @Override
  public ArrayList<String> getBlockReportInDim( UniqueIdentifier pUIDFromhand, UUID pPlayerUID, int pDimension )
  {
    return new ArrayList<String>();
  }

  @Override
  public ArrayList<String> getBlockReport( UniqueIdentifier pUIDFromhand, UUID pPlayerUID )
  {
    return new ArrayList<String>();
  }
}
