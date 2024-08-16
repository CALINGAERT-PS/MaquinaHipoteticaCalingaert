import javax.swing.*;
import java.awt.*;

public class InterfaceGrafica {
    public static void main(String[] args) {
        // Criando a janela principal
        JFrame frame = new JFrame("Simulador");
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

        JPanel registersPanel = new JPanel();
        registersPanel.setBorder(BorderFactory.createTitledBorder("Registradores"));

        // Criando os botões
        JButton runButton = new JButton("Run");
        JButton stepButton = new JButton("Step");
        JButton cleanButton = new JButton("Clean");


        c.insets = new Insets(10, 10, 10, 10); // Espaçamento entre os componentes

        // Painel dos Inputs
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 2;
        c.weightx = 0.3;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        frame.add(inputsPanel, c);

        // Painel da Mémoria
        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 2;
        c.weightx = 0.3;
        c.fill = GridBagConstraints.BOTH;
        frame.add(memoryPanel, c);

        // Botões 
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

        // Outputs do painel
        c.gridx = 0;
        c.gridy = 2;
        c.gridheight = 2;
        c.weightx = 0.3;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        frame.add(outputsPanel, c);

        // Painel dos registradores
        c.gridx = 2;
        c.gridy = 2;
        c.gridwidth = 1;
        c.weightx = 0.3;
        c.weighty = 0.3;
        c.fill = GridBagConstraints.BOTH;
        frame.add(registersPanel, c);

        // Exibindo a janela
        frame.setVisible(true);
    }
}
