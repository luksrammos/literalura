package br.com.literalura.literalura_tiago.dto;

import br.com.literalura.literalura_tiago.model.Autor;
import br.com.literalura.literalura_tiago.model.Livro;

import java.util.List;

public class LivrosAutoresDTO {

    private final Autor autor;
    private final List<Livro> livros;

    public LivrosAutoresDTO(Autor autor, List<Livro> livros) {
        this.autor = autor;
        this.livros = livros;
    }

    public Autor getAutor() {
        return autor;
    }

    public List<Livro> getLivros() {
        return livros;
    }
}
