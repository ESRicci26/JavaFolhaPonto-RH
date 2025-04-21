# Folha de Ponto - Aplicação Java Swing

Aplicação para registro e cálculo de horas trabalhadas e horas extras.

## Funcionalidades

- Registro diário de horários de entrada e saída
- Cálculo automático de horas trabalhadas e horas extras
- Visualização mensal com totais consolidados
- Suporte a diferentes jornadas de trabalho
- Geração automática da folha para qualquer mês/ano

## Componentes Principais

### Campos de Entrada
- **Nome**: Campo para inserção do nome do funcionário
- **Hora Base**: Define a jornada diária padrão (padrão: 8 horas)
- **Seleção de Mês/Ano**: Comboboxes para selecionar o período desejado

### Tabela de Registros
- **Data**: Data do registro (não editável)
- **Dia**: Dia da semana (não editável)
- **Entrada Manhã**: Horário de entrada no turno da manhã
- **Saída Manhã**: Horário de saída no turno da manhã
- **Entrada Tarde**: Horário de entrada no turno da tarde
- **Saída Tarde**: Horário de saída no turno da tarde
- **Horas Totais**: Cálculo automático do total de horas trabalhadas no dia
- **Horas Extras**: Cálculo automático das horas extras no dia

### Botões
- **Calcular**: Recalcula todos os registros
- **Limpar**: Remove todos os horários inseridos
- **Gerar**: Regenera a folha para o mês/ano selecionado

### Totais
- **Total de Horas**: Soma de todas as horas trabalhadas no mês
- **Total de Horas Extras**: Soma de todas as horas extras no mês

## Como Usar

1. Selecione o mês e ano desejados
2. Insira seu nome (opcional)
3. Ajuste a hora base se necessário (padrão 8:00)
4. Preencha os horários de entrada e saída para cada dia útil
5. Os cálculos são feitos automaticamente quando você sai do campo

## Tecnologias Utilizadas

- Java Swing para interface gráfica
- SimpleDateFormat para manipulação de datas e horas
- JTable com modelo customizado para exibição dos dados

## Requisitos

- Java 8 ou superior

## Licença

[MIT License](https://opensource.org/licenses/MIT)


