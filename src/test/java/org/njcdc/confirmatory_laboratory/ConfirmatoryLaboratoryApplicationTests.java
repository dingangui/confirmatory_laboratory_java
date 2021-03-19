package org.njcdc.confirmatory_laboratory;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.njcdc.confirmatory_laboratory.common.lang.Result;
import org.njcdc.confirmatory_laboratory.entity.DetectionRecord;
import org.njcdc.confirmatory_laboratory.service.DetectionRecordService;
import org.njcdc.confirmatory_laboratory.service.SampleBasicInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    DetectionRecordService detectionRecordsService;
    @Test
    public Result save(@RequestBody DetectionRecord detectionRecord){
        QueryWrapper<DetectionRecord> queryWrapper = new QueryWrapper<>();
        detectionRecordsService.count(queryWrapper.eq("acceptanceNumber", detectionRecord.getAcceptanceNumber()));
        detectionRecordsService.save(detectionRecord);
        return Result.success("保存成功",null);
    }

}
