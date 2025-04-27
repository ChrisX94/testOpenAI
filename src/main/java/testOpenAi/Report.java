package testOpenAi;

public class Report {
    private String title;
    private int score;
    private String description;

    // 建構子
    public Report(String title, int score, String description) {
        this.title = title;
        this.score = score;
        this.description = description;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
