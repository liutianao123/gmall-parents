package com.atguigu.gmall.admin.ums.controller;

import com.atguigu.gmall.admin.utils.JwtTokenUtil;
import com.atguigu.gmall.ums.entity.Member;
import com.atguigu.gmall.ums.entity.MemberLevel;
import com.atguigu.gmall.ums.service.MemberLevelService;
import com.atguigu.gmall.ums.service.MemberService;
import io.swagger.annotations.Api;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

//memberLevel/list?defaultStatus=0
@CrossOrigin
@RestController
@Api("会员")
@RequestMapping("memberLevel")
public class MemberController {
    @Reference
    private MemberLevelService memberLevelService;
//memberLevel/list?defaultStatus=0
    @GetMapping("list")
    public List<MemberLevel> getLeve(@RequestParam(value = "defaultStatus") Integer defaultStatus){
        List<MemberLevel> memberLevels = memberLevelService.select(defaultStatus);
        return memberLevels;
    }

}
