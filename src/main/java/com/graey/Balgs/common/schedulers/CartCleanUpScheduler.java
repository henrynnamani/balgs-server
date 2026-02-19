package com.graey.Balgs.common.schedulers;

import com.graey.Balgs.model.Cart;
import com.graey.Balgs.repo.CartRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CartCleanUpScheduler {

    @Autowired
    private CartRepo repo;

    @Scheduled(fixedRate = 60000 * 2)
    public void clearExpiredCarts() {
        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(5);

        List<Cart> expiredCarts =
                repo.findByLastUpdatedAtBefore(expiryTime);

        repo.deleteAll(expiredCarts);
    }
}
