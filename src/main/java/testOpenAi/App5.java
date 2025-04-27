package testOpenAi;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.net.URI;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Arrays;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.io.FileOutputStream;



public class App5 {
    private static final String KEY = new Key().getKey();
    private static final String OPEN_AI_URL = "https://api.openai.com/v1/chat/completions";
//    public static String filePath = "./sourceFile/nvda1.csv";

    public static String filePath = "./sourceFile/intc.csv";

    public static String outputFileName = "INTC" + ".xlsx";
    public static String outputFilePath = "./output/" + outputFileName;


    public static void main(String[] args){
//        File file = new File(filePath);
//        System.out.println(file.exists());
        String role = "你是專業的投資理財專員，請幫我依據傳送的資料，分析出重要的資訊，回傳資料格" +
                "式請用json，回傳的資料僅限於json字串內容，不要放入其他說明文字或符號；因為" +
                "你會分析出多個重點，所以json 中是包含多個物件的陣列，json 中的物件有title," +
                "score, description三個屬性，title 是你分析重點的標題，像是「營收增長」或是" +
                "「成本與支出」，scroe 是從1到5 ，對於這個分析項目的評分，最後description 是" +
                "你對這個項目的分析內容，例如「2024年的總營收為609.22億美元，相較於2023年" +
                "的269.74億美元，增長了約126%。這是一個非常顯著的增長，顯示出公司在這一年內的銷售能力顯著提升」";

        StringBuilder data = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))){
            String line = "";
            while((line = reader.readLine()) != null){
                data.append(line);
            }
            data.toString();
        }catch(IOException e){
            e.printStackTrace();
        }

        try{
            List<Message> messages = new ArrayList<>();
            messages.add(new Message("system", role));
            messages.add(new Message("user", "請幫忙從財報資料中分析出這間公司的營收狀況，並用JSON格式回答，格式如下：[{'title':'','score':0,'description':''}]，只回傳JSON，不要多餘文字！，幫助我判斷是否能投資其股票，資料： " + data));
            Gson gson = new Gson();
            JsonObject json = new JsonObject();
            json.addProperty("model", "gpt-4o-mini");
            json.add("messages", gson.toJsonTree(messages));

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(OPEN_AI_URL))
                    .header("content-Type", "application/json")
                    .header("Authorization", "Bearer " + KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(json)))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            JsonObject jsonRes = gson.fromJson(res.body(), JsonObject.class);
            String result = jsonRes.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();

            System.out.println(result);
            System.out.println("===============================");
            System.out.println(jsonRes.toString());
            System.out.println(res.body());
            // POI output file to Excel format
            creatReport(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void creatReport(String json){
        Gson gson = new Gson();
        Report[] reportArray = gson.fromJson(json, Report[].class);
        List<Report> reports = Arrays.asList(reportArray);
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Financial Report");
        int rowCount = 0;
        Row headerRow = sheet.createRow(rowCount++);
        headerRow.createCell(0).setCellValue("Title");
        headerRow.createCell(1).setCellValue("Score");
        headerRow.createCell(2).setCellValue("Description");

        for (Report report : reports) {
            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(report.getTitle());
            row.createCell(1).setCellValue(report.getScore());
            row.createCell(2).setCellValue(report.getDescription());
        }
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }

        try(FileOutputStream fileOut = new FileOutputStream(outputFilePath)){
            workbook.write(fileOut);
            workbook.close();
            System.out.println("Report created successfully: \n" + outputFilePath);
        }catch (IOException e){
            e.printStackTrace();
        }

    }

}
