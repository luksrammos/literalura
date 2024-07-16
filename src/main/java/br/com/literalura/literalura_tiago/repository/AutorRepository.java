package br.com.literalura.literalura_tiago.repository;

import br.com.literalura.literalura_tiago.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Integer> {

    Optional<Autor> findByName(String name);

    @Query("SELECT a FROM Autor a LEFT JOIN FETCH a.livrosEAutores WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Autor> findByNameContainingIgnoreCaseWithBooks(@Param("name") String name);

    @Query("SELECT a FROM Autor a WHERE a.birthYear = :year")
    List<Autor> findAuthorsByBirthYear(@Param("year") String year);

    @Query("SELECT a FROM Autor a WHERE a.deathYear = :year")
    List<Autor> findAuthorsByDeathYear(@Param("year") String s);
}
