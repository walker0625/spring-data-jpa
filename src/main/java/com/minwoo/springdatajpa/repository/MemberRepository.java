package com.minwoo.springdatajpa.repository;

import com.minwoo.springdatajpa.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
