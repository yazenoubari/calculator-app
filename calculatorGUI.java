package oubari.calculator_app;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

/**
 * @Oubari, January 2021
 * Calculator App. Replicated the functions of the MacOS calculator App using Java swing.
 * Used the NetBeans design ability for the visuals.
 */
public class calculatorGUI extends javax.swing.JFrame {
    
    private final ArrayList<String> fullEquation = new ArrayList<String>();

    public calculatorGUI() {
        initComponents();
        getContentPane().setBackground(Color.decode("0X393a3b"));
    }
    
    public int setFontSize() {
        int textFieldSize = jTextField1.getText().length();
        
        if (textFieldSize > 8 && textFieldSize < 35) {
            
            return (int)(Math.floor(Math.pow(0.8502, (textFieldSize - 29.027)) + 17.6433));
        }
        
        if (textFieldSize <= 8)
            return 48;
        else
            return 17;
    }
    
    public void divide() {
        if (!fullEquation.contains("/")) {
            fullEquation.add(jTextField1.getText());
            fullEquation.add("/");
            jTextField1.setText("");
        }
    }
    
    public void multiply() {
        if (!fullEquation.contains("*")) {
        fullEquation.add(jTextField1.getText());
        fullEquation.add("*");
        jTextField1.setText("");
        }
    }
    
    public void subtract() {
        if (!fullEquation.contains("-")) {
        fullEquation.add(jTextField1.getText());
        fullEquation.add("-");
        jTextField1.setText("");
        }
    }
    
    public void add() {
        if (!fullEquation.contains("+")) {
        fullEquation.add(jTextField1.getText());
        fullEquation.add("+");
        jTextField1.setText("");
        }
    }
    
    public void percent() {
        if (!fullEquation.contains("/")) {
        fullEquation.add(jTextField1.getText());
        fullEquation.add("/");
        fullEquation.add("100");
        jTextField1.setText("");
        }
    }
    
    public void plusMinus() {
        if ((!jTextField1.getText().isEmpty()) && (!jTextField1.getText().matches("0"))) {
            if (jTextField1.getText().startsWith("-")) {
                jTextField1.setText(jTextField1.getText().replace("-", ""));
            } else
                jTextField1.setText("-" + jTextField1.getText());
        }
    }
    
    public void point() {
        if (jTextField1.getText().isEmpty() && !jTextField1.getText().contains("."))
            jTextField1.setText("0.");
        else if (!jTextField1.getText().contains("."))
            jTextField1.setText(jTextField1.getText() + ".");
    }
    
    public void enter() {
        try {
            solution();
        } catch (ScriptException ex) {
                Logger.getLogger(calculatorGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //jTextField1.setText(fullEquation.toString());
        
        fullEquation.removeAll(fullEquation);
    }
    
    public void clear() {
        jTextField1.setText("");
    }
    
    public static double eval(final String str) {
    return new Object() {
        int pos = -1, ch;

        void nextChar() {
            ch = (++pos < str.length()) ? str.charAt(pos) : -1;
        }

        boolean eat(int charToEat) {
            while (ch == ' ') nextChar();
            if (ch == charToEat) {
                nextChar();
                return true;
            }
            return false;
        }

        double parse() {
            nextChar();
            double x = parseExpression();
            if (pos < str.length()) 
                throw new RuntimeException("Unexpected: " + (char)ch);
            return x;
        }

        double parseExpression() {
            double x = parseTerm();
            for (;;) {
                if      (eat('+')) x += parseTerm(); // addition
                else if (eat('-')) x -= parseTerm(); // subtraction
                else return x;
            }
        }

        double parseTerm() {
            double x = parseFactor();
            for (;;) {
                if (eat('*')) 
                    x *= parseFactor(); // multiplication
                else if (eat('/')) 
                    x /= parseFactor(); // division
                else 
                    return x;
            }
        }

        double parseFactor() {
            if (eat('+')) return parseFactor(); // unary plus
            if (eat('-')) return -parseFactor(); // unary minus

            double x;
            int startPos = this.pos;
            if (eat('(')) { // parentheses
                x = parseExpression();
                eat(')');
            } 
            if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                x = Double.parseDouble(str.substring(startPos, this.pos));
            } else if (ch >= 'a' && ch <= 'z') { // functions
                while (ch >= 'a' && ch <= 'z') nextChar();
                String func = str.substring(startPos, this.pos);
                x = parseFactor();
                if (func.equals("sqrt")) x = Math.sqrt(x);
                else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                else throw new RuntimeException("Unknown function: " + func);
            } else {
                throw new RuntimeException("Unexpected: " + (char)ch);
            }

            if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

            return x;
        }
    }.parse();
}
    
    public int getNumberOfDecimalPlaces(BigDecimal bigDecimal) {
        return Math.max(0, bigDecimal.stripTrailingZeros().scale());
    }
    
    private static double round(double value) {

        String doubleToString = Double.toString(Math.abs(value));
        int integerPlaces = doubleToString.indexOf(".");
        
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale((10-integerPlaces), RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    public void solution() throws ScriptException {
        
        fullEquation.add(jTextField1.getText());
        
        double solution = eval((String.join("", fullEquation)));

        String rounded = Double.toString(round(solution));
        
        //String rounded = String.format("%,d", round(solution));
        
        if (rounded.endsWith(".0")) {
            jTextField1.setText(rounded.replace(".0", ""));
            jTextField1.setFont(new java.awt.Font("Microsoft JhengHei UI", 0, setFontSize()));
        } else {
            jTextField1.setText(rounded);
            jTextField1.setFont(new java.awt.Font("Microsoft JhengHei UI", 0, setFontSize()));
        }
    }
    
    public void CloseFrame() {
        super.dispose();
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        divisionButton = new javax.swing.JButton();
        multiplicationButton = new javax.swing.JButton();
        additionButton = new javax.swing.JButton();
        subtractionButton = new javax.swing.JButton();
        equalsButton = new javax.swing.JButton();
        sixButton = new javax.swing.JButton();
        percentButton = new javax.swing.JButton();
        nineButton = new javax.swing.JButton();
        threeButton = new javax.swing.JButton();
        pointButton = new javax.swing.JButton();
        fiveButton = new javax.swing.JButton();
        plusMinusButton = new javax.swing.JButton();
        eightButton = new javax.swing.JButton();
        twoButton = new javax.swing.JButton();
        fourButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        sevenButton = new javax.swing.JButton();
        oneButton = new javax.swing.JButton();
        zeroButton = new javax.swing.JButton();
        clearButton1 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Oubari.Calculator");
        setAlwaysOnTop(true);
        setBackground(new java.awt.Color(55, 57, 58));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setForeground(new java.awt.Color(55, 57, 58));
        setIconImages(null);
        setLocation(new java.awt.Point(500, 250));
        setResizable(false);
        setType(java.awt.Window.Type.UTILITY);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        jTextField1.setEditable(false);
        jTextField1.setBackground(new java.awt.Color(57, 58, 59));
        jTextField1.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 48)); // NOI18N
        jTextField1.setForeground(new java.awt.Color(255, 255, 255));
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField1.setToolTipText("");
        jTextField1.setBorder(null);
        jTextField1.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                jTextField1InputMethodTextChanged(evt);
            }
        });
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
        });

        divisionButton.setBackground(new java.awt.Color(255, 155, 0));
        divisionButton.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        divisionButton.setForeground(new java.awt.Color(255, 255, 255));
        divisionButton.setText("÷");
        divisionButton.setToolTipText("Divide");
        divisionButton.setBorderPainted(false);
        divisionButton.setFocusable(false);
        divisionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                divisionButtonActionPerformed(evt);
            }
        });

        multiplicationButton.setBackground(new java.awt.Color(255, 155, 0));
        multiplicationButton.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        multiplicationButton.setForeground(new java.awt.Color(255, 255, 255));
        multiplicationButton.setText("×");
        multiplicationButton.setToolTipText("Multiply");
        multiplicationButton.setBorderPainted(false);
        multiplicationButton.setFocusable(false);
        multiplicationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                multiplicationButtonActionPerformed(evt);
            }
        });

        additionButton.setBackground(new java.awt.Color(255, 155, 0));
        additionButton.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        additionButton.setForeground(new java.awt.Color(255, 255, 255));
        additionButton.setText("+");
        additionButton.setToolTipText("Add");
        additionButton.setBorderPainted(false);
        additionButton.setFocusable(false);
        additionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                additionButtonActionPerformed(evt);
            }
        });

        subtractionButton.setBackground(new java.awt.Color(255, 155, 0));
        subtractionButton.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        subtractionButton.setForeground(new java.awt.Color(255, 255, 255));
        subtractionButton.setText("-");
        subtractionButton.setToolTipText("Subtract");
        subtractionButton.setBorderPainted(false);
        subtractionButton.setFocusable(false);
        subtractionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subtractionButtonActionPerformed(evt);
            }
        });

        equalsButton.setBackground(new java.awt.Color(255, 155, 0));
        equalsButton.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        equalsButton.setForeground(new java.awt.Color(255, 255, 255));
        equalsButton.setText("=");
        equalsButton.setToolTipText("Equal");
        equalsButton.setBorderPainted(false);
        equalsButton.setFocusable(false);
        equalsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                equalsButtonActionPerformed(evt);
            }
        });

        sixButton.setBackground(new java.awt.Color(114, 114, 115));
        sixButton.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        sixButton.setForeground(new java.awt.Color(255, 255, 255));
        sixButton.setText("6");
        sixButton.setBorderPainted(false);
        sixButton.setFocusable(false);
        sixButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sixButtonActionPerformed(evt);
            }
        });

        percentButton.setBackground(new java.awt.Color(74, 75, 76));
        percentButton.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        percentButton.setForeground(new java.awt.Color(255, 255, 255));
        percentButton.setText("%");
        percentButton.setToolTipText("Percent");
        percentButton.setBorderPainted(false);
        percentButton.setFocusable(false);
        percentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                percentButtonActionPerformed(evt);
            }
        });

        nineButton.setBackground(new java.awt.Color(114, 114, 115));
        nineButton.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        nineButton.setForeground(new java.awt.Color(255, 255, 255));
        nineButton.setText("9");
        nineButton.setBorderPainted(false);
        nineButton.setFocusable(false);
        nineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nineButtonActionPerformed(evt);
            }
        });

        threeButton.setBackground(new java.awt.Color(114, 114, 115));
        threeButton.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        threeButton.setForeground(new java.awt.Color(255, 255, 255));
        threeButton.setText("3");
        threeButton.setBorderPainted(false);
        threeButton.setFocusable(false);
        threeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                threeButtonActionPerformed(evt);
            }
        });

        pointButton.setBackground(new java.awt.Color(114, 114, 115));
        pointButton.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        pointButton.setForeground(new java.awt.Color(255, 255, 255));
        pointButton.setText(".");
        pointButton.setBorderPainted(false);
        pointButton.setFocusable(false);
        pointButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pointButtonActionPerformed(evt);
            }
        });

        fiveButton.setBackground(new java.awt.Color(114, 114, 115));
        fiveButton.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        fiveButton.setForeground(new java.awt.Color(255, 255, 255));
        fiveButton.setText("5");
        fiveButton.setBorderPainted(false);
        fiveButton.setFocusable(false);
        fiveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fiveButtonActionPerformed(evt);
            }
        });

        plusMinusButton.setBackground(new java.awt.Color(74, 75, 76));
        plusMinusButton.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        plusMinusButton.setForeground(new java.awt.Color(255, 255, 255));
        plusMinusButton.setText("+/-");
        plusMinusButton.setToolTipText("Negate the displayed value (or press Alt [-])");
        plusMinusButton.setBorderPainted(false);
        plusMinusButton.setFocusable(false);
        plusMinusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plusMinusButtonActionPerformed(evt);
            }
        });

        eightButton.setBackground(new java.awt.Color(114, 114, 115));
        eightButton.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        eightButton.setForeground(new java.awt.Color(255, 255, 255));
        eightButton.setText("8");
        eightButton.setBorderPainted(false);
        eightButton.setFocusable(false);
        eightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eightButtonActionPerformed(evt);
            }
        });

        twoButton.setBackground(new java.awt.Color(114, 114, 115));
        twoButton.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        twoButton.setForeground(new java.awt.Color(255, 255, 255));
        twoButton.setText("2");
        twoButton.setBorderPainted(false);
        twoButton.setFocusable(false);
        twoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                twoButtonActionPerformed(evt);
            }
        });

        fourButton.setBackground(new java.awt.Color(114, 114, 115));
        fourButton.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        fourButton.setForeground(new java.awt.Color(255, 255, 255));
        fourButton.setText("4");
        fourButton.setBorderPainted(false);
        fourButton.setFocusable(false);
        fourButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fourButtonActionPerformed(evt);
            }
        });

        clearButton.setBackground(new java.awt.Color(74, 75, 76));
        clearButton.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        clearButton.setForeground(new java.awt.Color(255, 255, 255));
        clearButton.setText("AC");
        clearButton.setToolTipText("Clear (esc)");
        clearButton.setBorderPainted(false);
        clearButton.setFocusable(false);
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        sevenButton.setBackground(new java.awt.Color(114, 114, 115));
        sevenButton.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        sevenButton.setForeground(new java.awt.Color(255, 255, 255));
        sevenButton.setText("7");
        sevenButton.setBorderPainted(false);
        sevenButton.setFocusable(false);
        sevenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sevenButtonActionPerformed(evt);
            }
        });
        sevenButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                sevenButtonKeyPressed(evt);
            }
        });

        oneButton.setBackground(new java.awt.Color(114, 114, 115));
        oneButton.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        oneButton.setForeground(new java.awt.Color(255, 255, 255));
        oneButton.setText("1");
        oneButton.setBorderPainted(false);
        oneButton.setFocusable(false);
        oneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oneButtonActionPerformed(evt);
            }
        });
        oneButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                oneButtonKeyPressed(evt);
            }
        });

        zeroButton.setBackground(new java.awt.Color(114, 114, 115));
        zeroButton.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        zeroButton.setForeground(new java.awt.Color(255, 255, 255));
        zeroButton.setText("0");
        zeroButton.setBorderPainted(false);
        zeroButton.setDefaultCapable(false);
        zeroButton.setFocusable(false);
        zeroButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zeroButtonActionPerformed(evt);
            }
        });
        zeroButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                zeroButtonKeyPressed(evt);
            }
        });

        clearButton1.setBackground(new java.awt.Color(57, 58, 59));
        clearButton1.setFont(new java.awt.Font("Tahoma", 2, 10)); // NOI18N
        clearButton1.setForeground(new java.awt.Color(255, 255, 255));
        clearButton1.setText("Copy");
        clearButton1.setToolTipText("Copy to clipboard");
        clearButton1.setFocusable(false);
        clearButton1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                clearButton1FocusLost(evt);
            }
        });
        clearButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clearButton1MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                clearButton1MouseExited(evt);
            }
        });
        clearButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButton1ActionPerformed(evt);
            }
        });

        jMenu1.setText("File");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem1.setText("Close");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);
        jMenu1.add(jSeparator2);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem2.setText("Copy");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem3.setText("Paste");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField1)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(clearButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sevenButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fourButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(oneButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(plusMinusButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(eightButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fiveButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(twoButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(zeroButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(percentButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nineButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sixButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(threeButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pointButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(clearButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(divisionButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
                    .addComponent(multiplicationButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
                    .addComponent(subtractionButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
                    .addComponent(additionButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
                    .addComponent(equalsButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(clearButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(divisionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(multiplicationButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(subtractionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(additionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(equalsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(percentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(nineButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(sixButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(threeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(pointButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(plusMinusButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(eightButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(fiveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(twoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(clearButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(sevenButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(fourButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(oneButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, 0)
                        .addComponent(zeroButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0))
        );

        getAccessibleContext().setAccessibleDescription("@Oubari 2021");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void oneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oneButtonActionPerformed
        jTextField1.setText(jTextField1.getText() + "1");
        
        jTextField1.setFont(new java.awt.Font("Microsoft JhengHei UI", 0, setFontSize()));
    }//GEN-LAST:event_oneButtonActionPerformed

    private void twoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_twoButtonActionPerformed
        jTextField1.setText(jTextField1.getText() + "2");
        
        jTextField1.setFont(new java.awt.Font("Microsoft JhengHei UI", 0, setFontSize()));
    }//GEN-LAST:event_twoButtonActionPerformed

    private void threeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_threeButtonActionPerformed
        jTextField1.setText(jTextField1.getText() + "3");
        
        jTextField1.setFont(new java.awt.Font("Microsoft JhengHei UI", 0, setFontSize()));
    }//GEN-LAST:event_threeButtonActionPerformed

    private void fourButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fourButtonActionPerformed
        jTextField1.setText(jTextField1.getText() + "4");
        
        jTextField1.setFont(new java.awt.Font("Microsoft JhengHei UI", 0, setFontSize()));
    }//GEN-LAST:event_fourButtonActionPerformed

    private void fiveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fiveButtonActionPerformed
        jTextField1.setText(jTextField1.getText() + "5");
        
        jTextField1.setFont(new java.awt.Font("Microsoft JhengHei UI", 0, setFontSize()));
    }//GEN-LAST:event_fiveButtonActionPerformed

    private void sixButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sixButtonActionPerformed
        jTextField1.setText(jTextField1.getText() + "6");
        
        jTextField1.setFont(new java.awt.Font("Microsoft JhengHei UI", 0, setFontSize()));
    }//GEN-LAST:event_sixButtonActionPerformed

    private void sevenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sevenButtonActionPerformed
        jTextField1.setText(jTextField1.getText() + "7");
        
        jTextField1.setFont(new java.awt.Font("Microsoft JhengHei UI", 0, setFontSize()));
    }//GEN-LAST:event_sevenButtonActionPerformed

    private void eightButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eightButtonActionPerformed
        jTextField1.setText(jTextField1.getText() + "8");
        
        jTextField1.setFont(new java.awt.Font("Microsoft JhengHei UI", 0, setFontSize()));
    }//GEN-LAST:event_eightButtonActionPerformed

    private void nineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nineButtonActionPerformed
        jTextField1.setText(jTextField1.getText() + "9");
        
        jTextField1.setFont(new java.awt.Font("Microsoft JhengHei UI", 0, setFontSize()));
    }//GEN-LAST:event_nineButtonActionPerformed

    private void zeroButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zeroButtonActionPerformed
        if (!jTextField1.getText().isEmpty())
            jTextField1.setText(jTextField1.getText() + "0");
        
        jTextField1.setFont(new java.awt.Font("Microsoft JhengHei UI", 0, setFontSize()));
    }//GEN-LAST:event_zeroButtonActionPerformed

    private void pointButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pointButtonActionPerformed
        point();
        
        jTextField1.setFont(new java.awt.Font("Microsoft JhengHei UI", 0, setFontSize()));
    }//GEN-LAST:event_pointButtonActionPerformed

    private void divisionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_divisionButtonActionPerformed
        divide();
    }//GEN-LAST:event_divisionButtonActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        clear();
    }//GEN-LAST:event_clearButtonActionPerformed

    private void plusMinusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plusMinusButtonActionPerformed
        plusMinus();
    }//GEN-LAST:event_plusMinusButtonActionPerformed

    private void multiplicationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multiplicationButtonActionPerformed
        multiply();
    }//GEN-LAST:event_multiplicationButtonActionPerformed

    private void subtractionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subtractionButtonActionPerformed
        subtract();
    }//GEN-LAST:event_subtractionButtonActionPerformed

    private void additionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_additionButtonActionPerformed
        add();
    }//GEN-LAST:event_additionButtonActionPerformed

    private void equalsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_equalsButtonActionPerformed
        enter();
    }//GEN-LAST:event_equalsButtonActionPerformed

    private void percentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_percentButtonActionPerformed
        percent();
    }//GEN-LAST:event_percentButtonActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
    
        Action plusMinusAction = new AbstractAction("Plus-minus") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Plus-minus");
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        
        plusMinusAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_DOWN_MASK));
        
        plusMinusButton.setAction(plusMinusAction);
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void sevenButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sevenButtonKeyPressed

    }//GEN-LAST:event_sevenButtonKeyPressed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed

    }//GEN-LAST:event_formKeyPressed

    private void zeroButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_zeroButtonKeyPressed

    }//GEN-LAST:event_zeroButtonKeyPressed

    private void oneButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_oneButtonKeyPressed
        
    }//GEN-LAST:event_oneButtonKeyPressed

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
    
        jTextField1.setFont(new java.awt.Font("Microsoft JhengHei UI", 0, setFontSize()));
        
        int keyCode = evt.getKeyCode();

        switch(keyCode) {
            case KeyEvent.VK_0:
                if (!jTextField1.getText().isEmpty()) {
                    jTextField1.setText(jTextField1.getText() + "0");
                }
                break;
            case KeyEvent.VK_1:
                jTextField1.setText(jTextField1.getText() + "1");
                break;
            case KeyEvent.VK_2:
                jTextField1.setText(jTextField1.getText() + "2");
                break;
            case KeyEvent.VK_3:
                jTextField1.setText(jTextField1.getText() + "3");
                break;
            case KeyEvent.VK_4:
                jTextField1.setText(jTextField1.getText() + "4");
                break;
            case KeyEvent.VK_5:
                jTextField1.setText(jTextField1.getText() + "5");
                break;
            case KeyEvent.VK_6:
                jTextField1.setText(jTextField1.getText() + "6");
                break;
            case KeyEvent.VK_7:
                jTextField1.setText(jTextField1.getText() + "7");
                break;
            case KeyEvent.VK_8:
                jTextField1.setText(jTextField1.getText() + "8");
                break;
            case KeyEvent.VK_9:
                jTextField1.setText(jTextField1.getText() + "9");
                break;
            case KeyEvent.VK_DIVIDE:
                divide();
                break;
            case KeyEvent.VK_MULTIPLY:
                multiply();
                break;
            case KeyEvent.VK_SUBTRACT:
                subtract();
               break; 
            case KeyEvent.VK_ADD:
                add();
                break;
            case KeyEvent.VK_PERIOD:
                point();
                break;
            case KeyEvent.VK_ENTER:
                enter();
                break;
            case KeyEvent.VK_ESCAPE:
                clear();
        }
    }//GEN-LAST:event_jTextField1KeyPressed

    private void clearButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButton1ActionPerformed
        StringSelection stringSelection = new StringSelection(jTextField1.getText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        
        clearButton.requestFocus();
    }//GEN-LAST:event_clearButton1ActionPerformed

    private void clearButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearButton1MouseClicked
        clearButton1.setBackground(Color.decode("0X3a9625"));
    }//GEN-LAST:event_clearButton1MouseClicked

    private void clearButton1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_clearButton1FocusLost

    }//GEN-LAST:event_clearButton1FocusLost

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        CloseFrame();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        clearButton1ActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable t = clipboard.getContents(this);
        
        if (t == null)
            return;
        
        String initialPaste = null;
        
        try {
            initialPaste = (String) t.getTransferData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException ex) {
            Logger.getLogger(calculatorGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String replaceString = initialPaste.replaceAll("[^\\d-+*/.]", "");
        
        try {
            jTextField1.setText(replaceString);
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jTextField1InputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_jTextField1InputMethodTextChanged

    }//GEN-LAST:event_jTextField1InputMethodTextChanged

    private void clearButton1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearButton1MouseExited
        clearButton1.setBackground(Color.decode("0X393a3b"));
    }//GEN-LAST:event_clearButton1MouseExited

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(calculatorGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(calculatorGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(calculatorGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(calculatorGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new calculatorGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton additionButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JButton clearButton1;
    private javax.swing.JButton divisionButton;
    private javax.swing.JButton eightButton;
    private javax.swing.JButton equalsButton;
    private javax.swing.JButton fiveButton;
    private javax.swing.JButton fourButton;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton multiplicationButton;
    private javax.swing.JButton nineButton;
    private javax.swing.JButton oneButton;
    private javax.swing.JButton percentButton;
    private javax.swing.JButton plusMinusButton;
    private javax.swing.JButton pointButton;
    private javax.swing.JButton sevenButton;
    private javax.swing.JButton sixButton;
    private javax.swing.JButton subtractionButton;
    private javax.swing.JButton threeButton;
    private javax.swing.JButton twoButton;
    private javax.swing.JButton zeroButton;
    // End of variables declaration//GEN-END:variables
}
