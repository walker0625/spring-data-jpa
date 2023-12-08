package com.minwoo.springdatajpa.controller;

import com.minwoo.springdatajpa.entity.Member;
import com.minwoo.springdatajpa.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        return memberRepository.findById(id).get().getUsername();
    }

    // 단순 조회시는 사용할만하나 비추천
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) { // select query가 동작
        return member.getUsername();
    }

    @PostConstruct
    public void init() {
        memberRepository.save(new Member("member1"));
    }

}