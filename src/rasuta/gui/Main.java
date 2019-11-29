package rasuta.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import rasuta.model.Rasuta;
import rasuta.model.RasutaSaPrekoraciocem;
import rasuta.model.Slog;
import rasuta.model.Transformacije;

public class Main extends JFrame{

	public JFrame frame;
	private RasutaSaPrekoraciocem rasuta;
	private ArrayList<BaketGUI> baketiGui = new ArrayList<>();
	private JPanel DiskGroup;
	private JPanel PrekoracenjeGroup;
	private JPanel MemoryGroup;
	private File rasutaFile;

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 764, 592);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{156, 586, 0};
		gridBagLayout.rowHeights = new int[]{250, 167, 154};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 1.0};
		frame.getContentPane().setLayout(gridBagLayout);
		

		
		JPanel MeniGroup = new JPanel();
	
		MeniGroup.setBorder(new TitledBorder(null, "Meni", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_MeniGroup = new GridBagConstraints();
		gbc_MeniGroup.gridheight = 2;
		gbc_MeniGroup.fill = GridBagConstraints.BOTH;
		gbc_MeniGroup.insets = new Insets(0, 0, 5, 5);
		gbc_MeniGroup.gridx = 0;
		gbc_MeniGroup.gridy = 0;
		frame.getContentPane().add(MeniGroup, gbc_MeniGroup);
		MeniGroup.setLayout(new BoxLayout(MeniGroup, BoxLayout.Y_AXIS));
		
		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		
		lblNewLabel_1.setToolTipText("Formiraj datoteku\r\n");
		lblNewLabel_1.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/datAdd.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
		
		
		lblNewLabel_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				lblNewLabel_1.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/datAdd.png")).getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH)));
				
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				lblNewLabel_1.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/datAdd.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
				
				
				formiranjeDatoteke();
				
			}});
		
		MeniGroup.add(lblNewLabel_1);
		//MeniGroup.addRecord(Box.createRigidArea(new Dimension(0,10)));
		MeniGroup.add(Box.createGlue());
		
		
		JLabel lblNewLabel_2 = new JLabel("");
		lblNewLabel_2.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		lblNewLabel_2.setToolTipText("Ucitaj datoteku");
		lblNewLabel_2.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/open.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
		
		
		lblNewLabel_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				lblNewLabel_2.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/open.png")).getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH)));
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				lblNewLabel_2.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/open.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
				ucitajRasutuDatoteku();
				
			}});
		
		
		
		MeniGroup.add(lblNewLabel_2);
		MeniGroup.add(Box.createGlue());
		
		JLabel lblNewLabel_3 = new JLabel("");
		lblNewLabel_3.setAlignmentX(JComponent.CENTER_ALIGNMENT);
	
		lblNewLabel_3.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/load.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
		
		lblNewLabel_3.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				lblNewLabel_3.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/load.png")).getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH)));
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				lblNewLabel_3.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/load.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
				
			}});
		
		MeniGroup.add(lblNewLabel_3);
		MeniGroup.add(Box.createGlue());
		
		JLabel lblNewLabel_4 = new JLabel("");
		lblNewLabel_4.setToolTipText("Unesi slog");
		lblNewLabel_4.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		lblNewLabel_4.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/plus.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
		lblNewLabel_4.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				lblNewLabel_4.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/plus.png")).getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH)));
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				lblNewLabel_4.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/plus.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
				unosSlogaDialog();
			}});
		
		MeniGroup.add(lblNewLabel_4);
		MeniGroup.add(Box.createGlue());
		
	
		JLabel lblNewLabel_5 = new JLabel("");
		lblNewLabel_5.setToolTipText("Fizicko brisanje");
		lblNewLabel_5.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		lblNewLabel_5.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/x.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
		lblNewLabel_5.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				lblNewLabel_5.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/x.png")).getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH)));
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				lblNewLabel_5.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/x.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
			}});
		
		MeniGroup.add(lblNewLabel_5);
		MeniGroup.add(Box.createGlue());
		
		
		JLabel lblNewLabel_6 = new JLabel("");
		lblNewLabel_6.setToolTipText("Logicko brisanje");
		lblNewLabel_6.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		lblNewLabel_6.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/xdash.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
		lblNewLabel_6.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				lblNewLabel_6.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/xdash.png")).getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH)));
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				lblNewLabel_6.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/xdash.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
			}});
		
		MeniGroup.add(lblNewLabel_6);
		MeniGroup.add(Box.createGlue());
		
		JLabel lblNewLabel_7 = new JLabel("");
		lblNewLabel_7.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		lblNewLabel_7.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/edit.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
		lblNewLabel_7.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				lblNewLabel_7.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/edit.png")).getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH)));
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				lblNewLabel_7.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/edit.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
			}});
		MeniGroup.add(lblNewLabel_7);
		MeniGroup.add(Box.createGlue());
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setToolTipText("Pronadji slog");
		lblNewLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		lblNewLabel.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/search.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
		lblNewLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				lblNewLabel.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/search.png")).getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH)));
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				lblNewLabel.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/search.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
				int s = pronadjiSlogDialog();
				pronadjiSlog(s);
			}});
		
		MeniGroup.add(lblNewLabel);
		
		
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(new TitledBorder(null, "Disk", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 0;
		frame.getContentPane().add(scrollPane, gbc_scrollPane);
		
		DiskGroup = new JPanel();
		scrollPane.setViewportView(DiskGroup);
		DiskGroup.setBorder(null);
		DiskGroup.setLayout(new BoxLayout(DiskGroup, BoxLayout.Y_AXIS));
			
		JScrollPane scrollPane_1 = new JScrollPane();
		//scrollPane_1.setVisible(false);
		//gridBagLayout.rowHeights = new int[]{250, 167};
		scrollPane_1.setViewportBorder(new TitledBorder(null, "Prekoracenje", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_1.gridx = 1;
		gbc_scrollPane_1.gridy = 2;
		frame.getContentPane().add(scrollPane_1, gbc_scrollPane_1);
		
		PrekoracenjeGroup = new JPanel();
		scrollPane_1.setViewportView(PrekoracenjeGroup);
		PrekoracenjeGroup.setBorder(null);
		PrekoracenjeGroup.setLayout(new BoxLayout(PrekoracenjeGroup, BoxLayout.Y_AXIS));
		
		MemoryGroup = new JPanel();
		MemoryGroup.setBorder(BorderFactory.createTitledBorder("Memory"));
		GridBagConstraints gbc_MemoryGroup = new GridBagConstraints();
		gbc_MemoryGroup.fill = GridBagConstraints.BOTH;
		gbc_MemoryGroup.gridx = 1;
		gbc_MemoryGroup.gridy = 1;
		frame.getContentPane().add(MemoryGroup, gbc_MemoryGroup);
		MemoryGroup.setLayout(new BoxLayout(MemoryGroup, BoxLayout.Y_AXIS));
	}
	
	private void prikaziDatoteku(RasutaSaPrekoraciocem rasuta) {
		DiskGroup.removeAll();
		for (int i = 0; i < rasuta.getBrojBaketa(); i++) {
			BaketGUI b = new BaketGUI();
			b.addBaket(rasuta.getPrimarnaZona().get(i));
			String baketTitle = "Baket" + String.valueOf(i+1);
			b.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), baketTitle, TitledBorder.CENTER, TitledBorder.TOP, null, Color.RED));
			baketiGui.add(b);
			DiskGroup.add(b);
		}
		DiskGroup.revalidate();
	}
	
	private void upisiDatoteku() {
		/*
			funkcija upisuje sve izmene iz "rasute" u 
			fajl na disku, kao "Save" funkcionise
		*/
		
		try {
			FileOutputStream f = new FileOutputStream(rasutaFile);
			ObjectOutputStream o = new ObjectOutputStream(f);
			o.writeObject(rasuta);
			o.close();
			f.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void formiranjeDatoteke() {
		
		Dialog_Formiranje dF = new Dialog_Formiranje();
		
		dF.setVisible(true);
	
		rasuta = dF.getRasuta();
		rasutaFile = dF.getFajl();
		
		upisiDatoteku();
	
		JOptionPane.showMessageDialog(this, "Datoteka uspesno kreirana!");
		
	}

	private void ucitajRasutuDatoteku() {

		
		JFileChooser fileChooser = new JFileChooser();
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			
			try {
				
				FileInputStream fi = new FileInputStream(fileChooser.getSelectedFile());
				ObjectInputStream oi = new ObjectInputStream(fi);
				
				rasuta = (RasutaSaPrekoraciocem) oi.readObject();
				rasutaFile = fileChooser.getSelectedFile();
				
				oi.close();
				fi.close();
				
				
			} catch (IOException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			prikaziDatoteku(rasuta);
			DiskGroup.revalidate();
			
		}
	}
	
	private void unosSlogaDialog(){
		
		Dialog_UnosaSloga dus = new Dialog_UnosaSloga();
		dus.setVisible(true);
		Slog s = dus.slog;
		unosSloga(s);
		
		
	}
	
	private int pronadjiSlogDialog() {
		
		String s = (String)JOptionPane.showInputDialog(
		                    frame,
		                    "Unesite sifru zatvorenika:",
		                    "Pretraga",
		                    JOptionPane.PLAIN_MESSAGE,
		                    null,
		                    null,
		                    null);

		//If a string was returned, say so.
		if ((s)!= null) {
		    return Integer.parseInt(s);
		}
		return 0;
		
	}
	
	private void pronadjiSlog(int sifraZatvorenika) {
	
		int adresa = Transformacije.metodaOstatkaPriDeljenju(sifraZatvorenika, rasuta.getBrojBaketa());
		int i = 0;
		
		
		for (i = 0; i < rasuta.getBrojBaketa(); i++) { 											
			if (rasuta.getPrimarnaZona().get(i).getAdresa() == adresa) {						
				for (int j = 0; j < rasuta.getFaktor_baketiranja(); j++) {				
					if (rasuta.getPrimarnaZona().get(i).getSlogovi().get(j).getStatus() == 0 && 
						rasuta.getPrimarnaZona().get(i).getSlogovi().get(j).getZatvorenik().getSifra()==sifraZatvorenika) {	
						//i = adresa maticnog baketa, da bi odatle krenula search animacija											
						break;
					}
				}
				break; 																
				
			}
		}
		
		BaketGUI bg = new BaketGUI();
		bg.addBaket(rasuta.getPrimarnaZona().get(i));
		bg.setAdresa(adresa);
		MemoryGroup.add(bg);
		MemoryGroup.revalidate();
		MemoryGroup.repaint();
		
		SwingWorker<Void, String> worker = new SwingWorker<Void, String>(){ 			//gui "animacija" nalazenje sloga
			
	        @Override
	        protected Void doInBackground() throws Exception {
	        	int curr = bg.getAdresa() -1;							//ne moze "i" odozogo da se koristi, a treba index baketa
	        	
	        	boolean pronadjen = false;
	        	while (!pronadjen) {
	        		int j = 0;
	        		BaketGUI bc = (BaketGUI) MemoryGroup.getComponent(0);				//radi se na baketu iz memori Grupe
		        	for (j = 0; j < rasuta.getFaktor_baketiranja(); j++) {
		        		Thread.sleep(1000);
		        		bc.slogoviGui.get(j).paintRed();
		        		Thread.sleep(1000);
		        		if(bc.slogoviGui.get(j).slog.getStatus()==2) {					//ako je prvi slog na koji naidje prazan, onda slog ne postoji (valjda???)
		        			pronadjen = true;
		        			JOptionPane.showMessageDialog(frame,
		        				    "Slog nije pronadjen.",
		        				    "Greska",
		        				    JOptionPane.WARNING_MESSAGE);
		        			bc.slogoviGui.get(j).resetBorder();
		        			MemoryGroup.remove(bc);
		        			MemoryGroup.repaint();
		        			break;
		        			
		        		}
		        		if(bc.slogoviGui.get(j).slog.getZatvorenik().getSifra()==sifraZatvorenika &&
		        		   bc.slogoviGui.get(j).slog.getStatus()==0) {
		        			
		        			bc.slogoviGui.get(j).paintGreen();
		        			Thread.sleep(1000);
		        			showSuccessDialog("Slog uspesno pronadjen.");
		        			bc.slogoviGui.get(j).resetBorder();
		        			MemoryGroup.remove(bc);
		        			MemoryGroup.repaint();
		        			
		        			pronadjen = true;
		        			break;
		        		}else {
		        			bc.slogoviGui.get(j).resetBorder();
		        		}
		        			
					}													//prosla svaki slog, nije pronasla odgovarajuci
		        	
		        	if(j == rasuta.getFaktor_baketiranja() && !pronadjen) {		//ucitavanje sledec baketa u memoriju
		
			        	if (curr == rasuta.getBrojBaketa()-1) {													
							curr = 0;																	
						}else {
							curr++;																	
						}
			        	MemoryGroup.remove(bc);
	        			MemoryGroup.repaint();
	        			Thread.sleep(1000);
	        			BaketGUI bg = new BaketGUI();
	        			bg.addBaket(rasuta.getPrimarnaZona().get(curr));
	        			bg.setAdresa(curr+1);
	        			MemoryGroup.add(bg);
	        			MemoryGroup.revalidate();
	        			MemoryGroup.repaint();
		
		        	}
	        	}
	            return null;
	        }

	    };

	    worker.execute();
		

	}
	
	
	private void unosSloga(Slog s) {
		
		int adresa = 0;
		switch (rasuta.getTransformacija()) {
		case 0:
			adresa = Transformacije.metodaOstatkaPriDeljenju(s.getZatvorenik().getSifra(),rasuta.getBrojBaketa());
			break;
		// OSTALE METODE 
		default:
			break;
		}
	
		
		System.out.println("Adresa dobijena transformacijom --> "+String.valueOf(adresa));
		
		
		int i = 0; 																	// index baketa za upis sloga
		
		for (i = 0; i < rasuta.getBrojBaketa(); i++) { 											//prolazi kros sve bakete
			if (rasuta.getPrimarnaZona().get(i).getAdresa() == adresa) {						//ako se adresa sloga poklopi sa adresom baketa
				for (int j = 0; j < rasuta.getFaktor_baketiranja(); j++) {				//prolazi kros sve slogove u baketu
					if (rasuta.getPrimarnaZona().get(i).getSlogovi().get(j).getStatus() != 0) {		//ako je slog logicki obrisan, jedino tu mozes da se upise novi
						//rasuta.getPrimarnaZona().get(i).getSlogovi().setRecord(j, s);
						 																//pronasao mesto za upis, ne trazi se dalje
						break;
					}
				}
				break; 																//proso kros sve slogove baketa i nije upisao-->baket PUN
				
			}
		}
		
		
		BaketGUI bg = new BaketGUI();												//mora da se pravi novi zato sto ne mogu vrednosti da se prenose u 
		bg.addBaket(rasuta.getPrimarnaZona().get(i));								// SwingWorker, ovo "i" , ne moze da se koristi u Swingworkeru
		bg.setAdresa(adresa);
		MemoryGroup.add(bg);
		MemoryGroup.revalidate();
		MemoryGroup.repaint();
		
		/*
		 * ne moze u memeoriGrup da se adduje direktno is BaketiGui,
			ako se to uradi onda ce da pilikom ubacivanja sloga u memoriGroup
			slog sa diska da se ukloni, sto ne treba
		*/
		SwingWorker<Void, String> worker = new SwingWorker<Void, String>(){ 			//gui "animacija" upisa sloga
			
	        @Override
	        protected Void doInBackground() throws Exception {
	        	int curr = bg.getAdresa()-1;								//ne moze "i" odozogo da se koristi, a treba index baketa
	        	
	        	System.out.println(String.valueOf(curr) + " = curr");
	        	boolean upisan = false;
	        	while (!upisan) {
	        		int j = 0;
	        		BaketGUI bc = (BaketGUI) MemoryGroup.getComponent(0);				//radi se na baketu iz memori Grupe
		        	for (j = 0; j < rasuta.getFaktor_baketiranja(); j++) {
		        		Thread.sleep(1000);
		        		bc.slogoviGui.get(j).paintRed();
		        		Thread.sleep(1000);
		        		if(bc.slogoviGui.get(j).slog.getStatus() !=0) {
		        			bc.slogoviGui.get(j).paintGreen();
		        			Thread.sleep(1000);
		        			bc.slogoviGui.get(j).addSlog(s);
		        			MemoryGroup.revalidate();
		        			Thread.sleep(1000);
		        			bc.slogoviGui.get(j).resetBorder();
		        			MemoryGroup.remove(bc);
		        			MemoryGroup.repaint();
		        			Thread.sleep(1000);
		        			rasuta.getPrimarnaZona().get(curr).getSlogovi().set(j, s);	//Slog se upisuje u "rasutu" promenljivu
		        			prikaziDatoteku(rasuta);
		        			upisiDatoteku();											// ovde upusuje
		        			showSuccessDialog("Slog upisan.");
		        			upisan = true;
		        			break;
		        		}else {
		        			bc.slogoviGui.get(j).resetBorder();
		        		}
		        			
					}													//prosla svaki slog, nije pronasla odgovarajuci
		        	
		        	if(j ==rasuta.getFaktor_baketiranja() && !upisan) {		//ucitavanje sledec baketa u memoriju
			        	if (curr == rasuta.getBrojBaketa()-1) {													
							curr = 0;																	
						}else {
							curr++;																	
						}
			        	MemoryGroup.remove(bc);
	        			MemoryGroup.repaint();
	        			Thread.sleep(1000);
	        			BaketGUI bg = new BaketGUI();
	        			bg.addBaket(rasuta.getPrimarnaZona().get(curr));
	        			bg.setAdresa(curr+1);
	        			MemoryGroup.add(bg);
	        			MemoryGroup.revalidate();
	        			MemoryGroup.repaint();
		
		        	}
	        	}
	            return null;
	        }

	    };

	    worker.execute();
		
	
	}
	
	public void showSuccessDialog(String message) {
		JOptionPane.showMessageDialog(this, message);
	}
	
}



