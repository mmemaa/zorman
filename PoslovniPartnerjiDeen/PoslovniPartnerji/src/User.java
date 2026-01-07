public class User {
    private int id;
    private String uporabniskoIme;
    private String ime;
    private String priimek;
    private String email;
    private String vloga;

    public User(int id, String uporabniskoIme, String ime, String priimek, String email, String vloga) {
        this.id = id;
        this.uporabniskoIme = uporabniskoIme;
        this.ime = ime;
        this.priimek = priimek;
        this.email = email;
        this.vloga = vloga;
    }

    public int getId() {
        return id;
    }

    public String getUporabniskoIme() {
        return uporabniskoIme;
    }

    public String getIme() {
        return ime;
    }

    public String getPriimek() {
        return priimek;
    }

    public String getEmail() {
        return email;
    }

    public String getVloga() {
        return vloga;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(vloga);
    }
}

