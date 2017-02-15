
package com.github.namikon.blocklimiter.xmlconfig;


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.github.namikon.blocklimiter.BlockLimiter;
import com.github.namikon.blocklimiter.auxiliary.Reference;
import com.github.namikon.blocklimiter.xmlconfig.BlockLimits.BlockLimit;
import com.github.namikon.blocklimiter.xmlconfig.BlockLimits.BlockLimit.Limit;

import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import eu.usrv.yamcore.auxiliary.ItemDescriptor;
import eu.usrv.yamcore.auxiliary.LogHelper;
import net.minecraft.item.EnumRarity;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import scala.collection.parallel.ParIterableLike.Drop;


public class BlockLimitsHandler
{
  private LogHelper _mLogger = BlockLimiter.Logger;
  private String _mConfigFileName;
  private BlockLimitsFactory _mBLF = new BlockLimitsFactory();

  private BlockLimits _mBlockLimits = null;

  private boolean _mInitialized = false;

  public List<BlockLimit> getBlockLimits()
  {
    return _mBlockLimits.getBlocks();
  }

  public boolean reload()
  {
    boolean tState = ReloadBlockLimits();
    if( _mInitialized )
    {
      if( !tState )
        _mLogger.error( "[BlockLimits] Reload of BlockLimits file failed" );
    }
    return tState;
  }

  public BlockLimit getBlockLimitObjectForUID( UniqueIdentifier pBlockUID )
  {
    for( BlockLimit bl : _mBlockLimits.getBlocks() )
    {
      if( bl.matches( pBlockUID ) )
        return bl;
    }

    return null;
  }

  /**
   * Verify loaded config before going live
   * 
   * @param pLimitsToCheck
   * @return Result of checked element to verify. Returns false if any errors are found
   */
  private boolean VerifyConfig( BlockLimits pLimitsToCheck )
  {
    boolean tSuccess = true;
    List<String> tKnownIdentifiers = new ArrayList<String>();

    for( BlockLimit X : pLimitsToCheck.getBlocks() )
    {
      if( tKnownIdentifiers.contains( X.getIdentifier() ) )
      {
        _mLogger.error( String.format( "[BlockLimits] Multiple definitions found for limited Block [%s]", X.getIdentifier() ) );
        tSuccess = false;
      }
      else
        tKnownIdentifiers.add( X.getIdentifier() );

      if( X.getLimits().size() == 0 && X.getInvertAllowLogic() )
      {
        _mLogger.error( String.format( "[BlockLimits] Limited Block [%s] is useless. InvertLogic is true, but no limits are defined]", X.getIdentifier() ) );
        tSuccess = false;
      }

      List<Integer> tKnownDimensions = new ArrayList<Integer>();
      boolean tIsServerWide = false;
      for( Limit Y : X.getLimits() )
      {
        // < 0 is reserved for "value not set"
        if( Y.getMaxPerPlayer() < 0 )
        {
          _mLogger.error( String.format( "[BlockLimits] BlockPerPlayer cannot be below Zero! Block: [%s]", X.getIdentifier() ) );
          tSuccess = false;
        }

        // Check that only one limit is configured with serverwide, and only one exists if it is serverwide
        if( Y.getIsServerSideLimit() )
        {
          if( !tIsServerWide && tKnownDimensions.size() == 0 )
            tIsServerWide = true;
          else
          {
            _mLogger.error( String.format( "[BlockLimits] Multiple definitions found for serverwide limits in limited Block [%s]", X.getIdentifier() ) );
            tSuccess = false;
          }
        }
        else
        {
          if( tIsServerWide )
          {
            _mLogger.error( String.format( "[BlockLimits] Found Serverwide and non-serverwide limits in limited Block [%s]", X.getIdentifier() ) );
            tSuccess = false;
          }
        }

        // Check if dimension IDs are double
        if( tKnownDimensions.contains( Y.getTargetDimension() ) )
        {
          _mLogger.error( String.format( "[BlockLimits] Multiple dimension definitions found for limited Block [%s]", X.getIdentifier() ) );
          tSuccess = false;
        }
        tKnownDimensions.add( Y.getTargetDimension() );
      }

    }
    return tSuccess;
  }

  /**
   * (Re)load BlockLimits
   * 
   * @return True on success
   */
  private boolean ReloadBlockLimits()
  {
    boolean tResult = false;

    try
    {
      JAXBContext tJaxbCtx = JAXBContext.newInstance( BlockLimits.class );
      Unmarshaller jaxUnmarsh = tJaxbCtx.createUnmarshaller();

      BlockLimits tNewItemCollection = null;
      File tConfigFile = new File( _mConfigFileName );
      tNewItemCollection = (BlockLimits) jaxUnmarsh.unmarshal( tConfigFile );

      if( !VerifyConfig( tNewItemCollection ) )
      {
        _mLogger.error( "[BlockLimits] New config will NOT be activated. Please check your error-log and try again" );
        tResult = false;
      }
      else
      {
        _mBlockLimits = tNewItemCollection;

        tResult = true;
      }

    }
    catch( Exception e )
    {
      e.printStackTrace();
    }

    return tResult;
  }

  /**
   * Init sample configuration if none could be found
   */
  public void InitSampleConfig()
  {
    Limit tOverWorldLimit20 = _mBLF.createLimit( 20, "", 0, false );
    Limit tServerWideLimit50 = _mBLF.createLimit( 50, "", 0, true );

    BlockLimit tLimitStone = _mBLF.createBlockLimit( "minecraft:stone", false, "" );
    BlockLimit tLimitCobble = _mBLF.createBlockLimit( "minecraft:cobblestone", false, "" );

    tLimitStone.getLimits().add( tOverWorldLimit20 );
    tLimitCobble.getLimits().add( tServerWideLimit50 );

    _mBlockLimits = new BlockLimits();
    _mBlockLimits.getBlocks().add( tLimitCobble );
    _mBlockLimits.getBlocks().add( tLimitStone );
  }

  public BlockLimitsHandler( File pConfigBaseDir )
  {
    File tConfDir = new File( pConfigBaseDir, Reference.COLLECTIONNAME );
    if( !tConfDir.exists() )
      tConfDir.mkdirs();

    _mConfigFileName = new File( tConfDir, "BlockLimits.xml" ).toString();
  }

  /**
   * Save the loot configuration to disk
   * 
   * @return True on success
   */
  public boolean SaveBlockLimits()
  {
    try
    {
      JAXBContext tJaxbCtx = JAXBContext.newInstance( BlockLimits.class );
      Marshaller jaxMarsh = tJaxbCtx.createMarshaller();
      jaxMarsh.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
      jaxMarsh.marshal( _mBlockLimits, new FileOutputStream( _mConfigFileName, false ) );

      return true;
    }
    catch( Exception e )
    {
      _mLogger.error( "[BlockLimiter] Unable to create new LootBags.xml. Is the config directory write protected?" );
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Load the configuration from disk. Will not overwrite the existing BlockList if errors occour Only called
   * ONCE! Upon PostLoad(). Call reload() instead if you want to reload the config on a server
   */
  public void LoadConfig()
  {
    if( _mInitialized )
    {
      _mLogger.error( "[BlockLimiter] Something just called LoadConfig AFTER it has been initialized!" );
      return;
    }

    File tConfigFile = new File( _mConfigFileName );
    if( !tConfigFile.exists() )
    {
      InitSampleConfig();
      BlockLimiter.AdminLogonErrors.AddErrorLogOnAdminJoin( "[BlockLimiter] Default config file is active!" );
      BlockLimiter.AdminLogonErrors.AddErrorLogOnAdminJoin( "[BlockLimiter] Check the ServerLog ASAP" );
      SaveBlockLimits();
    }

    // Fix for broken XML file; If it can't be loaded on reboot, keep it
    // there to be fixed, but load
    // default setting instead, so an Op/Admin can do reload ingame
    if( !ReloadBlockLimits() )
    {
      _mLogger.warn( "[BlockLimiter] Configuration File seems to be damaged, nothing will be loaded!" );
      BlockLimiter.AdminLogonErrors.AddErrorLogOnAdminJoin( "[BlockLimiter] Default config file is active!" );
      BlockLimiter.AdminLogonErrors.AddErrorLogOnAdminJoin( "[BlockLimiter] Check the ServerLog ASAP" );

      InitSampleConfig();
    }
    _mInitialized = true;
  }
}
