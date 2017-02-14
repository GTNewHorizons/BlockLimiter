
package com.github.namikon.blocklimiter.db;


import eu.usrv.yamcore.auxiliary.LogHelper;
import eu.usrv.yamcore.datasource.DatasourceSQL;
import eu.usrv.yamcore.datasource.Schema;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.github.namikon.blocklimiter.BlockLimiter;
import com.github.namikon.blocklimiter.blockwatcher.BlockInfoWithOwner;

import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;


public class BLDataSource extends DatasourceSQL
{
  public BLDataSource( LogHelper pLogger, Schema pSchema, String pHost, String pDBUser, String pDBPasswd, String pDBName )
  {
    super( pLogger, pSchema, pHost, pDBUser, pDBPasswd, pDBName );
  }

  @Override
  public boolean loadAll()
  {
    return loadBlocks();
  }

  @Override
  public boolean checkAll()
  {
    return false;
  }

  private boolean loadBlocks()
  {
    try
    {
      PreparedStatement loadBlocksStatement = prepare( "SELECT * FROM Blocks", true );
      ResultSet rs = loadBlocksStatement.executeQuery();

      while( rs.next() )
      {
        UUID tOwner = UUID.fromString( rs.getString( "owner" ) );

        BlockInfoWithOwner tBlock = new BlockInfoWithOwner( rs.getInt( "ID" ), tOwner );
        UniqueIdentifier tID = new UniqueIdentifier( rs.getString( "blockName" ) );
        int tBlockMeta = rs.getInt( "blockMeta" );
        tBlock.setBlockID( tID, tBlockMeta );

        tBlock.setLocation( rs.getInt( "x" ), rs.getInt( "y" ), rs.getInt( "z" ) );
        tBlock.setPlaced( rs.getLong( "placed" ) );
        BlockLimiter.instance.BWatcher.AddExistingBlock( rs.getInt( "dim" ), tBlock );
      }
    }
    catch( SQLException e )
    {
      BlockLimiter.Logger.error( "Failed to load Blocks from SQL!" );
      BlockLimiter.Logger.error( ExceptionUtils.getStackTrace( e ) );
      return false;
    }

    return true;
  }

}
