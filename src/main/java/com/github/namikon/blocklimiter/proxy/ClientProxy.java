
package com.github.namikon.blocklimiter.proxy;

import javax.management.RuntimeErrorException;

import com.github.namikon.blocklimiter.BlockLimiter;
import com.github.namikon.blocklimiter.auxiliary.Reference;

import eu.usrv.yamcore.YAMCore;

public class ClientProxy extends CommonProxy
{
  @Override
  public void registerProxy()
  {
    if (!YAMCore.isDebug())
      throw new RuntimeErrorException( null, String.format( "You are trying to run a SERVERSIDE mod on a client! Remove %s from your mods folder and try again", Reference.MODID ));
    else
      BlockLimiter.Logger.info( "CRASH! No jk. Have fun Debugging!" );
  }
}
