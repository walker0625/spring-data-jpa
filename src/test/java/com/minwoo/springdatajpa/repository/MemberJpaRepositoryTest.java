package com.minwoo.springdatajpa.repository;

import com.minwoo.springdatajpa.entity.Member;
import org.assertj.core.api.Assertions;
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

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        Member memberf1 = memberJpaRepository.findById(member1.getId()).get();
        Member memberf2 = memberJpaRepository.findById(member2.getId()).get();

        Assertions.assertThat(member1).isEqualTo(memberf1);
        Assertions.assertThat(member2).isEqualTo(memberf2);

        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long count2 = memberJpaRepository.count();
        assertThat(count2).isEqualTo(0);
    }

}