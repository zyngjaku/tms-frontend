package io.github.zyngjaku.tmsfrontend.entity;

import android.graphics.Bitmap;
import com.google.gson.Gson;

public class User {
    public enum Role {
        OWNER, FORWARDER, DRIVER
    }

    private Long id;
    private String mail;
    private String password;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private Bitmap avatarBitmap;
    private Localization localization;
    private Company company;
    private Role role;

    public User() {
    }

    public User(String firstName, String lastName, Bitmap avatarBitmap) {
        setFirstName(firstName);
        setLastName(lastName);
        setAvatarBitmap(avatarBitmap);
    }

    public User(String mail, String password, String firstName, String lastName, Localization localization, String avatarUrl, Company company, Role role) {
        setMail(mail);
        setPassword(password);
        setFirstName(firstName);
        setLastName(lastName);
        setLocalization(localization);
        setAvatarUrl(avatarUrl);
        setCompany(company);
        setRole(role);
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getMail() {
        return mail;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Company getCompany() {
        return company;
    }
    public void setCompany(Company company) {
         this.company = company;
    }
    public void setCompany(String company) {
        this.company = new Gson().fromJson(company, Company.class);
    }

    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }

    public Localization getLocalization() {
        return localization;
    }
    public void setLocalization(Localization localization) {
        this.localization = localization;
    }
    public void setLocalization(String localization) {
        this.localization = new Gson().fromJson(localization, Localization.class);
    }

    public Bitmap getAvatarBitmap() {
        return avatarBitmap;
    }
    public void setAvatarBitmap(Bitmap avatarBitmap) {
        this.avatarBitmap = avatarBitmap;
    }
}
