package compiler;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.JTabbedPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private final JPanel panel = new JPanel();
	private final JPanel panel_1 = new JPanel();
	private final JPanel panel_2 = new JPanel();
	private final JPanel panel_3 = new JPanel();
	private final JEditorPane inputPane = new JEditorPane();
	private final JScrollPane scrollPane = new JScrollPane();
	private final JButton btnCompile = new JButton("Do Everything (So Far)");
	private final JLabel lblAstImage = new JLabel();
	private final JLabel lblCstImage = new JLabel();
	private final JLabel lblSttImage = new JLabel();
	private final JScrollPane scrollPane_1 = new JScrollPane();
	private final JScrollPane scrollPane_2 = new JScrollPane();
	private final JScrollPane scrollPane_3 = new JScrollPane();

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
		scrollPane.setBounds(88, 36, 541, 427);
		
		panel.add(scrollPane);
		scrollPane.setViewportView(inputPane);
		btnCompile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					do_btnCompile_actionPerformed(arg0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		btnCompile.setBounds(188, 516, 234, 25);
		
		panel.add(btnCompile);
		
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
	protected void do_btnCompile_actionPerformed(ActionEvent arg0) throws Exception {
		String asTreeDot = "";
		String csTreeDot = "";
		String stTreeDot = "";
		GraphViz dotMaker = new GraphViz();
		byte[] cstAsByteFile;
	    byte[] astAsByteFile;
		byte[] sttAsByteFile;
	    
	    byte[] tester123;
        // Create tokens and print them
        ArrayList<Token> tokens = Lexer.lex(inputPane.getText());

        System.out.println("Congratulations, you successfully lexed with no errors");

			Parser myParser = new Parser(tokens);
			Tree renderASTree = myParser.getAST();
			Tree renderCSTree = myParser.getCST();
			SemanticAnalysis sa = new SemanticAnalysis(renderASTree);
			csTreeDot = renderCSTree.toDot();
			asTreeDot = renderASTree.toDot();
			stTreeDot = sa.toDot();

			



      
      System.out.print(stTreeDot);
      
      //Testing section of Graph maker
      /*
      String testDotString = "graph { \n \"027d9b27-2b7b-4858-a186-66a50f5761c1\" -- b \n b -- c \n  a -- d [color=blue] \n d[label=\"test\"]} ";
      tester123 = dotMaker.getGraph(testDotString, "gif", "dot");
      ImageIcon cstTestPic = new ImageIcon(tester123);
      lblAstImage.setIcon(cstTestPic);
      System.out.println(Arrays.toString(tester123));
      */
      //Actual Section of graph maker
      
      cstAsByteFile = dotMaker.getGraph(csTreeDot, "gif", "dot");
      astAsByteFile = dotMaker.getGraph(asTreeDot, "gif", "dot");
		sttAsByteFile = dotMaker.getGraph(stTreeDot, "gif", "dot");
      
      ImageIcon cstPicture = new ImageIcon(cstAsByteFile);
		ImageIcon astPicture = new ImageIcon(astAsByteFile);
		ImageIcon sstPicture = new ImageIcon(sttAsByteFile);
      
      lblCstImage.setIcon(cstPicture);
		lblAstImage.setIcon(astPicture);
		lblSttImage.setIcon(sstPicture);
   
      //System.out.println(Arrays.toString(cstAsByteFile));


      
      
      
      
	}
}
