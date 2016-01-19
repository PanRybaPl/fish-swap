/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.panryba.mc.swap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author PanRyba.pl
 */
public class PluginApi {
    private Set<Inventory> proposals;
    private Map<Player, SwapTransaction> playerTransactions;
    
    public PluginApi()
    {
        this.proposals = new HashSet<>();
        this.playerTransactions = new HashMap<>();
    }
    
    private ItemStack createAccept() {
        ItemStack accept = new ItemStack(Material.WOOL, 1, (short)5);
        ItemMeta acceptMeta = accept.getItemMeta();
        acceptMeta.setDisplayName("OK");
        List<String> acceptLore = acceptMeta.getLore();
        if(acceptLore == null) {
            acceptLore = new ArrayList<>();
        }
        acceptLore.add("Kliknij aby zaakceptowac");
        acceptMeta.setLore(acceptLore);
        accept.setItemMeta(acceptMeta);
        return accept;
    }
    
    private ItemStack createDecline() {
        ItemStack decline = new ItemStack(Material.WOOL, 1, (short)14);
        ItemMeta declineMeta = decline.getItemMeta();
        declineMeta.setDisplayName("Anuluj");
        List<String> declineLore = declineMeta.getLore();
        if(declineLore == null) {
            declineLore = new ArrayList<>();
        }
        declineLore.add("Kliknij aby anulowac");
        declineMeta.setLore(declineLore);
        decline.setItemMeta(declineMeta);
        return decline;
    }
    
    private Inventory createOfferInv(Player from, Player to) {
        Inventory chest = from.getServer().createInventory(null, 9, "oferta dla " + to.getName());        
        return chest;
    }
    
    private Inventory createOfferedInv(Player from, Player to) {
        Inventory chest = from.getServer().createInventory(null, 9, "oferta od " + from.getName());
        return chest;
    }    
    
    public void startTransaction(Player first, Player second) {
        if(this.getTransaction(first) != null || this.getTransaction(second) != null) {
            return;
        }
        
        Inventory chest = createOfferInv(first, second);        
        ItemStack accept = createAccept();
        ItemStack decline = createDecline();
        chest.setItem(7, accept);
        chest.setItem(8, decline);        
        
        InventoryView v = first.openInventory(chest);
        Inventory openedChest = v.getTopInventory();
        
        SwapTransaction tx = new SwapTransaction(first, second, v, openedChest, accept, decline);        
        
        this.playerTransactions.put(first, tx);
        this.playerTransactions.put(second, tx);        
        this.proposals.add(openedChest);
    }

    boolean isProposalInventory(Inventory inv) {
        return this.proposals.contains(inv);
    }

    SwapTransaction getTransaction(Player player) {
        return this.playerTransactions.get(player);
    }

    void removePlayerTransaction(Player player) {
        SwapTransaction tx = this.getTransaction(player);
        if(tx == null) {
            return;
        }
        
        this.proposals.remove(tx.getOfferInventory());
        this.playerTransactions.remove(tx.getFirst());
        this.playerTransactions.remove(tx.getSecond());
    }

    void offer(SwapTransaction tx) {
        tx.getOfferView().close();
        
        Player from;
        Player to;
        
        if(tx.getState() == TransactionState.FIRST_PROPOSAL) {
            tx.setFirstOffer();
            
            to = tx.getSecond();
            from = tx.getFirst();
            tx.setState(TransactionState.SECOND_ACCEPT);
        } else {
            tx.setSecondOffer();
            
            to = tx.getFirst();
            from = tx.getSecond();
            tx.setState(TransactionState.FIRST_ACCEPT);
        }
        
        this.proposals.remove(tx.getOfferInventory());
        
        Inventory currInv = tx.getOfferInventory();
        Inventory offerInv = createOfferedInv(from, to);
        
        offerInv.setContents(currInv.getContents());
        InventoryView v = to.openInventory(offerInv);
        
        this.proposals.add(v.getTopInventory());
        tx.setInventory(v.getTopInventory());
        tx.setView(v);
    }

    void acceptOffer(SwapTransaction tx) {
        if(tx.getState() == TransactionState.FIRST_ACCEPT) {
            
            tx.getOfferView().close();
            this.removePlayerTransaction(tx.getFirst());
            
            tx.commit();
            return;
        }
        
        tx.setState(TransactionState.SECOND_PROPOSAL);
        
        this.proposals.remove(tx.getOfferInventory());        
        tx.getOfferView().close();
        
        Inventory chest = createOfferInv(tx.getSecond(), tx.getFirst());
        ItemStack accept = createAccept();
        ItemStack decline = createDecline();
        chest.setItem(7, accept);
        chest.setItem(8, decline);
        
        InventoryView v = tx.getSecond().openInventory(chest);
        Inventory openedChest = v.getTopInventory();

        tx.setInventory(openedChest);
        tx.setView(v);
        
        this.proposals.add(openedChest);        
    }

    void declineOffer(SwapTransaction tx) {
        tx.rollback();
        this.proposals.remove(tx.getOfferInventory());
        tx.getOfferView().close();
        tx.setState(TransactionState.CANCELLED);
        this.removePlayerTransaction(tx.getFirst());
    }

    void cancelOffer(SwapTransaction tx) {
        Player from;
        
        if(tx.getState() == TransactionState.FIRST_PROPOSAL) {
            from = tx.getFirst();
        } else {
            from = tx.getSecond();
        }
        
        tx.rollback();
        tx.setState(TransactionState.CANCELLED);
        
        tx.getOfferView().close();
        this.removePlayerTransaction(from);
    }

    void cancelPlayerTransaction(Player player) {
        SwapTransaction tx = this.getTransaction(player);
        tx.rollback();
        this.removePlayerTransaction(player);
    }
}
