
package com.github.namikon.blocklimiter.db;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.github.namikon.blocklimiter.BlockLimiter;
import com.github.namikon.blocklimiter.blockwatcher.BlockInfoWithOwner;

import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import eu.usrv.yamcore.auxiliary.LogHelper;
import eu.usrv.yamcore.datasource.DatasourceSQL;
import eu.usrv.yamcore.datasource.Schema;


public class BLDataSource extends DatasourceSQL implements IBLDataSource
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
      int tAdded = 0;
      
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
        tAdded++;
      }
      
      BlockLimiter.Logger.info( String.format("Loaded %d Block locations from Database", tAdded) );
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

  @Override
  public boolean isDummy()
  {
    return false;
  }

  @Override
  public ArrayList<String> getBlockReportInDim( UniqueIdentifier pUIDFromhand, UUID pPlayerUID, int pDimension )
  {
    ArrayList<String> tRetLst = new ArrayList<String>();
    try
    {
      PreparedStatement countBlockStatement = prepare( "SELECT CONCAT('x: ', `x`,' y: ', `y`,' z: ', `z`) FROM blocks WHERE `owner` = ? AND `blockname` = ? AND `dim` = ?", true );
      countBlockStatement.setString( 1, pPlayerUID.toString() );
      countBlockStatement.setString( 2, pUIDFromhand.toString() );
      countBlockStatement.setInt( 3, pDimension );
      
      ResultSet rs = countBlockStatement.executeQuery();

      while( rs.next() )
        tRetLst.add(rs.getString( 1 ));
    }
    catch( SQLException e )
    {
      BlockLimiter.Logger.error( "Failed to save Block to SQL!" );
      BlockLimiter.Logger.error( ExceptionUtils.getStackTrace( e ) );
      tRetLst.add( "DatabaseError. Contact Admin" );
    }
    return tRetLst;
  }
  
  @Override
  public ArrayList<String> getBlockReport( UniqueIdentifier pUIDFromhand, UUID pPlayerUID )
  {
    ArrayList<String> tRetLst = new ArrayList<String>();
    try
    {
      PreparedStatement countBlockStatement = prepare( "SELECT dim, COUNT(*) FROM blocks WHERE `owner` = ? AND `blockname` = ? GROUP BY `dim`", true );
      countBlockStatement.setString( 1, pPlayerUID.toString() );
      countBlockStatement.setString( 2, pUIDFromhand.toString() );
      ResultSet rs = countBlockStatement.executeQuery();

      while( rs.next() )
        tRetLst.add(String.format( "DimID: %d Placed: %d", rs.getInt( 1 ), rs.getInt( 2 )));
    }
    catch( SQLException e )
    {
      BlockLimiter.Logger.error( "Failed to save Block to SQL!" );
      BlockLimiter.Logger.error( ExceptionUtils.getStackTrace( e ) );
      tRetLst.add( "DatabaseError. Contact Admin" );
    }
    return tRetLst;
  }
}
