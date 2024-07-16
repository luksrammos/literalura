package br.com.literalura.literalura_tiago.service;

import br.com.literalura.literalura_tiago.dto.LivrosAutoresDTO;
import br.com.literalura.literalura_tiago.model.Autor;
import br.com.literalura.literalura_tiago.model.Livro;
import br.com.literalura.literalura_tiago.repository.AutorRepository;
import br.com.literalura.literalura_tiago.repository.LivroRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AutorService {
    @Autowired
    private final AutorRepository autorRepository;

    @Autowired
    private final LivroRepository livroRepository;

    @Autowired
    private final EntityManager entityManager;


    @Autowired
    public AutorService(AutorRepository autorRepository, LivroRepository livroRepository, EntityManager entityManager) {
        this.autorRepository = autorRepository;
        this.livroRepository = livroRepository;
        this.entityManager = entityManager;
    }

    /**
     * Get all authors with their respective books.
     * Obtém todos os autores com seus respectivos livros.
     */
    public List<LivrosAutoresDTO> todosAutoresLivros() {
        List<Autor> autores = autorRepository.findAll();
        return autores.stream().map(autor -> {
            List<Livro> livros = livroRepository.findByAuthorsContaining(autor);
            return new LivrosAutoresDTO(autor, livros);
        }).collect(Collectors.toList());
    }

    /**
     * Display authors with their respective books.
     * Exibe os autores com seus respectivos livros.
     */
    public void telaAutoresLivros() {
        List<LivrosAutoresDTO> autoresComLivros = todosAutoresLivros();
        List<Autor> autores = autorRepository.findAll();

        for (LivrosAutoresDTO autorComLivros : autoresComLivros) {
            System.out.println("\n------------AUTOR------------");
            System.out.println("Autor: " + autorComLivros.getAutor().getName());
            Autor autor = autorComLivros.getAutor();
            System.out.println("Ano de Nascimento: " + (autor.getBirthYear() != null ? autor.getBirthYear() : "N/A"));
            System.out.println("Ano de Falecimento: " + (autor.getDeathYear() != null ? autor.getDeathYear() : "N/A"));

            int posicaoLivro = 1;
            for (Livro livro : autorComLivros.getLivros()) {
                System.out.println("Livro - " + posicaoLivro + " : " + livro.getTitle());
                posicaoLivro++;
            }
            System.out.println("-----------------------------\n");
        }
    }

    /**
     * Display authors alive in a given year along with their books.
     * Exibe os autores vivos em um determinado ano juntamente com seus livros.
     */
    @Transactional
    public void autoresVivosAno(Integer year) {
        List<Autor> autores = autorRepository.findAll();

        if (autores.isEmpty()) {
            System.out.println("Nenhum autor encontrado no banco de dados.");
            return;
        }

        boolean encontrouAutorVivo = false;

        for (Autor autor : autores) {
            if ((autor.getBirthYear() != null && autor.getBirthYear() <= year) &&
                    (autor.getDeathYear() == null || autor.getDeathYear() >= year)) {
                encontrouAutorVivo = true;
                System.out.println("\n------------ AUTOR ------------");
                System.out.println("Nome: " + autor.getName());
                System.out.println("Ano de Nascimento: " + autor.getBirthYear());
                System.out.println("Ano de Falecimento: " + (autor.getDeathYear() != null ? autor.getDeathYear() : "Vivo"));

                List<Livro> autoresLivros = autor.getLivrosEAutores();
                if (autoresLivros.isEmpty()) {
                    System.out.println("   - Nenhum livro encontrado.");
                } else {
                    int posicaoLivro = 1;
                    for (Livro livro : autoresLivros) {
                        System.out.println("Livro " + posicaoLivro + ": " + livro.getTitle());
                        posicaoLivro++;
                    }
                }
                System.out.println("------------------------------\n");
            }
        }

        if (!encontrouAutorVivo) {
            System.out.println("Nenhum autor vivo encontrado no ano " + year + ".");
        }
    }

    /**
     * Find authors by name and display their information along with their books.
     * Encontra autores pelo nome e exibe suas informações juntamente com seus livros.
     */

    public void encontarAutorPeloNome(String name) {
        List<Autor> autores;
        if (name.contains(",")) {
            autores = autorRepository.findByNameContainingIgnoreCaseWithBooks(name);
        } else {
            autores = autorRepository.findByNameContainingIgnoreCaseWithBooks(name);
            if (autores.isEmpty()) {
                String reversedName = reverseName(name);
                autores = autorRepository.findByNameContainingIgnoreCaseWithBooks(reversedName);
            }
        }

        if (autores.isEmpty()) {
            System.out.println("\nNenhum autor encontrado com o nome: " + name);
            return;
        }

        for (Autor autor : autores) {
            System.out.println("\n------------ AUTOR ------------");
            System.out.println("Nome: " + (autor.getName() != null ? autor.getName() : "N/A"));
            System.out.println("Ano de Nascimento: " + (autor.getBirthYear() != null ? autor.getBirthYear() : "N/A"));
            System.out.println("Ano de Falecimento: " + (autor.getDeathYear() != null ? autor.getDeathYear() : "N/A"));
            if (autor.getLivrosEAutores() != null && !autor.getLivrosEAutores().isEmpty()) {
                int posicaoLivro = 1;
                for (Livro livro : autor.getLivrosEAutores()) {
                    System.out.println("Livros - " + posicaoLivro + ": " + livro.getTitle());
                    posicaoLivro++;
                }
            } else {
                System.out.println("   - N/A");
            }
            System.out.println("-------------------------------\n");
        }
    }

    private String reverseName(String name) {
        String[] parts = name.split(" ");
        if (parts.length > 1) {
            return parts[parts.length - 1] + ", " + String.join(" ", Arrays.copyOf(parts, parts.length - 1));
        }
        return name;
    }

    private boolean isNumeric(String str) {
        return str != null && str.matches("[0-9]+");
    }

    /**
     * Find authors by birth year and display their information along with their books.
     * Encontra autores pelo ano de nascimento e exibe suas informações juntamente com seus livros.
     */
    @Transactional
    public void econtrarAutorPorAnoDeNascimento(String anoNascimento) {
        try {
            if (!isNumeric(anoNascimento)) {
                throw new IllegalArgumentException("Ano de nascimento deve ser um valor numérico.");
            }

            int ano = Integer.parseInt(anoNascimento);

            List<Autor> autores = autorRepository.findAuthorsByBirthYear(String.valueOf(ano));

            if (autores.isEmpty()) {
                System.out.println("\nNenhum autor encontrado com o ano de nascimento: " + anoNascimento);
                return;
            }

            for (Autor author : autores) {
                System.out.println("\n------------ AUTOR ------------");
                System.out.println("Nome: " + (author.getName() != null ? author.getName() : "N/A"));
                System.out.println("Ano de Nascimento: " + (author.getBirthYear() != null ? author.getBirthYear() : "N/A"));
                System.out.println("Ano de Falecimento: " + (author.getDeathYear() != null ? author.getDeathYear() : "N/A"));
                if (author.getLivrosEAutores() != null && !author.getLivrosEAutores().isEmpty()) {
                    int posicaoLivro = 1;
                    for (Livro livro : author.getLivrosEAutores()) {
                        System.out.println("Livros - " + posicaoLivro + ":" + livro.getTitle());
                        posicaoLivro++;
                    }
                } else {
                    System.out.println("   - N/A");
                }
                System.out.println("-------------------------------\n");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao tentar encontrar autores pelo ano de nascimento: " + e.getMessage());
        }
    }

    /**
     * Find authors by death year and display their information along with their books.
     * Encontra autores pelo ano de falecimento e exibe suas informações juntamente com seus livros.
     */
    @Transactional
    public void encontrarAutorPorAnoDeFalecimento(String anoFalecimento) {
        try {
            if (!isNumeric(anoFalecimento)) {
                throw new IllegalArgumentException("Ano de falecimento deve ser um valor numérico.");
            }

            int ano = Integer.parseInt(anoFalecimento);

            List<Autor> autores = autorRepository.findAuthorsByDeathYear(String.valueOf(ano));

            if (autores.isEmpty()) {
                System.out.println("\nNenhum autor encontrado com o ano de falecimento: " + anoFalecimento);
                return;
            }

            for (Autor author : autores) {
                System.out.println("\n------------ AUTOR ------------");
                System.out.println("Nome: " + (author.getName() != null ? author.getName() : "N/A"));
                System.out.println("Ano de Nascimento: " + (author.getBirthYear() != null ? author.getBirthYear() : "N/A"));
                System.out.println("Ano de Falecimento: " + (author.getDeathYear() != null ? author.getDeathYear() : "N/A"));
                if (author.getLivrosEAutores() != null && !author.getLivrosEAutores().isEmpty()) {
                    int posicaoLivro = 1;
                    for (Livro livro : author.getLivrosEAutores()) {
                        System.out.println("Livros - " + posicaoLivro + ":" + livro.getTitle());
                        posicaoLivro++;
                    }
                } else {
                    System.out.println("   - N/A");
                }
                System.out.println("-------------------------------\n");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao tentar encontrar autores pelo ano de falecimento: " + e.getMessage());
        }
    }


    /**
     * Calculate and display the percentage of books per author.
     * Calcula e exibe o percentual de livros por autor.
     */
    @Transactional
    public void percentualLivrosPorAutor() {
        List<Autor> autores = autorRepository.findAll();
        long totalLivros = livroRepository.count();

        if (totalLivros == 0) {
            System.out.println("Nenhum livro registrado no banco de dados.");
            return;
        }

        DecimalFormat df = new DecimalFormat("0.00");

        for (Autor autor : autores) {
            long livrosAutor = autor.getLivrosEAutores().size();
            double percentual = (double) livrosAutor / totalLivros * 100;
            System.out.println("Autor: " + autor.getName() + " - Percentual de livros: " + df.format(percentual) + "%");
        }
    }


    /**
     * Calculate and display the percentage of books per language.
     * Calcula e exibe o percentual de livros por idioma.
     */
    @Transactional
    public void percentualLivrosPorIdioma() {
        List<Livro> livros = livroRepository.findAll();
        long totalLivros = livros.size();

        if (totalLivros == 0) {
            System.out.println("Nenhum livro registrado no banco de dados.");
            return;
        }

        Map<String, Long> contagemPorIdioma = livros.stream()
                .flatMap(livro -> livro.getLanguages().stream())
                .collect(Collectors.groupingBy(language -> {
                    switch (language) {
                        case "es":
                            return "es - espanhol";
                        case "en":
                            return "en - inglês";
                        case "fr":
                            return "fr - francês";
                        case "pt":
                            return "pt - português";
                        default:
                            return "outros idiomas";
                    }
                }, Collectors.counting()));

        DecimalFormat df = new DecimalFormat("0.00");

        contagemPorIdioma.forEach((idioma, count) -> {
            double percentual = (double) count / totalLivros * 100;
            System.out.println("Idioma: " + idioma + " - Percentual de livros: " + df.format(percentual) + "%");
        });
    }
}
