/*
ReportService.java

FIX SMELL #2 — LARGE CLASS
Semua fungsi laporan dipisahkan dari LibrarySystem ke class ini.
 
Tanggung jawab class ini:
- Menghasilkan laporan inventaris buku
- Menghasilkan laporan data anggota
- Menghasilkan laporan statistik peminjaman
*/
public class ReportService {

    private BookService      bookService;
    private MemberService    memberService;
    private BorrowingService borrowingService;
    private FineCalculator   fineCalculator;

    public ReportService(BookService bookService,
                         MemberService memberService,
                         BorrowingService borrowingService,
                         FineCalculator fineCalculator) {
        this.bookService      = bookService;
        this.memberService    = memberService;
        this.borrowingService = borrowingService;
        this.fineCalculator   = fineCalculator;
    }

    /* Laporan inventaris buku: total judul, total eksemplar, tersedia, dipinjam. */
    public void generateBookReport() {
        System.out.println("===== BOOK INVENTORY REPORT =====");
        System.out.println("Total titles : " + bookService.getAllBooks().size());

        int totalCopies = 0, availCopies = 0;
        for (Book b : bookService.getAllBooks()) {
            totalCopies += b.getTotalCopies();
            availCopies += b.getAvailableCopies();
        }
        System.out.println("Total copies     : " + totalCopies);
        System.out.println("Available copies : " + availCopies);
        System.out.println("Borrowed copies  : " + (totalCopies - availCopies));
    }

    /* Laporan anggota: total anggota dan jumlah pinjaman aktif per anggota. */
    public void generateMemberReport() {
        System.out.println("===== MEMBER REPORT =====");
        System.out.println("Total members : " + memberService.getAllMembers().size());
        for (Member m : memberService.getAllMembers()) {
            System.out.println("  " + m.getName() + " — active borrows: " + m.getBorrowCount());
        }
    }

    /* Laporan peminjaman: total, aktif, dikembalikan, dan overdue. */
    public void generateBorrowReport() {
        System.out.println("===== BORROW REPORT =====");
        int total = 0, active = 0, returned = 0, overdue = 0;

        for (BorrowRecord r : borrowingService.getAllBorrows()) {
            total++;
            if (r.isReturned()) {
                returned++;
            } else {
                active++;
                // FIX #5: kalkulasi denda menggunakan FineCalculator dengan konstanta
                double fine = fineCalculator.calculate(r.getDueDate(), "2026-05-12");
                if (fine > 0) overdue++;
            }
        }
        System.out.println("Total borrows : " + total);
        System.out.println("Active        : " + active);
        System.out.println("Returned      : " + returned);
        System.out.println("Overdue       : " + overdue);
    }
}
