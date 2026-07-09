package com.college.cms.service;

import com.college.cms.entity.Library;

import java.util.List;
import java.util.Optional;

public interface LibraryService {

    Library saveLibrary(Library library);

    List<Library> getAllLibrary();

    Optional<Library> getLibraryById(Long bookid);

    Library updateLibrary(Long bookid, Library library);

    void deleteLibrary(Long bookid);

}