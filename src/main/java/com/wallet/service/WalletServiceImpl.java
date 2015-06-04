package com.wallet.service;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wallet.entity.Wallet;

@Service
@Transactional
public class WalletServiceImpl implements WalletService {
	
	@PersistenceContext
    public transient EntityManager entityManager;
	
	
    public long countAllWallets() {
        return Wallet.countWallets();
    }
    
    public void deleteWallet(Wallet wallet) {
        wallet.remove();
    }
    
    public Wallet findWallet(Long id) {
        return Wallet.findWallet(id);
    }
    
    public List<Wallet> findAllWallets() {
        return Wallet.findAllWallets();
    }
    
    public List<Wallet> findWalletEntries(int firstResult, int maxResults) {
        return Wallet.findWalletEntries(firstResult, maxResults);
    }
    
    @Transactional
    public void saveWallet(Wallet wallet) {
        wallet.persist();
        wallet.flush();
    }
    
    @Transactional
    public Wallet updateWallet(Wallet wallet) {
    	//EntityTransaction tx = wallet.entityManager.getTransaction();
    	//wallet.entityManager.lock(wallet, LockModeType.OPTIMISTIC);
    	//wallet.entityManager.flush();
        wallet.merge();
        //tx.commit();
        
        return wallet;
    }
    
    public Wallet updateWalletValue(Long id, BigDecimal val){
    	
    	return Wallet.update(id,val,entityManager);
    }
    
}
