package com.minwoo.springdatajpa.repository;

import com.minwoo.springdatajpa.entity.Member;

import java.util.List;

public interface MemberCustomRepository {

    List<Member> findMembersByCustom();

}
