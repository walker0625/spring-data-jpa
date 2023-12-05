package com.minwoo.springdatajpa.repository;

import com.minwoo.springdatajpa.dto.MemberDto;
import com.minwoo.springdatajpa.entity.Member;
import com.minwoo.springdatajpa.entity.Team;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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

    @Test
    void paging() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        // 0 페이지부터 3개 가져오기
        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> memberPages = memberRepository.findByAge(age, pageRequest);

        // api 반환시 dto로 변환하여 return
        Page<MemberDto> memberDtos = memberPages.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        List<Member> content = memberPages.getContent();
        long totalElements = memberPages.getTotalElements();

        assertThat(totalElements).isEqualTo(5); // 총 요소 갯수
        assertThat(content.size()).isEqualTo(3); // 가져온 요소 갯수

        assertThat(memberPages.getTotalPages()).isEqualTo(2); // 총 페이지 수
        assertThat(memberPages.getNumber()).isEqualTo(0); // 현재 페이지 번호

        assertThat(memberPages.isFirst()).isTrue(); // 첫 페이지 여부
        assertThat(memberPages.hasNext()).isTrue(); // 다음 페이지 존재여부
    }

    @Test
    void slice() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        // 0 페이지부터 3개 가져오기
        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // 설정한 요소 갯수(3) + 1개를 가져와서 다음 페이지 여부가 있는지를 판단
        Slice<Member> memberSlices = memberRepository.findSliceByAge(age, pageRequest);

        List<Member> content = memberSlices.getContent();
        //long totalElements = memberSlices.getTotalElements(); Slice는 total을 가져오지 않음
        //assertThat(totalElements).isEqualTo(5); // 총 요소 갯수
        //assertThat(memberSlices.getTotalPages()).isEqualTo(2); // 총 페이지 수

        assertThat(content.size()).isEqualTo(3); // 가져온 요소 갯수
        assertThat(memberSlices.getNumber()).isEqualTo(0); // 현재 페이지 번호
        assertThat(memberSlices.isFirst()).isTrue(); // 첫 페이지 여부
        assertThat(memberSlices.hasNext()).isTrue(); // 다음 페이지 존재여부
    }

}