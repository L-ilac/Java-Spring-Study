package hello.jdbc.exception.basic;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * CheckedTest
 */
@Slf4j
public class CheckedTest {

    @Test
    void checked_catch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void check_throw() {
        Service service = new Service();
        assertThatThrownBy(() -> service.callThrow()).isInstanceOf(MyCheckedException.class);
    }

    // * Exception 을 상속받은 예외는 체크 예외가 된다.
    static class MyCheckedException extends Exception {

        public MyCheckedException(String message) {
            super(message);
        }

    }

    static class Service {
        Repository repository = new Repository();

        // 던져진 예외를 잡아서 처리하는 코드 -> try_catch로 예외를 잡아서 처리한다.
        public void callCatch() {
            try {
                repository.call();
            } catch (MyCheckedException e) {
                // 예외 처리 로직
                log.info("예외처리, message = {}", e.getMessage(), e);
            }
        }

        // 체크 예외를 밖으로 던지는 코드 -> 체크 예외는 예외를 잡지않고 밖으로 던지려면 'throws 예외'를 메서드에 필수로 선언해야함
        public void callThrow() throws MyCheckedException {
            repository.call();
        }

    }

    static class Repository {
        public void call() throws MyCheckedException {

            throw new MyCheckedException("ex");

        }
    }
}