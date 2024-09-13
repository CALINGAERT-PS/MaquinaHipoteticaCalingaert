import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

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
    private Vm vm;  
    
    public Interface(Vm vm) {
        this.vm = vm;  

        // Criando a janela principal
        frame = new JFrame("Simulador");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximiza a janela
        frame.setMinimumSize(new Dimension(800, 600)); // Define o tamanho mínimo da janela

        // Configurando o layout principal
        frame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Criando os painéis
        JPanel inputsPanel = new JPanel();
        inputsPanel.setBorder(BorderFactory.createTitledBorder("Inputs"));

        JPanel outputsPanel = new JPanel();
        outputsPanel.setBorder(BorderFactory.createTitledBorder("Outputs"));

        JPanel memoryPanel = new JPanel();
        memoryPanel.setBorder(BorderFactory.createTitledBorder("Memória"));
        memoryArea = new JTextArea(20, 20); 
        memoryArea.setEditable(false);      
        memoryPanel.add(new JScrollPane(memoryArea));
        memoryPanel.add(new JLabel("Ultima instrução executada:"));
        currentInstructionField = new JTextField(10);
        currentInstructionField.setEditable(false);
        memoryPanel.add(currentInstructionField);

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

        // Botões 
        c.insets = new Insets(10, 10, 10, 10); // Espaçamento entre os componentes
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        c.weightx = 0.1;
        c.weighty = 0.1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        frame.add(runButton, c);

        c.gridy = 1;
        frame.add(stepButton, c);

        c.gridy = 2;
        frame.add(cleanButton, c);

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
                vm.run();  
                updateInterface();  
            }
        });

        // Adicionando ação ao botão "Step"
        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vm.executeInstruction();  
                updateInterface();  
            }
        });

        cleanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Reiniciar a VM sem reatribuir vm
                vm.clear();  
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
            case 0 -> "BR";
            case 5 -> "BRNEG";
            case 1 -> "BRPOS";
            case 4 -> "BRZERO";
            case 15 -> "CALL";
            case 13 -> "COPY";
            case 10 -> "DIVIDE";
            case 3 -> "LOAD";
            case 14 -> "MULT";
            case 12 -> "READ";
            case 16 -> "RET";
            case 11 -> "STOP";
            case 7 -> "STORE";
            case 6 -> "SUB";
            case 8 -> "WRITE";
            default -> "Unknown Operation";
        }; 
    }
    private void updateInterface() {
        accField.setText(String.valueOf(vm.getAcc()));
        pcField.setText(String.valueOf(vm.getPc()));
        spField.setText(String.valueOf(vm.getSp()));
        mopField.setText(String.valueOf(vm.getMop()));
        reField.setText(String.valueOf(vm.getRe()));
        riField.setText(String.valueOf(vm.getRi()));
        // Ta mostrando a ultima instrução executada, não sei se mantemos assim depois ou se mudamos para mostrar a que será executada depois, da pra mostrar as 2 tb
        // Ta meio cagada essa função ai retornando a string com o nome nao sei se gostei da solução q eu fiz, deve ter um jeito melhor
        // o endereço da execução ta completamente errado, tem que ver como puxar o endereço certinho, eu acho que tem que mexer na VM
        currentInstructionField.setText(getCurrentInstruction() + " " + String.valueOf(vm.getRe()));
        StringBuilder memoryContent = new StringBuilder();
        for (int i = 0; i < 20; i++) {  
            memoryContent.append("Memória[").append(i).append("] = ")
                         .append(vm.getMemoryValue(i)).append("\n");
        }
        memoryArea.setText(memoryContent.toString());
    }
}