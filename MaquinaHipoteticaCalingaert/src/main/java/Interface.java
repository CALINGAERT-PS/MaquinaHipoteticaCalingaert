import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class Interface {

    private JFrame frame;
    private JTextArea memoryArea;
    private JTextField accField;
    private JTextField pcField;
    private JTextField spField;
    private JTextField mopField;
    private JTextField riField;
    private JTextField reField;
    private JTextField currentInstructionField;
    private JTextField statusOpField;
    private Vm vm;  
    private ProcessadorMacros processadorMacros;
    private NewAssembler assembler;
    private Linker linker;
    public int contadorStep;
    public boolean verifica;
    public Interface(Vm vm,ProcessadorMacros processadorMacros,NewAssembler assembler,Linker linker) {
        contadorStep = 1;
        verifica = false;
        this.vm = vm;  
        this.processadorMacros = processadorMacros;
        this.assembler = assembler;
        this.linker = linker;
        // Criando a janela principal
        frame = new JFrame("Simulador");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximiza a janela
        frame.setMinimumSize(new Dimension(800, 600)); // Define o tamanho mínimo da janela

        // Configurando o layout principal
        frame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10); // Espaçamento entre os componentes
        // Criando os painéis
        JPanel comandosPanel = new JPanel(new GridBagLayout());
        comandosPanel.setBorder(BorderFactory.createTitledBorder("Comandos"));

        JPanel arquivosPanel = new JPanel(new GridBagLayout());
        arquivosPanel.setBorder(BorderFactory.createTitledBorder("Arquivos"));

        JPanel memoryPanel = new JPanel(new GridBagLayout());
        memoryPanel.setBorder(BorderFactory.createTitledBorder("Memória"));
        memoryArea = new JTextArea(40, 20); 
        memoryArea.setEditable(false);
        c.gridx = 1;
        c.gridy = 1;
        c.gridheight = 2;
        c.weightx = 0.1;
        c.weighty = 0.1;
        c.anchor = GridBagConstraints.EAST;
        memoryPanel.add(new JScrollPane(memoryArea),c);
        c.anchor = GridBagConstraints.CENTER;
        c.ipadx = 100;
        c.ipady = 100;
        currentInstructionField = new JTextField(10);
        currentInstructionField.setEditable(false);
        currentInstructionField.setBackground(Color.GRAY);
        currentInstructionField.setForeground(Color.BLACK);
        currentInstructionField.setFont(new Font("Arial",Font.PLAIN,40));
        currentInstructionField.setHorizontalAlignment(JTextField.CENTER);
        currentInstructionField.setBorder(new LineBorder(Color.BLACK,2));
        memoryPanel.add(currentInstructionField,c);
        c.ipadx = 0;
        c.ipady = 0;    
        // da pra repensar como ficou todos registradores, nao sei se gostei da forma q eles tão expostos no painel
        // mas fodase ta funcional ,isso é dps
        JPanel registersPanel = new JPanel();
        registersPanel.setBorder(BorderFactory.createTitledBorder("Registradores"));
        registersPanel.setLayout(new GridLayout(2, 2));
        registersPanel.add(new JLabel("ACC:"));
        accField = new JTextField(10);
        accField.setEditable(false);
        registersPanel.add(accField);
        registersPanel.add(new JLabel("PC:"));
        pcField = new JTextField(10);
        pcField.setEditable(false);
        registersPanel.add(pcField);
        registersPanel.add(new JLabel("SP:"));
        spField = new JTextField(10);
        spField.setEditable(false);
        registersPanel.add(spField);
        registersPanel.add(new JLabel("MOP:"));
        mopField = new JTextField(10);
        mopField.setEditable(false);
        registersPanel.add(mopField);
        registersPanel.add(new JLabel("RI:"));
        riField = new JTextField(10);
        riField.setEditable(false);
        registersPanel.add(riField);
        registersPanel.add(new JLabel("RE:"));
        reField = new JTextField(10);
        reField.setEditable(false);
        registersPanel.add(reField);

        // Criando os botões
        JButton runButton = new JButton("Run");
        JButton stepButton = new JButton("Step");
        JButton cleanButton = new JButton("Clean");
        // Painel Comandos
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.1;
        c.weighty = 0.1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        comandosPanel.add(runButton,c);
        c.gridy = 1;
        comandosPanel.add(stepButton,c);
        c.gridy = 2;
        comandosPanel.add(cleanButton,c);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        frame.add(comandosPanel,c);

        //Painel Arquivos
        JTextField arquivo1Field = new JTextField("entrada 1 arquivo de macros",20);
        JTextField arquivo2Field = new JTextField("entrada 2 arquivo de macros",20);
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        c.weightx = 0.1;
        c.weighty = 0.1;
        c.anchor = GridBagConstraints.NORTH;
        arquivosPanel.add(arquivo1Field,c);
        c.gridy = 1;
        arquivosPanel.add(arquivo2Field,c);

        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        frame.add(arquivosPanel,c);

        statusOpField = new JTextField("status operacao",24);
        statusOpField.setEditable(false);
        statusOpField.setBackground(Color.GRAY);
        statusOpField.setForeground(Color.BLACK);
        statusOpField.setFont(new Font("Arial",Font.PLAIN,16));
        statusOpField.setHorizontalAlignment(JTextField.CENTER);
        statusOpField.setBorder(new LineBorder(Color.BLACK,2));
        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.NONE;
        frame.add(statusOpField,c);
        // Painel da memória
        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 2;
        c.weightx = 0.3;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        frame.add(memoryPanel, c);

        // Painel dos registradores
        c.gridx = 2;
        c.gridy = 2;
        c.gridwidth = 1;
        c.weightx = 0.3;
        c.weighty = 0.3;
        c.fill = GridBagConstraints.BOTH;
        frame.add(registersPanel, c);

        // Adicionando ação ao botão "Run"
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeRun(arquivo1Field.getText(), arquivo2Field.getText());  
                updateInterface();  
            }
        });

        // Adicionando ação ao botão "Step"
        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int mode = verificaValidadeArquivos(arquivo1Field.getText(), arquivo2Field.getText());
                if(mode == 3){
                    executeStep(arquivo1Field.getText(),arquivo2Field.getText());
                }
                if(mode == 2){
                    executeStep(arquivo2Field.getText());
                }
                if(mode == 1){
                    executeStep(arquivo1Field.getText());
                }
                if(mode == 0){
                    statusOpField.setText("nenhum arquivo valido!");
                }
                updateInterface();  
            }
        });

        cleanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Reiniciar a VM sem reatribuir vm
                vm.clear();
                contadorStep = 1;
                verifica = false;
                updateInterface();  
            }
        });
        
        updateInterface(); 
    }

    public void show() {
        frame.setVisible(true);
    }
    // retorna string com o nome da instrução, tem que fazer um check se ta bem certinho dps
    public String getCurrentInstruction(){
        return switch (vm.getRi()) {
            case 2 -> "ADD";
            case 130 -> "ADD";
            case 0 -> "BR";
            case 5 -> "BRNEG";
            case 1 -> "BRPOS";
            case 4 -> "BRZERO";
            case 15 -> "CALL";
            case 13 -> "COPY";
            case 141 -> "COPY";
            case 10 -> "DIVIDE";
            case 138 -> "DIVIDE";
            case 3 -> "LOAD";
            case 131 -> "LOAD";
            case 14 -> "MULT";
            case 142 -> "MULT";
            case 12 -> "READ";
            case 16 -> "RET";
            case 11 -> "STOP";
            case 7 -> "STORE";
            case 6 -> "SUB";
            case 134 -> "SUB";
            case 8 -> "WRITE";
            case 136 -> "WRITE";
            default -> "Unknown Operation";
        }; 
    }
    public void updateInterface() {
        accField.setText(String.valueOf(vm.getAcc()));
        pcField.setText(String.valueOf(vm.getPc()));
        spField.setText(String.valueOf(vm.getSp()));
        mopField.setText(String.valueOf(vm.getMop()));
        reField.setText(String.valueOf(vm.getRe()));
        riField.setText(String.valueOf(vm.getRi()));
        // Ta mostrando a ultima instrução executada, não sei se mantemos assim depois ou se mudamos para mostrar a que será executada depois, da pra mostrar as 2 tb
        // Ta meio cagada essa função ai retornando a string com o nome nao sei se gostei da solução q eu fiz, deve ter um jeito melhor
        // o endereço da execução ta completamente errado, tem que ver como puxar o endereço certinho, eu acho que tem que mexer na VM
        if(getCurrentInstruction().equals("STOP")){
            currentInstructionField.setText(getCurrentInstruction());
        }else{
        currentInstructionField.setText(getCurrentInstruction() + " " + String.valueOf(vm.getRe()));
        System.out.println("NBAO IGUAL");
        }
        StringBuilder memoryContent = new StringBuilder();
        for (int i = 0; i < 100; i++) {  
            memoryContent.append("Memória[").append(i).append("] = ")
                         .append(vm.getMemoryValue(i)).append("\n");
        }
        memoryArea.setText(memoryContent.toString());
        System.out.println("update interface!");
    }
    public void executeStep(String path1, String path2){
        switch(contadorStep){
            case 1:
                if(processadorMacros.verificaPath(path1) == true){
                    processadorMacros.runProcessador(path1,verifica);
                    contadorStep++;
                    verifica = true;
                    statusOpField.setText("primeiro arquivo processado!");
                }
                else{
                statusOpField.setText("FALHA NO PROCESSAMENTO DO PRIMEIRO ARQUIVO!");
                }
                break;
            case 2:
                if(processadorMacros.verificaPath(path2) == true){
                    processadorMacros.runProcessador(path2,verifica);
                    contadorStep++;
                    statusOpField.setText("segundo arquivo processado!");
                }
                else{
                statusOpField.setText("FALHA NO PROCESSAMENTO DO SEGUNDO ARQUIVO");
                }
                break;
            case 3:
                assembler.assemble("MASMAPRG.asm","program.obj","program.lst");
                contadorStep++;
                statusOpField.setText("primeiro arquivo montado!");
                break;
            case 4:
                assembler.assemble("MASMAPRG2.asm","program2.obj","program2.lst");
                contadorStep++;
                statusOpField.setText("segundo arquivo montado!");
                break;
            case 5:
                linker.link2("program.obj","program2.obj");
                contadorStep++;
                statusOpField.setText("arquivos linkados!");
                break;
            case 6:
                vm.loadProgramFromFile("src/files/output.hpx");
                contadorStep++;
                statusOpField.setText("programa carregado!");
                break;
            default:
                if(vm.executeInstruction() == false){
                    frame.dispose();
                };
                statusOpField.setText("rodando da VM!");
                contadorStep++;
                break;           
        }

    }

    public void executeStep(String path1){
        switch(contadorStep){
            case 1:
                if(processadorMacros.verificaPath(path1) == true){
                    processadorMacros.runProcessador(path1,verifica);
                    contadorStep++;
                    verifica = true;
                    statusOpField.setText("primeiro arquivo processado!");
                }
                else{
                statusOpField.setText("FALHA NO PROCESSAMENTO DO PRIMEIRO ARQUIVO!");
                }
                break;
            case 2:
                assembler.assemble("MASMAPRG.asm","program.obj","program.lst");
                contadorStep++;
                statusOpField.setText("primeiro arquivo montado!");
                break;
            case 3:
                linker.link("program.obj","a");
                contadorStep++;
                statusOpField.setText("arquivo linkado!");
                break;
            case 4:
                vm.loadProgramFromFile("src/files/output.hpx");
                contadorStep++;
                statusOpField.setText("programa carregado!");
                break;
            default:
                if(vm.executeInstruction() == false){
                    statusOpField.setText("Programa finalizado!");;
                };
                statusOpField.setText("rodando da VM!");
                contadorStep++;
                break;           
        }
    }
    public void executeRun(String path1,String path2){
        if(processadorMacros.verificaPath(path1) == true){
            processadorMacros.runProcessador(path1,verifica);
            verifica = true;
            statusOpField.setText("primeiro arquivo processado!");
        }
        else{
            statusOpField.setText("FALHA NO PROCESSAMENTO DO PRIMEIRO ARQUIVO!");
            return;
        }
        if(processadorMacros.verificaPath(path2) == true){
            processadorMacros.runProcessador(path2,verifica);
            statusOpField.setText("segundo arquivo processado!");
        }
        else{
            statusOpField.setText("FALHA NO PROCESSAMENTO DO SEGUNDO ARQUIVO");
            return;
        }
            assembler.assemble("MASMAPRG.asm","program.obj","program.lst");
            statusOpField.setText("primeiro arquivo montado!");
            assembler.assemble("MASMAPRG2.asm","program2.obj","program2.lst");
            statusOpField.setText("segundo arquivo montado!");
       // try {
         //   linker.link("program.obj","program2.obj");
           // } catch (IOException ex) {
        //}
            statusOpField.setText("arquivos linkados!");
            vm.loadProgramFromFile("src/files/output.hpx");
            statusOpField.setText("programa carregado!");
            statusOpField.setText("rodando da VM!");
            //vm.run();     
    }

    public int verificaValidadeArquivos(String arquivo1, String arquivo2){
        File file1 = new File(arquivo1);
        File file2 = new File(arquivo2);
        // 3 - modo de 2 arquivos
        // 2 - modo de 1 arquivo campo 2
        // 1 - modo de 1 arquivo campo 1
        // 0 - nenhum arquivo encontrado
        if(file1.exists() && file2.exists()){
            System.out.println("2 arquivos encontrados!");
            return 3;
        }else if(file1.exists()){
            System.out.println("1 arquivo encontrado! - campo 1");
            return 1;
        }else if(file2.exists()){
            System.out.println("1 arquivo encontrado! - campo 2");
            return 2;
        }
        else{
            System.out.println("Nenhum arquivo encontrado!");
            return 0;
        }
    }
}

