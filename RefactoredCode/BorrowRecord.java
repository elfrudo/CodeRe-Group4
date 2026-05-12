/**
BorrowRecord.java

FIX SMELL #2 — LARGE CLASS (bagian dari pemecahan LibrarySystem)
Data peminjaman dipisahkan menjadi class tersendiri agar lebih rapi.

Class ini merepresentasikan satu transaksi peminjaman buku.
Status peminjaman: "BORROWED" atau "RETURNED".
*/
public class BorrowRecord {
    private String borrowId;
    private String memberId;
    private String bookId;
    private String borrowDate;
    private String dueDate;
    private String returnDate;
    private String status;

    public BorrowRecord(String borrowId, String memberId, String bookId,
                        String borrowDate, String dueDate) {
        this.borrowId   = borrowId;
        this.memberId   = memberId;
        this.bookId     = bookId;
        this.borrowDate = borrowDate;
        this.dueDate    = dueDate;
        this.returnDate = "";
        this.status     = "BORROWED";
    }

    // ─── Getters ─────────────────────────────────────────────────
    public String getBorrowId()   { return borrowId; }
    public String getMemberId()   { return memberId; }
    public String getBookId()     { return bookId; }
    public String getBorrowDate() { return borrowDate; }
    public String getDueDate()    { return dueDate; }
    public String getReturnDate() { return returnDate; }
    public String getStatus()     { return status; }

    // ─── Behaviour ───────────────────────────────────────────────
    public void markReturned(String returnDate) {
        this.returnDate = returnDate;
        this.status     = "RETURNED";
    }

    public boolean isBorrowed() { return "BORROWED".equals(status); }
    public boolean isReturned() { return "RETURNED".equals(status); }

    @Override
    public String toString() {
        return "BorrowID: "  + borrowId
             + " | Member: "  + memberId
             + " | Book: "    + bookId
             + " | Due: "     + dueDate
             + " | Status: "  + status;
    }
}
