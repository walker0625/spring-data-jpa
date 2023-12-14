package com.minwoo.springdatajpa.repository;

import com.minwoo.springdatajpa.dto.MemberDto;
import com.minwoo.springdatajpa.entity.Member;
import com.minwoo.springdatajpa.entity.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    MemberQueryRepository memberQueryRepository;

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

    @Test
    void bulkAgePlus() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 20));
        memberRepository.save(new Member("member5", 20));

        int count = memberRepository.bulkAgePlus(10);

        assertThat(count).isEqualTo(2);

        // bulk 연산 내용을 db에 반영하고 1차 캐시를 날려야
        // 다시 find 했을 때 data와 동기화가 가능
        // entityManager.clear();

        assertThat(memberRepository.findMemberByUsername("member1").getAge()).isEqualTo(11);
    }

    @Test
    void findMemberLazy() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);
        
        entityManager.flush();
        entityManager.clear();

        /*
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
        */

        Member member = memberRepository.findAllEntityGraphByUsername("member1").get(0);
        System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
    }

    @Test
    void queryHint() {
        Member member1 = memberRepository.save(new Member("member1", 10));
        entityManager.flush();
        entityManager.clear();

        Member member2 = memberRepository.findMemberByUsername(member1.getUsername());
        member2.setUsername("member2");

        entityManager.flush(); // 더티체킹으로 update -> 원본/변경본 : 2개의 객체가 필요함
        entityManager.clear();

        // readonly로 가져온 객체라 더티체킹 하지 않음
        Member member3 = memberRepository.findReadOnlyByUsername(member2.getUsername());
        member3.setUsername("member3"); // update 되지 않음

        entityManager.flush();

        Member member4 = memberRepository.findMemberByUsername(member3.getUsername());

        System.out.println("member4 = " + member4);
    }

    @Test
    void lock() {
        Member member1 = memberRepository.save(new Member("member1", 10));
        entityManager.flush();
        entityManager.clear();


        /*
        select
                m1_0.member_id,
                m1_0.age,
                m1_0.team_id,
                m1_0.username
        from
            member m1_0
        where
            m1_0.username=? for update // lock
         */
        Member findMember = memberRepository.findLockByUsername("member1");
    }
    
    @Test
    void findCustom() {
        Member member1 = memberRepository.save(new Member("member1", 10));
        Member member2 = memberRepository.save(new Member("member2", 20));

        List<Member> membersByCustom = memberRepository.findMembersByCustom();

        for (Member member : membersByCustom) {
            System.out.println("member = " + member);
        }
    }

    @Test
    void findQuery() {
        Member member1 = memberRepository.save(new Member("member1", 10));
        Member member2 = memberRepository.save(new Member("member2", 20));

        List<Member> membersByQuery = memberQueryRepository.findMemberByQuery();

        for (Member member : membersByQuery) {
            System.out.println("member = " + member);
        }
    }

    @Test
    void baseEntity() throws InterruptedException {
        Member member1 = memberRepository.save(new Member("member1", 10));

        member1.setUsername("member3");

        Thread.sleep(1000);

        entityManager.flush();
        entityManager.clear();

        List<Member> membersByQuery = memberQueryRepository.findMemberByQuery();

        System.out.println("membersByQuery.get(0).getCreatedDate() = " + membersByQuery.get(0).getCreatedDate());
        System.out.println("membersByQuery.get(0).getUpdatedDate() = " + membersByQuery.get(0).getUpdatedDate());
        System.out.println("membersByQuery.get(0).getCreatedBy() = " + membersByQuery.get(0).getCreatedBy());
        System.out.println("membersByQuery.get(0).getUpdatedBy() = " + membersByQuery.get(0).getUpdatedBy());
    }

    @Test
    void nativeQuery() {
        String id = memberRepository.findByNativeQuery("member1", 1);
        System.out.println("member1 = " + id);
    }

    @Test
    void nativeQueryByProjection() {
        Page<MemberProjection> byNativeQueryByProjection = memberRepository.findByNativeQueryByProjection(PageRequest.of(0, 5));
    }

}