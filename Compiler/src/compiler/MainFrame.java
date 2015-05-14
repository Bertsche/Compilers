package compiler;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.JTabbedPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * I created this frame for the purpose of easily displaying the images of the generated trees and symbol tables.
 * This also allows for easily testing a bunch of files, by copying and pasting the test file into the box.
 * This is now the main caller for the compiler. The source is compiled when the button on the main tab is pressed.
 * Each time the button is pressed, the source is read and then everything is done automatically.  Many different sources van be trested
 * without closing the frame, just put the new source in, and press the button
 */
public class MainFrame extends JFrame {

	private JPanel contentPane;
	private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private final JPanel panel = new JPanel();
	private final JPanel panel_1 = new JPanel();
	private final JPanel panel_2 = new JPanel();
	private final JPanel panel_3 = new JPanel();
	private final JTextArea inputPane = new JTextArea();
	private final JTextArea outputPane = new JTextArea();
	private final JScrollPane scrollPane = new JScrollPane();
	private final JButton btnCompile = new JButton("Do Everything");
	private final JLabel lblAstImage = new JLabel();
	private final JLabel lblCstImage = new JLabel();
	private final JLabel lblSttImage = new JLabel();
	private final JScrollPane scrollPane_1 = new JScrollPane();
	private final JScrollPane outPutScroll = new JScrollPane();
	private final JScrollPane scrollPane_2 = new JScrollPane();
	private final JScrollPane scrollPane_3 = new JScrollPane();
	private final JLabel lblInput = new JLabel("Input");
	private final JTextArea errorPane = new JTextArea();
	private final JLabel lblConsoleOutput = new JLabel("Console Output");
	private final JTextArea outptPane = new JTextArea();
	private final JLabel lblGeneratedCode = new JLabel("Generated Code");
	private final JScrollPane scrollPane_4 = new JScrollPane();
	private final JScrollPane scrollPane_5 = new JScrollPane();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		jbInit();
	}
	private void jbInit() {
		redirectSysOutput();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setTitle("Bertsche Compiler");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1044, 1142);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		tabbedPane.setBounds(12, 34, 1002, 830);
		
		contentPane.add(tabbedPane);
		
		tabbedPane.addTab("Main Page", null, panel, null);
		panel.setLayout(null);
		scrollPane.setBounds(12, 36, 539, 427);
		//outPutScroll.setBounds(88, 575, 541, 225);
		
		panel.add(scrollPane);
		//panel.add(outPutScroll);
		inputPane.setTabSize(2);
		scrollPane.setViewportView(inputPane);
		//outPutScroll.setViewportView(outputPane);
		//outPutScroll.getVerticalScrollBar().setUnitIncrement(16);
		btnCompile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					do_btnCompile_actionPerformed(arg0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		btnCompile.setBounds(144, 486, 234, 25);
		
		panel.add(btnCompile);
		lblInput.setBounds(12, 9, 119, 15);
		
		panel.add(lblInput);
		scrollPane_5.setBounds(12, 603, 973, 188);
		
		panel.add(scrollPane_5);
		scrollPane_5.setViewportView(errorPane);
		lblConsoleOutput.setBounds(12, 566, 206, 25);
		
		panel.add(lblConsoleOutput);
		scrollPane_4.setBounds(621, 37, 364, 426);
		scrollPane_4.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		outptPane.setLineWrap(true);

		panel.add(scrollPane_4);
		scrollPane_4.setViewportView(outptPane);
		outptPane.setLineWrap(true);

		lblGeneratedCode.setBounds(621, 9, 247, 25);
		
		panel.add(lblGeneratedCode);
		
		tabbedPane.addTab("CST", null, panel_1, null);
		panel_1.setLayout(null);
		panel_1.add(scrollPane_2);
		scrollPane_2.setBounds(12, 26, 973, 765);
		scrollPane_2.setViewportView(lblCstImage);
		scrollPane_2.getHorizontalScrollBar().setUnitIncrement(16);
		scrollPane_2.getVerticalScrollBar().setUnitIncrement(16);
		lblCstImage.setVisible(true);

		tabbedPane.addTab("AST", null, panel_2, null);
		panel_2.setLayout(null);
		panel_2.add(scrollPane_1);
		scrollPane_1.setBounds(12, 26, 973, 765);
		scrollPane_1.setViewportView(lblAstImage);
		scrollPane_1.getHorizontalScrollBar().setUnitIncrement(16);
		scrollPane_1.getVerticalScrollBar().setUnitIncrement(16);
	    lblAstImage.setVisible(true);

		tabbedPane.addTab("STT", null, panel_3, null);
		panel_3.setLayout(null);
		panel_3.add(scrollPane_3);
		scrollPane_3.setBounds(12, 26, 973, 765);
		scrollPane_3.setViewportView(lblSttImage);
		scrollPane_3.getHorizontalScrollBar().setUnitIncrement(16);
		scrollPane_3.getVerticalScrollBar().setUnitIncrement(16);
		lblSttImage.setVisible(true);


		
	}

	private void updateTextArea(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				errorPane.append(text);
			}
		});
	}

	private void redirectSysOutput() {
		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				updateTextArea(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				updateTextArea(new String(b, off, len));
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};

		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(out, true));
	}

	/**
	 * This is now the main caller for a the compiler.  It does all the work when this button action is triggered
	 * @param arg0
	 * @throws Exception
	 */
	protected void do_btnCompile_actionPerformed(ActionEvent arg0) throws Exception {
		//Strings for the dot files for each tree
		String asTreeDot = "";
		String csTreeDot = "";
		String stTreeDot = "";
		//This is the helper class from the internet that does what graphviz does, but in java
		GraphViz dotMaker = new GraphViz();
		//byte files that will hold the image as a byte array
		byte[] cstAsByteFile;
	    byte[] astAsByteFile;
		byte[] sttAsByteFile;
	    
	    //byte[] tester123;
        // lexes and returns token list from text in input pane in frame
        ArrayList<Token> tokens = Lexer.lex(inputPane.getText());

        System.out.println("Congratulations, you successfully lexed with no errors");
			//creates and runs parser for tokens returned by lex
			Parser myParser = new Parser(tokens);

			//Gets gets the ast and cst from the parser
			Tree renderASTree = myParser.getAST();
			Tree renderCSTree = myParser.getCST();

			//runs the semantic analysis from the retrieved ast
			SemanticAnalysis sa = new SemanticAnalysis(renderASTree);

			//run code gen and print it to output
			MachineCodeGenerator codeGen = new MachineCodeGenerator(renderASTree, sa.stt);


			//get dot files for all of the trees
			csTreeDot = renderCSTree.toDot();
			asTreeDot = renderASTree.toDot();
			stTreeDot = sa.toDot();

			



      
      //System.out.print(stTreeDot);
      
      //Testing section of Graph maker
      /*
      String testDotString = "graph { \n \"027d9b27-2b7b-4858-a186-66a50f5761c1\" -- b \n b -- c \n  a -- d [color=blue] \n d[label=\"test\"]} ";
      tester123 = dotMaker.getGraph(testDotString, "gif", "dot");
      ImageIcon cstTestPic = new ImageIcon(tester123);
      lblAstImage.setIcon(cstTestPic);
      System.out.println(Arrays.toString(tester123));
      */
      //Actual Section of graph maker

		//runs the graphiz to get byte files from all the dot files
      cstAsByteFile = dotMaker.getGraph(csTreeDot, "gif", "dot");
      astAsByteFile = dotMaker.getGraph(asTreeDot, "gif", "dot");
		sttAsByteFile = dotMaker.getGraph(stTreeDot, "gif", "dot");

		//assigns the byte files to the image icons
      ImageIcon cstPicture = new ImageIcon(cstAsByteFile);
		ImageIcon astPicture = new ImageIcon(astAsByteFile);
		ImageIcon sstPicture = new ImageIcon(sttAsByteFile);

		//puts the image in the label to be displayed in the frame
      lblCstImage.setIcon(cstPicture);
		lblAstImage.setIcon(astPicture);
		lblSttImage.setIcon(sstPicture);
   
      //System.out.println(Arrays.toString(cstAsByteFile));
		String hexCode = codeGen.getOutput();

		System.out.println(hexCode);
		outptPane.append(hexCode);

      
      
      
	}
}
