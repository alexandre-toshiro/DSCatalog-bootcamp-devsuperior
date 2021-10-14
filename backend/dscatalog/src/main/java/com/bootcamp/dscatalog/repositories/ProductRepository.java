package com.bootcamp.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bootcamp.dscatalog.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {// A classe de domínio, tipo da PK

}
