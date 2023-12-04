package com.minwoo.springdatajpa.repository;

import com.minwoo.springdatajpa.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    void saveAndFind() {
        Member savedMember = memberRepository.save(new Member("memberA"));
        Member findedMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(savedMember.getId()).isEqualTo(findedMember.getId());
        assertThat(findedMember.getUsername()).isEqualTo("memberA");
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        Member memberf1 = memberRepository.findById(member1.getId()).get();
        Member memberf2 = memberRepository.findById(member2.getId()).get();

        Assertions.assertThat(member1).isEqualTo(memberf1);
        Assertions.assertThat(member2).isEqualTo(memberf2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long count2 = memberRepository.count();
        assertThat(count2).isEqualTo(0);
    }

}