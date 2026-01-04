import java.util.UUID;

public class Partner {
    private UUID id;
    private String maticnaStevilka;
    private String davcnaStevilka;
    private String naziv;
    private String kratkiNaziv;
    private String pravnaOblikaId;
    private String status;
    private String dejavnost;
    private String naslov;
    private String postaStevilka;
    private String kraj;
    private Integer drzavaId;
    private String telefonskaStevilka;
    private String elektronskaPosta;
    private String spletnaStran;
    private String velikostPodjetja;
    private Boolean jeDavcniZavezanec;
    private String stevilkaEvidenceDdv;
    private String opombe;

    // getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getMaticnaStevilka() { return maticnaStevilka; }
    public void setMaticnaStevilka(String maticnaStevilka) { this.maticnaStevilka = maticnaStevilka; }
    public String getDavcnaStevilka() { return davcnaStevilka; }
    public void setDavcnaStevilka(String davcnaStevilka) { this.davcnaStevilka = davcnaStevilka; }
    public String getNaziv() { return naziv; }
    public void setNaziv(String naziv) { this.naziv = naziv; }
    public String getKratkiNaziv() { return kratkiNaziv; }
    public void setKratkiNaziv(String kratkiNaziv) { this.kratkiNaziv = kratkiNaziv; }
    public String getPravnaOblikaId() { return pravnaOblikaId; }
    public void setPravnaOblikaId(String pravnaOblikaId) { this.pravnaOblikaId = pravnaOblikaId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDejavnost() { return dejavnost; }
    public void setDejavnost(String dejavnost) { this.dejavnost = dejavnost; }
    public String getNaslov() { return naslov; }
    public void setNaslov(String naslov) { this.naslov = naslov; }
    public String getPostaStevilka() { return postaStevilka; }
    public void setPostaStevilka(String postaStevilka) { this.postaStevilka = postaStevilka; }
    public String getKraj() { return kraj; }
    public void setKraj(String kraj) { this.kraj = kraj; }
    public Integer getDrzavaId() { return drzavaId; }
    public void setDrzavaId(Integer drzavaId) { this.drzavaId = drzavaId; }
    public String getTelefonskaStevilka() { return telefonskaStevilka; }
    public void setTelefonskaStevilka(String telefonskaStevilka) { this.telefonskaStevilka = telefonskaStevilka; }
    public String getElektronskaPosta() { return elektronskaPosta; }
    public void setElektronskaPosta(String elektronskaPosta) { this.elektronskaPosta = elektronskaPosta; }
    public String getSpletnaStran() { return spletnaStran; }
    public void setSpletnaStran(String spletnaStran) { this.spletnaStran = spletnaStran; }
    public String getVelikostPodjetja() { return velikostPodjetja; }
    public void setVelikostPodjetja(String velikostPodjetja) { this.velikostPodjetja = velikostPodjetja; }
    public Boolean getJeDavcniZavezanec() { return jeDavcniZavezanec; }
    public void setJeDavcniZavezanec(Boolean jeDavcniZavezanec) { this.jeDavcniZavezanec = jeDavcniZavezanec; }
    public String getStevilkaEvidenceDdv() { return stevilkaEvidenceDdv; }
    public void setStevilkaEvidenceDdv(String stevilkaEvidenceDdv) { this.stevilkaEvidenceDdv = stevilkaEvidenceDdv; }
    public String getOpombe() { return opombe; }
    public void setOpombe(String opombe) { this.opombe = opombe; }

    @Override
    public String toString() {
        return "Partner{" +
                "id=" + id +
                ", maticnaStevilka='" + maticnaStevilka + '\'' +
                ", naziv='" + naziv + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Partner)) return false;
        Partner partner = (Partner) o;
        if (id == null && partner.id == null) return true;
        if (id == null || partner.id == null) return false;
        return id.equals(partner.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}
