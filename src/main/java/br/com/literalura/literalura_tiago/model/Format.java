package br.com.literalura.literalura_tiago.model;

import jakarta.persistence.*;

@Entity
@Table(name = "formats")
public class Format {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "format_key")
    private String key;

    @Column(name = "format_value")
    private String value;

    @ManyToOne
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;

    public Format() {
    }

    public Format(String key, String value, Livro livro) {
        this.key = key;
        this.value = value;
        this.livro = livro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }
}
