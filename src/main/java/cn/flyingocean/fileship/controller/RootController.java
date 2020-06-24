package cn.flyingocean.fileship.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RootController {
    /**
     * 返回上传/下面页面
     *
     * @return
     */
    @RequestMapping("/")
    public String index() {
        return "index";
    }
}
