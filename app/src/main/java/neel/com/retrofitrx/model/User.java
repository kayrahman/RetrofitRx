package neel.com.retrofitrx.model;

public class User {

   private String username;
    private String email;
    private String gender;
    private String password;
    private String account;
    private String full_name;
    private String birthday;
    private String[] categories;
    private String token;
    private String message;

    public User(String username, String email, String gender, String password,
                String account, String full_name, String birthday, String[] categories) {
        this.username = username;
        this.email = email;
        this.gender = gender;
        this.password = password;
        this.account = account;
        this.full_name = full_name;
        this.birthday = birthday;
        this.categories = categories;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }


    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getPassword() {
        return password;
    }

    public String getAccount() {
        return account;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getBirthday() {
        return birthday;
    }

    public String[] getCategories() {
        return categories;
    }
}
