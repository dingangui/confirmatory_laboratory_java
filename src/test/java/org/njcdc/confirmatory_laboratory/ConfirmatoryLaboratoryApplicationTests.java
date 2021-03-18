package org.njcdc.confirmatory_laboratory;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.njcdc.confirmatory_laboratory.common.lang.Result;
import org.njcdc.confirmatory_laboratory.entity.DetectionRecords;
import org.njcdc.confirmatory_laboratory.mapper.SampleBasicInfoMapper;
import org.njcdc.confirmatory_laboratory.service.DetectionRecordsService;
import org.njcdc.confirmatory_laboratory.service.SampleBasicInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@SpringBootTest
class ConfirmatoryLaboratoryApplicationTests {
    @Autowired
    SampleBasicInfoService sampleBasicInfoService;

    @Test
    void contextLoads() {

        int num = sampleBasicInfoService.count();
        System.out.println(num);
    }

    @Autowired
    DetectionRecordsService detectionRecordsService;
    @Test
    public Result save(@RequestBody DetectionRecords detectionRecords){
        QueryWrapper<DetectionRecords> queryWrapper = new QueryWrapper<>();
        detectionRecordsService.count(queryWrapper.eq("acceptanceNumber", detectionRecords.getAcceptanceNumber()));
        detectionRecordsService.save(detectionRecords);
        return Result.success("保存成功",null);
    }

}
