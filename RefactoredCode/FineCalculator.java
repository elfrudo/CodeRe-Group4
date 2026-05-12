/*
FineCalculator.java

FIX SMELL #2 — LARGE CLASS
Logika kalkulasi denda dipisahkan dari LibrarySystem
ke class tersendiri.

FIX SMELL #5 — MAGIC NUMBERS
Sebelum: lateDays * 1000.0 dan if (fine > 50000)
Sesudah: menggunakan LibraryConstants.DAILY_FINE_IDR
          dan LibraryConstants.MAX_FINE_IDR

Tanggung jawab class ini:
- Menghitung jumlah hari keterlambatan
- Menghitung total denda berdasarkan konstanta
*/
public class FineCalculator {

    /*
    Menghitung denda berdasarkan tanggal jatuh tempo dan tanggal kembali.
    Format tanggal: "YYYY-MM-DD"
    
    FIX #5: DAILY_FINE_IDR dan MAX_FINE_IDR dari LibraryConstants,
    bukan angka 1000 dan 50000 secara langsung.
    */
    public double calculate(String dueDate, String returnDate) {
        int lateDays = computeLateDays(dueDate, returnDate);
        if (lateDays <= 0) return 0;

        double fine = lateDays * LibraryConstants.DAILY_FINE_IDR;
        return Math.min(fine, LibraryConstants.MAX_FINE_IDR);
    }

    /*
    Menghitung selisih hari antara dueDate dan returnDate.
    Nilai positif berarti terlambat.
    */
    private int computeLateDays(String dueDate, String returnDate) {
        try {
            int dueTot = toTotalDays(dueDate);
            int retTot = toTotalDays(returnDate);
            return retTot - dueTot;
        } catch (Exception e) {
            System.out.println("Warning: Could not parse dates.");
            return 0;
        }
    }

    /*
    Mengonversi tanggal "YYYY-MM-DD" menjadi total hari
    (perkiraan sederhana untuk perbandingan).
    */
    private int toTotalDays(String date) {
        String[] parts = date.split("-");
        int year  = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day   = Integer.parseInt(parts[2]);
        return year * 365 + month * 30 + day;
    }
}
