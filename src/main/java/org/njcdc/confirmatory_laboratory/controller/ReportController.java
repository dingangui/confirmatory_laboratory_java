package org.njcdc.confirmatory_laboratory.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
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

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/report")
public class ReportController {


    @Autowired
    DetectionRecordService detectionRecordsService;

    @Autowired
    SampleBasicInfoService sampleBasicInfoService;

    // 导出确证检测报告
    @GetMapping("/confReportOutput/{acceptanceNumber}")
    public Result confReportOutput(@PathVariable String acceptanceNumber, HttpServletResponse response) throws UnsupportedEncodingException {

        SampleBasicInfo sampleBasicInfo = sampleBasicInfoService.getOne(new QueryWrapper<SampleBasicInfo>().eq("acceptanceNumber",acceptanceNumber));
        // 获取确证检测记录
        DetectionRecord detectionRecord = detectionRecordsService.getOne(new QueryWrapper<DetectionRecord>().eq("acceptanceNumber",acceptanceNumber).eq("sequence",4));

        Document document = new Document("src/main/resources/static/file/阳性报告模板.doc");
        document.replace("inspectionUnit", "111", true, true);

/*

        if (detectionRecord.getConclusion().equals("HIV-1抗体不确定")) {

        }
        if (detectionRecord.getConclusion().equals("HIV-1抗体阳性")) {

        }
        if (detectionRecord.getConclusion().equals("HIV抗体阴性")) {

        }
*/


        document.saveToFile("src/main/resources/static/file/ELISATemp.docx", FileFormat.Docx_2013);
        //把doc输出到输出流中

        // 浏览器下载word
        String fileName = "HIV.docx";//被下载文件的名称
        fileName = new String(fileName.getBytes(), StandardCharsets.ISO_8859_1);
        File file = new File("src/main/resources/static/file/ELISATemp.docx");
        downloadFile(file, response, fileName);

        return null;
    }

    //  浏览器下载文件
    public void downloadFile(File file, HttpServletResponse response, String fileName) throws UnsupportedEncodingException {
        if (file.exists()) {
            // 配置文件下载
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            // 下载文件能正常显示中文
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

            // 实现文件下载
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                System.out.println("Download the song successfully!");
            }
            catch (Exception e) {
                System.out.println("Download the song failed!");
            }
            finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
