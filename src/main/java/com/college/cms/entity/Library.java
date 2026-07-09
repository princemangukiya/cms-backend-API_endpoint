package com.college.cms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "library_detail")
public class Library {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookid")
    private Long bookid;

    @Column(name = "bookname")
    private String bookname;

    @Column(name = "authorname")
    private String authorname;

    @Column(name = "booklanguage")
    private String booklanguage;

    @Column(name = "totalbook")
    private Integer totalbook;

    @Column(name = "bookprice")
    private Double bookprice;

    public Library() {
    }

    public Long getBookid() {
        return bookid;
    }

    public void setBookid(Long bookid) {
        this.bookid = bookid;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getAuthorname() {
        return authorname;
    }

    public void setAuthorname(String authorname) {
        this.authorname = authorname;
    }

    public String getBooklanguage() {
        return booklanguage;
    }

    public void setBooklanguage(String booklanguage) {
        this.booklanguage = booklanguage;
    }

    public Integer getTotalbook() {
        return totalbook;
    }

    public void setTotalbook(Integer totalbook) {
        this.totalbook = totalbook;
    }

    public Double getBookprice() {
        return bookprice;
    }

    public void setBookprice(Double bookprice) {
        this.bookprice = bookprice;
    }
}