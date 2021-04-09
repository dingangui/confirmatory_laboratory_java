package org.njcdc.confirmatory_laboratory.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.njcdc.confirmatory_laboratory.common.lang.Result;
import org.njcdc.confirmatory_laboratory.entity.DetectionRecord;
import org.njcdc.confirmatory_laboratory.entity.SampleBasicInfo;
import org.njcdc.confirmatory_laboratory.service.DetectionRecordService;
import org.njcdc.confirmatory_laboratory.service.SampleBasicInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
public class ReportController {


    @Autowired
    DetectionRecordService detectionRecordsService;

    @Autowired
    SampleBasicInfoService sampleBasicInfoService;

    @GetMapping("/confReportOutput/{acceptanceNumber}")
    public Result confReportOutput(@PathVariable String acceptanceNumber){

        SampleBasicInfo sampleBasicInfo = sampleBasicInfoService.getOne(new QueryWrapper<SampleBasicInfo>().eq("acceptanceNumber",acceptanceNumber));
        // 获取确证检测记录
        DetectionRecord detectionRecord = detectionRecordsService.getOne(new QueryWrapper<DetectionRecord>().eq("acceptanceNumber",acceptanceNumber).eq("sequence",4));

        if (detectionRecord.getConclusion().equals("HIV-1抗体不确定")) {

        }
        if (detectionRecord.getConclusion().equals("HIV-1抗体阳性")) {

        }
        if (detectionRecord.getConclusion().equals("HIV抗体阴性")) {

        }

        return Result.success(null);
    }
}
