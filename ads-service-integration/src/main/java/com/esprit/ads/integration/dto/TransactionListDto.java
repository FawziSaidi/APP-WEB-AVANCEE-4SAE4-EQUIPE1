package com.esprit.ads.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionListDto {
    private List<TransactionDto> transactions;
    private int total;

    public TransactionListDto() {}

    public List<TransactionDto> getTransactions() { return transactions; }
    public void setTransactions(List<TransactionDto> transactions) { this.transactions = transactions; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
}
