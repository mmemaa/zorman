public class Dejavnost {
    private int id;
    private int uporabnikId;
    private String uporabnikIme;
    private String akcija;
    private String entiteta;
    private Integer entitetaId;
    private String opis;
    private String stareVrednosti;
    private String noveVrednosti;
    private String ipNaslov;
    private String createdAt;

    public Dejavnost() {
    }

    public Dejavnost(int uporabnikId, String akcija, String entiteta, Integer entitetaId, String opis) {
        this.uporabnikId = uporabnikId;
        this.akcija = akcija;
        this.entiteta = entiteta;
        this.entitetaId = entitetaId;
        this.opis = opis;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUporabnikId() {
        return uporabnikId;
    }

    public void setUporabnikId(int uporabnikId) {
        this.uporabnikId = uporabnikId;
    }

    public String getUporabnikIme() {
        return uporabnikIme;
    }

    public void setUporabnikIme(String uporabnikIme) {
        this.uporabnikIme = uporabnikIme;
    }

    public String getAkcija() {
        return akcija;
    }

    public void setAkcija(String akcija) {
        this.akcija = akcija;
    }

    public String getEntiteta() {
        return entiteta;
    }

    public void setEntiteta(String entiteta) {
        this.entiteta = entiteta;
    }

    public Integer getEntitetaId() {
        return entitetaId;
    }

    public void setEntitetaId(Integer entitetaId) {
        this.entitetaId = entitetaId;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public String getStareVrednosti() {
        return stareVrednosti;
    }

    public void setStareVrednosti(String stareVrednosti) {
        this.stareVrednosti = stareVrednosti;
    }

    public String getNoveVrednosti() {
        return noveVrednosti;
    }

    public void setNoveVrednosti(String noveVrednosti) {
        this.noveVrednosti = noveVrednosti;
    }

    public String getIpNaslov() {
        return ipNaslov;
    }

    public void setIpNaslov(String ipNaslov) {
        this.ipNaslov = ipNaslov;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

