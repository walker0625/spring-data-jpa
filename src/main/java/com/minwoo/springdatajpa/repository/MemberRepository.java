package com.minwoo.springdatajpa.repository;

import com.minwoo.springdatajpa.dto.MemberDto;
import com.minwoo.springdatajpa.entity.Member;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberCustomRepository {

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

    // countQuery를 분리하여 불필요한 부하 줄임
    @Query(value = "select m from Member m left join m.team t",
           countQuery = "select count(m.id) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    Slice<Member> findSliceByAge(int age, Pageable pageable);

    // clearAutomatically = true -> entityManager.clear()와 같은 역할(1차 캐시 비우기를 통해 update 이후 select와 동기화)
    @Modifying(clearAutomatically = true) // @Modifying -> executeUpdate() 역할 꼭 필요!(InvalidDataAccess Exception 발생)
    @Query("update Member m set m.age = m.age + 1 where m.age = :age")
    int bulkAgePlus(@Param("age") int age);

    // N + 1 문제를 해결하기 위해 fetch join 함
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    // 내부적으로 fetch join이 동작함
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findAllEntityGraph();

    @EntityGraph(attributePaths = {"team"})
    //@EntityGraph("Member.all")
    List<Member> findAllEntityGraphByUsername(@Param("username") String username);

    // 읽기 전용으로만 쓰게되어 dirty checking을 하지 않아 성능 최적화(update가 되지 않음)
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Member findLockByUsername(String username);

}