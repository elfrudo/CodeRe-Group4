import java.util.ArrayList;
import java.util.List;

/*
MemberService.java

FIX SMELL #2 — LARGE CLASS
Semua operasi yang berhubungan dengan anggota perpustakaan
dipindahkan dari LibrarySystem ke class ini.

FIX SMELL #3 — LONG PARAMETER LIST
addMember() sekarang menerima 1 objek Member,
bukan 8 parameter terpisah.

FIX SMELL #6 — DATA CLUMPS (via class Address di Member)
Street, city, province, postalCode tidak lagi dioper satu per satu.

Tanggung jawab class ini:
- Menyimpan daftar anggota (in-memory)
- Menambah, menghapus, mencari, dan menampilkan anggota
*/
public class MemberService {
    private List<Member> members = new ArrayList<>();

    /*
    Mendaftarkan anggota baru.
    
    FIX #3: menerima objek Member, bukan 8 parameter.
    Sebelum: addMember(id, name, email, phone, street, city, province, postal)
    Sesudah: addMember(member)
    */
    public void addMember(Member member) {
        if (member.getMemberId() == null || member.getMemberId().isEmpty()) {
            System.out.println("Error: Member ID cannot be empty.");
            return;
        }
        if (findById(member.getMemberId()) != null) {
            System.out.println("Error: Member ID already exists.");
            return;
        }
        members.add(member);
        System.out.println("Member registered: " + member.getName());
    }

    /* Menghapus anggota berdasarkan ID. */
    public void removeMember(String memberId) {
        members.removeIf(m -> m.getMemberId().equals(memberId));
        System.out.println("Member removed: " + memberId);
    }

    /* Mencari anggota berdasarkan ID. Mengembalikan null jika tidak ditemukan. */
    public Member findById(String memberId) {
        for (Member m : members) {
            if (m.getMemberId().equals(memberId)) return m;
        }
        return null;
    }

    /* Mengembalikan seluruh daftar anggota. */
    public List<Member> getAllMembers() { return members; }

    /* Menampilkan semua anggota ke console. */
    public void displayAll() {
        System.out.println("===== ALL MEMBERS =====");
        for (Member m : members) {
            System.out.println(m);
        }
    }
}
