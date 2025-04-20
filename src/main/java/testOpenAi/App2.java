package testOpenAi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class App2 {
    // API key
    private static final String KEY = new Key().getKey();
    // https://platform.openai.com/docs/api-reference/authentication
    private static final String OPEN_AI_URL = "https://api.openai.com/v1/chat/completions";

    public static void main(String[] args) {
        String text = "我忘記密碼，帳號被鎖定了怎麼辦";
        try{
            List<Message> messages = new ArrayList<>();
            // 先定義要Open AI 的功能
            messages.add(new Message("system", "你是一位網站客服，請幫忙回答使用者一般網站常發生的問題"));
            // 再傳送要gpt處理的事件內容
            messages.add(new Message("user", text));
            // 用google的gson 套件將java物件轉成json物件
            Gson gson = new Gson();
            JsonObject json = new JsonObject();
            // 設定要使用的模型
            json.addProperty("model", "gpt-4o-mini");
            // 將在ArrayList設定好的資料轉成json物件
            json.add("messages", gson.toJsonTree(messages));

            // 進行傳輸
            HttpRequest req = HttpRequest.newBuilder() // 建立 request
                    .uri(URI.create(OPEN_AI_URL)) // 建立連線到openAI URL
                    .header("Content-Type", "application/json") // header中的物件形式
                    .header("Authorization", "Bearer " +  KEY) // header中的授權資訊
                    // 用post方法傳送｜ HttpRequest.BodyPublishers.ofString定義要傳送的是字串 |  gson.toJson(json) 將json物件轉成json字串
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(json)))
                    .build(); // 建立 HttpRequest物件
            // 建立client 端連線
            HttpClient client = HttpClient.newHttpClient();
            // 用 HttpResponse<String> 去接client.send(req, HttpResponse.BodyHandlers.ofString())| client.send 中定義回傳的資訊是字串
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            // 因為回傳的資料是json所以要用JsonObject去接 ｜gson.fromJson(res.body(), JsonObject.class)去取的回傳的內容
            JsonObject jsonResponse = gson.fromJson(res.body(), JsonObject.class);
            //這裡是操作JsonObject去取得gpt回傳的資料
            String translations = jsonResponse.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
            System.out.println(translations);
            System.out.println("===============================");
            System.out.println(jsonResponse.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
