package com.example.googlesch;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/searches")
@EnableScheduling
public class SearchController {
    @Autowired
    private SearchService searchService;

    @GetMapping("/crawl/{keyword}")
    public void crawlAndSaveSearches(@PathVariable String keyword){
        searchService.crawlAndSaveSearches(keyword);
    }

    @GetMapping("/export")
    public ResponseEntity<ByteArrayResource> exportToExcel() throws IOException{
        List<Search> searches = searchService.getAllSearches();

        //엑셀 워크북 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Searches");

        //열 제목 생성
        String[] headers = {"논문 제목", "저자", "발행 연도", "발행처", "링크"};
        Row headerRow = sheet.createRow(0);
        for(int i = 0; i < headers.length; i++){
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        //논문 데이터 삽입
        int rowIndex = 1;
        for(Search search : searches){
            Row dataRow = sheet.createRow(rowIndex++);
            dataRow.createCell(0).setCellValue(search.getTitle());
            dataRow.createCell(1).setCellValue(search.getAuthors());
            dataRow.createCell(2).setCellValue(search.getPublicationYear());
            dataRow.createCell(3).setCellValue(search.getPublisher());
            dataRow.createCell(4).setCellValue(search.getLink());
        }

        //엑셀 파일로 변환
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        //엑셀 파일을 응답으로 전송
        byte[] fileBytes = outputStream.toByteArray();
        ByteArrayResource resource = new ByteArrayResource(fileBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=Papers.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(fileBytes.length)
                .body(resource);
    }
}

