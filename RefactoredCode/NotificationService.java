/*
NotificationService.java

FIX SMELL #4 — DUPLICATE CODE
Sebelumnya, blok kode pengiriman email yang sama
di-copy-paste di 3 tempat berbeda:
1. processBookBorrowing()
2. processReturn()
3. sendOverdueReminders()

Sekarang semua pengiriman email dilakukan melalui
satu method terpusat: sendEmail().

FIX SMELL #2 — LARGE CLASS (bagian dari pemecahan LibrarySystem)

Keuntungan:
- Prinsip DRY (Don't Repeat Yourself) terpenuhi
- Jika format email perlu diubah, cukup ubah di satu tempat
- Mudah diganti dengan implementasi email sungguhan (SMTP, dll)
*/
public class NotificationService {

    /*
    Method terpusat untuk mengirim email.
    
    FIX #4: Sebelumnya blok print ini copy-paste 3 kali.
    Sekarang cukup panggil sendEmail() dari mana saja.
    */
    public void sendEmail(String to, String subject, String body) {
        System.out.println("--- Sending Email ---");
        System.out.println("To: "      + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body:\n"   + body);
        System.out.println("--- Email Sent ---");
    }

    /*
    Mengirim konfirmasi peminjaman buku.
    Dipanggil dari BorrowingService setelah peminjaman berhasil.
    */
    public void sendBorrowConfirmation(Member member, Book book, String dueDate) {
        String subject = "Borrowing Confirmation - " + book.getTitle();
        String body    = "Dear " + member.getName() + ",\n"
                       + "You have borrowed: " + book.getTitle() + "\n"
                       + "Due date: " + dueDate + "\n"
                       + "Please return on time to avoid fines.\n"
                       + "Thank you,\nLibrary System";
        sendEmail(member.getEmail(), subject, body);
    }

    /*
    Mengirim konfirmasi pengembalian buku.
    Dipanggil dari BorrowingService setelah pengembalian berhasil.
    */
    public void sendReturnConfirmation(Member member, Book book,
                                       String returnDate, double fine) {
        String subject = "Return Confirmation - " + book.getTitle();
        String body    = "Dear " + member.getName() + ",\n"
                       + "You have returned: " + book.getTitle() + "\n"
                       + "Return date: " + returnDate + "\n"
                       + (fine > 0
                           ? "Outstanding fine: Rp " + fine + "\n"
                           : "No fine. Thank you!\n")
                       + "Thank you,\nLibrary System";
        sendEmail(member.getEmail(), subject, body);
    }

    /*
    Mengirim notifikasi keterlambatan.
    Dipanggil dari BorrowingService saat pengecekan overdue.
    */
    public void sendOverdueNotice(Member member, Book book,
                                  String dueDate, double fine) {
        String subject = "Overdue Notice - " + book.getTitle();
        String body    = "Dear " + member.getName() + ",\n"
                       + "Your borrowed book '" + book.getTitle() + "' is overdue.\n"
                       + "Due date: " + dueDate + "\n"
                       + "Current fine: Rp " + fine + "\n"
                       + "Please return immediately.\n"
                       + "Thank you,\nLibrary System";
        sendEmail(member.getEmail(), subject, body);
    }
}
