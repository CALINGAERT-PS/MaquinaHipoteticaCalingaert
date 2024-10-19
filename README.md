Repositório dedicado à disciplina de Programação de Sistemas, ministrada pelo professor Anderson Priebe Ferrugem.
# Integrantes
- Bruno Martins
- Chistian Kringel
- Gustavo Reginato
- Henrique Versiani
- Lucio Gularte
- Miguel Carraro
- Pedro Ravazolo
- Samuel Starke
- Vitor Colombo

A interface conta com 3 botões:
Step -> Executa um passo
Run -> Executa todos passos
Clean -> Zera a interface, volta do zero

A execução segue a ordem do processador de macros, montador, ligador, carregador e máquina virtual.

A aba de arquivos aceita 2 arquivos .txt, porém caso seja digitado apenas 1 arquivo válido, a interface
verifica e roda no modo de um arquivo somente.

O status de operação informa o que foi feito na execução, caso seja executado o programa passo a passo.
Erros de carregamento de arquivo são informados no status de operação.

Após gerar todos arquivos, a interface chega na máquina virtual, onde temos três principais painéis.
O painel da memória na direita, que mostra a memória da máquina virtual.
O painel na parte inferior da interface, que mostra os registradores da máquina virtual.
O painel central que indica a última instrução executada pela VM.
