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


public class App4 {
    private static final String KEY = new Key().getKey();
    private static final String OPEN_AI_URL = "https://api.openai.com/v1/chat/completions";
    public static String filePath = "./sourceFile/nvda.csv";

    public static void main(String[] args){
//        File file = new File(filePath);
//        System.out.println(file.exists());

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
            messages.add(new Message("system", "你是一位專業的投資理財顧問，請幫忙依據傳送的資料，分析出重要的資訊"));
            messages.add(new Message("user", "請幫忙從財報資料中分析出這間公司的營收狀況，幫助我判斷是否能投資其股票: " + data));
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
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
