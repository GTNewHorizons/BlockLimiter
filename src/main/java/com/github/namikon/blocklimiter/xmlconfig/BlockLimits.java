
package com.github.namikon.blocklimiter.xmlconfig;


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;


@XmlAccessorType( XmlAccessType.FIELD )
@XmlRootElement( name = "BlockLimits" )
public class BlockLimits
{
  @XmlElement( name = "Block" )
  private List<BlockLimits.BlockLimit> mBlockList;

  private void Init()
  {
    if( mBlockList == null )
      mBlockList = new ArrayList<BlockLimits.BlockLimit>();
  }

  public List<BlockLimits.BlockLimit> getBlocks()
  {
    Init();
    return mBlockList;
  }

  @XmlAccessorType( XmlAccessType.FIELD )
  @XmlType
  public static class BlockLimit
  {
    @XmlAttribute( name = "identifier" )
    protected String mIdentifier;

    @XmlAttribute( name = "invertAllowLogic" )
    protected boolean mInvertAllowLogic;

    @XmlAttribute( name = "customDenyMessage" )
    protected String mCustomDeny;

    @XmlElement( name = "Limit" )
    private List<BlockLimits.BlockLimit.Limit> mLimits;

    @XmlTransient
    private UniqueIdentifier tUID;

    private UniqueIdentifier getUniqueIdentifier()
    {
      if( tUID == null )
        tUID = new UniqueIdentifier( mIdentifier );

      return tUID;
    }

    public int getMaxAmountForDimension(int pDimensionID)
    {
      int tReturn = -1;
      for (Limit l : getLimits())
      {
        if (l.getIsServerSideLimit() || l.getTargetDimension() == pDimensionID)
          tReturn = l.getMaxPerPlayer();
      }
      
      return tReturn;
    }
    
    public boolean matches( UniqueIdentifier pUID )
    {
      return getUniqueIdentifier().equals( pUID );
    }

    public List<BlockLimits.BlockLimit.Limit> getLimits()
    {
      Init();
      return mLimits;
    }

    private void Init()
    {
      if( mLimits == null )
        mLimits = new ArrayList<BlockLimits.BlockLimit.Limit>();
    }

    public String getIdentifier()
    {
      return mIdentifier;
    }

    public boolean getInvertAllowLogic()
    {
      return mInvertAllowLogic;
    }

    public String getCustomDenyMessage()
    {
      return mCustomDeny;
    }
    
    public String getCustomDenyMessage(int pDimensionID)
    {
      String tDenyMessage = getCustomDenyMessage();
      for (Limit l : getLimits())
      {
        if (l.getIsServerSideLimit() || l.getTargetDimension() == pDimensionID)
          tDenyMessage = l.getCustomDenyMessage();
      }
      
      return mCustomDeny;
    }

    @XmlAccessorType( XmlAccessType.FIELD )
    @XmlType
    public static class Limit
    {
      @XmlAttribute( name = "isServerWide" )
      protected boolean mIsServerWide;

      @XmlAttribute( name = "dimensionID" )
      protected int mDimensionID;

      @XmlAttribute( name = "amountPerPlayer" )
      protected int mAmountPerPlayer;

      @XmlAttribute( name = "customDenyMessage" )
      protected String mCustomDeny;

      public String getCustomDenyMessage()
      {
        return mCustomDeny;
      }

      public int getTargetDimension()
      {
        return mDimensionID;
      }

      public int getMaxPerPlayer()
      {
        return mAmountPerPlayer;
      }

      public boolean getIsServerSideLimit()
      {
        return mIsServerWide;
      }
    }
  }
}