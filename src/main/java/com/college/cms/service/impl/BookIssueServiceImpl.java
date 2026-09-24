package com.college.cms.service.impl;

import com.college.cms.entity.BookIssue;
import com.college.cms.entity.Library;
import com.college.cms.repository.BookIssueRepository;
import com.college.cms.repository.LibraryRepository;
import com.college.cms.service.BookIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookIssueServiceImpl implements BookIssueService {

    @Autowired
    private BookIssueRepository repository;

    @Autowired
    private LibraryRepository libraryRepository;

    @Override
    public BookIssue saveBookIssue(BookIssue bookIssue) {
        if (bookIssue.getBookId() != null) {
            Library book = libraryRepository.findById(bookIssue.getBookId())
                    .orElseThrow(() -> new RuntimeException("Book not found in library with ID: " + bookIssue.getBookId()));

            int available = (book.getTotalbook() != null) ? book.getTotalbook() : 0;
            if (available <= 0) {
                throw new RuntimeException("Book '" + (book.getBookname() != null ? book.getBookname() : "Selected Book") + "' is Out of Stock (Available: 0). Cannot issue!");
            }

            // Deduct available book quantity by 1
            book.setTotalbook(available - 1);
            libraryRepository.save(book);
        }

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
    public List<BookIssue> getBookIssuesByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public BookIssue updateBookIssue(Long issueId, BookIssue bookIssue) {

        BookIssue existing = repository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Book Issue Not Found"));

        // If bookId is being changed, restore quantity of old book (+1) and deduct from new book (-1)
        if (existing.getBookId() != null && !existing.getBookId().equals(bookIssue.getBookId())) {
            libraryRepository.findById(existing.getBookId()).ifPresent(oldBook -> {
                int oldTotal = (oldBook.getTotalbook() != null) ? oldBook.getTotalbook() : 0;
                oldBook.setTotalbook(oldTotal + 1);
                libraryRepository.save(oldBook);
            });

            if (bookIssue.getBookId() != null) {
                Library newBook = libraryRepository.findById(bookIssue.getBookId())
                        .orElseThrow(() -> new RuntimeException("New book not found with ID: " + bookIssue.getBookId()));
                int newTotal = (newBook.getTotalbook() != null) ? newBook.getTotalbook() : 0;
                if (newTotal <= 0) {
                    throw new RuntimeException("Book '" + newBook.getBookname() + "' is Out of Stock!");
                }
                newBook.setTotalbook(newTotal - 1);
                libraryRepository.save(newBook);
            }
        }

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

        // When a book issue record is deleted / returned, restore book quantity (+1)
        if (existing.getBookId() != null) {
            libraryRepository.findById(existing.getBookId()).ifPresent(book -> {
                int current = (book.getTotalbook() != null) ? book.getTotalbook() : 0;
                book.setTotalbook(current + 1);
                libraryRepository.save(book);
            });
        }

        repository.delete(existing);
    }
}