package com.minwoo.springdatajpa.repository;

import com.minwoo.springdatajpa.dto.MemberDto;
import com.minwoo.springdatajpa.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query(name = "Member.findByUsername") // 생략 가능(@NamedQuery 안의 name을 먼저 찾음)
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age > :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findByUsernameList();

    @Query("select new com.minwoo.springdatajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    // 검색 결과가 없으면 null이 아니라 사이즈가 0인 collection이 나오는 것 주의(null 체크가 아니라 size 체크를 해야함)
    List<Member> findListByUsername(String username);

    // 검색 결과가 없으면 null이 반환됨() - SpringDataJpa가 NoResultException을 try-catch하여 null로 반환함
    // null 처리보다는 Optional 처리가 권장됨
    // 기본 jpa는 예외(NoResultException)발생
    // 단건 조회인데 2개 이상일 경우 예외(NonUniqueResultException(jpa) -> IncorrectResultSizeDataAccessException(spring)) 발생
    Member findMemberByUsername(String username);

    // Optional로 반환되기 때문에 null일 경우 처리 추가
    Optional<Member> findOptionalByUsername(String username);

}