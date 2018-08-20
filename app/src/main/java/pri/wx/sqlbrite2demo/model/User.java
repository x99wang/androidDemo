package pri.wx.sqlbrite2demo.model;

public class User {

    private String id;
    private String username;
    private String description;

    public User(String id, String username, String description) {
        this.id = id;
        this.username = username;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "\nId=" + id +
                ", Name=" + username +
                ", Description=" + description +
                "\n";
    }

}
