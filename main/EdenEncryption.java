package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
//import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.Apint;

public class EdenEncryption implements ActionListener {
	
	private static Timer timer = new Timer();
	
	private static JFrame frame  = new JFrame("Eden Encryption");
	private static JPanel messages = new JPanel();
	private static JTextArea textInput = new JTextArea();
	private static JTextArea textOutput = new JTextArea();
	private static JTextField textKey = new JTextField();
	private static JTextField textRoot = new JTextField();
	private static JTextField textDigits = new JTextField();
	public JButton textConvert = new JButton("Convert");
	public JButton textClear = new JButton("Clear");
	public JButton fileConvert = new JButton("Convert");
	public static JComboBox<String> conversionFrom = new JComboBox<String>();
	public static JComboBox<String> conversionTo = new JComboBox<String>();
	public static JComboBox<String> conversionType = new JComboBox<String>();
	public static JComboBox<String> fileconversionType = new JComboBox<String>();
	
	//Create JFrame
	public EdenEncryption() {
		textConvert.addActionListener(this);
		textClear.addActionListener(this);
		fileConvert.addActionListener(this);
		
		for (String s:Arrays.asList("ASCII","Denary","Hex","Binary")) {
			conversionFrom.addItem(s);
			conversionTo.addItem(s);
		}
		for (String s:Arrays.asList("Encrypt","Decrypt")) {
			conversionType.addItem(s);
			fileconversionType.addItem(s);
		}
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(true);
		frame.setSize(320, 600);
		frame.setVisible(true);
		frame.add(messages);
		messages.add(new JLabel("With thanks to Mikko Tommila for Apfloat"),BorderLayout.PAGE_START);
		JPanel r1 = new JPanel(new GridLayout(5,3));
		r1.add(new JLabel("Key"));
		r1.add(new JLabel("Root"));
		r1.add(new JLabel("Digits"));
		r1.add(textKey);
		r1.add(textRoot);
		r1.add(textDigits);
		r1.add(textConvert);
		r1.add(new JLabel("Convert from"));
		r1.add(conversionFrom);
		r1.add(textClear);
		r1.add(new JLabel("Convert to"));
		r1.add(conversionTo);
		r1.add(new JLabel(""));
		r1.add(new JLabel("Conversion"));
		r1.add(conversionType);
		messages.add(r1,BorderLayout.CENTER);
		messages.add(textInput,BorderLayout.CENTER);
		messages.add(textOutput,BorderLayout.CENTER);
	
		textInput.setWrapStyleWord(true);
		textOutput.setWrapStyleWord(true);
		textInput.setLineWrap(true);
		textOutput.setLineWrap(true);
		textInput.setPreferredSize(new Dimension(300,200));
		textOutput.setPreferredSize(new Dimension(300,200));
		textOutput.setEditable(false);
		
	}
	
	//When button pressed
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == textClear) {
			textInput.setText("");
			textOutput.setText("");
			textKey.setText("");
			textRoot.setText("");
			textDigits.setText("");
		}
		else if (e.getSource() == textConvert) {
			textOutput.setText("");
			boolean capable = true;
			try {
				if (new Apint(textKey.getText()).signum() > 0) {
					blip(textKey,true);
				}
				else {
					blip(textKey,false);
					capable = false;
				}
			} catch (NumberFormatException ex) {
				blip(textKey,false);
				capable = false;
			}

			try {
				if (new Apint(textRoot.getText()).signum() > 0) {
					blip(textRoot,true);
				}
				else {
					blip(textRoot,false);
					capable = false;
				}
			} catch (NumberFormatException ex) {
				blip(textRoot,false);
				capable = false;
			}
			
			try {
				if (Integer.valueOf(textDigits.getText()) > 0 && Integer.valueOf(textDigits.getText()) <= 8) {
					blip(textDigits,true);
				}
				else {
					blip(textDigits,false);
					capable = false;
				}
			} catch (NumberFormatException ex) {
				blip(textDigits,false);
				capable = false;
			}
			
			if (textInput.getText().length() > 0) {
				blip(textInput,true);
			}
			else {
				blip(textInput,false);
				capable = false;
			}
			
			if (capable) {
				String text = textInput.getText();
				List<Integer> in = new ArrayList<Integer>();
				
				if (conversionFrom.getSelectedItem() == "Binary") {
					String[] sp = text.split(" ");
					for (String item:sp) {
						int value = Integer.parseInt(item, 2);
						in.add(value);
					}
				}
				
				if (conversionFrom.getSelectedItem() == "Denary") {
					String[] sp = text.split(" ");
					for (String item:sp) {
						int value = Integer.parseInt(item, 10);
						in.add(value);
					}
				}
				
				if (conversionFrom.getSelectedItem() == "Hex") {
					String[] sp = text.split(" ");
					for (String item:sp) {
						int value = Integer.parseInt(item, 16);
						in.add(value);
					}
				}
				
				if (conversionFrom.getSelectedItem() == "ASCII") {
					for (char item: text.toCharArray()) {
						int value = (int) item;
						in.add(value);
					}
				}
				
				Apint a = new Apint(textKey.getText());
				Apint b = new Apint(textRoot.getText());
				int c = Integer.valueOf(textDigits.getText());
				List<Integer> keys = getKey(a,b,c,in.size());
				
				if (keys.size() > in.size()) {
					String out = "";
					int shift = keys.get(0);
					if (conversionTo.getSelectedItem() == "Binary") {
						for (int r = 0; r < in.size(); r++) {
							int num = in.get(r);
							if (conversionType.getSelectedItem() == "Encrypt") {
								num += shift;
							}
							else if (conversionType.getSelectedItem() == "Decrypt") {
								num -= shift;
							}
							num = (num + 128) % 128;
							shift = (shift + keys.get(r+1)) % 128;
							out += Integer.toBinaryString(num);
							if (r != in.size()-1) {
								out += " ";
							}
						}
					}
					if (conversionTo.getSelectedItem() == "Denary") {
						for (int r = 0; r < in.size(); r++) {
							int num = in.get(r);
							if (conversionType.getSelectedItem() == "Encrypt") {
								num += shift;
							}
							else if (conversionType.getSelectedItem() == "Decrypt") {
								num -= shift;
							}
							num = (num + 128) % 128;
							shift = (shift + keys.get(r+1)) % 128;
							out += String.valueOf(num);
							if (r != in.size()-1) {
								out += " ";
							}
						}
					}	
					if (conversionTo.getSelectedItem() == "Hex") {
						for (int r = 0; r < in.size(); r++) {
							int num = in.get(r);
							if (conversionType.getSelectedItem() == "Encrypt") {
								num += shift;
							}
							else if (conversionType.getSelectedItem() == "Decrypt") {
								num -= shift;
							}
							num = (num + 128) % 128;
							shift = (shift + keys.get(r+1)) % 128;
							out += Integer.toHexString(num);
							if (r != in.size()-1) {
								out += " ";
							}
						}
					}
					if (conversionTo.getSelectedItem() == "ASCII") {
						for (int r = 0; r < in.size(); r++) {
							int num = in.get(r);
							if (conversionType.getSelectedItem() == "Encrypt") {
								num += shift;
							}
							else if (conversionType.getSelectedItem() == "Decrypt") {
								num -= shift;
							}
							num = (num + 128) % 128;
							shift = (shift + keys.get(r+1)) % 128;
							out += (char) (num & 0x7F);
						}
					}
					textOutput.setText(out);
				}
				else {
					textOutput.setText("Key is terminating and too short");
				}
			}
		}
	}
	
	//Generates key
	public List<Integer> getKey(Apint key1, Apint key2, int key3,int length) {
		Apfloat s = key1.precision(key3*(length+1));
		Apfloat d = key2.precision(key3*(length+1));
		Apfloat sd = ApfloatMath.pow(s,Apfloat.ONE.divide(d));
		
		String k = sd.toString();
		k = k.replace("0.","");
		k = k.replace(".","");
		k = String.format("%1$-" + key3*(length+1) + "s", k);
		k = k.replace(" ","0");
		List<Integer> out = new ArrayList<Integer>();
		for (int repeat = 0; repeat < key3*(length+1); repeat += key3) {
			out.add(Integer.valueOf(k.substring(repeat, repeat+key3)));
		}
		return out;
	}
	
	//Colour change to show if correct or not
	public void blip(final Object o,boolean b) {
		if (b) {
			((JComponent) o).setBackground(Color.green);
		}
		else {
			((JComponent) o).setBackground(Color.red);
		}
		timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
            	((JComponent) o).setBackground(new Color(255,255,255));
            }
        }, 
        500);
	}
}
