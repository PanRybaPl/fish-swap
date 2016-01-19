/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.panryba.mc.swap;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author PanRyba.pl
 */
public class Plugin extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginApi api = new PluginApi();
        
        getServer().getPluginManager().registerEvents(new SwapListener(api), this);
        getCommand("wymiana").setExecutor(new SwapCommand(api));
    }
    
}
