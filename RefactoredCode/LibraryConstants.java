/*
LibraryConstants.java

FIX SMELL #5 — MAGIC NUMBERS
Semua angka "ajaib" yang sebelumnya tersebar di kode
(seperti 3, 1000.0, 50000) sekarang dijadikan konstanta
bernama di satu tempat.

Keuntungan:
- Mudah dibaca: MAX_BORROW_LIMIT lebih jelas dari angka 3
- Mudah diubah: cukup ubah di file ini, berlaku ke seluruh program
*/
public class LibraryConstants {
    public static final int    MAX_BORROW_LIMIT = 3;        // maks buku per anggota
    public static final double DAILY_FINE_IDR   = 1000.0;  // denda per hari (Rupiah)
    public static final double MAX_FINE_IDR     = 50000.0; // denda maksimum (Rupiah)
}
