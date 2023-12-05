package com.minwoo.springdatajpa.repository;

import com.minwoo.springdatajpa.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    
    @Test
    void findUserByUsernameAndAge() {
        memberJpaRepository.save(new Member("aaa", 10));
        memberJpaRepository.save(new Member("aaa", 20));
        
        List<Member> memberList = memberJpaRepository.findByUsernameAndAgeGreaterThan("aaa", 10);
        for (Member member : memberList) {
            System.out.println("member = " + member);
        }
    }

    @Test
    void namedQuery() {
        memberJpaRepository.save(new Member("aaa", 10));
        memberJpaRepository.save(new Member("bbb", 20));
        List<Member> aaa = memberJpaRepository.findByUsernameUseNamedQuery("aaa");

        assertThat("aaa").isEqualTo(aaa.get(0).getUsername());
    }

    @Test
    void paging() {
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 10));
        memberJpaRepository.save(new Member("member3", 10));
        memberJpaRepository.save(new Member("member4", 10));
        memberJpaRepository.save(new Member("member5", 10));

        long count = memberJpaRepository.totalCount(10);
        assertThat(count).isEqualTo(5);

        List<Member> members = memberJpaRepository.findByPage(10, 0, 2);
        for (Member member : members) {
            System.out.println("member = " + member);
        }
    }

}