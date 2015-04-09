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
	private final JEditorPane inputPane = new JEditorPane();
	private final JScrollPane scrollPane = new JScrollPane();
	private final JButton btnCompile = new JButton("Do Everything (So Far)");
	private final JLabel lblAstImage = new JLabel();
	private final JScrollPane scrollPane_1 = new JScrollPane();

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
				do_btnCompile_actionPerformed(arg0);
			}
		});
		btnCompile.setBounds(188, 516, 234, 25);
		
		panel.add(btnCompile);
		
		tabbedPane.addTab("CST", null, panel_1, null);
		panel_1.setLayout(null);
		
		tabbedPane.addTab("AST", null, panel_2, null);
		panel_2.setLayout(null);
		panel_2.add(scrollPane_1);
		scrollPane_1.setBounds(12, 26, 973, 765);
		scrollPane_1.setViewportView(lblAstImage);
	    lblAstImage.setVisible(true);
		
		
	}
	protected void do_btnCompile_actionPerformed(ActionEvent arg0) 
	{
		String asTreeDot = "";
		String csTreeDot = "";
		GraphViz dotMaker = new GraphViz();
		byte[] cstAsByteFile;
	    byte[] astAsByteFile;
	    
	    byte[] tester123;
        // Create tokens and print them
        ArrayList<Token> tokens = Lexer.lex(inputPane.getText());

        System.out.println("Congratulations, you successfully lexed with no errors");
        try {
			Parser myParser = new Parser(tokens);
			Tree renderASTree = myParser.getAST();
			Tree renderCSTree = myParser.getCST();
			csTreeDot = renderCSTree.toDot();
			asTreeDot = renderASTree.toDot();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
      System.out.print(asTreeDot);
      
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
      
      ImageIcon cstPicture = new ImageIcon(cstAsByteFile);
      
      lblAstImage.setIcon(cstPicture);
   
      System.out.println(Arrays.toString(cstAsByteFile));
      
      
      
      
      
	}
}
