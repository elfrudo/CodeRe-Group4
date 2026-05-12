/*
Book.java

FIX SMELL #3 — LONG PARAMETER LIST
Sebelumnya addBook() menerima 7 parameter terpisah:
addBook(bookId, title, author, genre, publisher, year, totalCopies)

Sekarang semua data buku dibungkus dalam class Book.
Cukup buat objek Book, lalu kirim 1 objek ke BookService.addBook(book).

Keuntungan:
- Method signature lebih bersih dan tidak mudah tertukar urutan parameter
- Data buku dan perilakunya (decreaseAvailable, increaseAvailable) ada di satu tempat

FIX SMELL #2 — LARGE CLASS (bagian dari pemecahan LibrarySystem)
*/
public class Book {
    private String bookId;
    private String title;
    private String author;
    private String genre;
    private String publisher;
    private int    year;
    private int    totalCopies;
    private int    availableCopies;

    public Book(String bookId, String title, String author,
                String genre, String publisher, int year, int totalCopies) {
        this.bookId          = bookId;
        this.title           = title;
        this.author          = author;
        this.genre           = (genre == null || genre.isEmpty()) ? "General" : genre;
        this.publisher       = publisher;
        this.year            = year;
        this.totalCopies     = totalCopies;
        this.availableCopies = totalCopies;
    }


    public String getBookId()          { return bookId; }
    public String getTitle()           { return title; }
    public String getAuthor()          { return author; }
    public String getGenre()           { return genre; }
    public String getPublisher()       { return publisher; }
    public int    getYear()            { return year; }
    public int    getTotalCopies()     { return totalCopies; }
    public int    getAvailableCopies() { return availableCopies; }


    public void decreaseAvailable() {
        if (availableCopies > 0) availableCopies--;
    }

    public void increaseAvailable() {
        availableCopies++;
    }

    @Override
    public String toString() {
        return "ID: "       + bookId
             + " | Title: "  + title
             + " | Author: " + author
             + " | Genre: "  + genre
             + " | Avail: "  + availableCopies + "/" + totalCopies;
    }
}
