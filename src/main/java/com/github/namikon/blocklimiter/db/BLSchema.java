package com.github.namikon.blocklimiter.db;

import eu.usrv.yamcore.datasource.Schema;
import eu.usrv.yamcore.datasource.bridge.BridgeSQL;

public class BLSchema extends Schema 
{
  @Override
  public void initializeUpdates(BridgeSQL pBridge) 
  {
    updates.add(new DBUpdate("02.14.2017.1", "Updates Table", "CREATE TABLE IF NOT EXISTS " + pBridge.prefix + "Updates (" +
        "id VARCHAR(20) NOT NULL," +
        "description VARCHAR(50) NOT NULL," +
        "PRIMARY KEY(id)" +
        ");"));
    updates.add(new DBUpdate("02.14.2017.2", "Worlds Table", "CREATE TABLE IF NOT EXISTS " + pBridge.prefix + "Blocks(" +
        "`ID` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT, " +
        "`owner` CHAR(36) NOT NULL, " + 
        "`blockName` VARCHAR(255) NOT NULL, " + 
        "`blockMeta` TINYINT NOT NULL, " + 
        "`x` INT(11) NOT NULL, " + 
        "`y` INT(11) NOT NULL, " + 
        "`z` INT(11) NOT NULL, " + 
        "`dim` INT(11) NOT NULL, " + 
        "PRIMARY KEY (`ID`) );"));
    updates.add(new DBUpdate("02.14.2017.3", "Placed timestamp", "ALTER TABLE `blocklimitertest`.`blocks` ADD COLUMN `placed` INT(11) NOT NULL AFTER `dim`;"));
  }
}
