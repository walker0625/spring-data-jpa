package com.minwoo.springdatajpa.repository;

import com.minwoo.springdatajpa.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

    private final EntityManager em;

    public List<Member> findMemberByQuery() {
        return em.createQuery("select m from Member m", Member.class)
                 .getResultList();
    }

}
