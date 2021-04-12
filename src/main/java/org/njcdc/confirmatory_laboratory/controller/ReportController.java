package org.njcdc.confirmatory_laboratory.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import org.apache.tomcat.util.http.fileupload.IOUtils;
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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/report")
public class ReportController {


    @Autowired
    DetectionRecordService detectionRecordsService;

    @Autowired
    SampleBasicInfoService sampleBasicInfoService;

    @GetMapping("/reportOutput/{acceptanceNumber}")
    public String reportOutput(HttpServletResponse response, @PathVariable String acceptanceNumber) throws IOException {
        SampleBasicInfo sampleBasicInfo = sampleBasicInfoService.getOne(new QueryWrapper<SampleBasicInfo>().eq("acceptanceNumber", acceptanceNumber));

        if (sampleBasicInfo.getOperation().equals("导出筛查阴性报告"))
            return screeningNegativeReport(response, acceptanceNumber);
        else
            return confReportOutput(response, acceptanceNumber);
    }

    // 导出确证检测报告
    public String confReportOutput(HttpServletResponse response, String acceptanceNumber) throws IOException {


        SampleBasicInfo sampleBasicInfo = sampleBasicInfoService.getOne(new QueryWrapper<SampleBasicInfo>().eq("acceptanceNumber", acceptanceNumber));

        DetectionRecord firstDetectionRecord = detectionRecordsService.getOne(new QueryWrapper<DetectionRecord>().eq("acceptanceNumber", acceptanceNumber).eq("sequence", 2));
        DetectionRecord secondDetectionRecord = detectionRecordsService.getOne(new QueryWrapper<DetectionRecord>().eq("acceptanceNumber", acceptanceNumber).eq("sequence", 3));

        // 获取确证检测记录
        DetectionRecord confDetectionRecord = detectionRecordsService.getOne(new QueryWrapper<DetectionRecord>().eq("acceptanceNumber", acceptanceNumber).eq("sequence", 4));

        Document document = new Document("src/main/resources/static/file/ConfReportTemplate.docx");
        /*
         * 以下是替换样品基本信息的步骤
         * */
        document.replace("inspectionUnit", sampleBasicInfo.getInspectionUnit(), true, true);
        document.replace("inspectionDate", String.valueOf(sampleBasicInfo.getInspectionDate()), true, true);
        document.replace("sampleType", sampleBasicInfo.getSampleType(), true, true);
        document.replace("inspectedType", sampleBasicInfo.getInspectedType(), true, true);
        document.replace("name", sampleBasicInfo.getName(), true, true);
        document.replace("age", String.valueOf(sampleBasicInfo.getAge()), true, true);
        document.replace("sex", sampleBasicInfo.getSex(), true, true);
        document.replace("profession", sampleBasicInfo.getProfession(), true, true);
        document.replace("country", sampleBasicInfo.getCountry(), true, true);
        document.replace("nation", sampleBasicInfo.getNation(), true, true);
        document.replace("marriage", sampleBasicInfo.getMarriage(), true, true);
        document.replace("educationalLevel", sampleBasicInfo.getEducationalLevel(), true, true);
        document.replace("IDNumber", sampleBasicInfo.getIDNumber(), true, true);
        document.replace("phone", sampleBasicInfo.getPhone(), true, true);
        document.replace("presentAddress", sampleBasicInfo.getPresentAddress(), true, true);
        document.replace("residenceAddress", sampleBasicInfo.getResidenceAddress(), true, true);


        /*
         * 以下是替换样品检测信息的步骤
         * */
        document.replace("detectionDate0", String.valueOf(firstDetectionRecord.getDetectionDate()), true, true);
        document.replace("detectionDate1", String.valueOf(secondDetectionRecord.getDetectionDate()), true, true);
        document.replace("detectionDate2", String.valueOf(confDetectionRecord.getDetectionDate()), true, true);
        document.replace("testResult0", firstDetectionRecord.getTestResult(), true, true);
        document.replace("testResult1", secondDetectionRecord.getTestResult(), true, true);
        document.replace("testResult2", confDetectionRecord.getTestResult(), true, true);


        /*
         * 以下是对批号和有效日期进行替换
         * 都是采用确证检测记录的信息
         * */
        document.replace("batchNumber", confDetectionRecord.getBatchNumber(), true, true);
        document.replace("effectiveDate", String.valueOf(confDetectionRecord.getEffectiveDate()), true, true);

        /*
         * 对样品编号、报告编号、报告日期进行替换
         * */
        document.replace("acceptanceNumber", acceptanceNumber, true, true);
        document.replace("reportNumber", sampleBasicInfo.getReportNumber(), true, true);
        document.replace("reportDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()), true, true);


        /*
        * 以下对结论和备注定制化处理（不同的结论对应不一样的报告）
        * 生成报告编号
        *
            筛查阴性报告编号示例：如：2019-1，即：年度-流水号，流水号5位，
            确证阴性报告编号示例：艾2019-南京-阴-1，2019是年度，1是流水
            确证阳性报告编号示例：艾2019-南京-1，2019是年度，1是流水
            结果不确定报告编号示例：艾2019-南京-疑-1，2019是年度，1是流水
        * */

        if (confDetectionRecord.getConclusion().equals("HIV-1抗体不确定")) {
            document.replace("conclusion", "HIV-1抗体不确定", true, true);
            document.replace("note", "本报告一式三份，一份给送检单位，两份本中心留存。\n" +
                    "建议进行核酸检测，或2-4周后随访", true, true);
        }
        if (confDetectionRecord.getConclusion().equals("HIV-1抗体阳性")) {
            document.replace("conclusion", "HIV-1抗体阳性", true, true);
            document.replace("note", "本报告一式三份，一份给送检单位，两份本中心留存。", true, true);

        }
        if (confDetectionRecord.getConclusion().equals("HIV抗体阴性")) {
            document.replace("conclusion", "HIV抗体阴性", true, true);
            document.replace("note", "本报告一式二份，一份给送检单位，一份本中心留存。", true, true);
        }

        document.saveToFile("src/main/resources/static/file/Temp.doc", FileFormat.Doc);
        //把doc输出到输出流中

        // 浏览器下载word
        String fileName = sampleBasicInfo.getReportNumber() + ".doc";//被下载文件的名称
        File file = new File("src/main/resources/static/file/Temp.doc");
        if (downloadFile(response, file, fileName))
            return "/";
        return "/logout";
    }


    // 导出筛查阴性报告
    public String screeningNegativeReport(HttpServletResponse response, String acceptanceNumber) throws IOException {
        SampleBasicInfo sampleBasicInfo = sampleBasicInfoService.getOne(new QueryWrapper<SampleBasicInfo>().eq("acceptanceNumber", acceptanceNumber));
        DetectionRecord firstDetectionRecord = detectionRecordsService.getOne(new QueryWrapper<DetectionRecord>().eq("acceptanceNumber", acceptanceNumber).eq("sequence", 2));
        DetectionRecord secondDetectionRecord = detectionRecordsService.getOne(new QueryWrapper<DetectionRecord>().eq("acceptanceNumber", acceptanceNumber).eq("sequence", 3));

        Document document = new Document("src/main/resources/static/file/ScreeningNegativeReport.doc");

        /*
         * 以下是替换样品基本信息的步骤
         * */
        document.replace("inspectionUnit", sampleBasicInfo.getInspectionUnit(), true, true);
        document.replace("inspectionDate", String.valueOf(sampleBasicInfo.getInspectionDate()), true, true);
        document.replace("sampleType", sampleBasicInfo.getSampleType(), true, true);
        document.replace("inspectedType", sampleBasicInfo.getInspectedType(), true, true);
        document.replace("name", sampleBasicInfo.getName(), true, true);
        document.replace("age", String.valueOf(sampleBasicInfo.getAge()), true, true);
        document.replace("sex", sampleBasicInfo.getSex(), true, true);
        document.replace("profession", sampleBasicInfo.getProfession(), true, true);
        document.replace("country", sampleBasicInfo.getCountry(), true, true);
        document.replace("nation", sampleBasicInfo.getNation(), true, true);
        document.replace("marriage", sampleBasicInfo.getMarriage(), true, true);
        document.replace("educationalLevel", sampleBasicInfo.getEducationalLevel(), true, true);
        document.replace("IDNumber", sampleBasicInfo.getIDNumber(), true, true);
        document.replace("phone", sampleBasicInfo.getPhone(), true, true);
        document.replace("presentAddress", sampleBasicInfo.getPresentAddress(), true, true);
        document.replace("residenceAddress", sampleBasicInfo.getResidenceAddress(), true, true);


        /*
         * 以下是替换样品检测信息的步骤
         * */
        document.replace("detectionDate0", String.valueOf(firstDetectionRecord.getDetectionDate()), true, true);
        document.replace("detectionDate1", String.valueOf(firstDetectionRecord.getDetectionDate()), true, true);
        document.replace("testResult0", firstDetectionRecord.getTestResult(), true, true);
        document.replace("testResult1", secondDetectionRecord.getTestResult(), true, true);
        document.replace("reagentsAndManufacturers0", firstDetectionRecord.getReagentsAndManufacturers(), true, true);
        document.replace("reagentsAndManufacturers1", secondDetectionRecord.getReagentsAndManufacturers(), true, true);

        /*
         * 对样品编号、报告编号、报告日期进行替换
         * */
        document.replace("acceptanceNumber", acceptanceNumber, true, true);
        document.replace("reportNumber", sampleBasicInfo.getReportNumber(), true, true);
        document.replace("reportDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()), true, true);

        document.saveToFile("src/main/resources/static/file/Temp.doc", FileFormat.Doc);
        //把doc输出到输出流中

        // 浏览器下载word
        String fileName = sampleBasicInfo.getReportNumber() + ".doc";//被下载文件的名称
        File file = new File("src/main/resources/static/file/Temp.doc");
        downloadFile(response, file, fileName);
        if (downloadFile(response, file, fileName))
            return "/";
        return "/logout";
    }

    public Boolean downloadFile(HttpServletResponse response, File file, String fileName) throws IOException {
        FileInputStream fis = null;
        ServletOutputStream os = null;
        try {

            if (file.exists()) {
                //响应头的下载内容格式
                response.setContentType("application/octet-stream");
                response.setHeader("content-type", "application/octet-stream");
                response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));

                fis = new FileInputStream(file);
                byte[] bytes = new byte[1024];

                os = response.getOutputStream();
//                FileOutputStream os = new FileOutputStream(new File("C:\\Users\\ASUS\\Desktop\\"+filename));
                int i = fis.read(bytes);    //从文件夹中读取文件通过流写入指定文件中
                while (i != -1) {
                    os.write(bytes);
                    i = fis.read(bytes);
                }
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            assert os != null;
            os.close();
            fis.close();
        }
        return false;
    }
}
