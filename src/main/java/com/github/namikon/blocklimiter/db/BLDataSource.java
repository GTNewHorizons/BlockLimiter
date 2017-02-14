
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
import com.github.namikon.blocklimiter.blockwatcher.DimensionWrapper;

import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;


public class BLDataSource extends DatasourceSQL
{
  public BLDataSource( LogHelper pLogger, Schema pSchema, String pHost,
      String pDBUser, String pDBPasswd, String pDBName )
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

  public boolean deleteBlock( int pDatabaseID )
  {
    if( pDatabaseID < 0 )
      return true;
    try
    {
      PreparedStatement deleteBlockStatement = prepare( "DELETE FROM Blocks WHERE `ID`=?", true );
      deleteBlockStatement.setInt( 1, pDatabaseID );
      deleteBlockStatement.execute();
    }
    catch( SQLException e )
    {
      BlockLimiter.Logger.error( "Failed to save Block to SQL!" );
      BlockLimiter.Logger.error( ExceptionUtils.getStackTrace( e ) );
      return false;
    }

    return true;
  }

  public boolean saveBlock( int pDimensionID, BlockInfoWithOwner pBIWO )
  {
    try
    {
      PreparedStatement insertStatement = prepare( "INSERT INTO Blocks (`owner`, `blockName`, `blockMeta`, `x`, `y`, `z`, `dim`, `placed`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", true );
      insertStatement.setString( 1, pBIWO.getOwner().toString() );
      insertStatement.setString( 2, pBIWO.getBlockIdentifier().toString() );
      insertStatement.setInt( 3, pBIWO.getBlockMeta() );
      insertStatement.setInt( 4, pBIWO.getX() );
      insertStatement.setInt( 5, pBIWO.getY() );
      insertStatement.setInt( 6, pBIWO.getZ() );
      insertStatement.setInt( 7, pDimensionID );
      insertStatement.setLong( 8, pBIWO.getPlacedTime() );

      insertStatement.executeUpdate();

      ResultSet keys = insertStatement.getGeneratedKeys();
      if( keys.next() )
        pBIWO.setDBID( keys.getInt( 1 ) );

      return true;
    }
    catch( SQLException e )
    {
      BlockLimiter.Logger.error( "Failed to save Block to SQL!" );
      BlockLimiter.Logger.error( ExceptionUtils.getStackTrace( e ) );
      return false;
    }
  }
}
