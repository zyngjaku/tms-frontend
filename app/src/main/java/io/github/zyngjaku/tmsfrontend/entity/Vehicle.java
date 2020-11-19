package io.github.zyngjaku.tmsfrontend.entity;

import com.google.gson.annotations.Expose;
import java.time.LocalDate;

public class Vehicle {
    private Long id;
    private String name;
    private String registration;
    private LocalDate reviewDate;
    private Company company;

    public Vehicle() {
    }

    public Vehicle(String name, String registration, Company company) {
        this.name = name;
        this.registration = registration;
        this.company = company;
    }

    public Vehicle(String name, String registration, LocalDate reviewDate, Company company) {
        this(name, registration, company);
        this.reviewDate = reviewDate;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getRegistration() {
        return registration;
    }
    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }
    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Company getCompany() {
        return company;
    }
    public void setCompany(Company company) {
        this.company = company;
    }
}
