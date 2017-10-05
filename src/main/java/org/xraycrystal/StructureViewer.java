package org.xraycrystal;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import org.jmol.adapter.smarter.AtomSetCollection;
import org.jmol.adapter.smarter.AtomSetCollectionReader;
import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolAdapter;

import org.xraycrystal.StructureGLListener.ShowAtoms;

import javax.swing.*;
import javax.vecmath.Point3f;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class StructureViewer {
    private float[] atomsTransMat = {
            1f, 0f, 0f,
            0f, 1f, 0f,
            0f, 0f, 1f
    };

    private int oldMouseX = 0;
    private int oldMouseY = 0;

    private JmolAdapter adapter;

    private JFileChooser fc;
    private String file;

    private GLCanvas structureView;

    public StructureViewer(){
        SwingUtilities.invokeLater(this::initUI);
    }

    private void initUI() {
        adapter = new SmarterJmolAdapter();

        GLCapabilities config = new GLCapabilities(GLProfile.get(GLProfile.GL4));

        JFrame frame = new JFrame("Structure viewer");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        frame.setLayout(layout);

        config.setSampleBuffers(true);
        config.setNumSamples(4);
        structureView = new GLCanvas(config);
        StructureGLListener structureRenderer = new StructureGLListener();
        structureView.addGLEventListener(structureRenderer);
        structureView.setPreferredSize(new Dimension(768,768));

        JPanel cellsNumberPanel = new JPanel();

        cellsNumberPanel.add(new JLabel("Number of unit cells: "));

        JLabel cellsNumberLbl = new JLabel("1");
        cellsNumberPanel.add(cellsNumberLbl);

        JSlider cellsNumberSlider = new JSlider(0, 10);
        cellsNumberSlider.setValue(1);
        cellsNumberSlider.setMajorTickSpacing(5);
        cellsNumberSlider.setMinorTickSpacing(1);

        Hashtable<Integer, JLabel> cellsSliderLabels = new Hashtable<>();
        cellsSliderLabels.put(1, new JLabel("1"));
        cellsSliderLabels.put(5, new JLabel("5"));
        cellsSliderLabels.put(10, new JLabel("10"));
        cellsNumberSlider.setLabelTable(cellsSliderLabels);
        cellsNumberSlider.setPaintLabels(true);

        JButton readFileBtn = new JButton("Open file...");

        c.insets.set(6,6,6,6);

        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 10;

        frame.add(structureView, c);

        c.insets.set(6,6,3,6);

        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;

        c.insets.set(3,6,3,6);

        c.gridy = 1;
        frame.add(cellsNumberPanel, c);

        c.gridy = 2;
        frame.add(cellsNumberSlider, c);

        c.insets.set(3,6,6,6);

        c.gridy = 3;
        frame.add(readFileBtn, c);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        cellsNumberSlider.addChangeListener(e ->{
            int cellsNumber = cellsNumberSlider.getValue();

            cellsNumberLbl.setText(String.valueOf(cellsNumber));

            if(null != file && !file.isEmpty()){
                AtomSetCollection atoms = loadFile(file, cellsNumber);

                structureRenderer.setAtoms(atoms, cellsNumberSlider.getValue() == 0 ? ShowAtoms.UNEQUIVALENT : ShowAtoms.ALL);

                structureView.display();
            }
        });

        readFileBtn.addActionListener(e -> {
            if(null == fc) {
                fc = new JFileChooser(new File("."));
                fc.setMultiSelectionEnabled(false);
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            }

            int retval = fc.showOpenDialog(frame);
            if(JFileChooser.APPROVE_OPTION == retval){
                file = fc.getSelectedFile().getAbsolutePath();
                AtomSetCollection atoms = loadFile(file, cellsNumberSlider.getValue());

                structureRenderer.setAtoms(atoms, cellsNumberSlider.getValue() == 0 ? ShowAtoms.UNEQUIVALENT : ShowAtoms.ALL);

                structureView.display();
            }

        });

        structureView.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                updateTransMatrix(e);

                structureRenderer.setTransformMatrix(atomsTransMat);

                structureView.display();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                updateMousePos(e);
            }
        });
    }

    private void updateMousePos(MouseEvent e) {
        oldMouseX = e.getX();
        oldMouseY = e.getY();
    }

    private void updateTransMatrix(MouseEvent e) {
        double w = 0.01;
        float cosX = (float)Math.cos(w*(e.getX() - oldMouseX));
        float sinX = (float)Math.sin(w*(e.getX() - oldMouseX));

        float cosY = (float)Math.cos(w*(e.getY() - oldMouseY));
        float sinY = (float)Math.sin(w*(e.getY() - oldMouseY));

        updateMousePos(e);

        float[] My = {
                cosX, 0, sinX,
                0, 1,    0,
                -sinX, 0, cosX
        };

        float[] Mx = {
                1,    0,     0,
                0, cosY, -sinY,
                0, sinY,  cosY
        };

        float[] diffMatrix = Utils.matMul( My, Mx, 3);

        atomsTransMat = Utils.matMul(atomsTransMat, diffMatrix, 3);
    }

    private AtomSetCollection loadFile(String name, int cellsNumber){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(name)));

            Map<String, Object> htParams = new HashMap<>();
            htParams.put("spaceGroupIndex", -1);
            if(0 < cellsNumber) {
                htParams.put("lattice", new Point3f(cellsNumber, cellsNumber, cellsNumber));
            } else {
                htParams.put("lattice", new Point3f(1, 1, 1));
            }
            htParams.put("packed", true);

            AtomSetCollectionReader fileReader = (AtomSetCollectionReader) adapter.getAtomSetCollectionReader(name, null, reader, htParams);

            Object result =  adapter.getAtomSetCollection(fileReader);
            if(result instanceof AtomSetCollection){
                return (AtomSetCollection) result;
            } else if (result instanceof String) {
                throw new IOError(new Error((String)result));
            } else {
                throw new AssertionError("Unhandled read result type");
            }
        } catch (FileNotFoundException e){
            throw new IOError(e);
        }
    }


    public static void main(String[] args){
        GLProfile.initSingleton();
        new StructureViewer();
    }
}
