package com.graey.Balgs.service;

import com.graey.Balgs.model.Cart;
import com.graey.Balgs.model.CartItem;
import com.graey.Balgs.repo.CartRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class CartCleanUpService {

    @Autowired
    private CartRepo cartRepo;

    @Async
    @Transactional
    public void removeProductsFromAllCarts(List<UUID> productIds, UUID excludeCartId) {
        List<Cart> affectedCarts = excludeCartId != null
                ? cartRepo.findCartsContainingProductsExcluding(productIds, excludeCartId)
                : cartRepo.findCartsContainingProducts(productIds);

        affectedCarts.forEach(cart -> {
            cart.getItems().removeIf(
                    item -> productIds.contains(item.getProduct().getId())
            );
            cart.setTotalPrice(
                    cart.getItems().stream()
                            .map(CartItem::getPriceAtAdd)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
            );
        });

        cartRepo.saveAll(affectedCarts);
    }
}
