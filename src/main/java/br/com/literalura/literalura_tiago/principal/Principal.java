package br.com.literalura.literalura_tiago.principal;

import br.com.literalura.literalura_tiago.service.AutorService;
import br.com.literalura.literalura_tiago.service.LivroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Scanner;

@Component
public class Principal {

    private final Scanner leitura = new Scanner(System.in);


    private final LivroService livroService;

    private final AutorService autorService;

    @Autowired
    public Principal(LivroService livroService, AutorService autorService) {
        this.livroService = livroService;
        this.autorService = autorService;
    }

    public void exibeMenu() {


        var menu = """
                 \n
                Digite a opção desejada:
                1  - Buscar livros pelo título
                2  - Listar livros registrados
                3  - Listar autores registrados
                4  - Listar por autores que estavam vivos em um determinado ano 
                5  - Listar livros em um determinado idioma  
                6  - Listar Top 10 livros mais baixados
                7  - Buscar autor por nome
                8  - Buscar autor por ano de nascimento
                9  - Buscar autor por ano de falecimento
                10 - Estatisticas dos livros registrados no banco de dados
                
                 0 - Sair                                 
                 """;

        int opcao = -1;

        while (opcao != 0) {
            System.out.println(menu);

            try {
                System.out.print("Escolha uma opção: ");
                opcao = Integer.parseInt(leitura.nextLine());

                switch (opcao) {
                    case 1 -> {
                        System.out.println("Opção 1 selecionada");
                        buscarLivroPeloTitulo();
                    }
                    case 2 -> {
                        System.out.println("Opção 2 selecionada");
                        listarLivrosRegis();
                    }
                    case 3 -> {
                        System.out.println("Opção 3 selecionada");
                        listarAutoresRegis();
                    }
                    case 4 -> {
                        System.out.println("Opção 4 selecionada");
                        autoresVivosEmAno();
                    }
                    case 5 -> {
                        System.out.println("Opção 5 selecionada");
                        buscarLivrosPorIdioma();
                    }
                    case 6 -> {
                        System.out.println("Opção 6 selecionada");
                        top10Downloads();
                    }
                    case 7 -> {
                        System.out.println("Opção 7 selecionada");
                        bascaAutorPorNome();
                    }
                    case 8 -> {
                        System.out.println("Opção 8 selecionada");
                        bascaAutorPorAnoDeNascimento();
                    }
                    case 9 -> {
                        System.out.println("Opção 9 selecionada");
                        bascaAutorPorAnoDeFalecimento();
                    }
                    case 10 -> {
                        System.out.println("Opção 10 selecionada");
                        estatisticasLivros();
                    }
                    case 0 -> {
                        System.out.println("Saindo ...");
                    }
                    default -> {
                        System.out.println("Opção inválida");
                    }
                }
            } catch (NumberFormatException e) {
                leitura.nextLine();
                System.out.println("Entrada inválida. Por favor, insira um número válido.");
            }
        }
        System.exit(0);
    }

    private void buscarLivroPeloTitulo() {
        System.out.println("Digite o nome do livro que deseja buscar: ");
        var nomeLivro = leitura.nextLine();

        livroService.salvarLivrosAutores(nomeLivro);
    }

    private void listarLivrosRegis() {
        livroService.imprimirTodosLivros();
    }

    private void listarAutoresRegis() {
        autorService.telaAutoresLivros();
    }

    private void autoresVivosEmAno() {
        System.out.print("Insira o ano que deseja pesquisar: ");
        var ano = leitura.nextInt();
        leitura.nextLine();

        autorService.autoresVivosAno(ano);
    }

    private void buscarLivrosPorIdioma() {
        System.out.println("Insira o idioma para realizar a busca: ");
        System.out.print("\nes - espanhol" + "\n" + "en - inglês" + "\n" + "fr - francês" + "\n" + "pt - português\n");
        var idioma = leitura.nextLine();

        livroService.listarLivrosPeloIdioma(idioma);
    }

    @Transactional
    private void top10Downloads() {
        livroService.exibirTop10Downloads();
    }

    private void bascaAutorPorNome() {
        System.out.print("Insira o nome do Autor que deseja pesquisar: ");
        var name = leitura.nextLine();
        autorService.encontarAutorPeloNome(name);
    }

    private void bascaAutorPorAnoDeNascimento() {
        System.out.print("Insira o ano de nascimento do Autor que deseja pesquisar: ");
        var anoNascimento = leitura.nextLine();

        autorService.econtrarAutorPorAnoDeNascimento(anoNascimento);
    }

    private void bascaAutorPorAnoDeFalecimento() {
        System.out.print("Insira o ano de falecimento do Autor que deseja pesquisar: ");
        var anoFalecimento = leitura.nextLine();

        autorService.encontrarAutorPorAnoDeFalecimento(anoFalecimento);
    }

    private void estatisticasLivros() {
        int escolha = -1;

        while (escolha != 0) {
            System.out.print("\nEscolha uma opção:\n" +
                    "1 - Percentual de livros por autor\n" +
                    "2 - Percentual de livros por idioma\n" +
                    "0 - Voltar ao menu inicial\n");

            try {
                System.out.print("Escolha uma opção: ");
                escolha = Integer.parseInt(leitura.nextLine());

                switch (escolha) {
                    case 1 -> {
                        autorService.percentualLivrosPorAutor();
                    }
                    case 2 -> {
                        autorService.percentualLivrosPorIdioma();
                    }
                    case 0 -> {
                        System.out.println("Voltando ao menu inicial...");
                    }
                    default -> {
                        System.out.println("Opção inválida. Por favor, insira um número válido.");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, insira um número válido.");
            }
        }
    }

}
