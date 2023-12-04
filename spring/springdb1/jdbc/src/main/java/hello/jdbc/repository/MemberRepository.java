package hello.jdbc.repository;

import hello.jdbc.domain.Member;

// ! 특정 Exception에 종속적이지 않은 리포지토리 인터페이스
public interface MemberRepository {
    Member save(Member member);

    Member findById(String memberId);

    void update(String memberId, int money);

    void delete(String memberId);
}
