package hello.login.web.filter;

import java.io.IOException;

import org.springframework.util.PatternMatchUtils;

import hello.login.web.SessionConst;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginCheckFilter implements Filter {

    private static final String[] whitelist = { "/", "/members/add", "/login", "/logout", "/css/*" };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            log.info("인증 체크 필터 시작 {}", requestURI);
            if (isLoginCheckPath(requestURI)) {
                log.info("인증 체크 로직 실행 {}", requestURI);

                HttpSession session = httpRequest.getSession(false);
                log.info("session = {}", session);

                if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
                    log.info("미인증 사용자 요청 {}", requestURI);

                    // ! 로그인이 수행되면 로그인 페이지전에 접속하려했던 원래 페이지로 돌아갈 수 있게 하기 위함
                    httpResponse.sendRedirect("/login?redirectURL=" + requestURI);

                    return; // ! 로그인이 수행되어야하는 페이지 접근이라면, 그 다음 동작을 이어서 수행하면 안됌. 

                }
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            // TODO: handle exception
            throw e;
        } finally {
            log.info("인증 체크 로직 종료 {}", requestURI);
        }

    }

    private boolean isLoginCheckPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }

}
