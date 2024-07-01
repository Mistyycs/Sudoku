import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AttributeSet;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SudokuGUI extends JFrame {
    private static final int SIZE = 9;
    private JTextField[][] cells = new JTextField[SIZE][SIZE];
    private JButton importButton, solveButton, verifyButton;
    private Sudoku sudoku;

class DigitFilter extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (isNumeric(string)) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (isNumeric(text)) {
            super.replace(fb, offset, length, text, attrs);
        }
    }

    private boolean isNumeric(String str) {
        return str != null && str.matches("[0-9]*");
    }
}


    public SudokuGUI() {
        setTitle("Sudoku Solver");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Grille Sudoku
        JPanel gridPanel = new JPanel(new GridLayout(SIZE, SIZE));
        Border defaultBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col] = new JTextField();
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                cells[row][col].setFont(new Font("Arial", Font.BOLD, 20));
                cells[row][col].setBorder(defaultBorder);

                int top = (row % 3 == 0) ? 2 : 1;
                int left = (col % 3 == 0) ? 2 : 1;
                int bottom = (row == SIZE - 1) ? 2 : 1;
                int right = (col == SIZE - 1) ? 2 : 1; 

                Border cellBorder = BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK);
                cells[row][col].setBorder(cellBorder);

                 ((AbstractDocument) cells[row][col].getDocument()).setDocumentFilter(new DigitFilter());

                gridPanel.add(cells[row][col]);
            }
        }
        add(gridPanel, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel();
        importButton = new JButton("Importer");
        solveButton = new JButton("Résoudre");
        verifyButton = new JButton("Vérifier");

        buttonPanel.add(importButton);
        buttonPanel.add(verifyButton);
        buttonPanel.add(solveButton);
        
        add(buttonPanel, BorderLayout.SOUTH);

        // Gestionnaires d'événements pour les boutons
        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importSudoku();
            }
        });

        verifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                verifySudoku();
            }
        });

        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                solveSudoku();
            }
        });
    }

    // Importer une grille à partir d'un fichier écrit comme ceci : 1002429007...
    public void importSudoku() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        sudoku = new Sudoku(SIZE);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String line = reader.readLine();
                if (line != null && line.length() == SIZE * SIZE) {
                    for (int row = 0; row < SIZE; row++) {
                        for (int col = 0; col < SIZE; col++) {
                            char c = line.charAt(row * SIZE + col);
                            if (c == '0') {
                                cells[row][col].setText("");
                                cells[row][col].setForeground(Color.BLACK);
                                sudoku.grid[row][col] = 0;
                            } else {
                                cells[row][col].setText(String.valueOf(c));
                                cells[row][col].setForeground(Color.BLUE);
                                sudoku.grid[row][col] = Character.getNumericValue(c);
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Le fichier sélectionné n'est pas valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la lecture du fichier.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void verifySudoku(){
        // Faut mettre à jour la grille avant le test
        if(sudoku.finish()){
            if(sudoku.verify()){
                JOptionPane.showMessageDialog(SudokuGUI.this, "C'est une solution correcte !", "Vérification", JOptionPane.INFORMATION_MESSAGE);
            }
            else{
                JOptionPane.showMessageDialog(SudokuGUI.this, "Cette proposition est invalide !", "Vérification", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        else{
            JOptionPane.showMessageDialog(SudokuGUI.this, "Le Sudoku n'est pas terminé !", "Vérification", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void solveSudoku() {
        SwingWorker<Void, int[]> worker = new SwingWorker<Void, int[]>() {
            @Override
            protected Void doInBackground() throws Exception {
                solveAndPublish(0, 0);
                return null;
            }

            private boolean solveAndPublish(int x, int y) {
                if (x == SIZE) {
                    return true;
                } else if (y == SIZE) {
                    return solveAndPublish(x + 1, 0);
                } else if (sudoku.grid[x][y] != 0) {
                    return solveAndPublish(x, y + 1);
                } else {
                    for (int i = 1; i <= SIZE; i++) {
                        if (sudoku.is_valid(x, y, i)) {
                            sudoku.grid[x][y] = i;
                            publish(new int[]{x, y, i});
                            try {
                                Thread.sleep(2); // Pour voir la mise à jour en temps réel
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (solveAndPublish(x, y + 1)) {
                                return true;
                            }
                            sudoku.grid[x][y] = 0;
                            publish(new int[]{x, y, 0});
                        }
                    }
                    return false;
                }
            }

            @Override
            protected void process(java.util.List<int[]> chunks) {
                for (int[] chunk : chunks) {
                    int row = chunk[0];
                    int col = chunk[1];
                    int value = chunk[2];
                    cells[row][col].setText(value == 0 ? "" : String.valueOf(value));
                }
            }

            @Override
            protected void done() {
                if (!sudoku.finish()) {
                    JOptionPane.showMessageDialog(SudokuGUI.this, "Impossible de résoudre ce Sudoku.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
                sudoku.affichage();
            }
        };
        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SudokuGUI().setVisible(true);
            }
        });
    }
}
