package com.minwoo.springdatajpa.repository;

import com.minwoo.springdatajpa.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@Repository 생략 가능 > 인터페이스만 봐도 파악 가능
public interface TeamRepository extends JpaRepository<Team, Long> {
}
