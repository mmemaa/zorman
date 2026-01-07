public class OblikaPodjetja {
    private int id;
    private String kratica;
    private String polnoIme;

    public OblikaPodjetja(int id, String kratica, String polnoIme) {
        this.id = id;
        this.kratica = kratica;
        this.polnoIme = polnoIme;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKratica() {
        return kratica;
    }

    public void setKratica(String kratica) {
        this.kratica = kratica;
    }

    public String getPolnoIme() {
        return polnoIme;
    }

    public void setPolnoIme(String polnoIme) {
        this.polnoIme = polnoIme;
    }

    @Override
    public String toString() {
        return kratica + " - " + polnoIme;
    }
}

