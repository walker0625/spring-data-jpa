package com.minwoo.springdatajpa.repository;

import com.minwoo.springdatajpa.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

// QueryDSL/JDBCTemplate 사용하는 경우 활용
// Base가 되는 repository(MemberRepository)명 + Impl으로 naming해주면 data jpa가 연결시킴
// 그냥 별도의 class(MemberQueryRepository)를 만들어서 사용하는 것도 방법
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberCustomRepository {

    private final EntityManager em;

    @Override
    public List<Member> findMembersByCustom() {
        return em.createQuery("select m from Member m", Member.class)
                 .getResultList();
    }

}