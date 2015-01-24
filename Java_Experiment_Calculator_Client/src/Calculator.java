import javax.swing.JFrame;

class Calculator {
	public static void main(String[] args) {
		Window calc = new Window();
		calc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// calc.pack();
		calc.setVisible(true);
	}
}
