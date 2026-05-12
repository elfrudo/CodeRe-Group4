import java.util.ArrayList;
import java.util.List;

/*

Code Smells pada file ini:

1. LONG METHOD         → processBookBorrowing()
2. LARGE CLASS         → LibrarySystem class does everything
3. LONG PARAMETER LIST → addBook(...) membutuhkan 7+ parameter
4. DUPLICATE CODE      → sendEmailNotification() diulang
5. MAGIC NUMBERS       → Terdapat beberapa hard-coded numbers
6. DATA CLUMPS         → field-field address selalu digunakan bersama

 */


/* CODE SMELL #2 — LARGE CLASS
Class LibrarySystem ini menangani buku, anggota, logika peminjaman,
notifikasi email, perhitungan denda, laporan, dan lainnya.
Kelas ini seharusnya dipisahkan menjadi: BookService, MemberService,
BorrowingService, NotificationService, ReportService, dan sebagainya.
*/
public class LibrarySystem {

    /* CODE SMELL #5 — MAGIC NUMBERS
    Angka-angka mentah ini (14, 5000, 0.05, 3) muncul di seluruh
    kode tanpa penjelasan. Angka tersebut seharusnya dijadikan konstanta
    bernama seperti:
    MAX_BORROW_DAYS, MAX_FINE_IDR, DAILY_FINE_RATE, MAX_BOOKS.
    */
    private List<String[]> books    = new ArrayList<>();
    private List<String[]> members  = new ArrayList<>();
    private List<String[]> borrows  = new ArrayList<>();

    // ─────────────────────────────────────────────────────────────
    // BOOK MANAGEMENT
    // ─────────────────────────────────────────────────────────────

    /*CODE SMELL #3 — LONG PARAMETER LIST
    Method ini menerima 7 parameter terpisah. Sebaiknya buat
    objek/kelas Book dan kirimkan satu objek tersebut saja.
    */
    public void addBook(String bookId,
                        String title,
                        String author,
                        String genre,
                        String publisher,
                        int year,
                        int totalCopies) {
        // Validate book ID
        if (bookId == null || bookId.isEmpty()) {
            System.out.println("Error: Book ID cannot be empty.");
            return;
        }
        // Check for duplicate
        for (String[] b : books) {
            if (b[0].equals(bookId)) {
                System.out.println("Error: Book ID already exists.");
                return;
            }
        }
        // genre defaults
        if (genre == null || genre.isEmpty()) {
            genre = "General";
        }
        String[] book = {
            bookId, title, author, genre,
            publisher, String.valueOf(year),
            String.valueOf(totalCopies),
            String.valueOf(totalCopies)   // availableCopies
        };
        books.add(book);
        System.out.println("Book added: " + title);
    }

    public void removeBook(String bookId) {
        books.removeIf(b -> b[0].equals(bookId));
        System.out.println("Book removed: " + bookId);
    }

    public void displayAllBooks() {
        System.out.println("===== ALL BOOKS =====");
        for (String[] b : books) {
            System.out.println(
                "ID: "        + b[0] +
                " | Title: "  + b[1] +
                " | Author: " + b[2] +
                " | Genre: "  + b[3] +
                " | Avail: "  + b[7] + "/" + b[6]
            );
        }
    }

    public String[] findBookById(String bookId) {
        for (String[] b : books) {
            if (b[0].equals(bookId)) return b;
        }
        return null;
    }

    public List<String[]> searchBooksByAuthor(String author) {
        List<String[]> result = new ArrayList<>();
        for (String[] b : books) {
            if (b[2].equalsIgnoreCase(author)) result.add(b);
        }
        return result;
    }

    public List<String[]> searchBooksByGenre(String genre) {
        List<String[]> result = new ArrayList<>();
        for (String[] b : books) {
            if (b[3].equalsIgnoreCase(genre)) result.add(b);
        }
        return result;
    }

    // ─────────────────────────────────────────────────────────────
    // MEMBER MANAGEMENT
    // ─────────────────────────────────────────────────────────────

    /*CODE SMELL #6 — DATA CLUMPS

    street, city, province, dan postalCode selalu digunakan bersama.
    Field-field tersebut seharusnya dikelompokkan ke dalam
    kelas/objek Address.
    */
    public void addMember(String memberId,
                          String name,
                          String email,
                          String phone,
                          String street,
                          String city,
                          String province,
                          String postalCode) {
        if (memberId == null || memberId.isEmpty()) {
            System.out.println("Error: Member ID cannot be empty.");
            return;
        }
        for (String[] m : members) {
            if (m[0].equals(memberId)) {
                System.out.println("Error: Member ID already exists.");
                return;
            }
        }

        String[] member = {
            memberId, name, email, phone,
            street, city, province, postalCode,
            "0"   // borrowCount
        };
        members.add(member);
        System.out.println("Member registered: " + name);
    }

    public void removeMember(String memberId) {
        members.removeIf(m -> m[0].equals(memberId));
        System.out.println("Member removed: " + memberId);
    }

    public String[] findMemberById(String memberId) {
        for (String[] m : members) {
            if (m[0].equals(memberId)) return m;
        }
        return null;
    }

    public void displayAllMembers() {
        System.out.println("===== ALL MEMBERS =====");
        for (String[] m : members) {
            // CODE SMELL #6 (lanjutan) — address diatur secara manual
            System.out.println(
                "ID: "       + m[0] +
                " | Name: "  + m[1] +
                " | Email: " + m[2] +
                " | Phone: " + m[3] +
                " | Address: " + m[4] + ", " + m[5] + ", " + m[6] + " " + m[7]
            );
        }
    }

    // ─────────────────────────────────────────────────────────────
    // BORROWING MANAGEMENT
    // ─────────────────────────────────────────────────────────────

    /*
    CODE SMELL #1 — LONG METHOD
    processBookBorrowing() terlalu panjang (70+ baris). Method ini menangani
    validasi, pengecekan ketersediaan, pengecekan anggota, batas peminjaman,
    pembuatan record, DAN notifikasi — semuanya dalam satu method.
    Setiap tanggung jawab tersebut seharusnya dipisahkan ke dalam
    method atau class tersendiri.
    */
    public void processBookBorrowing(String borrowId, String memberId,
                                     String bookId, String borrowDate,
                                     String dueDate) {

        // --- Step 1: Validate inputs ---
        if (borrowId == null || borrowId.isEmpty()) {
            System.out.println("Error: Borrow ID is required.");
            return;
        }
        if (memberId == null || memberId.isEmpty()) {
            System.out.println("Error: Member ID is required.");
            return;
        }
        if (bookId == null || bookId.isEmpty()) {
            System.out.println("Error: Book ID is required.");
            return;
        }
        if (borrowDate == null || borrowDate.isEmpty()) {
            System.out.println("Error: Borrow date is required.");
            return;
        }
        if (dueDate == null || dueDate.isEmpty()) {
            System.out.println("Error: Due date is required.");
            return;
        }
        for (String[] bw : borrows) {
            if (bw[0].equals(borrowId)) {
                System.out.println("Error: Borrow ID already exists.");
                return;
            }
        }

        // --- Step 2: Find member ---
        String[] member = null;
        for (String[] m : members) {
            if (m[0].equals(memberId)) {
                member = m;
                break;
            }
        }
        if (member == null) {
            System.out.println("Error: Member not found.");
            return;
        }

        // --- Step 3: Check borrow limit ---
        int currentBorrows = Integer.parseInt(member[8]);
        // CODE SMELL #5 (lanjutan) — MAGIC NUMBER: 3
        // seharusnya: if (currentBorrows >= MAX_BORROW_LIMIT)
        if (currentBorrows >= 3) {
            System.out.println("Error: Member has reached the maximum borrow limit of 3 books.");
            return;
        }

        // --- Step 4: Find book ---
        String[] book = null;
        for (String[] b : books) {
            if (b[0].equals(bookId)) {
                book = b;
                break;
            }
        }
        if (book == null) {
            System.out.println("Error: Book not found.");
            return;
        }

        // --- Step 5: Check book availability ---
        int available = Integer.parseInt(book[7]);
        if (available <= 0) {
            System.out.println("Error: No available copies for book: " + book[1]);
            return;
        }

        // --- Step 6: Create borrow record ---
        book[7] = String.valueOf(available - 1);
        member[8] = String.valueOf(currentBorrows + 1);
        String[] borrow = {
            borrowId, memberId, bookId,
            borrowDate, dueDate,
            "", "BORROWED"   // returnDate, status
        };
        borrows.add(borrow);
        System.out.println("Book borrowed successfully: " + book[1] + " by " + member[1]);

        // --- Step 7: Send confirmation email ---
        // CODE SMELL #4 (lanjutan) — Blok email yang sama di-copy-paste berulang kali.
        String subjectBorrow = "Borrowing Confirmation - " + book[1];
        String bodyBorrow    = "Dear " + member[1] + ",\n"
            + "You have borrowed: " + book[1] + "\n"
            + "Due date: " + dueDate + "\n"
            + "Please return on time to avoid fines.\n"
            + "Thank you,\nLibrary System";
        System.out.println("--- Sending Email ---");
        System.out.println("To: "      + member[2]);
        System.out.println("Subject: " + subjectBorrow);
        System.out.println("Body:\n"   + bodyBorrow);
        System.out.println("--- Email Sent ---");
    }

    // ─────────────────────────────────────────────────────────────
    // RETURN PROCESSING
    // ─────────────────────────────────────────────────────────────

    public double processReturn(String borrowId, String actualReturnDate) {

        String[] borrow = null;
        for (String[] bw : borrows) {
            if (bw[0].equals(borrowId)) {
                borrow = bw;
                break;
            }
        }
        if (borrow == null) {
            System.out.println("Error: Borrow record not found.");
            return 0;
        }
        if ("RETURNED".equals(borrow[6])) {
            System.out.println("Error: Book already returned.");
            return 0;
        }

        // Update borrow record
        borrow[5] = actualReturnDate;
        borrow[6] = "RETURNED";

        // Restore book availability
        String[] book = findBookById(borrow[2]);
        if (book != null) {
            book[7] = String.valueOf(Integer.parseInt(book[7]) + 1);
        }

        // Update member borrow count
        String[] member = findMemberById(borrow[1]);
        if (member != null) {
            int bc = Integer.parseInt(member[8]);
            if (bc > 0) member[8] = String.valueOf(bc - 1);
        }

        // Calculate fine
        double fine = calculateFine(borrow[4], actualReturnDate);
        if (fine > 0) {
            System.out.println("Late return! Fine: Rp " + fine);
        }

        /*
        CODE SMELL #4 — DUPLICATE CODE

        Blok pengiriman email ini identik dengan yang ada di
        processBookBorrowing() di atas, dan juga dengan yang ada di
        sendOverdueReminders() di bawah. Sebaiknya ekstrak ke dalam
        helper method seperti:
        private void sendEmail(String to, String subject, String body)
        */
        if (member != null && book != null) {
            String subjectReturn = "Return Confirmation - " + book[1];
            String bodyReturn    = "Dear " + member[1] + ",\n"
                + "You have returned: " + book[1] + "\n"
                + "Return date: " + actualReturnDate + "\n"
                + (fine > 0 ? "Outstanding fine: Rp " + fine + "\n" : "No fine. Thank you!\n")
                + "Thank you,\nLibrary System";
            System.out.println("--- Sending Email ---");
            System.out.println("To: "      + member[2]);
            System.out.println("Subject: " + subjectReturn);
            System.out.println("Body:\n"   + bodyReturn);
            System.out.println("--- Email Sent ---");
        }

        return fine;
    }

    // ─────────────────────────────────────────────────────────────
    // FINE CALCULATION
    // ─────────────────────────────────────────────────────────────

    public double calculateFine(String dueDate, String returnDate) {
        // Simplified: assume dates are "YYYY-MM-DD" and use length diff
        // In real code use LocalDate; this is intentionally simplified
        int lateDays = 0;
        try {
            String[] due    = dueDate.split("-");
            String[] ret    = returnDate.split("-");
            int dueDay  = Integer.parseInt(due[2]);
            int retDay  = Integer.parseInt(ret[2]);
            int dueMon  = Integer.parseInt(due[1]);
            int retMon  = Integer.parseInt(ret[1]);
            int dueYr   = Integer.parseInt(due[0]);
            int retYr   = Integer.parseInt(ret[0]);
            int dueTot  = dueYr * 365 + dueMon * 30 + dueDay;
            int retTot  = retYr * 365 + retMon * 30 + retDay;
            lateDays = retTot - dueTot;
        } catch (Exception e) {
            System.out.println("Warning: Could not parse dates.");
        }

        if (lateDays <= 0) return 0;

        // CODE SMELL #5 (continued) — MAGIC NUMBERS: 1000 (daily fine), 50000 (max fine)
        // seharusnya: DAILY_FINE_RUPIAH = 1000, MAX_FINE_RUPIAH = 50000
        double fine = lateDays * 1000.0;
        if (fine > 50000) fine = 50000;
        return fine;
    }

    // ─────────────────────────────────────────────────────────────
    // OVERDUE REMINDERS
    // ─────────────────────────────────────────────────────────────

    public void sendOverdueReminders(String todayDate) {
        System.out.println("Checking overdue borrows for: " + todayDate);
        for (String[] bw : borrows) {
            if (!"BORROWED".equals(bw[6])) continue;
            double fine = calculateFine(bw[4], todayDate);
            if (fine <= 0) continue;
            String[] member = findMemberById(bw[1]);
            String[] book   = findBookById(bw[2]);
            if (member == null || book == null) continue;

           
            /* CODE SMELL #4 (lanjutan) — DUPLICATE CODE (3rd occurrence)
            Logika pengiriman email ini sama persis dengan yang ada di
            processBookBorrowing() dan processReturn().
            */
            String subjectOverdue = "Overdue Notice - " + book[1];
            String bodyOverdue    = "Dear " + member[1] + ",\n"
                + "Your borrowed book '" + book[1] + "' is overdue.\n"
                + "Due date: " + bw[4] + "\n"
                + "Current fine: Rp " + fine + "\n"
                + "Please return immediately.\n"
                + "Thank you,\nLibrary System";
            System.out.println("--- Sending Email ---");
            System.out.println("To: "      + member[2]);
            System.out.println("Subject: " + subjectOverdue);
            System.out.println("Body:\n"   + bodyOverdue);
            System.out.println("--- Email Sent ---");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // REPORTS
    // ─────────────────────────────────────────────────────────────

    public void generateBorrowReport() {
        System.out.println("===== BORROW REPORT =====");
        int total = 0, active = 0, returned = 0, overdue = 0;
        for (String[] bw : borrows) {
            total++;
            if ("RETURNED".equals(bw[6])) {
                returned++;
            } else {
                active++;
                double fine = calculateFine(bw[4], "2026-05-12");
                if (fine > 0) overdue++;
            }
        }
        System.out.println("Total borrows : " + total);
        System.out.println("Active        : " + active);
        System.out.println("Returned      : " + returned);
        System.out.println("Overdue       : " + overdue);
    }

    public void generateMemberReport() {
        System.out.println("===== MEMBER REPORT =====");
        System.out.println("Total members : " + members.size());
        for (String[] m : members) {
            System.out.println("  " + m[1] + " — active borrows: " + m[8]);
        }
    }

    public void generateBookReport() {
        System.out.println("===== BOOK INVENTORY REPORT =====");
        System.out.println("Total titles : " + books.size());
        int totalCopies = 0, availCopies = 0;
        for (String[] b : books) {
            totalCopies += Integer.parseInt(b[6]);
            availCopies += Integer.parseInt(b[7]);
        }
        System.out.println("Total copies     : " + totalCopies);
        System.out.println("Available copies : " + availCopies);
        System.out.println("Borrowed copies  : " + (totalCopies - availCopies));
    }

    // ─────────────────────────────────────────────────────────────
    // UTILITY / SEED DATA
    // ─────────────────────────────────────────────────────────────

    public void seedData() {
        // CODE SMELL #3 (continued) — LONG PARAMETER LIST: 7 args
        addBook("B001", "Clean Code", "Robert C. Martin",
                "Technology", "Prentice Hall", 2008, 5);
        addBook("B002", "The Pragmatic Programmer", "Andrew Hunt",
                "Technology", "Addison-Wesley", 1999, 3);
        addBook("B003", "Refactoring", "Martin Fowler",
                "Technology", "Addison-Wesley", 2018, 4);
        addBook("B004", "Design Patterns", "Gang of Four",
                "Technology", "Addison-Wesley", 1994, 2);

        // CODE SMELL #6 (continued) — DATA CLUMPS: street/city/province/postal
        // always passed together; should be an Address object
        addMember("M001", "Budi Santoso", "budi@email.com", "08111111111",
                  "Jl. Sudirman No. 1", "Jakarta", "DKI Jakarta", "10220");
        addMember("M002", "Sari Dewi",   "sari@email.com", "08222222222",
                  "Jl. Gatot Subroto No. 5", "Bandung", "Jawa Barat", "40161");
        addMember("M003", "Andi Wijaya", "andi@email.com", "08333333333",
                  "Jl. Malioboro No. 10", "Yogyakarta", "DIY", "55271");
    }

    // ─────────────────────────────────────────────────────────────
    // MAIN — Demo Run
    // ─────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        LibrarySystem lib = new LibrarySystem();
        lib.seedData();

        System.out.println("\n========== BORROWING ==========");
        lib.processBookBorrowing(
            "BW001", "M001", "B001",
            "2026-04-01", "2026-04-15"
        );
        lib.processBookBorrowing(
            "BW002", "M002", "B002",
            "2026-04-05", "2026-04-19"
        );

        System.out.println("\n========== RETURN ==========");
        // M001 returns late → fine applies
        lib.processReturn("BW001", "2026-05-01");

        System.out.println("\n========== OVERDUE REMINDERS ==========");
        lib.sendOverdueReminders("2026-05-12");

        System.out.println("\n========== REPORTS ==========");
        lib.generateBookReport();
        lib.generateMemberReport();
        lib.generateBorrowReport();

        System.out.println("\n========== BOOK SEARCH ==========");
        lib.displayAllBooks();
    }
}
