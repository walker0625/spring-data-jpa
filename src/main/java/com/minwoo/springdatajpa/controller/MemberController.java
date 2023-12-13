package com.minwoo.springdatajpa.controller;

import com.minwoo.springdatajpa.dto.MemberDto;
import com.minwoo.springdatajpa.entity.Member;
import com.minwoo.springdatajpa.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    // /members?page=3&size=10&sort=id,desc
    @GetMapping("/members")
    public Page<MemberDto> list(@Qualifier("member") @PageableDefault(size = 5, sort = "id") Pageable memberPageable
                             //,@Qualifier("order") @PageableDefault(size = 15, sort = "id") Pageable orderPageable
    ) {
        return memberRepository.findAll(memberPageable).map(MemberDto::new); // m -> new MemberDto(m)
    }

    @PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("member" + i));
        }
    }

}