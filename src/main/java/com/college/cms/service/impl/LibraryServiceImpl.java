package com.college.cms.service.impl;

import com.college.cms.entity.Library;
import com.college.cms.repository.LibraryRepository;
import com.college.cms.service.LibraryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibraryServiceImpl implements LibraryService {

    @Autowired
    private LibraryRepository libraryRepository;

    @Override
    public Library saveLibrary(Library library) {
        return libraryRepository.save(library);
    }

    @Override
    public List<Library> getAllLibrary() {
        return libraryRepository.findAll();
    }

    @Override
    public Optional<Library> getLibraryById(Long bookid) {
        return libraryRepository.findById(bookid);
    }

    @Override
    public Library updateLibrary(Long bookid, Library library) {

        Library existing = libraryRepository.findById(bookid)
                .orElseThrow(() -> new RuntimeException("Book Not Found"));

        existing.setBookname(library.getBookname());
        existing.setAuthorname(library.getAuthorname());
        existing.setBooklanguage(library.getBooklanguage());
        existing.setTotalbook(library.getTotalbook());
        existing.setBookprice(library.getBookprice());

        return libraryRepository.save(existing);
    }

    @Override
    public void deleteLibrary(Long bookid) {

        Library existing = libraryRepository.findById(bookid)
                .orElseThrow(() -> new RuntimeException("Book Not Found"));

        libraryRepository.delete(existing);
    }
}