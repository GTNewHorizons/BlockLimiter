
package com.github.namikon.blocklimiter.db;


import java.util.ArrayList;
import java.util.UUID;

import com.github.namikon.blocklimiter.blockwatcher.BlockInfoWithOwner;

import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;


public interface IBLDataSource
{
  boolean saveBlock( int pDimensionID, BlockInfoWithOwner pBIWO );

  boolean deleteBlock( int pDatabaseID );
  boolean isDummy();

  ArrayList<String> getBlockReportInDim( UniqueIdentifier pUIDFromhand, UUID pPlayerUID, int pDimension );
  ArrayList<String> getBlockReport( UniqueIdentifier pUIDFromhand, UUID pPlayerUID );
}
