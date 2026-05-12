/* Address.java
 
FIX SMELL #6 — DATA CLUMPS
Sebelumnya, field street, city, province, dan postalCode
selalu muncul bersama sebagai parameter terpisah di mana-mana.
Sekarang keempat field tersebut dikelompokkan dalam satu class.

Keuntungan:
- Tidak perlu kirim 4 parameter terpisah, cukup 1 objek Address
- Mudah diperluas (misal tambah field "country") di satu tempat
- toString() otomatis menghasilkan alamat lengkap
*/
public class Address {
    private String street;
    private String city;
    private String province;
    private String postalCode;

    public Address(String street, String city, String province, String postalCode) {
        this.street     = street;
        this.city       = city;
        this.province   = province;
        this.postalCode = postalCode;
    }

    public String getStreet()     { return street; }
    public String getCity()       { return city; }
    public String getProvince()   { return province; }
    public String getPostalCode() { return postalCode; }

    @Override
    public String toString() {
        return street + ", " + city + ", " + province + " " + postalCode;
    }
}
