package com.minwoo.springdatajpa.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {

    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY) // id를 자동생성 값이 아니라 임의의 값으로 하는 경우
    private String id;
    // entityInformation.isNew(entity)가 null이나 0이 아니므로(임의의 id 값) persist가 아니라 merge로 동작
    // merge로 동작하는 경우 : 먼저 select 후에 없으면 save(비효율적) / merge 자체가 비추천되는 동작

    @CreatedDate
    private LocalDateTime createdDate;

    public Item(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return createdDate == null;
    }

}
