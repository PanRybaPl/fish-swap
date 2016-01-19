/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.panryba.mc.swap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author PanRyba.pl
 */
class SwapCommand implements CommandExecutor {
    private final PluginApi api;
    
    public SwapCommand(PluginApi api) {
        this.api = api;
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if(!cs.isOp()) {
            return false;
        }
        
        if(!(cs instanceof Player)) {
            return false;
        }
        
        if(strings.length != 1) {
            return false;
        }
        
        String secondName = strings[0];
        
        Player player = (Player)cs;
        Player second = player.getServer().getPlayer(secondName);
        
        if(second == null) {
            cs.sendMessage(ChatColor.GRAY + "Nie znaleziono graczo podanym nicku");
            return true;
        }
        
        api.startTransaction(player, second);
        return true;
    }
}
