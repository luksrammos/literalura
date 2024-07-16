package br.com.literalura.literalura_tiago.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "livros")
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String title;

    @OneToMany(mappedBy = "livro", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Format> formats = new ArrayList<>();

    @Column(name = "download_count")
    private Double downloadCount;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "languages", joinColumns = @JoinColumn(name = "livro_id"))
    @Column(name = "language")
    private List<String> languages = new ArrayList<>();

    @ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    @JoinTable(
            name = "livro_autor",
            joinColumns = { @JoinColumn(name = "livro_id") },
            inverseJoinColumns = { @JoinColumn(name = "autor_id") }
    )
    private List<Autor> authors = new ArrayList<>();

    public Livro() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFormats(List<Format> formats) {
        this.formats = formats;
    }

    public Double getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Double downloadCount) {
        this.downloadCount = downloadCount;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<Autor> getAuthors() {
        return authors;
    }
}
