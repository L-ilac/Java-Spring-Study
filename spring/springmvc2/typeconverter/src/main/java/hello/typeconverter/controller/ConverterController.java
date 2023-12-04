package hello.typeconverter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import hello.typeconverter.type.IpPort;
import lombok.Data;

@Controller
public class ConverterController {

    @GetMapping("/converter-view")
    public String converterView(Model model) {
        model.addAttribute("number", 10000);
//        model.addAttribute("ipPort", new IpPort("127.0.0.1", 8080));

        return "converter-view";
    }

//    @GetMapping("/converter/edit")
//    public String conveterForm(Model model) {
//        IpPort ipPort = new IpPort("127.0.0.1", 8080);
//        System.out.println(ipPort);
//
//        Form form = new Form(ipPort);
//        System.out.println(form);
//
//        model.addAttribute("form", form);
//        return "converter-form";
//
//    }

    @PostMapping("/converter/edit")
    public String conveterEdit(@ModelAttribute Form form, Model model) {
        IpPort ipPort = form.getIpPort();
        model.addAttribute("ipPort", ipPort);
        return "converter-view";

    }

    @Data
    static class Form {
        private IpPort ipPort;

        public Form(IpPort ipPort) {
            this.ipPort = ipPort;
        }

    }

}
