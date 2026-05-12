import java.util.ArrayList;
import java.util.List;

/*
BorrowingService.java

FIX SMELL #2 — LARGE CLASS
Logika peminjaman dan pengembalian dipisahkan dari LibrarySystem
ke class ini.

FIX SMELL #1 — LONG METHOD
processBookBorrowing() sebelumnya ~70 baris dan melakukan segalanya.
Sekarang dipecah menjadi 3 method kecil yang masing-masing
punya satu tanggung jawab:
- validateBorrowInput()  → validasi parameter dasar
- validateBorrowRules()  → validasi aturan bisnis
- createBorrowRecord()   → buat record dan kirim notifikasi

FIX SMELL #4 — DUPLICATE CODE (via NotificationService)
Tidak ada lagi blok email copy-paste; cukup panggil notificationService.

FIX SMELL #5 — MAGIC NUMBERS (via LibraryConstants)
Angka 3 diganti dengan LibraryConstants.MAX_BORROW_LIMIT.

Tanggung jawab class ini:
- Memproses peminjaman buku baru
- Memproses pengembalian buku
- Mengirim pengingat keterlambatan
*/
public class BorrowingService {

    private List<BorrowRecord>  borrows;
    private BookService         bookService;
    private MemberService       memberService;
    private NotificationService notificationService;
    private FineCalculator      fineCalculator;

    public BorrowingService(BookService bookService,
                            MemberService memberService,
                            NotificationService notificationService,
                            FineCalculator fineCalculator) {
        this.borrows             = new ArrayList<>();
        this.bookService         = bookService;
        this.memberService       = memberService;
        this.notificationService = notificationService;
        this.fineCalculator      = fineCalculator;
    }

    /* 
    FIX #1 — LONG METHOD
    Sebelum: satu method 70+ baris yang melakukan segalanya.
    Sesudah: entry point pendek yang mendelegasikan ke method-method kecil.
    */ 

    /*
    Memproses permintaan peminjaman buku.
    Dipecah menjadi 3 langkah yang masing-masing punya fokus sendiri.
    */
    public void processBookBorrowing(String borrowId, String memberId,
                                     String bookId, String borrowDate,
                                     String dueDate) {
        // Langkah 1: validasi parameter input
        if (!validateBorrowInput(borrowId, memberId, bookId, borrowDate, dueDate)) return;

        // Langkah 2: validasi aturan bisnis (cek member, buku, dan limit)
        Member member = memberService.findById(memberId);
        Book   book   = bookService.findById(bookId);
        if (!validateBorrowRules(member, book)) return;

        // Langkah 3: buat record peminjaman dan kirim notifikasi
        createBorrowRecord(borrowId, member, book, borrowDate, dueDate);
    }

    /*
    FIX #1: Memvalidasi parameter input dasar.
    Satu tanggung jawab: pastikan tidak ada parameter yang kosong atau duplikat.
    */
    private boolean validateBorrowInput(String borrowId, String memberId,
                                        String bookId, String borrowDate,
                                        String dueDate) {
        if (borrowId   == null || borrowId.isEmpty())   { System.out.println("Error: Borrow ID required.");   return false; }
        if (memberId   == null || memberId.isEmpty())   { System.out.println("Error: Member ID required.");   return false; }
        if (bookId     == null || bookId.isEmpty())     { System.out.println("Error: Book ID required.");     return false; }
        if (borrowDate == null || borrowDate.isEmpty()) { System.out.println("Error: Borrow date required."); return false; }
        if (dueDate    == null || dueDate.isEmpty())    { System.out.println("Error: Due date required.");    return false; }
        if (findBorrowById(borrowId) != null)           { System.out.println("Error: Borrow ID exists.");     return false; }
        return true;
    }

    /*
    FIX #1: Memvalidasi aturan bisnis.
    Satu tanggung jawab: pastikan member ada, batas pinjam belum tercapai,
    buku ada, dan masih tersedia.
    */
    private boolean validateBorrowRules(Member member, Book book) {
        if (member == null) {
            System.out.println("Error: Member not found.");
            return false;
        }
        // FIX #5: pakai konstanta MAX_BORROW_LIMIT, bukan angka 3
        if (member.getBorrowCount() >= LibraryConstants.MAX_BORROW_LIMIT) {
            System.out.println("Error: Member reached max borrow limit ("
                + LibraryConstants.MAX_BORROW_LIMIT + " books).");
            return false;
        }
        if (book == null) {
            System.out.println("Error: Book not found.");
            return false;
        }
        if (book.getAvailableCopies() <= 0) {
            System.out.println("Error: No available copies for: " + book.getTitle());
            return false;
        }
        return true;
    }

    /*
    FIX #1: Membuat record peminjaman dan mengirim notifikasi.
    Satu tanggung jawab: update data dan beritahu member via email.
    */
    private void createBorrowRecord(String borrowId, Member member,
                                    Book book, String borrowDate, String dueDate) {
        book.decreaseAvailable();
        member.incrementBorrowCount();
        borrows.add(new BorrowRecord(borrowId, member.getMemberId(),
                                     book.getBookId(), borrowDate, dueDate));
        System.out.println("Book borrowed: " + book.getTitle() + " by " + member.getName());

        // FIX #4: panggil NotificationService — tidak ada lagi copy-paste email
        notificationService.sendBorrowConfirmation(member, book, dueDate);
    }

    // ─────────────────────────────────────────────────────────────────────
    // PENGEMBALIAN BUKU
    // ─────────────────────────────────────────────────────────────────────

    /*
    Memproses pengembalian buku.
    Mengembalikan jumlah denda (0 jika tidak terlambat).
    */
    public double processReturn(String borrowId, String actualReturnDate) {
        BorrowRecord record = findBorrowById(borrowId);

        if (record == null) {
            System.out.println("Error: Borrow record not found.");
            return 0;
        }
        if (record.isReturned()) {
            System.out.println("Error: Book already returned.");
            return 0;
        }

        record.markReturned(actualReturnDate);

        // Kembalikan stok buku dan kurangi jumlah pinjam anggota
        Book   book   = bookService.findById(record.getBookId());
        Member member = memberService.findById(record.getMemberId());
        if (book   != null) book.increaseAvailable();
        if (member != null) member.decrementBorrowCount();

        // FIX #5: kalkulasi denda menggunakan konstanta via FineCalculator
        double fine = fineCalculator.calculate(record.getDueDate(), actualReturnDate);
        if (fine > 0) System.out.println("Late return! Fine: Rp " + fine);

        // FIX #4: kirim email via NotificationService (bukan copy-paste)
        if (member != null && book != null) {
            notificationService.sendReturnConfirmation(member, book, actualReturnDate, fine);
        }
        return fine;
    }

    // ─────────────────────────────────────────────────────────────────────
    // PENGINGAT KETERLAMBATAN
    // ─────────────────────────────────────────────────────────────────────

    /*
    Mengecek semua peminjaman aktif dan mengirim notifikasi
    kepada anggota yang terlambat mengembalikan buku.
    */
    public void sendOverdueReminders(String todayDate) {
        System.out.println("Checking overdue borrows for: " + todayDate);
        for (BorrowRecord record : borrows) {
            if (!record.isBorrowed()) continue;

            double fine = fineCalculator.calculate(record.getDueDate(), todayDate);
            if (fine <= 0) continue;

            Member member = memberService.findById(record.getMemberId());
            Book   book   = bookService.findById(record.getBookId());
            if (member == null || book == null) continue;

            // FIX #4: panggil NotificationService — tidak ada lagi copy-paste email
            notificationService.sendOverdueNotice(member, book, record.getDueDate(), fine);
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // HELPER
    // ─────────────────────────────────────────────────────────────────────

    /* Mencari record peminjaman berdasarkan ID. */
    public BorrowRecord findBorrowById(String borrowId) {
        for (BorrowRecord r : borrows) {
            if (r.getBorrowId().equals(borrowId)) return r;
        }
        return null;
    }

    /* Mengembalikan seluruh daftar peminjaman. */
    public List<BorrowRecord> getAllBorrows() { return borrows; }
}
