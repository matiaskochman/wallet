package com.wallet.processor;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;

import com.wallet.service.WalletService;

public class Processor implements Runnable{

	private CountDownLatch countDownLatch;
	private BigDecimal val;
	private Long id;
	Random r = new Random();
	Integer instancia;
    WalletService walletService;
	
	public Processor(CountDownLatch countDownLatch,Long walletId, BigDecimal val, WalletService walletService2, int i){
		this.countDownLatch = countDownLatch;
		this.val = val;
		this.id = walletId;
		this.walletService = walletService2;
		this.instancia = i;
	}
			
	@Override
	public void run() {
		
		try{
			
			int ran = r.nextInt(150);
			
			Thread.sleep(ran);
			
			walletService.updateWalletValue(id,val);
			
		}catch(JpaOptimisticLockingFailureException ex){
			System.out.println("instancia: "+instancia);
			System.out.println("lock fail 1");
			
			try {
				int ran = r.nextInt(1000);
				Thread.sleep(2000-ran);
				walletService.updateWalletValue(id,val);
			}catch(JpaOptimisticLockingFailureException ext){
				System.out.println("instancia: "+instancia);
				System.out.println("lock fail 2");
				
				int ran = r.nextInt(1000);
				try {
					Thread.sleep(2000-ran);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				walletService.updateWalletValue(id,val);
				
			}catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//ex.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			countDownLatch.countDown();
		}
	}

}
