package br.com.literalura.literalura_tiago.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "autores")
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "birth_year")
    private Integer birthYear;

    @Column(name = "death_year")
    private Integer deathYear; // Integer para permitir valores nulos

    @ManyToMany(mappedBy = "authors")
    private List<Livro> livrosEAutores;

    public Autor() {
    }

    public Autor(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Autor(String name, int birthYear, Integer deathYear) {
        this.name = name;
        this.birthYear = birthYear;
        this.deathYear = deathYear;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public Integer getDeathYear() {
        return deathYear;
    }

    public void setDeathYear(Integer deathYear) {
        this.deathYear = deathYear;
    }

    public List<Livro> getLivrosEAutores() {
        return livrosEAutores;
    }

    public void setLivrosEAutores(List<Livro> livrosEAutores) {
        this.livrosEAutores = livrosEAutores;
    }
}
