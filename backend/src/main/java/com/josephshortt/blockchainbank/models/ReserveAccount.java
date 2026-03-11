package com.josephshortt.blockchainbank.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "reserve_account" , schema = "blockchain")
public class ReserveAccount {
    @Id
    private String bankId;

    private String bankName;

    private BigDecimal reserveBalance;

    public  ReserveAccount(){
    }

    public ReserveAccount(String bankId, String bankName, BigDecimal reserveBalance) {
        this.bankId = bankId;
        this.bankName = bankName;
        this.reserveBalance = reserveBalance;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public BigDecimal getReserveBalance() {
        return reserveBalance;
    }

    public void setReserveBalance(BigDecimal reserveBalance) {
        this.reserveBalance = reserveBalance;
    }
}
