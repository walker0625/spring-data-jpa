package com.minwoo.springdatajpa.repository;

import com.minwoo.springdatajpa.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional // 없으면 jpa 동작 error
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    void saveAndFind() {
        Member savedMember = memberJpaRepository.save(new Member("memberA"));
        Member findedMember = memberJpaRepository.find(savedMember.getId());

        assertThat(savedMember.getId()).isEqualTo(findedMember.getId());
        assertThat(findedMember.getUsername()).isEqualTo("memberA");
    }

}