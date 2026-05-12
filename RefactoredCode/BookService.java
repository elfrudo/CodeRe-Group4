import java.util.ArrayList;
import java.util.List;

/*
BookService.java

FIX SMELL #2 — LARGE CLASS
Semua operasi yang berhubungan dengan buku dipindahkan dari
LibrarySystem ke class ini.

FIX SMELL #3 — LONG PARAMETER LIST
addBook() sekarang menerima 1 objek Book,
bukan 7 parameter terpisah.

Tanggung jawab class ini:
- Menyimpan daftar buku (in-memory)
- Menambah, menghapus, mencari, dan menampilkan buku
*/
public class BookService {
    private List<Book> books = new ArrayList<>();

    /*
    Menambahkan buku baru ke koleksi.
     
    FIX #3: menerima objek Book, bukan 7 parameter.
    Sebelum: addBook(bookId, title, author, genre, publisher, year, totalCopies)
    Sesudah: addBook(book)
    */
    public void addBook(Book book) {
        if (book.getBookId() == null || book.getBookId().isEmpty()) {
            System.out.println("Error: Book ID cannot be empty.");
            return;
        }
        if (findById(book.getBookId()) != null) {
            System.out.println("Error: Book ID already exists.");
            return;
        }
        books.add(book);
        System.out.println("Book added: " + book.getTitle());
    }

    /* Menghapus buku berdasarkan ID. */
    public void removeBook(String bookId) {
        books.removeIf(b -> b.getBookId().equals(bookId));
        System.out.println("Book removed: " + bookId);
    }

    /* Mencari buku berdasarkan ID. Mengembalikan null jika tidak ditemukan. */
    public Book findById(String bookId) {
        for (Book b : books) {
            if (b.getBookId().equals(bookId)) return b;
        }
        return null;
    }

    /* Mencari semua buku berdasarkan nama penulis (case-insensitive). */
    public List<Book> findByAuthor(String author) {
        List<Book> result = new ArrayList<>();
        for (Book b : books) {
            if (b.getAuthor().equalsIgnoreCase(author)) result.add(b);
        }
        return result;
    }

    /* Mencari semua buku berdasarkan genre (case-insensitive). */
    public List<Book> findByGenre(String genre) {
        List<Book> result = new ArrayList<>();
        for (Book b : books) {
            if (b.getGenre().equalsIgnoreCase(genre)) result.add(b);
        }
        return result;
    }

    /* Mengembalikan seluruh daftar buku. */
    public List<Book> getAllBooks() { return books; }

    /* Menampilkan semua buku ke console. */
    public void displayAll() {
        System.out.println("===== ALL BOOKS =====");
        for (Book b : books) {
            System.out.println(b);
        }
    }
}
