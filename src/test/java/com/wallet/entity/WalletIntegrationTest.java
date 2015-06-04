package com.wallet.entity;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.wallet.processor.Processor;
import com.wallet.service.WalletService;

@Configurable
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/spring/applicationContext*.xml")
@Transactional
public class WalletIntegrationTest {

    //@Test
    public void testMarkerMethod() {
    }

	@Autowired
    WalletDataOnDemand dod;

	@Autowired
    WalletService walletService;

	//@Test
    public void testCountAllWallets() {
        Assert.assertNotNull("Data on demand for 'Wallet' failed to initialize correctly", dod.getRandomWallet());
        long count = walletService.countAllWallets();
        Assert.assertTrue("Counter for 'Wallet' incorrectly reported there were no entries", count > 0);
    }

	//@Test
    public void testFindWallet() {
        Wallet obj = dod.getRandomWallet();
        Assert.assertNotNull("Data on demand for 'Wallet' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Wallet' failed to provide an identifier", id);
        obj = walletService.findWallet(id);
        Assert.assertNotNull("Find method for 'Wallet' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'Wallet' returned the incorrect identifier", id, obj.getId());
    }

	//@Test
    public void testFindAllWallets() {
        Assert.assertNotNull("Data on demand for 'Wallet' failed to initialize correctly", dod.getRandomWallet());
        long count = walletService.countAllWallets();
        Assert.assertTrue("Too expensive to perform a find all test for 'Wallet', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<Wallet> result = walletService.findAllWallets();
        Assert.assertNotNull("Find all method for 'Wallet' illegally returned null", result);
        Assert.assertTrue("Find all method for 'Wallet' failed to return any data", result.size() > 0);
    }

	//@Test
    public void testFindWalletEntries() {
        Assert.assertNotNull("Data on demand for 'Wallet' failed to initialize correctly", dod.getRandomWallet());
        long count = walletService.countAllWallets();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<Wallet> result = walletService.findWalletEntries(firstResult, maxResults);
        Assert.assertNotNull("Find entries method for 'Wallet' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'Wallet' returned an incorrect number of entries", count, result.size());
    }

	//@Test
    public void testFlush() {
        Wallet obj = dod.getRandomWallet();
        Assert.assertNotNull("Data on demand for 'Wallet' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Wallet' failed to provide an identifier", id);
        obj = walletService.findWallet(id);
        Assert.assertNotNull("Find method for 'Wallet' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyWallet(obj);
        Integer currentVersion = obj.getVersion();
        obj.flush();
        Assert.assertTrue("Version for 'Wallet' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	//@Test
    public void testUpdateWalletUpdate() {
        Wallet obj = dod.getRandomWallet();
        Assert.assertNotNull("Data on demand for 'Wallet' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Wallet' failed to provide an identifier", id);
        obj = walletService.findWallet(id);
        boolean modified =  dod.modifyWallet(obj);
        Integer currentVersion = obj.getVersion();
        Wallet merged = walletService.updateWallet(obj);
        obj.flush();
        Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        Assert.assertTrue("Version for 'Wallet' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	//@Test
    public void testSaveWallet() {
        Assert.assertNotNull("Data on demand for 'Wallet' failed to initialize correctly", dod.getRandomWallet());
        Wallet obj = dod.getNewTransientWallet(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'Wallet' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'Wallet' identifier to be null", obj.getId());
        try {
            walletService.saveWallet(obj);
        } catch (final ConstraintViolationException e) {
            final StringBuilder msg = new StringBuilder();
            for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                final ConstraintViolation<?> cv = iter.next();
                msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
            }
            throw new IllegalStateException(msg.toString(), e);
        }
        obj.flush();
        Assert.assertNotNull("Expected 'Wallet' identifier to no longer be null", obj.getId());
    }
    //@Test
    public void testDeleteWallet() {
        Wallet obj = dod.getRandomWallet();
        Assert.assertNotNull("Data on demand for 'Wallet' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Wallet' failed to provide an identifier", id);
        obj = walletService.findWallet(id);
        walletService.deleteWallet(obj);
        obj.flush();
        Assert.assertNull("Failed to remove 'Wallet' with identifier '" + id + "'", walletService.findWallet(id));
    }
	
    //@Test
    public void create(){
    	Wallet wallet = new Wallet();
    	wallet.setAvailableMoney(new BigDecimal(100));
    	walletService.saveWallet(wallet);
    }
    
    @Test
	public void testMultithreadingUpdate(){
		
    	int cantidad = 2000;
    	
    	Long identif = 1L;
    	Wallet wallet = new Wallet();
    	wallet.setAvailableMoney(new BigDecimal(5000));
    	
    	walletService.saveWallet(wallet);
    	
		CountDownLatch countDownLatch=new CountDownLatch(cantidad);
		ExecutorService executorService=Executors.newFixedThreadPool(50);
		
		int count = 0;
		try {
			for (int i = 0; i < cantidad; i++) {
				executorService.submit(new Processor(countDownLatch,identif,dameUnNumero(i),walletService,i));
				System.out.println(++count);
			}
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			
			executorService.shutdown();
		}		
		
		Wallet w = walletService.findWallet(identif);
		System.out.println(w.getAvailableMoney());
		
	}
    
    public BigDecimal dameUnNumero(int count){
    	if(count %2 ==0){
    		return new BigDecimal(10);
    	}else{
    		return new BigDecimal(-10);
    	}
    }
}
