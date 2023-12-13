package com.minwoo.springdatajpa.repository;

import com.minwoo.springdatajpa.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
