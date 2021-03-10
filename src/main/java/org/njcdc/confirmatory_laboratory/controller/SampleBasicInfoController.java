package org.njcdc.confirmatory_laboratory.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.njcdc.confirmatory_laboratory.common.lang.Result;
import org.njcdc.confirmatory_laboratory.entity.SampleBasicInfo;
import org.njcdc.confirmatory_laboratory.service.SampleBasicInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author dingangui
 * @since 2021-03-01
 */
@RestController
@RequestMapping("/sample-basic-info")
public class SampleBasicInfoController {

    @Autowired
    SampleBasicInfoService sampleBasicInfoService;


    @PostMapping("/save")
    public Result save(@Validated @RequestBody SampleBasicInfo sampleBasicInfo) {

        Calendar cal = Calendar.getInstance();
        System.out.println(sampleBasicInfo.toString());
        int num = sampleBasicInfoService.list().size();
        String acceptanceNumber = cal.get(Calendar.YEAR) + " - " + (num + 1);
        sampleBasicInfo.setAcceptanceNumber(acceptanceNumber);
        Assert.isTrue(sampleBasicInfoService.save(sampleBasicInfo), "保存失败");
        return Result.success("保存成功");

    }

    @GetMapping("/getAcceptanceNumber")
    public Result getAcceptanceNumber() {
        Calendar cal = Calendar.getInstance();
        int num = sampleBasicInfoService.list().size();
        String acceptanceNumber = cal.get(Calendar.YEAR) + " - " + (num + 1);
        return Result.success(acceptanceNumber);
    }

    @GetMapping("/getSampleInfoByAcceptanceNumber/{acceptanceNumber}")
    public Result getSampleInfoByAcceptanceNumber(@PathVariable(name = "acceptanceNumber") String acceptanceNumber ){

        return Result.success(sampleBasicInfoService.getOne(new QueryWrapper<SampleBasicInfo>().eq("acceptanceNumber",acceptanceNumber)));
    }
}
