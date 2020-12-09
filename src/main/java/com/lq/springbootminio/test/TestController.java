package com.lq.springbootminio.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * description
 *
 * @author quan.luo@hand-china.com 2020/11/28 15:44
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/ceshi")
    private String test(){
        return "hello";
    }
}
