package hello.jdbc.exception.basic;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import hello.jdbc.exception.basic.CheckedTest.MyCheckedException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UnCheckedTest {

    @Test
    void unchecked_catch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void unchecked_throw() {
        Service service = new Service();
        assertThatThrownBy(() -> service.callThrow()).isInstanceOf(MyCheckedException.class);

    }

    static class MyUncheckedException extends RuntimeException {

        public MyUncheckedException(String message) {
            super(message);
        }

    }

    static class Service {
        Repository repository = new Repository();

        // 필요한 경우 예외를 잡아서 처리하면 된다.
        public void callCatch() {
            try {
                repository.call();
            } catch (MyUncheckedException e) {
                // 예외 처리 로직
                log.info("예외처리, message = {}", e.getMessage(), e);
            }
        }

        // 예외를 밖으로 던지는 코드 -> 언체크 예외는 'throws 예외'를 생략하면 알아서 밖으로 예외를 던진다.
        public void callThrow() {
            repository.call();
        }

    }

    static class Repository {
        // * 언체크 예외는 예외를 잡거나, 던지지 않아도 된다.
        // * 'throws 예외'를 생략할 수 있다. 생략하는 경우 자동으로 던진다.
        public void call() {
            throw new MyUncheckedException("ex");
        }
    }
}
