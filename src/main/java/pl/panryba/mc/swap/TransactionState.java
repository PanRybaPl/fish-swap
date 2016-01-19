/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.panryba.mc.swap;

/**
 *
 * @author PanRyba.pl
 */
public enum TransactionState {
    FIRST_PROPOSAL,
    SECOND_ACCEPT,
    SECOND_PROPOSAL,
    FIRST_ACCEPT,
    DONE,
    CANCELLED
}
