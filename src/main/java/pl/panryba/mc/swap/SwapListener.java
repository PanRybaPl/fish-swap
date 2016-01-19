/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.swap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author PanRyba.pl
 */
class SwapListener implements Listener {

    private final PluginApi api;

    public SwapListener(PluginApi api) {
        this.api = api;
    }
    
    @EventHandler
    public void onInventoryClosed(InventoryCloseEvent event) {
        if(!(event.getPlayer() instanceof Player)) {
            return;
        }
        
        Player player = (Player)event.getPlayer();
        api.cancelPlayerTransaction(player);
    }
    
    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        api.cancelPlayerTransaction(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInv2(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (api.isProposalInventory(event.getInventory())) {
            SwapTransaction tx = api.getTransaction(player);

            if (tx.getOfferInventory() == event.getInventory()) {
                switch (tx.getState()) {
                    case SECOND_ACCEPT: {
                        event.setCancelled(true);
                        break;
                    }

                    case FIRST_PROPOSAL: {
                        ItemStack item = event.getCursor();
                        ItemStack item2 = event.getOldCursor();

                        if (tx.isAccept(item) || tx.isDecline(item) || tx.isAccept(item2) || tx.isDecline(item2)) {
                            event.setCancelled(true);
                        }
                        break;
                    }
                    
                    case SECOND_PROPOSAL: {
                        ItemStack item = event.getCursor();
                        ItemStack item2 = event.getOldCursor();

                        if (tx.isAccept(item) || tx.isDecline(item) || tx.isAccept(item2) || tx.isDecline(item2)) {
                            event.setCancelled(true);
                        }                        
                        break;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventory(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (api.isProposalInventory(event.getInventory())) {
            SwapTransaction tx = api.getTransaction(player);

            if (tx.getOfferInventory() == event.getInventory()) {

                switch (tx.getState()) {
                    case FIRST_PROPOSAL: {
                        ItemStack item = event.getCurrentItem();
                        if (tx.isAccept(item) || tx.isDecline(item)) {
                            event.setCancelled(true);

                            if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) {
                                tx.getOfferView().close();
                                if (tx.isAccept(item)) {
                                    api.offer(tx);
                                } else if (tx.isDecline(item)) {
                                    api.cancelOffer(tx);
                                }
                            }
                        }
                        break;
                    }
                    
                    case SECOND_ACCEPT: {
                        ItemStack item = event.getCurrentItem();
                        event.setCancelled(true);
                        if(tx.isAccept(item)) {
                            api.acceptOffer(tx);
                        } else if(tx.isDecline(item)) {
                            api.declineOffer(tx);
                        } else {
                            event.setCancelled(true);
                        }
                        break;
                    }                    
                    
                    case SECOND_PROPOSAL: {
                        ItemStack item = event.getCurrentItem();
                        if (tx.isAccept(item) || tx.isDecline(item)) {
                            event.setCancelled(true);

                            if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) {
                                if (tx.isAccept(item)) {
                                    api.offer(tx);
                                } else if (tx.isDecline(item)) {
                                    api.cancelOffer(tx);
                                }
                            }
                        }
                        break;
                    }
                    
                    case FIRST_ACCEPT: {
                        ItemStack item = event.getCurrentItem();
                        event.setCancelled(true);
                        if(tx.isAccept(item)) {
                            api.acceptOffer(tx);
                        } else if(tx.isDecline(item)) {
                            api.declineOffer(tx);
                        } else {
                            event.setCancelled(true);
                        }
                        break;
                    }                          
                }
            }
        }
    }
}
