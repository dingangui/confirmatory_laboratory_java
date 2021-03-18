package org.njcdc.confirmatory_laboratory.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.njcdc.confirmatory_laboratory.common.lang.Result;
import org.njcdc.confirmatory_laboratory.entity.DetectionRecords;
import org.njcdc.confirmatory_laboratory.entity.SampleBasicInfo;
import org.njcdc.confirmatory_laboratory.service.DetectionRecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.sql.Wrapper;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author dingangui
 * @since 2021-03-18
 */
@RestController
@RequestMapping("/detectionRecords")
public class DetectionRecordsController {

    @Autowired
    DetectionRecordsService detectionRecordsService;
    @PostMapping("/save")
    public Result save(@RequestBody DetectionRecords detectionRecords){
        QueryWrapper<DetectionRecords> queryWrapper = new QueryWrapper<>();
        int testTime = detectionRecordsService.count(queryWrapper.eq("acceptanceNumber", detectionRecords.getAcceptanceNumber()));
        detectionRecords.setTestTime(testTime+1);
        detectionRecordsService.save(detectionRecords);
        return Result.success("保存成功",null);
    }

}
