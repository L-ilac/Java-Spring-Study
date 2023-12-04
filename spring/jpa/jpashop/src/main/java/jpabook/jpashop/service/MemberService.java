package jpabook.jpashop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원 가입
    @Transactional
    public Long join(Member member) {

        validateDuplicateMember(member); // 중복회원 검증

        // 중복회원 검증을 동시에 통과한 동일한 사용자 정보가 저장될 수 있는 위험이 있으므로,
        // name을 DB에서 unique 제약 조건을 설정해줄 필요가 있다.
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 전체 회원 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 단일 회원 조회
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);

    }

    @Transactional
    public void update(Long memberId, String name) {
        Member member = memberRepository.findOne(memberId);
        member.setName(name);

        // * 여기에서 변경된 member를 반환하면, update 함수가 엔티티를 변경하는 기능뿐만 아니라 조회의 기능도 동시에 하게되는 상황이 된다.
        // * 가급적 커맨드와 쿼리를 철저히 분리한다. -> update 함수는 update의 역할만 하도록 하고, 업데이트가 된 엔티티를 '조회'하는 것은 조회하는 기능을 가진 함수를 이용하여 조회하는 것이 좋다.
    }
}
