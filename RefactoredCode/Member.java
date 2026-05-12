/*
Member.java

FIX SMELL #3 — LONG PARAMETER LIST
Sebelumnya addMember() menerima 8 parameter terpisah
(termasuk 4 field alamat yang juga merupakan Data Clump).
Sekarang data anggota dibungkus dalam class Member,
dan alamat diwakilkan oleh objek Address.

FIX SMELL #6 — DATA CLUMPS (via class Address)
Field street, city, province, postalCode tidak lagi
dioper satu per satu — cukup kirim 1 objek Address.

FIX SMELL #2 — LARGE CLASS (bagian dari pemecahan LibrarySystem)
*/
public class Member {
    private String  memberId;
    private String  name;
    private String  email;
    private String  phone;
    private Address address;      // FIX #6: Address sebagai satu objek
    private int     borrowCount;

    public Member(String memberId, String name, String email,
                  String phone, Address address) {
        this.memberId    = memberId;
        this.name        = name;
        this.email       = email;
        this.phone       = phone;
        this.address     = address;
        this.borrowCount = 0;
    }


    public String  getMemberId()    { return memberId; }
    public String  getName()        { return name; }
    public String  getEmail()       { return email; }
    public String  getPhone()       { return phone; }
    public Address getAddress()     { return address; }
    public int     getBorrowCount() { return borrowCount; }


    public void incrementBorrowCount() { borrowCount++; }

    public void decrementBorrowCount() {
        if (borrowCount > 0) borrowCount--;
    }

    @Override
    public String toString() {
        return "ID: "        + memberId
             + " | Name: "    + name
             + " | Email: "   + email
             + " | Phone: "   + phone
             + " | Address: " + address;  // Address.toString() dipanggil otomatis
    }
}
