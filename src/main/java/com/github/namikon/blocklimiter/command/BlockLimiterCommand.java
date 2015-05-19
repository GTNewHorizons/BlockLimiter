package com.github.namikon.blocklimiter.command;

import java.util.ArrayList;
import java.util.List;

import com.github.namikon.blocklimiter.BlockLimiter;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import eu.usrv.yamcore.auxiliary.PlayerChatHelper;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class BlockLimiterCommand implements ICommand {
	private List aliases;
	public BlockLimiterCommand()
	{
		this.aliases = new ArrayList();
		this.aliases.add("blimitreload");
	}
	
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName() {
		return "blimitreload";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "/blimitreload";
	}

	@Override
	public List getCommandAliases() {

		return this.aliases;
	}

	@Override
	public void processCommand(ICommandSender pCmdSender, String[] pArgs)
	{
		if (!BlockLimiter.Config.Reload())
			PlayerChatHelper.SendError(pCmdSender, "Blocks could not be reloaded properly. Please check the console output and fix the config file");
		else
			PlayerChatHelper.SendInfo(pCmdSender, String.format("Blocklimiter config reloaded. Now monitoring %d Blocks", BlockLimiter.Config.LimitedBlocks.size()-1));
	}

	private void ProcessDenyinDims(EntityPlayer tEp, ItemStack inHand,
			String[] pArgs) {
				
	}

	private void ProcessAllowinAllDims(EntityPlayer tEp, ItemStack inHand) {
		// TODO Auto-generated method stub
		
	}

	private void ProcessDenyinAllDims(EntityPlayer tEp, ItemStack inHand) {
		// TODO Auto-generated method stub
		
	}

	private void ProcessAllowinDims(EntityPlayer tEp, ItemStack inHand,
			String[] pArgs) {
		// TODO Auto-generated method stub
		
	}

	private void SendHelpToPlayer(ICommandSender pCmdSender)
	{
		PlayerChatHelper.SendInfo(pCmdSender, "  /hazarditems addpotion <potionID> <tickDuration> <level>");
	}
	
	/* 
	 * Make sure only an op/admin can execute this command
	 */
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender pCommandSender)
	{
		  if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && !FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer())
			  return true;
		  
		  if(pCommandSender instanceof EntityPlayerMP)
		  {
			  EntityPlayerMP tEP = (EntityPlayerMP)pCommandSender;
			  return MinecraftServer.getServer().getConfigurationManager().func_152596_g(tEP.getGameProfile());
		  }
		  return false;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender p_71516_1_,
			String[] p_71516_2_) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
		return false;
	}

}
