/** This is an implementation of a simple calculator in java.
 *  It supports only the basic operations (addition, subtraction, multiplication and division)
 *  It does not support brackets. However all operations with real numbers are possible. 
 *  (including the ones with negative numbers, such as 3/-2 or -3*-2)
 *  Consecutive expressions are possible (such as 3 + 2/3 - 1)
 *  Multiplication and division have priority against addition and subtraction. 
 * 
 *  The mathematical expression that the user enters is first transformed into a tree, 
 *  which is then used to make the computation. 
 * 
 * 	Tomas Pllaha
 *  December 2012
 *  */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * The window class. The container of all GUI components.
 * */
public class Window extends JFrame {
	/**
	 * 
	 */
	// 1 instance variable per button + 1 instance variable for the text box
	// (The calculator's screen)
	private JTextField screen;
	private JButton b1;
	private JButton b2;
	private JButton b3;
	private JButton b4;
	private JButton b5;
	private JButton b6;
	private JButton b7;
	private JButton b8;
	private JButton b9;
	private JButton b0;
	private JButton plus;
	private JButton minus;
	private JButton times;
	private JButton div;
	private JButton equals;
	private JButton point;
	private JButton clear;
	private JButton clearEntry;
	private JButton log;

	// Constructor
	public Window() {
		super("Calculator");
		// initialize all the buttons
		setSize(250, 175);
		screen = new JTextField(10);
		b1 = new JButton("1");
		b2 = new JButton("2");
		b3 = new JButton("3");
		b4 = new JButton("4");
		b5 = new JButton("5");
		b6 = new JButton("6");
		b7 = new JButton("7");
		b8 = new JButton("8");
		b9 = new JButton("9");
		b0 = new JButton("0");
		plus = new JButton("+");
		minus = new JButton("-");
		times = new JButton("x");
		div = new JButton("/");
		equals = new JButton("=");
		point = new JButton(".");
		clearEntry = new JButton("CE");
		clear = new JButton("C");
		log = new JButton("log");
		// initialize the event handler
		HandlerClass handler = new HandlerClass();

		// add the event handler to all buttons
		b1.addActionListener(handler);
		b2.addActionListener(handler);
		b3.addActionListener(handler);
		b4.addActionListener(handler);
		b5.addActionListener(handler);
		b6.addActionListener(handler);
		b7.addActionListener(handler);
		b8.addActionListener(handler);
		b9.addActionListener(handler);
		b0.addActionListener(handler);
		plus.addActionListener(handler);
		minus.addActionListener(handler);
		times.addActionListener(handler);
		div.addActionListener(handler);
		point.addActionListener(handler);
		equals.addActionListener(handler);
		clear.addActionListener(handler);
		clearEntry.addActionListener(handler);
		screen.addActionListener(handler);
		log.addActionListener(handler);

		// Create the top panel (it contains the screen and the 2 clear buttons
		JPanel p1 = new JPanel();
		p1.setLayout(new FlowLayout(3));
		p1.add(screen);
		p1.add(clearEntry);
		p1.add(clear);

		// Second panel, contains all numbers and operators
		JPanel p2 = new JPanel();
		p2.setLayout(new GridLayout(4, 4));
		b1.setPreferredSize(new Dimension(53, 30));
		p2.add(b1);
		p2.add(b2);
		p2.add(b3);
		p2.add(times);
		p2.add(b4);
		p2.add(b5);
		p2.add(b6);
		p2.add(div);
		p2.add(b7);
		p2.add(b8);
		p2.add(b9);
		p2.add(plus);
		p2.add(equals);
		p2.add(b0);
		p2.add(point);
		p2.add(minus);
		p2.add(log);

		// The big container, which contains both panels
		JPanel p = new JPanel();
		p.add(p1, BorderLayout.NORTH);
		p.add(p2, BorderLayout.CENTER);

		add(p);
	}

	private class HandlerClass implements ActionListener {
		public LogSynchronizer logsync = new LogSynchronizer();

		public void actionPerformed(ActionEvent event) {
			// All number buttons, operators and the decimal point,
			// simply add a character to the screen
			if (event.getSource() == b1)
				screen.setText(screen.getText() + "1");
			if (event.getSource() == b2)
				screen.setText(screen.getText() + "2");
			if (event.getSource() == b3)
				screen.setText(screen.getText() + "3");
			if (event.getSource() == b4)
				screen.setText(screen.getText() + "4");
			if (event.getSource() == b5)
				screen.setText(screen.getText() + "5");
			if (event.getSource() == b6)
				screen.setText(screen.getText() + "6");
			if (event.getSource() == b7)
				screen.setText(screen.getText() + "7");
			if (event.getSource() == b8)
				screen.setText(screen.getText() + "8");
			if (event.getSource() == b9)
				screen.setText(screen.getText() + "9");
			if (event.getSource() == b0)
				screen.setText(screen.getText() + "0");
			if (event.getSource() == div)
				screen.setText(screen.getText() + "/");
			if (event.getSource() == times)
				screen.setText(screen.getText() + "*");
			if (event.getSource() == minus)
				screen.setText(screen.getText() + "-");
			if (event.getSource() == plus)
				screen.setText(screen.getText() + "+");
			if (event.getSource() == point)
				screen.setText(screen.getText() + ".");
			if (event.getSource() == clear)
				// clear all
				screen.setText("");
			// The EQUALS operator makes the computation
			if (event.getSource() == equals) {
				// Transform the text into a tree
				Tree tree = new Tree(screen.getText());
				String tempExp = screen.getText();
				// Solve the tree recursively
				String result = tree.solve() + "";
				// replace the displayed text
				screen.setText(result);
				logsync.sendLog(tempExp, result);

			}
			if (event.getSource() == screen) {
				// same as "equals"
				Tree tree = new Tree(screen.getText());
				String result = tree.solve() + "";
				screen.setText(result);
			}
			if (event.getSource() == clearEntry) {
				// clear only 1 character
				screen.setText(screen.getText().substring(0,
						screen.getText().length() - 1));
			}
			if (event.getSource() == log) {
				String log = logsync.recvLog();
				JOptionPane.showMessageDialog(null, log);
			}
		}
	}

}
