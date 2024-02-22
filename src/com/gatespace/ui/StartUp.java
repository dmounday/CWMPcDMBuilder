/*
 * StartUp.java
 *
 * Created on __DATE__, __TIME__
 */

package com.gatespace.ui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.falcontechnology.tr69.acsdatamodel.Builder;
import com.falcontechnology.tr69.acsdatamodel.BuilderProfiles;
import com.falcontechnology.tr69.acsdatamodel.DataModelDope;
import com.falcontechnology.tr69.acsdatamodel.DataModels;
import com.falcontechnology.tr69.acsdatamodel.RootDataModel;
import com.falcontechnology.tr69.treetable.DMTreeTableSelectionForm;
import com.falcontechnology.tr69.treetable.DModelTreeTable;
import com.falcontechnology.tr69.treetable.JTreeTable;
import com.gatespace.tr69.codegen.CWMPcCodeGenerator;
import com.gatespace.tr69.codegen.CustomObjParam;
import com.gatespace.tr69.codegen.MergeCode;

/**
 * 
 * @author Don Mounday 
 * 
 */
public class StartUp extends javax.swing.JFrame implements ChangeListener {

	static final String DMSTATEFILE = ".dmbuildstate";
	static final String CUSTOMOBJPARAMFILE = ".dmbuildobjparam";
	static GlobalOptions gOpt = new GlobalOptions();

	//
	private DataModels allDMs;
	private ArrayList<DataModelDope> rootDMs;
	private int selectedModel = -1; // index of model in rootDMs
	private String sourceDMFile; // source file for model selected.

	private ArrayList<DataModelDope> servicesDMs;
	private int selectedService = -1;

	Builder builder;
	RootDataModel rootDM;
	private String dmName;

	private SelectList profileList;
	private ProfileFilterForm pfForm;

	//private DMSelectionForm dmForm;
	//private ObjectTree objectTree; // used for override object/param selection.

	DMTreeTableSelectionForm dtForm;
	DModelTreeTable dmTreeTable;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static private final String newline = "\n";
	private JFileChooser fc;
	private CustomObjParam selectedCustom;

	/**
	 * Set the Button and menu item enable properties based on current state of
	 * user inputs.
	 */
	private void setEnable() {

		jButtonNormalize.setEnabled(gOpt.includeDir != null
				&& (selectedModel != -1 || selectedService != -1));
		jButtonProfileFilter.setEnabled(gOpt.includeDir != null
				&& (selectedModel != -1 || selectedService != -1)
				&& builder != null);
		jButtonGenerateCode
				.setEnabled((selectedModel != -1 && gOpt.cWMPcDataModelDir != null)
						|| (selectedService != -1 && gOpt.cWMPcServiceDir != null));
		overRideButton
				.setEnabled((selectedModel != -1 && gOpt.cWMPcDataModelDir != null)
						|| (selectedService != -1 && gOpt.cWMPcServiceDir != null));

	}

	private void runBuilder() {
		builder = new Builder(gOpt.includeDir);
		// run the builder on either the selecteModel or the serviceSelected.
		if (selectedModel >= 0) {
			gOpt.codeGenDirPath = gOpt.cWMPcDataModelDir;
			try {
				dmName = rootDMs.get(selectedModel).dataModel;
				sourceDMFile = rootDMs.get(selectedModel).fileName;
			} catch (IndexOutOfBoundsException e) {
				dmName = null;
			}
			rootDM = builder.parseTopLevel(sourceDMFile, dmName);
			jLogTextArea.append(builder.getLog());
			// save Device DM.

			rootDM.saveXml(gOpt.cWMPcDataModelDir + "dm-instance.xml");
			// save some text information files.
			rootDM.writeObjectList(gOpt.cWMPcDataModelDir + "DMobjectList.txt");
			rootDM.writeObjParamList(gOpt.cWMPcDataModelDir + "DMparamList.txt");
			rootDM.writerProfileObjParamList(gOpt.cWMPcDataModelDir
					+ "full-profileList.txt");
		} else {
			if (selectedService >= 0) {
				gOpt.codeGenDirPath = gOpt.cWMPcServiceDir;
				Builder service = new Builder(gOpt.includeDir);
				rootDM = service.parseTopLevel(
						servicesDMs.get(selectedService).fileName,
						servicesDMs.get(selectedService).dataModel);
				rootDM.saveXml(gOpt.cWMPcServiceDir + "dm-instance.xml");
				// save some text information files.
				rootDM.writeObjectList(gOpt.cWMPcServiceDir
						+ "DMobjectList.txt");
				rootDM.writeObjParamList(gOpt.cWMPcServiceDir
						+ "DMparamList.txt");
				rootDM.writerProfileObjParamList(gOpt.cWMPcServiceDir
						+ "full-profileList.txt");
				jLogTextArea.append(service.getLog());

				jLogTextArea.append(builder.getLog());
			}
		}
		setEnable();
		repaint();
	}

	@SuppressWarnings("serial")
	private void getDataModels() {
		allDMs = new DataModels(gOpt.includeDir);
		rootDMs = allDMs.getRootDataModels();
		jModelList.setModel(new javax.swing.AbstractListModel() {
			public int getSize() {
				if (rootDMs == null)
					return 0;
				return rootDMs.size();
			}

			public Object getElementAt(int i) {
				String fileName = rootDMs.get(i).fileName;
				int idx = fileName.lastIndexOf(File.separatorChar);
				if (idx > 0)
					fileName = fileName.substring(idx + 1);
				String dmwithFile = rootDMs.get(i).dataModel + "  (" + fileName
						+ ")";
				return dmwithFile;
			}
		});
		servicesDMs = allDMs.getServicesModels();
		jServicesDMNameList.setModel(new javax.swing.AbstractListModel() {
			public int getSize() {
				if (servicesDMs == null)
					return 0;
				return servicesDMs.size();
			}

			public Object getElementAt(int i) {
				String fileName = servicesDMs.get(i).fileName;
				int idx = fileName.lastIndexOf(File.separatorChar);
				if (idx > 0)
					fileName = fileName.substring(idx + 1);
				String dmwithFile = servicesDMs.get(i).dataModel + "  ("
						+ fileName + ")";
				return dmwithFile;
			}
		});
	}

	/**
	 * Get the profile list from the rootDM.
	 * If a selected profiles file is present in the target DM directory then
	 * set the profiles selected.
	 * @return
	 */
	private SelectList getProfileList() {

		SelectList retList = new SelectList(rootDM.getProfiles());
		// if selected-profile.xml is present then initialize SelectList.
		File f = new File(gOpt.cWMPcDataModelDir
				+ GlobalOptions.DM_SELECTED_PROFILES);
		if (f.exists()) {
			BuilderProfiles selectedProfiles = new BuilderProfiles(
					gOpt.cWMPcDataModelDir + GlobalOptions.DM_SELECTED_PROFILES);
			ArrayList<String> selNames = selectedProfiles.getProfileNames();
			for (String s : selNames) {
				retList.setSelected(s);
			}
		}
		return retList;
	}

	public class XmlFilter extends FileFilter {
		// Accept all directories and all xml files.
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			String ext = null;
			String s = f.getName();
			int i = s.lastIndexOf('.');

			if (i > 0 && i < s.length() - 1) {
				ext = s.substring(i + 1).toLowerCase();
			}
			return (ext != null && ext.equals("xml"));
		}

		// Filter description
		public String getDescription() {
			return "XML files";
		}
	}

	/** Creates new form StartUp */
	public StartUp() {
		// restore program state.
		try {
			FileInputStream fs = new FileInputStream(DMSTATEFILE);
			ObjectInputStream in = new ObjectInputStream(fs);
			gOpt = (GlobalOptions) in.readObject();
			in.close();
		} catch (FileNotFoundException e) {
			System.err.println("Unable to open state file.");
		} catch (IOException e) {
			System.err.println("Unable to read state file.");
		} catch (ClassNotFoundException ex) {
			System.err
					.println("Unable to restore GlobalOptions from state file");
		}

		initComponents();

		if (gOpt.includeDir != null) {
			jImportDirTextField.setText(gOpt.includeDir);
			getDataModels();
		}
		if (gOpt.codeGenDirPath != null)
			jCWMPcDataModelDirTextField.setText(gOpt.codeGenDirPath);
		if (gOpt.cWMPcServiceDir != null)
			jCWMPcServiceDirTextField.setText(gOpt.cWMPcServiceDir);
		setEnable();
	}

	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jPanel2 = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel();
		jImportDirTextField = new javax.swing.JTextField();
		jImportDirActionButton = new javax.swing.JButton();
		jSourceFilePanel = new javax.swing.JPanel();
		jLabel4 = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		jModelList = new javax.swing.JList();
		jLabel1 = new javax.swing.JLabel();
		jCWMPcDataModelDirTextField = new javax.swing.JTextField();
		jCWMPcDataModelDirActionButton = new javax.swing.JButton();
		jLabel3 = new javax.swing.JLabel();
		jServicesDMFilesPanel = new javax.swing.JPanel();
		jLabel5 = new javax.swing.JLabel();
		jScrollPane2 = new javax.swing.JScrollPane();
		jServicesDMNameList = new javax.swing.JList();
		jLabel7 = new javax.swing.JLabel();
		jLabel6 = new javax.swing.JLabel();
		jCWMPcServiceDirTextField = new javax.swing.JTextField();
		jCWMPcServiceDirActionButton = new javax.swing.JButton();
		jScrollPane3 = new javax.swing.JScrollPane();
		jLogTextArea = new javax.swing.JTextArea();
		jButtonProfileFilter = new javax.swing.JButton();
		jResetButton = new javax.swing.JButton();
		jLabel9 = new javax.swing.JLabel();
		jButtonGenerateCode = new javax.swing.JButton();
		jLabel10 = new javax.swing.JLabel();
		jLabel8 = new javax.swing.JLabel();
		jButtonNormalize = new javax.swing.JButton();
		overRideButton = new javax.swing.JButton();
		jLabel11 = new javax.swing.JLabel();
		jMenuBar1 = new javax.swing.JMenuBar();
		jFileMenu = new javax.swing.JMenu();
		jMenuItem1 = new javax.swing.JMenuItem();
		jFileExitMenu = new javax.swing.JMenuItem();
		jMenu2 = new javax.swing.JMenu();
		jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
		jCheckBoxMenuItem2 = new javax.swing.JCheckBoxMenuItem();
		jMenuHelp = new javax.swing.JMenu();
		jMenuItemHelp = new javax.swing.JMenuItem();
		jMenuItemAbout = new javax.swing.JMenuItem();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("TR-069 Data Model Normalizer");
		setBounds(new java.awt.Rectangle(0, 0, 100, 512));

		jPanel2.setBorder(javax.swing.BorderFactory
				.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
		jPanel2.setMaximumSize(new java.awt.Dimension(300, 50));

		jLabel2.setText("Broadband Forum Data Model Directory:");

		jImportDirTextField.setEnabled(false);
		jImportDirTextField.setMaximumSize(new java.awt.Dimension(200, 200));
		jImportDirTextField
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jImportDirTextFieldActionPerformed(evt);
					}
				});

		jImportDirActionButton.setText("Browse");
		jImportDirActionButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jBrowseButtonAction(evt);
					}
				});

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(
				jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout
				.setHorizontalGroup(jPanel2Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								jPanel2Layout
										.createSequentialGroup()
										.addComponent(
												jLabel2,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												322, Short.MAX_VALUE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jImportDirTextField,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												438,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(18, 18, 18)
										.addComponent(jImportDirActionButton)
										.addGap(109, 109, 109)));
		jPanel2Layout
				.setVerticalGroup(jPanel2Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel2Layout
										.createSequentialGroup()
										.addGroup(
												jPanel2Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																jImportDirActionButton,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																26,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																jImportDirTextField,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																jLabel2,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																34,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		jSourceFilePanel.setBorder(javax.swing.BorderFactory
				.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

		jLabel4.setText("Select Data Model Name and Version:");

		jScrollPane1.setMaximumSize(new java.awt.Dimension(300, 400));

		jModelList.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "<empty>" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		jModelList
				.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		jModelList
				.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
					public void valueChanged(
							javax.swing.event.ListSelectionEvent evt) {
						jRootDMListValueChanged(evt);
					}
				});
		jScrollPane1.setViewportView(jModelList);

		jLabel1.setText("CWMPc Data Model directory");

		jCWMPcDataModelDirTextField
				.setFont(new java.awt.Font("Courier", 1, 12));
		jCWMPcDataModelDirTextField.setEnabled(false);
		jCWMPcDataModelDirTextField.setMaximumSize(new java.awt.Dimension(200,
				50));
		jCWMPcDataModelDirTextField
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jCWMPcDataModelDirTextFieldActionPerformed(evt);
					}
				});

		jCWMPcDataModelDirActionButton.setText("Browse");
		jCWMPcDataModelDirActionButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jCWMPcDataModelDirActionButtonjBrowseButtonAction(evt);
					}
				});

		jLabel3.setText("for device.");

		javax.swing.GroupLayout jSourceFilePanelLayout = new javax.swing.GroupLayout(
				jSourceFilePanel);
		jSourceFilePanel.setLayout(jSourceFilePanelLayout);
		jSourceFilePanelLayout
				.setHorizontalGroup(jSourceFilePanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jSourceFilePanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jSourceFilePanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																javax.swing.GroupLayout.Alignment.TRAILING,
																jSourceFilePanelLayout
																		.createSequentialGroup()
																		.addGroup(
																				jSourceFilePanelLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								jLabel3,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								105,
																								javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addComponent(
																								jLabel1,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								253,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jCWMPcDataModelDirTextField,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				496,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addGap(18,
																				18,
																				18)
																		.addComponent(
																				jCWMPcDataModelDirActionButton)
																		.addGap(108,
																				108,
																				108))
														.addGroup(
																jSourceFilePanelLayout
																		.createSequentialGroup()
																		.addGroup(
																				jSourceFilePanelLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								jScrollPane1,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								461,
																								javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addComponent(
																								jLabel4))
																		.addContainerGap(
																				504,
																				Short.MAX_VALUE)))));
		jSourceFilePanelLayout
				.setVerticalGroup(jSourceFilePanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jSourceFilePanelLayout
										.createSequentialGroup()
										.addGap(18, 18, 18)
										.addGroup(
												jSourceFilePanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jSourceFilePanelLayout
																		.createSequentialGroup()
																		.addGroup(
																				jSourceFilePanelLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								jCWMPcDataModelDirActionButton,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								26,
																								javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addComponent(
																								jCWMPcDataModelDirTextField,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								25,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
														.addGroup(
																javax.swing.GroupLayout.Alignment.TRAILING,
																jSourceFilePanelLayout
																		.createSequentialGroup()
																		.addComponent(
																				jLabel1)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jLabel3)
																		.addGap(4,
																				4,
																				4)))
										.addComponent(jLabel4)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jScrollPane1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												131,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap()));

		jServicesDMFilesPanel.setBorder(javax.swing.BorderFactory
				.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
		jServicesDMFilesPanel.setMaximumSize(new java.awt.Dimension(400, 200));

		jLabel5.setText("Select Optional Services Data Models");

		jScrollPane2.setMaximumSize(new java.awt.Dimension(400, 400));

		jServicesDMNameList.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "<empty>" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		jServicesDMNameList
				.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		jServicesDMNameList
				.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
					public void valueChanged(
							javax.swing.event.ListSelectionEvent evt) {
						jServicesDMNameListValueChanged(evt);
					}
				});
		jScrollPane2.setViewportView(jServicesDMNameList);

		jLabel7.setText("for service :    ");

		jLabel6.setText("CWMPc Data Model directory");

		jCWMPcServiceDirTextField.setFont(new java.awt.Font("Courier", 1, 12));
		jCWMPcServiceDirTextField.setEnabled(false);
		jCWMPcServiceDirTextField
				.setMaximumSize(new java.awt.Dimension(300, 20));
		jCWMPcServiceDirTextField
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jCWMPcServiceDirTextFieldActionPerformed(evt);
					}
				});

		jCWMPcServiceDirActionButton.setText("Browse");
		jCWMPcServiceDirActionButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jCWMPcServiceDirActionButtonjBrowseButtonAction(evt);
					}
				});

		javax.swing.GroupLayout jServicesDMFilesPanelLayout = new javax.swing.GroupLayout(
				jServicesDMFilesPanel);
		jServicesDMFilesPanel.setLayout(jServicesDMFilesPanelLayout);
		jServicesDMFilesPanelLayout
				.setHorizontalGroup(jServicesDMFilesPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jServicesDMFilesPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jServicesDMFilesPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jServicesDMFilesPanelLayout
																		.createSequentialGroup()
																		.addGroup(
																				jServicesDMFilesPanelLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								jScrollPane2,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								463,
																								javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addComponent(
																								jLabel5,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								345,
																								javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addContainerGap(
																				502,
																				Short.MAX_VALUE))
														.addGroup(
																jServicesDMFilesPanelLayout
																		.createSequentialGroup()
																		.addGroup(
																				jServicesDMFilesPanelLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addGroup(
																								jServicesDMFilesPanelLayout
																										.createSequentialGroup()
																										.addComponent(
																												jLabel7,
																												javax.swing.GroupLayout.DEFAULT_SIZE,
																												javax.swing.GroupLayout.DEFAULT_SIZE,
																												Short.MAX_VALUE)
																										.addGap(687,
																												687,
																												687))
																						.addGroup(
																								jServicesDMFilesPanelLayout
																										.createSequentialGroup()
																										.addComponent(
																												jLabel6,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												221,
																												javax.swing.GroupLayout.PREFERRED_SIZE)
																										.addGap(42,
																												42,
																												42)
																										.addComponent(
																												jCWMPcServiceDirTextField,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												496,
																												javax.swing.GroupLayout.PREFERRED_SIZE)
																										.addGap(18,
																												18,
																												18)))
																		.addGap(18,
																				18,
																				18)
																		.addComponent(
																				jCWMPcServiceDirActionButton)
																		.addContainerGap(
																				92,
																				Short.MAX_VALUE)))));
		jServicesDMFilesPanelLayout
				.setVerticalGroup(jServicesDMFilesPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jServicesDMFilesPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jServicesDMFilesPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																javax.swing.GroupLayout.Alignment.TRAILING,
																jServicesDMFilesPanelLayout
																		.createSequentialGroup()
																		.addGroup(
																				jServicesDMFilesPanelLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								jLabel6)
																						.addComponent(
																								jCWMPcServiceDirTextField,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jLabel7)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jLabel5,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				39,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jScrollPane2,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				108,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addComponent(
																jCWMPcServiceDirActionButton,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																26,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addContainerGap()));

		jScrollPane3.setBorder(javax.swing.BorderFactory
				.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
		jScrollPane3.setMaximumSize(new java.awt.Dimension(224, 32767));

		jLogTextArea.setColumns(20);
		jLogTextArea.setRows(5);
		jLogTextArea.setMaximumSize(new java.awt.Dimension(300, 32000));
		jScrollPane3.setViewportView(jLogTextArea);

		jButtonProfileFilter.setText("Profile Filter");
		jButtonProfileFilter.setEnabled(false);
		jButtonProfileFilter
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jButtonProfileFilterActionPerformed(evt);
					}
				});

		jResetButton.setText("Reset");
		jResetButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jResetButtonActionPerformed(evt);
			}
		});

		jLabel9.setText("Select supported profiles from Data Model.");

		jButtonGenerateCode.setText("Generate Code");
		jButtonGenerateCode.setEnabled(false);
		jButtonGenerateCode
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jButtonGenerateCodeActionPerformed(evt);
					}
				});

		jLabel10.setText("Generate tables and stubs in CWMPc directories.");

		jLabel8.setText("Create DM schema instance file in CWMPc data model directory.");

		jButtonNormalize.setText("Normalize");
		jButtonNormalize.setActionCommand("jNormalize");
		jButtonNormalize.setEnabled(false);
		jButtonNormalize.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonNormalizeActionPerformed(evt);
			}
		});

		overRideButton.setText("Object/param");
		overRideButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonObjectParamSelection(evt);
			}
		});

		jLabel11.setText("Override Object and Parameter Selections");

		jMenuBar1.setMaximumSize(new java.awt.Dimension(119, 50));

		jFileMenu.setText("File");

		jMenuItem1.setText("Options");
		jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuOptionsActionPerformed(evt);
			}
		});
		jFileMenu.add(jMenuItem1);

		jFileExitMenu.setText("Exit");
		jFileExitMenu.setMaximumSize(new java.awt.Dimension(400, 32767));
		jFileExitMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jFileExitMenuActionPerformed(evt);
			}
		});
		jFileMenu.add(jFileExitMenu);

		jMenuBar1.add(jFileMenu);

		jMenu2.setText("View");
		jMenu2.setEnabled(false);

		jCheckBoxMenuItem1.setSelected(true);
		jCheckBoxMenuItem1.setText("Parser Error Output");
		jCheckBoxMenuItem1.setMaximumSize(new java.awt.Dimension(400, 32767));
		jMenu2.add(jCheckBoxMenuItem1);

		jCheckBoxMenuItem2.setSelected(true);
		jCheckBoxMenuItem2.setText("Normalized Data Model");
		jCheckBoxMenuItem2.setMaximumSize(new java.awt.Dimension(400, 32767));
		jMenu2.add(jCheckBoxMenuItem2);

		jMenuBar1.add(jMenu2);

		jMenuHelp.setText("Help");

		jMenuItemHelp.setText("Help");
		jMenuItemHelp.setMaximumSize(new java.awt.Dimension(400, 400));
		jMenuItemHelp.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItemHelpActionPerformed(evt);
			}
		});
		jMenuHelp.add(jMenuItemHelp);

		jMenuItemAbout.setText("About");
		jMenuItemAbout.setMaximumSize(new java.awt.Dimension(400, 400));
		jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItemAboutActionPerformed(evt);
			}
		});
		jMenuHelp.add(jMenuItemAbout);

		jMenuBar1.add(jMenuHelp);

		setJMenuBar(jMenuBar1);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		jScrollPane3,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		821,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addContainerGap())
												.addGroup(
														javax.swing.GroupLayout.Alignment.TRAILING,
														layout.createSequentialGroup()
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.TRAILING)
																				.addComponent(
																						jResetButton,
																						javax.swing.GroupLayout.Alignment.LEADING,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						125,
																						Short.MAX_VALUE)
																				.addComponent(
																						jButtonGenerateCode,
																						javax.swing.GroupLayout.Alignment.LEADING,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						Short.MAX_VALUE)
																				.addComponent(
																						jButtonProfileFilter,
																						javax.swing.GroupLayout.Alignment.LEADING,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						125,
																						Short.MAX_VALUE)
																				.addComponent(
																						overRideButton,
																						javax.swing.GroupLayout.Alignment.LEADING,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						125,
																						Short.MAX_VALUE)
																				.addComponent(
																						jButtonNormalize,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						125,
																						Short.MAX_VALUE))
																.addGap(35, 35,
																		35)
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addGroup(
																						layout.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING,
																								false)
																								.addGroup(
																										layout.createSequentialGroup()
																												.addComponent(
																														jLabel10,
																														javax.swing.GroupLayout.PREFERRED_SIZE,
																														381,
																														javax.swing.GroupLayout.PREFERRED_SIZE)
																												.addPreferredGap(
																														javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																														javax.swing.GroupLayout.DEFAULT_SIZE,
																														Short.MAX_VALUE))
																								.addGroup(
																										javax.swing.GroupLayout.Alignment.TRAILING,
																										layout.createSequentialGroup()
																												.addGroup(
																														layout.createParallelGroup(
																																javax.swing.GroupLayout.Alignment.LEADING)
																																.addComponent(
																																		jLabel9,
																																		javax.swing.GroupLayout.PREFERRED_SIZE,
																																		430,
																																		javax.swing.GroupLayout.PREFERRED_SIZE)
																																.addComponent(
																																		jLabel8,
																																		javax.swing.GroupLayout.PREFERRED_SIZE,
																																		430,
																																		javax.swing.GroupLayout.PREFERRED_SIZE))
																												.addGap(344,
																														344,
																														344)))
																				.addComponent(
																						jLabel11,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						430,
																						javax.swing.GroupLayout.PREFERRED_SIZE))
																.addGap(1376,
																		1376,
																		1376))
												.addGroup(
														layout.createSequentialGroup()
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.TRAILING,
																				false)
																				.addComponent(
																						jPanel2,
																						javax.swing.GroupLayout.Alignment.LEADING,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						Short.MAX_VALUE)
																				.addComponent(
																						jServicesDMFilesPanel,
																						javax.swing.GroupLayout.Alignment.LEADING,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						Short.MAX_VALUE)
																				.addComponent(
																						jSourceFilePanel,
																						javax.swing.GroupLayout.Alignment.LEADING,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						Short.MAX_VALUE))
																.addContainerGap()))));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addComponent(jPanel2,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jSourceFilePanel,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(18, 18, 18)
								.addComponent(jServicesDMFilesPanel,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(
														jButtonNormalize,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														36, Short.MAX_VALUE)
												.addComponent(jLabel8))
								.addGap(18, 18, 18)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		jButtonProfileFilter)
																.addGap(18, 18,
																		18))
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		jLabel9,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE)
																.addGap(28, 28,
																		28)))
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING,
												false)
												.addComponent(
														jLabel11,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.addComponent(
														overRideButton,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE))
								.addGap(32, 32, 32)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(
														jLabel10,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														26, Short.MAX_VALUE)
												.addComponent(
														jButtonGenerateCode))
								.addGap(33, 33, 33)
								.addComponent(jResetButton)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jScrollPane3,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										375,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap()));

		pack();
	}// </editor-fold>
	//GEN-END:initComponents

	private void jMenuOptionsActionPerformed(java.awt.event.ActionEvent evt) {
		new OptionsDialog(this, true).setVisible(true);
	}

	private void jButtonGenerateCodeActionPerformed(
			java.awt.event.ActionEvent evt) {
		//TODO: make these setable.
		String dmName = gOpt.codeGenDirPath + GlobalOptions.DM_INSTANCE_FILE; /* name DM instance file */
		String tablesName = gOpt.codeGenDirPath + GlobalOptions.GEN_SUBDIR
				+ File.separatorChar + GlobalOptions.TABLES_FILE;
		String stubsName = gOpt.codeGenDirPath + GlobalOptions.GEN_SUBDIR
				+ File.separatorChar + GlobalOptions.STUBS_PREFIX;
		String selectedProfiles = gOpt.codeGenDirPath
				+ GlobalOptions.DM_SELECTED_PROFILES;
		String codeGenModel = gOpt.codeGenDirPath
				+ GlobalOptions.CODE_GEN_DM_INSTANCE;
		int indx;
		if (gOpt.codeGenDirPath.endsWith(File.separator)) {
			indx = gOpt.codeGenDirPath.lastIndexOf(File.separatorChar,
					gOpt.codeGenDirPath.lastIndexOf(File.separatorChar) - 1);
		} else
			indx = gOpt.codeGenDirPath.lastIndexOf(File.separatorChar);
		String cWMPcDir = gOpt.codeGenDirPath.substring(0, indx);
		// check code gen directory exists.

		File genDir = new File(gOpt.codeGenDirPath + GlobalOptions.GEN_SUBDIR);
		if (!genDir.exists())
			genDir.mkdir();
		// move old code if present
		MergeCode merger = new MergeCode(cWMPcDir, gOpt.codeGenDirPath,
				cWMPcDir + File.separatorChar + GlobalOptions.TEMPLATE_DIR);
		CWMPcCodeGenerator cGen = new CWMPcCodeGenerator(codeGenModel, dmName,
				selectedProfiles, selectedCustom);
		cGen.cwmpCDir = cWMPcDir;
		cGen.tablePreamble = cWMPcDir + File.separatorChar
				+ GlobalOptions.TABLES_PREAMBLE;
		cGen.functionsPreamble = cWMPcDir + File.separatorChar
				+ GlobalOptions.FUNCTIONS_PREAMBLE;
		cGen.genSCReference = gOpt.genSCReference;
		cGen.seperateStubs = gOpt.seperateStubs;
		cGen.genObjTypeDefs = gOpt.genObjTypeDefs;
		cGen.codeGenDir = gOpt.codeGenDirPath;
		cGen.genAliasInstanceGetSet = gOpt.genAliasGetSet;
		cGen.genAddObjStaticInstanceStubs = gOpt.genAddObjStaticInstanceStubs;
		cGen.ignoreDeprecated = gOpt.ignoreDeprecated;
		cGen.ignoreDeprecatedAlias = gOpt.ignoreDeprecatedAlias;

		if (!cGen.run(tablesName, stubsName))
			System.err.println("CWMPcCodeGenerator run failed");

		merger.mergeImplementation();
	}

	protected void jCWMPcDataModelDirActionButtonjBrowseButtonAction(
			ActionEvent evt) {
		fc = new JFileChooser();
		// fc.setCurrentDirectory(new java.io.File("."));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(true);
		int returnVal = fc.showOpenDialog(jPanel2);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			jLogTextArea.append("Choose directory: " + file.getName() + "."
					+ newline);
			String fName = file.getName();
			String fDirName = fc.getCurrentDirectory().getAbsolutePath();
			jCWMPcDataModelDirTextField.setText(fDirName + File.separatorChar
					+ fName);
			if (!file.exists()) {
				File dmdir = new File(fDirName + File.separatorChar + fName);
				if (!dmdir.mkdir()) {
					jLogTextArea.append("Unable to create new dir: " + fDirName
							+ File.separatorChar + fName);
					System.err.println("Unable to create new dir: " + fDirName
							+ File.separatorChar + fName);
				}
			}
			gOpt.cWMPcDataModelDir = fDirName + File.separatorChar + fName;
			if (gOpt.cWMPcDataModelDir.lastIndexOf(File.separatorChar) != gOpt.cWMPcDataModelDir
					.length() - 1)
				gOpt.cWMPcDataModelDir += "/";
			jLogTextArea.append("CWMPc Data Model directory path: "
					+ gOpt.cWMPcDataModelDir + newline);

		} else {
			jLogTextArea.append("command cancelled by user." + newline);
		}
		jLogTextArea.setCaretPosition(jLogTextArea.getDocument().getLength());
		setEnable();

	}

	protected void jCWMPcDataModelDirTextFieldActionPerformed(ActionEvent evt) {
		jLogTextArea.append(jCWMPcDataModelDirTextField.getText());

	}

	protected void jCWMPcServiceDirTextFieldActionPerformed(ActionEvent evt) {
		jLogTextArea.append(jCWMPcServiceDirTextField.getText());

	}

	protected void jCWMPcServiceDirActionButtonjBrowseButtonAction(
			ActionEvent evt) {
		fc = new JFileChooser();
		// fc.setCurrentDirectory(new java.io.File("."));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(true);
		int returnVal = fc.showOpenDialog(jPanel2);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			jLogTextArea.append("Choose directory: " + file.getName() + "."
					+ newline);
			String fName = file.getName();
			String fDirName = fc.getCurrentDirectory().getAbsolutePath();
			jCWMPcServiceDirTextField.setText(fDirName + File.separatorChar
					+ fName);
			gOpt.cWMPcServiceDir = fDirName + File.separatorChar + fName;
			if (gOpt.cWMPcServiceDir.lastIndexOf(File.separatorChar) != gOpt.cWMPcServiceDir
					.length() - 1)
				gOpt.cWMPcServiceDir += "/";
			// kludge for handling a single DM
			gOpt.cWMPcDataModelDir = gOpt.cWMPcServiceDir;
			jLogTextArea.append("CWMPc Service directory path: "
					+ gOpt.cWMPcServiceDir + newline);

		} else {
			jLogTextArea.append("command cancelled by user." + newline);
		}
		jLogTextArea.setCaretPosition(jLogTextArea.getDocument().getLength());
		setEnable();

	}

	private void jResetButtonActionPerformed(java.awt.event.ActionEvent evt) {

		selectedModel = -1;
		selectedService = -1;
		builder = null;
		profileList = null;
		gOpt.cWMPcServiceDir = null;
		gOpt.cWMPcDataModelDir = null;
		jModelList.setEnabled(true);
		jServicesDMNameList.setEnabled(true);
		jButtonNormalize.setEnabled(false);
		jButtonProfileFilter.setEnabled(false);
		overRideButton.setEnabled(false);
		jLogTextArea.setText(null);
		setEnable();
		getDataModels();
		repaint();
	}

	private void jButtonProfileFilterActionPerformed(
			java.awt.event.ActionEvent evt) {
		profileList = getProfileList();
		ProfileFilterList pfList = new ProfileFilterList(profileList);
		pfForm = new ProfileFilterForm(pfList.getpList());
		pfForm.addChangeListener(this); // Add this as listener to the Profileform buttons.

	}

	private void jButtonObjectParamSelection(java.awt.event.ActionEvent evt) {
		/*****
		BuilderProfiles selectedProfiles = null;
		DModelTree dmTree = new DModelTree(gOpt.codeGenDirPath + gOpt.DM_INSTANCE_FILE);
		File f = new File(gOpt.cWMPcDataModelDir + gOpt.DM_SELECTED_PROFILES);
		if (f.exists()) {
			selectedProfiles = new BuilderProfiles(gOpt.cWMPcDataModelDir
					+ GlobalOptions.DM_SELECTED_PROFILES);
		}
		objectTree = dmTree.init(selectedProfiles);
		dmForm = new DMSelectionForm(objectTree);
		dmForm.addChangeListener(this);
		 ******/
		/*** alternate object/parameter selection ****/
		dmTreeTable = new DModelTreeTable(gOpt.codeGenDirPath
				+ gOpt.DM_INSTANCE_FILE, gOpt.cWMPcDataModelDir
				+ GlobalOptions.DM_SELECTED_PROFILES, selectedCustom);
		JTreeTable treeTable = dmTreeTable.init();
		dtForm = new DMTreeTableSelectionForm(treeTable);
		dtForm.addChangeListener(this);
	}

	private void jButtonNormalizeActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			FileInputStream fs = new FileInputStream(gOpt.cWMPcDataModelDir
					+ CUSTOMOBJPARAMFILE);
			ObjectInputStream in = new ObjectInputStream(fs);
			selectedCustom = (CustomObjParam) in.readObject();
			in.close();
		} catch (FileNotFoundException e) {
			System.err.println("Unable to open custom selected file.");
		} catch (IOException e) {
			System.err.println("Unable to read custom selected file.");
		} catch (ClassNotFoundException ex) {
			System.err
					.println("Unable to restore CustomObjParam from state file");
		}
		runBuilder();
	}

	private void jMenuItemHelpActionPerformed(java.awt.event.ActionEvent evt) {
		new HelpForm().setVisible(true);
	}

	private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {
		new AboutDialog(this, true).setVisible(true);
	}

	private void jRootDMListValueChanged(
			javax.swing.event.ListSelectionEvent evt) {
		if ((!evt.getValueIsAdjusting()) || (evt.getFirstIndex() == -1))
			return;
		// only single selection is allowed.
		selectedModel = -1;
		for (int i = evt.getFirstIndex(); i <= evt.getLastIndex(); ++i) {
			if (((JList) evt.getSource()).isSelectedIndex(i)) {
				selectedModel = i;
				//jServicesDMNameList.setEnabled(false);
			}
		}
		setEnable();
	}

	private void jServicesDMNameListValueChanged(
			javax.swing.event.ListSelectionEvent evt) {
		if (servicesDMs.size() > 0) {
			if ((!evt.getValueIsAdjusting()) || (evt.getFirstIndex() == -1))
				return;
			selectedService = -1;
			for (int i = evt.getFirstIndex(); i <= evt.getLastIndex(); i++) {
				if (((JList) evt.getSource()).isSelectedIndex(i)) {
					selectedService = i;
					//jModelList.setEnabled(false);
				}
			}
		}
		setEnable();

	}

	private void jFileExitMenuActionPerformed(java.awt.event.ActionEvent evt) {
		dispose();
		System.out.println("save state");
		FileOutputStream fs;
		ObjectOutputStream out;

		try {
			fs = new FileOutputStream(DMSTATEFILE);
			out = new ObjectOutputStream(fs);
			out.writeObject(gOpt);
			out.close();
		} catch (FileNotFoundException e) {
			System.err.println("Unable to open state saving file.");
		} catch (IOException e) {
			System.err.println("Unable to save state file.");
		}
		try {
			fs = new FileOutputStream(gOpt.cWMPcDataModelDir
					+ CUSTOMOBJPARAMFILE);
			out = new ObjectOutputStream(fs);
			out.writeObject(selectedCustom);
			out.close();
		} catch (FileNotFoundException e) {
			System.err.println("Unable to open " + CUSTOMOBJPARAMFILE
					+ " file.");
		} catch (IOException e) {
			System.err.println("Unable to save " + CUSTOMOBJPARAMFILE
					+ " file.");
		}
		System.exit(0);
	}

	private void jImportDirTextFieldActionPerformed(
			java.awt.event.ActionEvent evt) {
		jLogTextArea.append(jImportDirTextField.getText());
	}

	private void jBrowseButtonAction(java.awt.event.ActionEvent e) {
		if (e.getSource() == jImportDirActionButton) {
			fc = new JFileChooser();
			// fc.setCurrentDirectory(new java.io.File("."));
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setAcceptAllFileFilterUsed(true);
			int returnVal = fc.showOpenDialog(jPanel2);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				jLogTextArea.append("Choose directory: " + file.getName() + "."
						+ newline);
				String fName = file.getName();
				String fDirName = fc.getCurrentDirectory().getAbsolutePath();
				jImportDirTextField.setText(fDirName + File.separatorChar
						+ fName);
				gOpt.includeDir = fDirName + File.separatorChar + fName;
				jLogTextArea.append("Include directory path: "
						+ gOpt.includeDir + newline);
				getDataModels();

			} else {
				jLogTextArea.append("command cancelled by user." + newline);
			}
			jLogTextArea.setCaretPosition(jLogTextArea.getDocument()
					.getLength());
		}
		setEnable();

	}

	private String getFullPathName(TreePath tp) {
		StringBuilder path = new StringBuilder();
		Object[] nodes = tp.getPath();
		for (int n = 1; n < nodes.length; ++n) {
			DefaultMutableTreeNode tNode = (DefaultMutableTreeNode) nodes[n];
			path.append(tNode.toString());
		}
		return path.toString();

	}

	/**
	 * Return an array of selected names. Each name is a full
	 * path name of the object or parameter.
	 * @return
	 */
	@SuppressWarnings("unused")
	private ArrayList<String> getSelected(JTree tree) {
		ArrayList<String> sList = new ArrayList<String>();
		int selected[] = tree.getSelectionRows();
		for (int sel : selected) {
			TreePath sPath = tree.getPathForRow(sel);
			String path = getFullPathName(sPath);
			sList.add(path);
			System.out.println("Selected node: " + path);
		}
		return sList;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource().equals(pfForm)) {
			// Check exit conditions for the ProfileFilterForm 
			System.out.println("ChangeEvent from pf form");
			if (pfForm.isCancel()) {
				jLogTextArea.append("Profile filter form cancelled");
			} else if (pfForm.isApply()) {
				jLogTextArea.append("Profile filter applied");
				rootDM.saveProfileXml(gOpt.cWMPcDataModelDir
						+ GlobalOptions.DM_SELECTED_PROFILES,
						profileList.getSelectedNames());
			}
			pfForm.dispose();
			pfForm = null;
			repaint();
			/******
			} else if (e.getSource().equals(dmForm)) {
				System.out.println("ChangeEvent from dmForm");
				if (dmForm.isApply()) {
					ObjectTree ot = dmForm.getObjectTree();
					selectedCustom = ot.getCustomSelected();
					dmForm.dispose();
					dmForm = null;
				} else if (dmForm.isCancel()) {
					dmForm.dispose();
					dmForm = null;
				}
				repaint();
			} ******/
		} else if (e.getSource().equals(dtForm)) {
			if (dtForm.isApply()) {
				System.out.println("ChangeEvent from dtForm: Apply");
				selectedCustom = dmTreeTable.getDataModelModel()
						.getSelectedCustom();
				dtForm.dispose();
				dtForm = null;
			} else if (dtForm.isCancel()) {
				System.out.println("ChangeEvent from dtForm: Cancel");
				dtForm.dispose();
				dtForm = null;
			} else {
				// assume reset-all and clear the selected custom settings.
				selectedCustom = null;
			}
			repaint();
		}

	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new StartUp().setVisible(true);
			}
		});

	}

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JButton jButtonGenerateCode;
	private javax.swing.JButton jButtonNormalize;
	private javax.swing.JButton jButtonProfileFilter;
	private javax.swing.JButton jCWMPcDataModelDirActionButton;
	private javax.swing.JTextField jCWMPcDataModelDirTextField;
	private javax.swing.JButton jCWMPcServiceDirActionButton;
	private javax.swing.JTextField jCWMPcServiceDirTextField;
	private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
	private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem2;
	private javax.swing.JMenuItem jFileExitMenu;
	private javax.swing.JMenu jFileMenu;
	private javax.swing.JButton jImportDirActionButton;
	private javax.swing.JTextField jImportDirTextField;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel10;
	private javax.swing.JLabel jLabel11;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JLabel jLabel9;
	private javax.swing.JTextArea jLogTextArea;
	private javax.swing.JMenu jMenu2;
	private javax.swing.JMenuBar jMenuBar1;
	private javax.swing.JMenu jMenuHelp;
	private javax.swing.JMenuItem jMenuItem1;
	private javax.swing.JMenuItem jMenuItemAbout;
	private javax.swing.JMenuItem jMenuItemHelp;
	private javax.swing.JList jModelList;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JButton jResetButton;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane3;
	private javax.swing.JPanel jServicesDMFilesPanel;
	private javax.swing.JList jServicesDMNameList;
	private javax.swing.JPanel jSourceFilePanel;
	private javax.swing.JButton overRideButton;
	// End of variables declaration//GEN-END:variables

}