package testOpenAi;

import java.io.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.List;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class App3 {
    private static final String KEY = new Key().getKey();
    private static final String OPEN_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final int BATCH_SIZE = 1000; // 每個Batch的最大字數

    public static void main(String[] args) {
        String inputFilePath = "./sourceFile/input1.txt";
//        File file = new File(inputFilePath); // 驗證檔案是否存在
//        System.out.println(file.exists());
        String outputFilePath = "./output/output1.txt";

        List<String> batches = new ArrayList<>(); // 用來存放batch
        StringBuilder batch = new StringBuilder(); // 用 StringBuilder 來減少不必要的記憶體使用
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) { //try with resources 的語法如遇到exception也會自動關閉資源
            String line;
            while ((line = reader.readLine()) != null) {
                if (batch.length() + line.length() > BATCH_SIZE) { // 目前在batch中的字數加上目前讀取的字數如果大於1000
                    batches.add(batch.toString()); // 將batch加到batches
                    batch = new StringBuilder(); //  並重新new一個StringBuilder
                }
                batch.append(line).append("\n"); // 如果沒有超過1000則加到batch中
            }
            if (!batch.isEmpty()) { // 這裡是為了能夠將最後一段也能夠讀取到
                batches.add(batch.toString()); // 將最後的字加到batches
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> translations = new ArrayList<>(); // 用來儲存翻譯後的結果
        int count = 1;
        for (String batchTemp : batches) { // 從batches中取出翻譯結果
            System.out.println("Translate batch No." + count);
            String translation = translateText(batchTemp);
            translations.add(translation); // 存到 translations中
            count++;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            for (String translation : translations) {
                writer.write(translation); // 將翻譯結果寫到檔案中
                writer.newLine(); // 新增一行
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Translation complete. -> " + outputFilePath);

    }


    private static String translateText(String text) {
        try{
            List<Message> messages = new ArrayList<>();
            messages.add(new Message("system", "你是翻譯助手，請將英文翻譯成繁體中文")); // 設定gpt角色
            messages.add(new Message("user", text)); // 輸入要翻譯的內容
            Gson gson = new Gson();
            JsonObject json = new JsonObject();
            json.addProperty("model", "gpt-4o-mini"); // 選擇模型
            json.add("messages", gson.toJsonTree(messages)); // 將List<Message> messages轉成json物件

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(OPEN_API_URL)) //建立連線
                    .header("Content-Type", "application/json") //傳送資料形式
                    .header("Authorization", "Bearer " + KEY) // 傳送key
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(json)))
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString()); // 取的HttpResponse 的物件
            JsonObject jsonRes = gson.fromJson(res.body(), JsonObject.class); // 取得res.body()中的json物件，並轉成JsonObject.class
            System.out.println(jsonRes.toString());
            String translation = jsonRes.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
            return translation;

        }catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

}
