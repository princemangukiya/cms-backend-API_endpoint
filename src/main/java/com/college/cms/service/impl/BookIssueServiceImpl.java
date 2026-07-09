package com.college.cms.service.impl;

import com.college.cms.entity.BookIssue;
import com.college.cms.repository.BookIssueRepository;
import com.college.cms.service.BookIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookIssueServiceImpl implements BookIssueService {

    @Autowired
    private BookIssueRepository repository;

    @Override
    public BookIssue saveBookIssue(BookIssue bookIssue) {
        return repository.save(bookIssue);
    }

    @Override
    public List<BookIssue> getAllBookIssues() {
        return repository.findAll();
    }

    @Override
    public Optional<BookIssue> getBookIssueById(Long issueId) {
        return repository.findById(issueId);
    }

    @Override
    public BookIssue updateBookIssue(Long issueId, BookIssue bookIssue) {

        BookIssue existing = repository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Book Issue Not Found"));

        existing.setBookId(bookIssue.getBookId());
        existing.setUserId(bookIssue.getUserId());
        existing.setIssueDate(bookIssue.getIssueDate());
        existing.setFine(bookIssue.getFine());
        existing.setReason(bookIssue.getReason());

        return repository.save(existing);
    }

    @Override
    public void deleteBookIssue(Long issueId) {

        BookIssue existing = repository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Book Issue Not Found"));

        repository.delete(existing);
    }
}