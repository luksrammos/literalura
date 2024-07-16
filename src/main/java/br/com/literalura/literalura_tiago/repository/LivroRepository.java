package br.com.literalura.literalura_tiago.repository;

import br.com.literalura.literalura_tiago.model.Autor;
import br.com.literalura.literalura_tiago.model.Livro;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Integer> {

    @Transactional
    @Override
    List<Livro> findAll();

    List<Livro> findByAuthorsContaining(Autor autore);

    @Query("SELECT l FROM Livro l JOIN FETCH l.authors WHERE :language MEMBER OF l.languages")
    List<Livro> findByLanguagesContaining(String language);

    @Query("SELECT l FROM Livro l ORDER BY l.downloadCount DESC")
    @EntityGraph(attributePaths = "authors", type = EntityGraph.EntityGraphType.FETCH)
    List<Livro> findTop10ByOrderByDownloadCountDesc(Pageable pageable);
}
