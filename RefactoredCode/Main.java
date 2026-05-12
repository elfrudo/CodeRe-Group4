/*
Main.java

Entry point program Library Management System (Refactored).

FIX SMELL #2 — LARGE CLASS
Class ini hanya bertugas sebagai "koordinator":
merakit semua service dan menjalankan demo.
Tidak ada lagi satu larga class yang melakukan segalanya.

 */
public class Main {

    public static void main(String[] args) {

        // Rakit semua service
        BookService         bookService   = new BookService();
        MemberService       memberService = new MemberService();
        FineCalculator      fineCalc      = new FineCalculator();
        NotificationService notifService  = new NotificationService();

        BorrowingService borrowService = new BorrowingService(
            bookService, memberService, notifService, fineCalc
        );
        ReportService reportService = new ReportService(
            bookService, memberService, borrowService, fineCalc
        );

        // FIX #3: addBook() menerima 1 objek Book, bukan 7 parameter
        bookService.addBook(new Book("B001", "Clean Code",
            "Robert C. Martin", "Technology", "Prentice Hall", 2008, 5));
        bookService.addBook(new Book("B002", "The Pragmatic Programmer",
            "Andrew Hunt", "Technology", "Addison-Wesley", 1999, 3));
        bookService.addBook(new Book("B003", "Refactoring",
            "Martin Fowler", "Technology", "Addison-Wesley", 2018, 4));
        bookService.addBook(new Book("B004", "Design Patterns",
            "Gang of Four", "Technology", "Addison-Wesley", 1994, 2));

        // FIX #6: Address sebagai objek (bukan 4 parameter terpisah)
        // FIX #3: addMember() menerima 1 objek Member
        memberService.addMember(new Member(
            "M001", "Budi Santoso", "budi@email.com", "08111111111",
            new Address("Jl. Sudirman No. 1", "Jakarta", "DKI Jakarta", "10220")
        ));
        memberService.addMember(new Member(
            "M002", "Sari Dewi", "sari@email.com", "08222222222",
            new Address("Jl. Gatot Subroto No. 5", "Bandung", "Jawa Barat", "40161")
        ));
        memberService.addMember(new Member(
            "M003", "Andi Wijaya", "andi@email.com", "08333333333",
            new Address("Jl. Malioboro No. 10", "Yogyakarta", "DIY", "55271")
        ));

        System.out.println("\n========== BORROWING ==========");
        borrowService.processBookBorrowing(
            "BW001", "M001", "B001", "2026-04-01", "2026-04-15");
        borrowService.processBookBorrowing(
            "BW002", "M002", "B002", "2026-04-05", "2026-04-19");

        System.out.println("\n========== RETURN ==========");
        borrowService.processReturn("BW001", "2026-05-01");

        System.out.println("\n========== OVERDUE REMINDERS ==========");
        borrowService.sendOverdueReminders("2026-05-12");

        System.out.println("\n========== REPORTS ==========");
        reportService.generateBookReport();
        reportService.generateMemberReport();
        reportService.generateBorrowReport();


        System.out.println("\n========== ALL BOOKS ==========");
        bookService.displayAll();

        System.out.println("\n========== ALL MEMBERS ==========");
        memberService.displayAll();
    }
}
