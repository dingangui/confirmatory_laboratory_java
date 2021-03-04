package org.njcdc.confirmatory_laboratory.controller;


import org.njcdc.confirmatory_laboratory.common.dto.SampleBasicInfoDto;
import org.njcdc.confirmatory_laboratory.common.lang.Result;
import org.njcdc.confirmatory_laboratory.entity.SampleBasicInfo;
import org.njcdc.confirmatory_laboratory.service.SampleBasicInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
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
    public Result save(@Validated @RequestBody SampleBasicInfoDto sampleBasicInfoDto){

        SampleBasicInfo sampleBasicInfo = new SampleBasicInfo();
        BeanUtils.copyProperties(sampleBasicInfoDto,sampleBasicInfo);
        Assert.isTrue(sampleBasicInfoService.save(sampleBasicInfo),"保存失败");
        return Result.success(sampleBasicInfoDto);

    }
}
