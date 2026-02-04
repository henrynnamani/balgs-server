package com.graey.Balgs.repo;

import com.graey.Balgs.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product, String> {
}
