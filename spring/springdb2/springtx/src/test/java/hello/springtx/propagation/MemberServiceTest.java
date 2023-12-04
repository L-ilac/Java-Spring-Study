package hello.springtx.propagation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class MemberServiceTest {

    @Autowired
    MemberService memberService; // @Transactional : OFF

    @Autowired
    MemberRepository memberRepository; // @Transactional : ON

    @Autowired
    LogRepository logRepository; // @Transactional : ON

    // ! MemberService    @Transactional : OFF
    // ! MemberRepository @Transactional : ON    
    // ! LogRepository    @Transactional : ON

    // @Transactional
    @Test
    void outerTxOff_success() {
        String username = "outerTxOff_success";

        memberService.joinV1(username);

        org.junit.jupiter.api.Assertions.assertTrue(memberRepository.find(username).isPresent());
        org.junit.jupiter.api.Assertions.assertTrue(logRepository.find(username).isPresent());
    }

    @Test

    void outerTxOff_fail() {
        String username = "로그예외outerTxOff_fail";

        assertThatThrownBy(() -> memberService.joinV1(username)).isInstanceOf(RuntimeException.class);

        org.junit.jupiter.api.Assertions.assertTrue(memberRepository.find(username).isPresent());
        org.junit.jupiter.api.Assertions.assertTrue(logRepository.find(username).isEmpty());

    }

    // ! MemberService    @Transactional : ON
    // ! MemberRepository @Transactional : OFF   
    // ! LogRepository    @Transactional : OFF

    @Test
    void singleTx_success() {
        String username = "singleTx_success";

        memberService.joinV1(username);

        org.junit.jupiter.api.Assertions.assertTrue(memberRepository.find(username).isPresent());
        org.junit.jupiter.api.Assertions.assertTrue(logRepository.find(username).isPresent());
    }

    @Test
    void singleTx_fail() {
        String username = "로그예외singleTx_fail";

        assertThatThrownBy(() -> memberService.joinV1(username)).isInstanceOf(RuntimeException.class);

        org.junit.jupiter.api.Assertions.assertTrue(memberRepository.find(username).isEmpty());
        org.junit.jupiter.api.Assertions.assertTrue(logRepository.find(username).isEmpty());
    }

    // ! MemberService    @Transactional : ON
    // ! MemberRepository @Transactional : ON   
    // ! LogRepository    @Transactional : ON

    @Test
    void outerTxOn_success() {
        String username = "outerTxOn_success";

        memberService.joinV1(username);

        org.junit.jupiter.api.Assertions.assertTrue(memberRepository.find(username).isPresent());
        org.junit.jupiter.api.Assertions.assertTrue(logRepository.find(username).isPresent());
    }

    @Test
    void outerTxOn_fail() {
        String username = "로그예외outerTxOn_fail";

        assertThatThrownBy(() -> memberService.joinV1(username)).isInstanceOf(RuntimeException.class);

        org.junit.jupiter.api.Assertions.assertTrue(memberRepository.find(username).isEmpty());
        org.junit.jupiter.api.Assertions.assertTrue(logRepository.find(username).isEmpty());

    }

    // ! MemberService    @Transactional : ON -> 예외를 잡아서 정상흐름으로 반환
    // ! MemberRepository @Transactional : ON   
    // ! LogRepository    @Transactional : ON

    @Test
    void recoverException_fail() {
        String username = "로그예외recoverException_fail";

        // ! rollbackOnly 속성 때문에 물리 트랜잭션이 commit을 호출하지만, 롤백되어야하므로 UnexpectedRollbackException 발생
        assertThatThrownBy(() -> memberService.joinV2(username)).isInstanceOf(UnexpectedRollbackException.class);

        org.junit.jupiter.api.Assertions.assertTrue(memberRepository.find(username).isEmpty());
        org.junit.jupiter.api.Assertions.assertTrue(logRepository.find(username).isEmpty());

    }

    @Test
    void recoverException_success() {
        String username = "로그예외recoverException_success";

        memberService.joinV2(username);

        org.junit.jupiter.api.Assertions.assertTrue(memberRepository.find(username).isPresent());
        org.junit.jupiter.api.Assertions.assertTrue(logRepository.find(username).isEmpty());

    }
}
