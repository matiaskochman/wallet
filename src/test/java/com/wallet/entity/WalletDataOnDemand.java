package com.wallet.entity;
import com.wallet.service.WalletService;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.dod.RooDataOnDemand;
import org.springframework.stereotype.Component;

@Configurable
@Component
@RooDataOnDemand(entity = Wallet.class)
public class WalletDataOnDemand {

	private Random rnd = new SecureRandom();

	private List<Wallet> data;

	@Autowired
    WalletService walletService;

	public Wallet getNewTransientWallet(int index) {
        Wallet obj = new Wallet();
        setAvailableMoney(obj, index);
        return obj;
    }

	public void setAvailableMoney(Wallet obj, int index) {
        BigDecimal availableMoney = BigDecimal.valueOf(index);
        obj.setAvailableMoney(availableMoney);
    }

	public Wallet getSpecificWallet(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        Wallet obj = data.get(index);
        Long id = obj.getId();
        return walletService.findWallet(id);
    }

	public Wallet getRandomWallet() {
        init();
        Wallet obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return walletService.findWallet(id);
    }

	public boolean modifyWallet(Wallet obj) {
        return false;
    }

	public void init() {
        int from = 0;
        int to = 10;
        data = walletService.findWalletEntries(from, to);
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'Wallet' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<Wallet>();
        for (int i = 0; i < 10; i++) {
            Wallet obj = getNewTransientWallet(i);
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
            data.add(obj);
        }
    }
}
