package com.college.cms.service;

import com.college.cms.entity.BookIssue;

import java.util.List;
import java.util.Optional;

public interface BookIssueService {

    BookIssue saveBookIssue(BookIssue bookIssue);

    List<BookIssue> getAllBookIssues();

    Optional<BookIssue> getBookIssueById(Long issueId);

    BookIssue updateBookIssue(Long issueId, BookIssue bookIssue);

    void deleteBookIssue(Long issueId);

}