package br.com.literalura.literalura_tiago.service;

import br.com.literalura.literalura_tiago.model.Autor;
import br.com.literalura.literalura_tiago.model.Format;
import br.com.literalura.literalura_tiago.model.Livro;
import br.com.literalura.literalura_tiago.repository.AutorRepository;
import br.com.literalura.literalura_tiago.repository.LivroRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LivroService {

    private static final String GUTENDEX_API_URL = "https://gutendex.com/books/";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private AutorRepository autorRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This method retrieves book data from an external API based on a given title,
     * saves the book information into the database, and associates authors with the books.
     * <p>
     * Este método recupera dados do livro de uma API externa com base em um título fornecido,
     * salva as informações do livro no banco de dados e associa autores aos livros.
     */
    public void salvarLivrosAutores(String title) {
        String apiUrl = GUTENDEX_API_URL + "?search=" + title;
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        JSONObject responseObject = new JSONObject(response.getBody());
        JSONArray booksArray = responseObject.getJSONArray("results");

        if (booksArray.length() > 0) {
            JSONObject bookJson = booksArray.getJSONObject(0);

            Livro livro = new Livro();
            if (bookJson.getString("title").length() > 255) {
                livro.setTitle(bookJson.getString("title").substring(0, 255));
            } else {
                livro.setTitle(bookJson.getString("title"));
            }

            livro.setDownloadCount(bookJson.getDouble("download_count"));

            JSONArray languagesJson = bookJson.getJSONArray("languages");
            List<String> languages = new ArrayList<>();
            for (int l = 0; l < languagesJson.length(); l++) {
                languages.add(languagesJson.getString(l));
            }
            livro.setLanguages(languages);

            List<Format> formats = new ArrayList<>();
            JSONObject formatsJson = bookJson.getJSONObject("formats");
            for (String chave : formatsJson.keySet()) {
                String valor = formatsJson.getString(chave);
                formats.add(new Format(chave, valor, livro));
            }
            livro.setFormats(formats);

            livroRepository.save(livro);

            JSONArray authorsArray = bookJson.getJSONArray("authors");
            for (int j = 0; j < authorsArray.length(); j++) {
                JSONObject authorJson = authorsArray.getJSONObject(j);
                String autorNome = authorJson.getString("name");

                Optional<Autor> existingAuthor = autorRepository.findByName(autorNome);
                Autor author;
                if (existingAuthor.isPresent()) {
                    author = existingAuthor.get();
                } else {
                    author = new Autor();
                    author.setName(authorJson.getString("name"));

                    if (authorJson.has("birth_year") && !authorJson.isNull("birth_year")) {
                        author.setBirthYear(authorJson.getInt("birth_year"));
                    } else {
                        author.setDeathYear(null);
                    }

                    if (authorJson.has("death_year") && !authorJson.isNull("death_year")) {
                        author.setDeathYear(authorJson.getInt("death_year"));
                    } else {
                        author.setDeathYear(null);
                    }

                    autorRepository.save(author);
                }

                livro.getAuthors().add(author);
            }

            livroRepository.save(livro);
            imprimirDetalhesLivros(livro);
        } else {
            System.out.println("Nenhum livro encontrado com o título fornecido.");
        }
    }

    /**
     * This method retrieves and prints all books stored in the database.
     *
     * Este método recupera e imprime todos os livros armazenados no banco de dados.
     */
    @Transactional
    public void imprimirTodosLivros() {
        List<Livro> livros = livroRepository.findAll();

        if (livros.isEmpty()) {
            System.out.println("\nNenhum livro encontrado no banco de dados.");
        } else {
            for (Livro livro : livros) {
                imprimirDetalhesLivros(livro);
            }
        }
    }

    /**
     * This method retrieves and prints books based on a specified language.
     *
     * Este método recupera e imprime livros com base em um idioma especificado.
     */
    @Transactional
    public void listarLivrosPeloIdioma(String language) {
        List<Livro> livros = livroRepository.findByLanguagesContaining(language);

        if (livros.isEmpty()) {
            System.out.println("\nNão existem livros no idioma " + language + " no banco de dados");
            return;
        }

        for (Livro livro : livros) {
            System.out.println("\n------------ LIVRO ------------");
            System.out.println("Título: " + (livro.getTitle() != null ? livro.getTitle() : "N/A"));

            if (!livro.getAuthors().isEmpty()) {
                for (Autor author : livro.getAuthors()) {
                    System.out.println("Autor(es): " + author.getName());
                }
            } else {
                System.out.println("Autor: N/A");
            }

            System.out.println("Idioma: " + (livro.getLanguages().isEmpty() ? "N/A" : language));
            System.out.println("Número de Downloads: " + (livro.getDownloadCount() != null ? livro.getDownloadCount() :
                    "N/A"));
            System.out.println("-------------------------------\n");
        }
    }


    /**
     * This method retrieves and prints the top 10 most downloaded books.
     *
     * Este método recupera e imprime os 10 livros mais baixados.
     */
    public void exibirTop10Downloads() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Livro> topDownloads = livroRepository.findTop10ByOrderByDownloadCountDesc(pageable);

        System.out.println("\nTop 10 Livros Mais Baixados:");
        System.out.println("\n-----------------------------\n");
        for (int i = 0; i < topDownloads.size(); i++) {
            Livro livro = topDownloads.get(i);
            System.out.println("Posição: " + (i + 1));
            System.out.println("Título: " + livro.getTitle());
            for (Autor author : livro.getAuthors()) {
                System.out.println("Autor(es): " + author.getName());
            }
            System.out.println("Número de Downloads: " + livro.getDownloadCount());
            System.out.println("\n-----------------------------\n");
        }
    }

    /**
     * This method just prints the details of a book.
     * <p>
     * Este método apenas imprime os detalhes de um livro.
     */
    private void imprimirDetalhesLivros(@NotNull Livro livro) {
        System.out.println("\n------------LIVRO------------");
        System.out.println("Título: " + (livro.getTitle() != null ? livro.getTitle() : "N/A"));

        if (livro.getAuthors() != null && !livro.getAuthors().isEmpty()) {
            for (Autor author : livro.getAuthors()) {
                System.out.println("Autores: " + author.getName());
            }
        } else {
            System.out.println("   - N/A");
        }

        if (livro.getLanguages() != null && !livro.getLanguages().isEmpty()) {
            for (String language : livro.getLanguages()) {
                System.out.println("Idiomas: " + language);
            }
        } else {
            System.out.println("Idiomas: N/A");
        }

        System.out.println("Número de Downloads: " + (livro.getDownloadCount() != null ? livro.getDownloadCount() : "N/A"));
        System.out.println("-----------------------------\n");
    }
}
