package hello.jdbc.service;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// ! 트랜잭션- 파라미터 연동(커넥션을 비즈니스 로직의 파라미터로 전달하여 동일한 커넥션을 사용하는 것을 보장), 풀을 고려한 종료
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        Connection con = dataSource.getConnection();

        try {
            con.setAutoCommit(false); // ! 트랜잭션 시작

            // * 비즈니스 로직
            bizLogic(fromId, toId, money, con);

            con.commit(); // * 성공시 commit

        } catch (Exception e) {
            con.rollback(); // ! 실패시 rollback
            throw new IllegalStateException(e);
        } finally {
            release(con);
        }

    }

    private void bizLogic(String fromId, String toId, int money, Connection con) throws SQLException {
        Member fromMember = memberRepository.findById(toId, con);
        Member toMember = memberRepository.findById(toId, con);

        memberRepository.update(fromId, fromMember.getMoney() - money, con);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money, con);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }

    private void release(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true); // ! 커넥션 풀에 반환할 때는 autocommit을 true로 설정
                con.close();
            } catch (Exception e) {
                log.info("error", e);
                // TODO: handle exception
            }
        }
    }
}
