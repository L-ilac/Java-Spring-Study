package hello.typeconverter.controller;

import org.springframework.web.bind.annotation.*;

import hello.typeconverter.type.IpPort;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class HelloController {
    @GetMapping("/hello-v1")
    public String hellov1(HttpServletRequest request) {
        String data = request.getParameter("data");
        Integer intValue = Integer.valueOf(data);
        System.out.println(intValue);
        return "ok";
    }

    @GetMapping("/hello-v2")
    public String hellov2(@RequestParam Integer data) {
        System.out.println(data);
        return "ok";
    }

    @GetMapping("/ip-port")
    public String ipPort(@RequestParam IpPort ipPort) {
        System.out.println(ipPort.getIp());
        System.out.println(ipPort.getPort());
        return "ok";
    }

    @GetMapping("/ip-port2")
    public String ipPort2(@ModelAttribute IpPort ipPort) {
        System.out.println(ipPort.getIp());
        System.out.println(ipPort.getPort());
        return "ok";
    }
}
