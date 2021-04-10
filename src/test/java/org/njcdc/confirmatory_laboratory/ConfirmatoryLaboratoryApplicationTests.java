package org.njcdc.confirmatory_laboratory;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.njcdc.confirmatory_laboratory.common.lang.Result;
import org.njcdc.confirmatory_laboratory.entity.DetectionRecord;
import org.njcdc.confirmatory_laboratory.service.DetectionRecordService;
import org.njcdc.confirmatory_laboratory.service.SampleBasicInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

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

    @GetMapping("/download")
    @Test
    public void download(HttpServletResponse response) throws IOException {
        FileInputStream fis = null;
        ServletOutputStream os = null;
        try {
            String filename = "1.docx";
            String filePath = "src/main/resources/static/file/" + filename;
            File file = new File(filePath);

            if (file.exists()) {
                //响应头的下载内容格式
                response.setContentType("application/octet-stream");
                response.setHeader("content-type", "application/octet-stream");
                response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(filename, "utf8"));

                fis = new FileInputStream(file);
                byte[] bytes = new byte[1024];

                os = response.getOutputStream();
//                FileOutputStream os = new FileOutputStream(new File("C:\\Users\\ASUS\\Desktop\\"+filename));
                int i = fis.read(bytes);    //从文件夹中读取文件通过流写入指定文件中
                while (i != -1) {
                    os.write(bytes);
                    i = fis.read(bytes);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            assert os != null;
            os.close();
            fis.close();
        }
    }
}
