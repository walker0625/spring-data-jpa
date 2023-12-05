package com.minwoo.springdatajpa.repository;

import com.minwoo.springdatajpa.dto.MemberDto;
import com.minwoo.springdatajpa.entity.Member;
import com.minwoo.springdatajpa.entity.Team;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

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

        assertThat(member1).isEqualTo(memberf1);
        assertThat(member2).isEqualTo(memberf2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long count2 = memberRepository.count();
        assertThat(count2).isEqualTo(0);
    }

    @Test
    void findUserByUsernameAndAge() {
        memberRepository.save(new Member("aaa", 10));
        memberRepository.save(new Member("aaa", 20));

        List<Member> memberList = memberRepository.findByUsernameAndAgeGreaterThan("aaa", 10);

        for (Member member : memberList) {
            System.out.println("member = " + member);
        }

        List<Member> all = memberRepository.findAll();
    }

    @Test
    void annotationQuery() {
        memberRepository.save(new Member("aaa", 10));
        memberRepository.save(new Member("bbb", 20));
        List<Member> aaa = memberRepository.findByUsername("aaa");

        assertThat("aaa").isEqualTo(aaa.get(0).getUsername());
    }

    @Test
    void repositoryQuery() {
        memberRepository.save(new Member("aaa", 10));
        memberRepository.save(new Member("bbb", 20));
        List<Member> aaa = memberRepository.findUser("aaa", 5);

        assertThat("aaa").isEqualTo(aaa.get(0).getUsername());
    }

    @Test
    void usernamesString() {
        memberRepository.save(new Member("aaa", 10));
        memberRepository.save(new Member("bbb", 20));
        List<String> byUsernameList = memberRepository.findByUsernameList();

        for (String s : byUsernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    void dto() {
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        memberRepository.save(new Member("aaa", 10, teamA));

        List<MemberDto> memberDto = memberRepository.findMemberDto();

        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    void findNames() {
        memberRepository.save(new Member("aaa", 10));
        memberRepository.save(new Member("bbb", 20));

        List<Member> byNames = memberRepository.findByNames(List.of("aaa", "bbb"));

        for (Member byName : byNames) {
            System.out.println("byName = " + byName);
        }
    }

    @Test
    void findReturnType() {
        memberRepository.save(new Member("aaa", 10));
        memberRepository.save(new Member("bbb", 20));

        List<Member> aaas = memberRepository.findListByUsername("tt");
        System.out.println("aaas.size() = " + aaas.size()); // 0(null이 아님)

        Member aaa = memberRepository.findMemberByUsername("tt");
        System.out.println("aaa = " + aaa); // null

        Optional<Member> aaao = memberRepository.findOptionalByUsername("tt");
        assertThatThrownBy(() -> aaao.get()).isInstanceOf(RuntimeException.class);
    }

}