package classes;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TelaAgenda extends JFrame {
    private JPanel painelTopo;
    private JPanel painelEsquerdo;
    private JPanel painelDireiro;
    private JList listaPessoas;
    private JButton buttonAdicionar;
    private JTextField textNome;
    private JTextField textIdade;
    private JTextField textAltura;
    private JButton buttonRemover;
    private JPanel painelMain;
    private JLabel labelIdade;
    private JLabel labelAltura;
    private JButton imprimirAgenda;
    private JTextField textBuscaContato;
    private JButton buscarButton;
    private JButton buttonImprimePosicao;
    private DefaultListModel listaPessoaModel;
    private SimpleDateFormat sdf;

    public TelaAgenda() {
        //Chama contrutor super classe.
        super("Agenda de Contatos POO");
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        Locale.setDefault(Locale.US);
        setContentPane(painelMain);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();

        listaPessoaModel = new DefaultListModel();
        listaPessoas.setModel(listaPessoaModel);
        carregarLista();

        listaPessoas.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                //Seleciona pela lista quando usa inferface.
                int numeroPessoa = listaPessoas.getSelectedIndex();
                // se numero de pessoas for maior que 0, siginifica que selecionou.
                if (numeroPessoa >= 0) {
                    selecionaPessoa(numeroPessoa);
                    //Ativa o bottao de adicionar
                    buttonAdicionar.setEnabled(true);
                }
            }
        });

        buttonAdicionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Delimita numero de pessoas na lista.
                if (getContatos().size() == 10) {
                    //Exibe mensagem lista cheia.
                    JOptionPane.showMessageDialog(null, "A lista de contatos está cheia!", "Lista", JOptionPane.PLAIN_MESSAGE);
                } else {
                    Pessoa p = null; //Inicia pessoa.
                    try {
                        p = new Pessoa(
                                textNome.getText(),
                                sdf.parse(textIdade.getText()),
                                Float.parseFloat(textAltura.getText())
                        );
                    } catch (ParseException e) {
                        JOptionPane.showMessageDialog(null, "Formato de data invalido.", "Busca contato", JOptionPane.PLAIN_MESSAGE);
                    }
                    armazenaPessoa(p);
                    carregarLista();
                    clearFields();
                }
            }

        });

        buttonRemover.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Pessoa p = null;
                try {
                    p = new Pessoa(
                            textNome.getText(),
                            sdf.parse(textIdade.getText()),
                            Float.parseFloat(textAltura.getText())
                    );
                } catch (ParseException e) {
                    JOptionPane.showMessageDialog(null, "Formato de data invalido.", "Busca contato", JOptionPane.PLAIN_MESSAGE);
                }
                removePessoa(p);
                carregarLista();
                clearFields();
            }
        });

        imprimirAgenda.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Compoem a string em pedacos.
                StringBuilder msg = new StringBuilder();
                //For com contatos
                //Para toda pessoa p o getcontato imprime uma linha.
                for (Pessoa p : getContatos()) {
                    msg.append(String.format("%s, %s, %.2f metros.\n", p.getNome(), sdf.format(p.getIdade()), p.getAltura()));
                }
                //lista impressa no Jpane
                JOptionPane.showMessageDialog(null, msg, "Lista de contatos", JOptionPane.PLAIN_MESSAGE);
                clearFields();
            }
        });

        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int index = buscaPessoa(textBuscaContato.getText());
                //Operacao ternaria para caso contato encontrado ou nao.
                String msg = index < 0 ? "Contato não encontrado" :
                        String.format("%s esta na posição %d", textBuscaContato.getText(), index);
                //Mostra informacoes do contato no Jpane
                JOptionPane.showMessageDialog(null, msg, "Busca contato", JOptionPane.PLAIN_MESSAGE);
                clearFields();
            }
        });

        buttonImprimePosicao.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    int index = Integer.parseInt(textBuscaContato.getText());
                    imprimePessoa(index);
                    clearFields();
                } catch (NumberFormatException e) {
                    //Caso aconteca erro de formato invalido.
                    JOptionPane.showMessageDialog(null, "Formato inválido", "Erro de entrada", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void clearFields() {
        textNome.setText(null);
        textIdade.setText(null);
        textAltura.setText(null);
        textBuscaContato.setText(null);
    }

    public void carregarLista() {
        //Primeiro remove tudo da lista para carregar novamente.
        listaPessoaModel.removeAllElements();
        for (Pessoa p : getContatos()) {
            listaPessoaModel.addElement(p.getNome());
        }
    }

    public void armazenaPessoa(Pessoa pessoa) {
        //Escreve o arquivo agenda.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("agenda.txt", true))) {
            //Formatacao da linha.
            String linha = String.format("%s;%s;%.2f\n",
                    pessoa.getNome(),
                    sdf.format(pessoa.getIdade()),
                    pessoa.getAltura()
            );
            //Escreve no arquivo.
            writer.write(linha);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void removePessoa(Pessoa pessoa) {
        //Pega a lista de contatos.
        List<Pessoa> contatos = getContatos();
        //Se o nome e idade da pessoa forem iguais, remove a pessoa.
        contatos.removeIf(p -> p.getNome().equals(pessoa.getNome()) && p.getIdade().equals(pessoa.getIdade()));
        //Grava novamente.
        gravarLista(contatos);
    }

    public int buscaPessoa(String nome) {
        //Recebe um nome.
        int index = 0;
        //For each para pegar pessoa.
        for (Pessoa p : getContatos()) {
            if (p.getNome().equalsIgnoreCase(nome))
                //Retorna sua possicao.
                return index;
            index++;
        }
        return -1;
    }

    public void imprimePessoa(int index) {
        try {
            List<Pessoa> contatos = getContatos();
            Pessoa pessoa = contatos.get(index);
            if (pessoa == null)
                throw new RuntimeException("Pessoa não encontrada");
            JOptionPane.showMessageDialog(null, String.format("Nome: %s\nIdade: %d anos\nAltura: %.2f metros", pessoa.getNome(), pessoa.getIdade(), pessoa.getAltura()), "Dados do contato", JOptionPane.PLAIN_MESSAGE);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(null, "Contato não encontrado", "Erro", JOptionPane.ERROR_MESSAGE);
        }

    }

    public void selecionaPessoa(int index) {
        try {
            List<Pessoa> list = getContatos();
            Pessoa p = list.get(index);
            //Preenche os campos na interface.
            textNome.setText(p.getNome());
            textAltura.setText(Float.toString(p.getAltura()));
            textIdade.setText(sdf.format(p.getIdade()));
            buttonAdicionar.setEnabled(true);
        } catch (RuntimeException e) {
            //throw new RuntimeException("Pessoa não encontrada.");
        }
    }

    public List<Pessoa> getContatos() {
        List<Pessoa> contatos = new ArrayList<>();
        //Le o arquivo.
        try (BufferedReader reader = new BufferedReader(new FileReader("agenda.txt"))) {
            String linha = reader.readLine();
            //Enquanto linha diferebte de nulo, continua lendo.
            while (linha != null) {
                String[] campos = linha.split(";");
                contatos.add(new Pessoa(
                        campos[0],
                        sdf.parse(campos[1]),
                        Float.parseFloat(campos[2]))
                );
                linha = reader.readLine();
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e.getMessage());
        }
        //Retona a lista de contatos.
        return contatos;
    }

    private void gravarLista(List<Pessoa> contatos) {
        //Grava o arquivo.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("agenda.txt", true))) {
            //Limpa o arquivo para escreve novamente.
            PrintWriter pw = new PrintWriter("agenda.txt");
            pw.print("");
            for (Pessoa p : contatos) {
                String linha = String.format("%s;%s;%.2f\n",
                        p.getNome(),
                        sdf.format(p.getIdade()),
                        p.getAltura()
                );
                writer.write(linha);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void main(String[] args) {
        //Incia aplicacao.
        //Tela visivel.
        TelaAgenda tela = new TelaAgenda();
        tela.setVisible(true);
    }
}
