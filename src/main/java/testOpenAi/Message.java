package testOpenAi;

public class Message {
    // 與Open AI 之間的傳輸用json 用這個class來做為的物件

    private String role;
    private String content;

    // 建構子
    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
