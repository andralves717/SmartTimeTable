# SmartTimeTable
Este projecto tem como objectivo a criação de um programa simples para ajudar a fazer horários para cada semestre de acordo com as preferências de cada estudante.

Criado por Diogo Regateiro de 2011 a 2017 no [Code UA](http://code.ua.pt/projects/stt)

## Funcionalidades:
- GUI com icons.
- Introdução e remoção de Cadeiras e Aulas (T, TP, P e OT).
- Editar directamente Cadeiras e Aulas já introduzidas.
- Escolher as turmas a considerar para o horário final sem ter de os eliminar.
- Salvaguarda de Cadeiras e Aulas introduzidas (XML)
- Visualização das Cadeiras e Aulas já introduzidas por meio de uma lista.
- Construção dos horários possíveis em formato html.
- Coloração personalizada das células dos horarios.
- Visualizar os horários no programa.
- Visualizar a lista de turmas em cada horário para facilitar a introdução no paco.
- Verificar as turmas que estão a causar "deadlock" de sobreposições.
- Possibilidade de permitir sobreposições dentro certos limites personalizáveis. (Requer mais alguns testes)
- Melhorar a coloração do horário para permitir uma maior personalização (como pintar cada cadeira de cor diferente).
- Shortcuts para 'New', 'Open' e 'Save'.

## [TimeTableParser-ua](https://github.com/RodrigoRosmaninho/TimeTableParser-ua)

De modo a facilitar a criação do horário existe um script em python feito pelo [Rodrigo Rosmaninho](https://github.com/RodrigoRosmaninho) para gerar o ficheiro .sttx (XML) usado neste programa através dos horários disponíveis nos [Horários do PACO](https://paco.ua.pt/horariosweb/).

## [Releases](https://github.com/andralves717/SmartTimeTable/releases)
Fazer download da versão mais recente em Releases.

## Pré-requesitos
É necessário ter o Java instalado no computador.\
Compatível com todos os sistemas operativos de desktop.

#### Linux
Dar permissões de execução ao ficheiro:\
```chmod +x SmartTimeTable.jar```\
Ou ir a propriedades do ficheiro e em Permissões permitir executar o ficheiro como um programa.

## Execução
Clicar para abrir o SmartTimeTable.jar ou no terminal:\
```java -jar SmartTimeTable.jar```

