package javaricci.com.br;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

public class FolhaPontoApp extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JTextField campoNome;
    private JTextField campoHorasBase;
    private JTable tabelaPonto;
    private JLabel labelTotalHoras;
    private JLabel labelTotalHorasExtras;
    private DefaultTableModel modeloTable;
    private SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm");
    private JComboBox<String> comboMes;
    private JComboBox<String> comboAno;

    public FolhaPontoApp() {
        setTitle("Folha de Ponto");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Painel principal
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel do cabeçalho
        JPanel painelCabecalho = new JPanel(new GridLayout(2, 4, 10, 10));

        // Seleção de mês e ano
        String[] meses = {"01 - Janeiro", "02 - Fevereiro", "03 - Março", "04 - Abril", 
                          "05 - Maio", "06 - Junho", "07 - Julho", "08 - Agosto", 
                          "09 - Setembro", "10 - Outubro", "11 - Novembro", "12 - Dezembro"};
        comboMes = new JComboBox<>(meses);
        
        ArrayList<String> anos = new ArrayList<>();
        int anoAtual = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = anoAtual - 5; i <= anoAtual + 5; i++) {
            anos.add(String.valueOf(i));
        }
        comboAno = new JComboBox<>(anos.toArray(new String[0]));
        comboAno.setSelectedItem(String.valueOf(anoAtual));
        
        painelCabecalho.add(new JLabel("Mês:"));
        painelCabecalho.add(comboMes);
        painelCabecalho.add(new JLabel("Ano:"));
        painelCabecalho.add(comboAno);
        
        // Campo nome
        painelCabecalho.add(new JLabel("Nome:"));
        campoNome = new JTextField("EDILSON S. RICCI");
        painelCabecalho.add(campoNome);

        // Horas base
        painelCabecalho.add(new JLabel("Hora Base:"));
        campoHorasBase = new JTextField("08:00");
        painelCabecalho.add(campoHorasBase);

        painelPrincipal.add(painelCabecalho, BorderLayout.NORTH);

        // Tabela
        criarTabelaPonto();
        JScrollPane painelRolagem = new JScrollPane(tabelaPonto);
        painelPrincipal.add(painelRolagem, BorderLayout.CENTER);

        // Painel rodapé
        JPanel painelRodape = new JPanel(new GridLayout(1, 4, 10, 10));
        painelRodape.add(new JLabel("Total de Horas:"));
        labelTotalHoras = new JLabel("0:00");
        painelRodape.add(labelTotalHoras);
        painelRodape.add(new JLabel("Total de Horas Extras:"));
        labelTotalHorasExtras = new JLabel("0:00");
        painelRodape.add(labelTotalHorasExtras);

        painelPrincipal.add(painelRodape, BorderLayout.SOUTH);

        // Painel de botões
        JPanel painelBotoes = new JPanel();
        JButton botaoCalcular = new JButton("Calcular");
        botaoCalcular.addActionListener(e -> calcularTodasLinhas());
        painelBotoes.add(botaoCalcular);
        
        JButton botaoLimpar = new JButton("Limpar");
        botaoLimpar.addActionListener(e -> limparFormulario());
        painelBotoes.add(botaoLimpar);
        
        JButton botaoGerar = new JButton("Gerar");
        botaoGerar.addActionListener(e -> gerarFolhaParaMes());
        painelBotoes.add(botaoGerar);
        
        painelPrincipal.add(painelBotoes, BorderLayout.EAST);

        add(painelPrincipal);
        
        // Adiciona listeners para combos de mês e ano
        ActionListener listenerMudancaData = e -> gerarFolhaParaMes();
        comboMes.addActionListener(listenerMudancaData);
        comboAno.addActionListener(listenerMudancaData);
        
        // Geração inicial dos dados
        gerarFolhaParaMes();
    }

    private void criarTabelaPonto() {
        String[] nomesColunas = {"Data", "Dia", "Entrada Manhã", "Saída Manhã", 
                               "Entrada Tarde", "Saída Tarde", "Horas Totais", "Horas Extras"};
        
        modeloTable = new DefaultTableModel(nomesColunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Colunas de data e dia não são editáveis
                return column != 0 && column != 1 && column != 6 && column != 7;
            }
        };
        
        tabelaPonto = new JTable(modeloTable);
        
        // Adiciona listeners para cálculo automático
        tabelaPonto.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        tabelaPonto.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (tabelaPonto.getSelectedRow() != -1) {
                    calcularLinha(tabelaPonto.getSelectedRow());
                }
            }
        });
        
        // Editor formatado para células de tempo
        DefaultCellEditor editorHora = new DefaultCellEditor(new JTextField()) {
            private JTextField campoTexto;
            
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, 
                    boolean isSelected, int row, int column) {
                campoTexto = (JTextField) super.getTableCellEditorComponent(
                        table, value, isSelected, row, column);
                return campoTexto;
            }
            
            @Override
            public Object getCellEditorValue() {
                String texto = campoTexto.getText();
                if (texto != null && !texto.trim().isEmpty()) {
                    try {
                        // Formata hora como HH:mm
                        if (texto.matches("\\d{1,2}:\\d{2}")) {
                            return texto;
                        } else if (texto.matches("\\d{1,2}")) {
                            return texto + ":00";
                        }
                    } catch (Exception e) {
                        return texto;
                    }
                }
                return texto;
            }
        };
        
        // Aplica editor de hora às colunas de tempo
        tabelaPonto.getColumnModel().getColumn(2).setCellEditor(editorHora); // Entrada manhã
        tabelaPonto.getColumnModel().getColumn(3).setCellEditor(editorHora); // Saída manhã
        tabelaPonto.getColumnModel().getColumn(4).setCellEditor(editorHora); // Entrada tarde
        tabelaPonto.getColumnModel().getColumn(5).setCellEditor(editorHora); // Saída tarde
        
        // Define larguras das colunas
        tabelaPonto.getColumnModel().getColumn(0).setPreferredWidth(100); // Data
        tabelaPonto.getColumnModel().getColumn(1).setPreferredWidth(50);  // Dia
        tabelaPonto.getColumnModel().getColumn(2).setPreferredWidth(100); // Entrada manhã
        tabelaPonto.getColumnModel().getColumn(3).setPreferredWidth(100); // Saída manhã
        tabelaPonto.getColumnModel().getColumn(4).setPreferredWidth(100); // Entrada tarde
        tabelaPonto.getColumnModel().getColumn(5).setPreferredWidth(100); // Saída tarde
        tabelaPonto.getColumnModel().getColumn(6).setPreferredWidth(100); // Horas totais
        tabelaPonto.getColumnModel().getColumn(7).setPreferredWidth(100); // Horas extras
        
        // Listener para recálculo quando célula é editada
        modeloTable.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int linha = e.getFirstRow();
                int coluna = e.getColumn();
                if (coluna >= 2 && coluna <= 5) {  // Se células de tempo foram editadas
                    calcularLinha(linha);
                }
            }
        });
    }
    
    private void gerarFolhaParaMes() {
        // Limpa dados existentes
        modeloTable.setRowCount(0);
        
        // Obtém mês e ano selecionados
        String mesSelecionado = (String) comboMes.getSelectedItem();
        String anoSelecionado = (String) comboAno.getSelectedItem();
        
        int mes = Integer.parseInt(mesSelecionado.substring(0, 2)) - 1; // Mês base 0
        int ano = Integer.parseInt(anoSelecionado);
        
        // Cria calendário para o mês selecionado
        Calendar calendario = Calendar.getInstance();
        calendario.set(ano, mes, 1);
        
        // Obtém número de dias no mês
        int diasNoMes = calendario.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        // Nomes dos dias em português
        String[] nomesDias = {"dom", "seg", "ter", "qua", "qui", "sex", "sáb"};
        
        // Adiciona uma linha para cada dia do mês
        for (int dia = 1; dia <= diasNoMes; dia++) {
            calendario.set(Calendar.DAY_OF_MONTH, dia);
            
            String data = String.format("%02d/%02d/%04d", dia, mes + 1, ano);
            String diaSemana = nomesDias[calendario.get(Calendar.DAY_OF_WEEK) - 1];
            
            // Para fins de semana, adiciona linha vazia
            if (diaSemana.equals("sáb") || diaSemana.equals("dom")) {
                modeloTable.addRow(new Object[]{data, diaSemana, "-", "-", "-", "-", "-", "-"});
            } else {
                // Para dias úteis, adiciona linha editável
                modeloTable.addRow(new Object[]{data, diaSemana, "", "", "", "", "", ""});
            }
        }
    }
    
    private void calcularLinha(int linha) {
        if (linha < 0 || linha >= modeloTable.getRowCount()) return;
        
        String diaSemana = (String) modeloTable.getValueAt(linha, 1);
        
        // Ignora cálculo para fins de semana
        if (diaSemana.equals("sáb") || diaSemana.equals("dom")) {
            return;
        }
        
        // Obtém valores de tempo
        String entradaManha = obterValorCelula(linha, 2);
        String saidaManha = obterValorCelula(linha, 3);
        String entradaTarde = obterValorCelula(linha, 4);
        String saidaTarde = obterValorCelula(linha, 5);
        
        // Ignora se algum campo estiver vazio
        if (entradaManha.isEmpty() || saidaManha.isEmpty() || 
            entradaTarde.isEmpty() || saidaTarde.isEmpty()) {
            return;
        }
        
        try {
            // Converte tempos para minutos desde meia-noite
            int minutosEntradaManha = horaParaMinutos(entradaManha);
            int minutosSaidaManha = horaParaMinutos(saidaManha);
            int minutosEntradaTarde = horaParaMinutos(entradaTarde);
            int minutosSaidaTarde = horaParaMinutos(saidaTarde);
            
            // Trata passagem da meia-noite
            if (minutosSaidaTarde < minutosEntradaTarde) {
                minutosSaidaTarde += 24 * 60; // Adiciona 24 horas
            }
            
            // Calcula minutos trabalhados totais
            int minutosManha = minutosSaidaManha - minutosEntradaManha;
            int minutosTarde = minutosSaidaTarde - minutosEntradaTarde;
            int minutosTotais = minutosManha + minutosTarde;
            
            // Formata horas totais como HH:MM
            String horasTotais = minutosParaHoraString(minutosTotais);
            modeloTable.setValueAt(horasTotais, linha, 6);
            
            // Calcula horas extras
            String horasBaseStr = campoHorasBase.getText();
            int minutosBase = horaParaMinutos(horasBaseStr);
            int minutosExtras = Math.max(0, minutosTotais - minutosBase);
            String horasExtras = minutosParaHoraString(minutosExtras);
            modeloTable.setValueAt(horasExtras, linha, 7);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao calcular tempos: " + e.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
        
        // Atualiza totais
        calcularTotais();
    }
    
    private void calcularTodasLinhas() {
        for (int i = 0; i < modeloTable.getRowCount(); i++) {
            calcularLinha(i);
        }
        calcularTotais();
    }
    
    private void calcularTotais() {
        int minutosTotais = 0;
        int minutosExtrasTotais = 0;
        
        for (int i = 0; i < modeloTable.getRowCount(); i++) {
            String horasTotaisStr = obterValorCelula(i, 6);
            String horasExtrasStr = obterValorCelula(i, 7);
            
            if (!horasTotaisStr.equals("-") && !horasTotaisStr.isEmpty()) {
                try {
                    minutosTotais += horaParaMinutos(horasTotaisStr);
                } catch (Exception e) {
                    // Ignora valores inválidos
                }
            }
            
            if (!horasExtrasStr.equals("-") && !horasExtrasStr.isEmpty()) {
                try {
                    minutosExtrasTotais += horaParaMinutos(horasExtrasStr);
                } catch (Exception e) {
                    // Ignora valores inválidos
                }
            }
        }
        
        labelTotalHoras.setText(minutosParaHoraString(minutosTotais));
        labelTotalHorasExtras.setText(minutosParaHoraString(minutosExtrasTotais));
    }
    
    private String obterValorCelula(int linha, int coluna) {
        Object valor = modeloTable.getValueAt(linha, coluna);
        return valor == null ? "" : valor.toString();
    }
    
    private int horaParaMinutos(String horaStr) throws ParseException {
        if (horaStr == null || horaStr.trim().isEmpty() || horaStr.equals("-")) {
            return 0;
        }
        
        // Trata formatos simples como "8:00" ou "8"
        String[] partes = horaStr.split(":");
        int horas = Integer.parseInt(partes[0]);
        int minutos = partes.length > 1 ? Integer.parseInt(partes[1]) : 0;
        
        return horas * 60 + minutos;
    }
    
    private String minutosParaHoraString(int minutos) {
        int horas = minutos / 60;
        int mins = minutos % 60;
        return String.format("%d:%02d", horas, mins);
    }
    
    private void limparFormulario() {
        // Limpa dados da tabela exceto colunas de data e dia
        for (int i = 0; i < modeloTable.getRowCount(); i++) {
            for (int j = 2; j <= 7; j++) {
                if (j <= 5) {
                    modeloTable.setValueAt("", i, j);
                } else {
                    modeloTable.setValueAt("", i, j);
                }
            }
        }
        
        // Reseta totais
        labelTotalHoras.setText("0:00");
        labelTotalHorasExtras.setText("0:00");
    }
    
    // Método para preencher o formulário com dados de exemplo
    private void preencherDadosExemplo() {
        // Dados de exemplo
        String[][] dadosExemplo = {
            {"01/01/2024", "seg", "08:00", "12:00", "13:00", "18:00"},
            {"02/01/2024", "ter", "08:00", "12:00", "13:00", "01:00"},
            {"03/01/2024", "qua", "17:00", "21:00", "21:30", "04:00"},
            {"04/01/2024", "qui", "07:50", "12:17", "13:40", "18:52"},
            {"05/01/2024", "sex", "17:00", "21:00", "21:30", "04:00"}
        };
        
        // Encontra linhas correspondentes e preenche
        for (String[] linha : dadosExemplo) {
            String data = linha[0];
            for (int i = 0; i < modeloTable.getRowCount(); i++) {
                if (modeloTable.getValueAt(i, 0).toString().equals(data)) {
                    modeloTable.setValueAt(linha[2], i, 2); // Entrada manhã
                    modeloTable.setValueAt(linha[3], i, 3); // Saída manhã
                    modeloTable.setValueAt(linha[4], i, 4); // Entrada tarde
                    modeloTable.setValueAt(linha[5], i, 5); // Saída tarde
                    calcularLinha(i);
                    break;
                }
            }
        }
        
        calcularTotais();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            FolhaPontoApp app = new FolhaPontoApp();
            app.setVisible(true);
        });
    }
}
