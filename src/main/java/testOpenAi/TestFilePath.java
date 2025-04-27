package testOpenAi;

import java.io.File;

public class TestFilePath {
    public static void main(String[] args) {
            String filePath = "./sourceFile/nvda1.csv";
//        String filePath = "./sourceFile/intc.csv";
        File file = new File(filePath);
        System.out.println(file.exists());
    }
}
