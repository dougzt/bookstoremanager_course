package com.rodrigopeleias.bookstoremanager.books.repository;

import com.rodrigopeleias.bookstoremanager.books.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BooksRepository extends JpaRepository<Book, Long> {
}
