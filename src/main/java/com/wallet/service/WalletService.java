package com.wallet.service;
import java.math.BigDecimal;
import java.util.List;

import com.wallet.entity.Wallet;

public interface WalletService {
    public abstract long countAllWallets();    
    public abstract void deleteWallet(Wallet wallet);    
    public abstract Wallet findWallet(Long id);    
    public abstract List<Wallet> findAllWallets();    
    public abstract List<Wallet> findWalletEntries(int firstResult, int maxResults);    
    public abstract void saveWallet(Wallet wallet);    
    public abstract Wallet updateWallet(Wallet wallet);    
    public Wallet updateWalletValue(Long id, BigDecimal val);

}
