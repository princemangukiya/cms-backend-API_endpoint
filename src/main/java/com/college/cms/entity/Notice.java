package com.college.cms.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "notice_detail")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    @JsonProperty("notice_id")
    @JsonAlias({"noticeId", "notice_id", "id"})
    private Integer noticeId;

    @Column(name = "title", nullable = false)
    @JsonProperty("title")
    @JsonAlias({"title"})
    private String title;

    @Column(name = "category", nullable = false)
    @JsonProperty("category")
    @JsonAlias({"category"})
    private String category;

    @Column(name = "audience", nullable = false)
    @JsonProperty("audience")
    @JsonAlias({"audience"})
    private String audience;

    @Column(name = "author")
    @JsonProperty("author")
    @JsonAlias({"author"})
    private String author;

    @Column(name = "created_date")
    @JsonProperty("created_date")
    @JsonAlias({"createdDate", "created_date", "date"})
    private String createdDate;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    @JsonProperty("message")
    @JsonAlias({"message", "description"})
    private String message;

    @Column(name = "priority")
    @JsonProperty("priority")
    @JsonAlias({"priority"})
    private String priority = "medium";

    public Notice() {
    }

    public Notice(Integer noticeId, String title, String category, String audience, String author, String createdDate, String message, String priority) {
        this.noticeId = noticeId;
        this.title = title;
        this.category = category;
        this.audience = audience;
        this.author = author;
        this.createdDate = createdDate;
        this.message = message;
        this.priority = priority;
    }

    // Frontend compatibility helpers
    @JsonProperty("id")
    public Integer getId() {
        return noticeId;
    }

    public void setId(Integer id) {
        if (this.noticeId == null) {
            this.noticeId = id;
        }
    }

    @JsonProperty("date")
    public String getDate() {
        return createdDate;
    }

    public void setDate(String date) {
        this.createdDate = date;
    }

    public Integer getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Integer noticeId) {
        this.noticeId = noticeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
