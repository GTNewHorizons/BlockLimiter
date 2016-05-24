package com.github.namikon.blocklimiter.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

import com.github.namikon.blocklimiter.BlockLimiter;
import com.github.namikon.blocklimiter.auxiliary.BlockInfo;
import com.github.namikon.blocklimiter.auxiliary.ItemInfo;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import eu.usrv.yamcore.auxiliary.PlayerChatHelper;

public class BlockLimiterCommand implements ICommand {
	private List aliases;
	public BlockLimiterCommand()
	{
		this.aliases = new ArrayList();
		this.aliases.add("blimit");
	}

	@Override
	public int compareTo(Object arg0) {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "blimit";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "/blimit reload|blockinfo|iteminfo";
	}

	@Override
	public List getCommandAliases() {

		return this.aliases;
	}

	@Override
	public void processCommand(ICommandSender pCmdSender, String[] pArgs)
	{
		if (pArgs.length < 1)
		{
			PlayerChatHelper.SendError(pCmdSender, "Usage: /blimit reload|blockinfo|iteminfo");
			return;
		}

		if (pArgs[0].equalsIgnoreCase("reload"))
		{
			if (!isOpOrAdmin(pCmdSender))
			{
				PlayerChatHelper.SendError(pCmdSender, "You need to be op or admin to reload the config");
				return;
			}
			else
			{
				if (!BlockLimiter.Config.Reload())
					PlayerChatHelper.SendError(pCmdSender, "Blocks could not be reloaded properly. Please check the console output and fix the config file");
				else
				{
					PlayerChatHelper.SendInfo(pCmdSender, "Blocklimiter config reloaded. Now monitoring:");
					PlayerChatHelper.SendInfo(pCmdSender, String.format(" - %d Block(s)", BlockLimiter.Config.LimitedBlocks.size()));
					PlayerChatHelper.SendInfo(pCmdSender, String.format(" - %d Item(s)", BlockLimiter.Config.LimitedItems.size()));
				}

			}
		}
		else if(pArgs[0].equalsIgnoreCase("blockinfo"))
		{
			if (BlockLimiter.Config.LimitedBlocks.size() == 0)
			{
				PlayerChatHelper.SendInfo(pCmdSender, String.format("There are currently no forbidden Blocks"));
			}
			else
			{
				PlayerChatHelper.SendInfo(pCmdSender, String.format("The following Blocks are monitored by BlockLimiter:"));
				for (BlockInfo bi : BlockLimiter.Config.LimitedBlocks)
				{
					PlayerChatHelper.SendInfo(pCmdSender, bi.getInfoString());
				}
			}
		}
		else if(pArgs[0].equalsIgnoreCase("iteminfo"))
		{
			if (BlockLimiter.Config.LimitedItems.size() == 0)
			{
				PlayerChatHelper.SendInfo(pCmdSender, String.format("There are currently no forbidden Items"));
			}
			else
			{
				PlayerChatHelper.SendInfo(pCmdSender, String.format("The following Items are monitored by BlockLimiter:"));
				for (ItemInfo bi : BlockLimiter.Config.LimitedItems)
				{
					PlayerChatHelper.SendInfo(pCmdSender, bi.getInfoString());
				}
			}
		}

		else
			PlayerChatHelper.SendError(pCmdSender, "Usage: /blimit reload|blockinfo|iteminfo");

	}

	private void ProcessDenyinDims(EntityPlayer tEp, ItemStack inHand,
			String[] pArgs) {

	}

	/* 
	 * Everyone shall execute this command to see a list of blocked Blocks
	 */
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender pCommandSender)
	{
		return true;
	}

	/**
	 * Check if commandSender is an op or admin
	 * @param pCommandSender
	 * @return
	 */
	private boolean isOpOrAdmin(ICommandSender pCommandSender)
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
