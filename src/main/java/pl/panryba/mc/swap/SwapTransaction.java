/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.swap;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author PanRyba.pl
 */
public class SwapTransaction {

    private TransactionState state;

    private final Player first;
    private List<ItemStack> firstOffer;

    private final Player second;
    private List<ItemStack> secondOffer;

    private Inventory offerInventory;
    private ItemStack accept;
    private ItemStack decline;
    private InventoryView offerView;

    public SwapTransaction(Player first, Player second, InventoryView offerView, Inventory offerInventory, ItemStack accept, ItemStack decline) {
        this.first = first;
        this.second = second;
        this.offerInventory = offerInventory;
        this.accept = accept;
        this.decline = decline;
        this.offerView = offerView;
        this.state = TransactionState.FIRST_PROPOSAL;
    }

    Inventory getOfferInventory() {
        return this.offerInventory;
    }

    boolean isAccept(ItemStack item) {
        return this.accept.equals(item);
    }

    boolean isDecline(ItemStack item) {
        return this.decline.equals(item);
    }

    InventoryView getOfferView() {
        return this.offerView;
    }

    ItemStack getAccept() {
        return this.accept;
    }

    ItemStack getDecline() {
        return this.decline;
    }

    Player getFirst() {
        return this.first;
    }

    Player getSecond() {
        return this.second;
    }

    void setInventory(Inventory offerInv) {
        this.offerInventory = offerInv;
    }

    void setView(InventoryView v) {
        this.offerView = v;
    }

    TransactionState getState() {
        return this.state;
    }

    void setState(TransactionState transactionState) {
        this.state = transactionState;
    }

    void setFirstOffer() {
        this.firstOffer = copyInvItems();
    }

    private List<ItemStack> copyInvItems() {
        List<ItemStack> result = new ArrayList<ItemStack>();
        for (int i = 0; i < 7; ++i) {
            ItemStack item = this.offerInventory.getItem(i);
            if(item != null && item.getType() != Material.AIR) {
                result.add(item);
            }
        }
        return result;
    }

    void setSecondOffer() {
        this.secondOffer = copyInvItems();
    }

    List<ItemStack> getFirstOffer() {
        return this.firstOffer;
    }

    List<ItemStack> getSecondOffer() {
        return this.secondOffer;
    }

    void commit() {
        move(this.secondOffer, this.first);
        move(this.firstOffer, this.second);
    }

    private void move(List<ItemStack> items, Player player) {
        Inventory inv = player.getInventory();
        for(ItemStack item : items) {
            inv.addItem(item);
        }
    }

    void rollback() {
        if(this.secondOffer != null) {
            move(this.secondOffer, this.second);
            this.secondOffer = null;
        }
        
        if(this.firstOffer != null) {
            move(this.firstOffer, this.first);
            this.firstOffer = null;
        }
    }
}
