/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import bench.Benchmark;
import bench.BenchmarkParameters;
import core.client.ClientsFactory;
import core.server.ServersFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import util.ObservableLog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author dsavvinov
 */
public class MainWindow extends javax.swing.JFrame implements Observer {
    private final Path root;
    private final String serverAddress;
    private final int port;

    public MainWindow(Path root, String serverAddress, int port) {
        this.root = root;
        this.serverAddress = serverAddress;
        this.port = port;
        initComponents();
    }

    public MainWindow(Path root) {
        this(root, "localhost", 10000);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> update(o, arg));
            return;
        }

        if (o instanceof ObservableLog) {
            logTextarea.append((String) arg);
        }
        repaint();
        invalidate();
    }

    /**
     * A lot of NetBeans-generated code
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        logTextarea = new javax.swing.JTextArea();
        logLabel = new javax.swing.JLabel();
        verboseLogCheckbox = new javax.swing.JCheckBox();
        clientTypeCombobox = new javax.swing.JComboBox<>();
        serverTypeCombobox = new javax.swing.JComboBox<>();
        serverConnectionTypeLabel = new javax.swing.JLabel();
        clientTypeLabel = new javax.swing.JLabel();
        serverProcessingType = new javax.swing.JComboBox<>();
        serverProcessingLabel = new javax.swing.JLabel();
        arraySizeLabel = new javax.swing.JLabel();
        arraySizeFromTextfield = new javax.swing.JTextField();
        arraySizeToTextfield = new javax.swing.JTextField();
        clientsAmountLabel = new javax.swing.JLabel();
        clientsAmountFromTextfield = new javax.swing.JTextField();
        clientsAmountToTextarea = new javax.swing.JTextField();
        deltaLabel = new javax.swing.JLabel();
        deltaFromTextfield = new javax.swing.JTextField();
        deltaToTextfield = new javax.swing.JTextField();
        deltaLabel1 = new javax.swing.JLabel();
        retriesTextfield = new javax.swing.JTextField();
        arraySizeStepTextfield = new javax.swing.JTextField();
        clientsAmountStepTextfield = new javax.swing.JTextField();
        delayStepTextfield = new javax.swing.JTextField();
        startButton = new javax.swing.JButton();
        runningTimePane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        arraySizeVariableRadiobutton = new javax.swing.JRadioButton();
        clientsAmountVariableRadiobutton = new javax.swing.JRadioButton();
        delayVariableRadiobutton = new javax.swing.JRadioButton();
        variableButtonGroup = new ButtonGroup();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        logTextarea.setEditable(false);
        logTextarea.setColumns(20);
        logTextarea.setRows(5);
        jScrollPane1.setViewportView(logTextarea);

        logLabel.setText("Log");

        verboseLogCheckbox.setText("Verbose");

        clientTypeCombobox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Single, TCP", "Multiple, TCP", "Multiple, UDP" }));

        serverTypeCombobox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TCP", "UDP" }));
        serverTypeCombobox.addItemListener(this::serverTypeComboboxItemStateChanged);

        serverProcessingType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Asynchronous", "Cached thread pool", "Raw threads", "Non-blocking", "Sequential" }));

        serverConnectionTypeLabel.setText("Server connection:");

        clientTypeLabel.setText("Client type:");

        serverProcessingLabel.setText("Server processing:");

        arraySizeLabel.setText("N");

        arraySizeFromTextfield.setText("From");
        arraySizeFromTextfield.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                arraySizeFromTextfieldFocusGained(evt);
            }
        });

        arraySizeToTextfield.setText("To");
        arraySizeToTextfield.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                arraySizeToTextfieldFocusGained(evt);
            }
        });

        clientsAmountLabel.setText("M");

        clientsAmountFromTextfield.setText("From");
        clientsAmountFromTextfield.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                clientsAmountFromTextfieldFocusGained(evt);
            }
        });

        clientsAmountToTextarea.setText("To");
        clientsAmountToTextarea.setEnabled(false);
        clientsAmountToTextarea.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                clientsAmountToTextareaFocusGained(evt);
            }
        });

        deltaLabel.setText("Delta");

        deltaFromTextfield.setText("From");
        deltaFromTextfield.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                deltaFromTextfieldFocusGained(evt);
            }
        });

        deltaToTextfield.setText("To");
        deltaToTextfield.setEnabled(false);
        deltaToTextfield.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                deltaToTextfieldFocusGained(evt);
            }
        });

        deltaLabel1.setText("X");

        arraySizeStepTextfield.setText("Step");
        arraySizeStepTextfield.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                arraySizeStepTextfieldFocusGained(evt);
            }
        });

        clientsAmountStepTextfield.setText("Step");
        clientsAmountStepTextfield.setEnabled(false);
        clientsAmountStepTextfield.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                clientsAmountStepTextfieldFocusGained(evt);
            }
        });

        delayStepTextfield.setText("Step");
        delayStepTextfield.setEnabled(false);
        delayStepTextfield.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                delayStepTextfieldFocusGained(evt);
            }
        });

        startButton.setText("Start!");
        startButton.addActionListener(this::startButtonActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 886, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 398, Short.MAX_VALUE)
        );

        runningTimePane.addTab("Client time", jPanel1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 886, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 398, Short.MAX_VALUE)
        );

        runningTimePane.addTab("Query time", jPanel2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 886, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 398, Short.MAX_VALUE)
        );

        runningTimePane.addTab("Running time", jPanel3);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 886, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 398, Short.MAX_VALUE)
        );

        runningTimePane.addTab("Server-side overhead", jPanel4);

        arraySizeVariableRadiobutton.setSelected(true);
        arraySizeVariableRadiobutton.setText("Var");
        arraySizeVariableRadiobutton.addItemListener(this::arraySizeVariableRadiobuttonItemStateChanged);

        clientsAmountVariableRadiobutton.setText("Var");
        clientsAmountVariableRadiobutton.addItemListener(this::clientsAmountVariableRadiobuttonItemStateChanged);

        delayVariableRadiobutton.setText("Var");
        delayVariableRadiobutton.addItemListener(this::delayVariableRadiobuttonItemStateChanged);

        variableButtonGroup.add(arraySizeVariableRadiobutton);
        variableButtonGroup.add(clientsAmountVariableRadiobutton);
        variableButtonGroup.add(delayVariableRadiobutton);
        setFocusTraversalPolicy(new FocusTraversalPolicy() {
            private Component[] order = new Component[]{
                    arraySizeFromTextfield,
                    arraySizeToTextfield,
                    arraySizeStepTextfield,
                    deltaFromTextfield,
                    deltaToTextfield,
                    delayStepTextfield,
                    clientsAmountFromTextfield,
                    clientsAmountToTextarea,
                    clientsAmountStepTextfield,
                    retriesTextfield
            };

            @Override
            public Component getComponentAfter(Container aContainer, Component aComponent) {
                boolean found = false;
                for (int i = 0; i < order.length; i++) {
                    if (order[i] == aComponent) {
                        found = true;
                        continue;
                    }
                    if (found && order[i].isEnabled()) {
                        return order[i];
                    }
                }
                return order[0];
            }

            @Override
            public Component getComponentBefore(Container aContainer, Component aComponent) {
                boolean found = false;
                for (int i = order.length - 1; i >=  0; i--) {
                    if (order[i] == aComponent) {
                        found = true;
                        continue;
                    }
                    if (found && order[i].isEnabled()) {
                        return order[i];
                    }
                }
                return order[0];
            }

            @Override
            public Component getFirstComponent(Container aContainer) {
                return order[0];
            }

            @Override
            public Component getLastComponent(Container aContainer) {
                return order[order.length - 1];
            }

            @Override
            public Component getDefaultComponent(Container aContainer) {
                return order[0];
            }
        });
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(clientTypeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(270, 270, 270)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(deltaLabel1)
                                    .addComponent(deltaLabel))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(deltaFromTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(retriesTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addComponent(deltaToTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(delayStepTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(delayVariableRadiobutton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 442, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(logLabel)
                                        .addGap(18, 18, 18)
                                        .addComponent(verboseLogCheckbox))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(clientTypeCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(140, 140, 140)
                                .addComponent(serverConnectionTypeLabel)
                                .addGap(18, 18, 18)
                                .addComponent(serverTypeCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(140, 140, 140)
                                .addComponent(serverProcessingLabel)
                                .addGap(18, 18, 18)
                                .addComponent(serverProcessingType, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(runningTimePane, javax.swing.GroupLayout.Alignment.CENTER)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(clientsAmountLabel)
                                            .addComponent(arraySizeLabel))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(arraySizeFromTextfield)
                                            .addComponent(clientsAmountFromTextfield))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(clientsAmountToTextarea, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(clientsAmountStepTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(clientsAmountVariableRadiobutton))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(arraySizeToTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(arraySizeStepTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(arraySizeVariableRadiobutton)))))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(489, 489, 489)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, arraySizeFromTextfield, arraySizeStepTextfield, arraySizeToTextfield, clientsAmountFromTextfield, clientsAmountStepTextfield, clientsAmountToTextarea, delayStepTextfield, deltaFromTextfield, deltaToTextfield);

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clientTypeCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(serverTypeCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(serverConnectionTypeLabel)
                    .addComponent(clientTypeLabel)
                    .addComponent(serverProcessingType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(serverProcessingLabel))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(logLabel)
                            .addComponent(verboseLogCheckbox))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(arraySizeLabel)
                            .addComponent(arraySizeFromTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(arraySizeToTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(deltaLabel)
                            .addComponent(deltaFromTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(deltaToTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(arraySizeStepTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(delayStepTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(arraySizeVariableRadiobutton)
                            .addComponent(delayVariableRadiobutton))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(clientsAmountLabel)
                            .addComponent(clientsAmountFromTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(clientsAmountToTextarea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(deltaLabel1)
                            .addComponent(retriesTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(clientsAmountStepTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(clientsAmountVariableRadiobutton))
                        .addGap(18, 18, 18)
                        .addComponent(runningTimePane)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(startButton)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int arraySize = Integer.parseInt(arraySizeFromTextfield.getText());
        int delay = Integer.parseInt(deltaFromTextfield.getText());
        int clientsAmount = Integer.parseInt(clientsAmountFromTextfield.getText());
        int retries = Integer.parseInt(retriesTextfield.getText());

        int step;
        int finish;
        BenchmarkParameters.VariableType variableType;
        if (arraySizeVariableRadiobutton.isSelected()) {
            variableType = BenchmarkParameters.VariableType.ARRAY_SIZE;
            step = Integer.parseInt(arraySizeStepTextfield.getText());
            finish = Integer.parseInt(arraySizeToTextfield.getText());
        } else if (delayVariableRadiobutton.isSelected()) {
            variableType = BenchmarkParameters.VariableType.DELAY;
            step = Integer.parseInt(delayStepTextfield.getText());
            finish = Integer.parseInt(deltaToTextfield.getText());
        } else {
            variableType = BenchmarkParameters.VariableType.CLIENTS_AMOUNT;
            step = Integer.parseInt(clientsAmountStepTextfield.getText());
            finish = Integer.parseInt(clientsAmountToTextarea.getText());
        }

        String clientTypeString = (String) clientTypeCombobox.getSelectedItem();
        ClientsFactory.ClientType clientType;
        if (clientTypeString.equals("Single, TCP")) {
            clientType = ClientsFactory.ClientType.SINGLE_TCP;
        } else if (clientTypeString.equals("Multiple, TCP")) {
            clientType = ClientsFactory.ClientType.MULTIPLE_TCP;
        } else {
            clientType = ClientsFactory.ClientType.MULTIPLE_UDP;
        }

        String servConn = (String) serverTypeCombobox.getSelectedItem();
        String servProc = (String) serverProcessingType.getSelectedItem();
        ServersFactory.ServerType serverType;
        if (servConn.equals("TCP") ) {
            if (servProc.equals("Asynchronous")) {
                serverType = ServersFactory.ServerType.ASYNC_TCP;
            } else if (servProc.equals("Cached thread pool")) {
                serverType = ServersFactory.ServerType.CACHED_POOL_TCP;
            } else if (servProc.equals("Raw threads")) {
                serverType = ServersFactory.ServerType.MULTITHREAD_TCP;
            } else if (servProc.equals("Non-blocking")) {
                serverType = ServersFactory.ServerType.NIO_TCP;
            } else {
                serverType = ServersFactory.ServerType.SINGLE_THREAD_TCP;
            }
        } else {
            if (servProc.equals("Fixed thread pool")) {
                serverType = ServersFactory.ServerType.FIXED_POOL_UDP;
            } else {
                serverType = ServersFactory.ServerType.MULTITHREAD_UDP;
            }
        }


        BenchmarkParameters params = new BenchmarkParameters.BenchmarkParametersBuilder()
                .setVariableType(variableType)
                .setServerHost(serverAddress)
                .setFinishValue(finish)
                .setRetries(retries)
                .setArraySize(arraySize)
                .setClientsAmount(clientsAmount)
                .setClientType(clientType)
                .setDelay(delay)
                .setServerPort(port)
                .setServerType(serverType)
                .setFinishValue(finish)
                .setStep(step)
                .build();

        ObservableLog log = new ObservableLog(verboseLogCheckbox.isSelected());
        log.addObserver(this);

        logTextarea.setText("");

        new SwingWorker<Void, Void>() {
            private final Benchmark benchmark;
            {
                benchmark = new Benchmark(params, log);
            }
            @Override
            protected Void doInBackground() throws Exception {
                benchmark.start();
                benchmark.saveStatistics(root);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(null, "Error occured during benchmark");
                    log.error("Error in benchmark: " + e.getMessage());
                    return;
                }
                JOptionPane.showMessageDialog(null, "Finished benchmark!");

                List<Integer> varValues = benchmark.getVarValues();
                List<Double> clientTimeValues = benchmark.getClientTimeValues();
                List<Double> queryTimeValues = benchmark.getQueryTimeValues();
                List<Double> runningTimeValues = benchmark.getRunningTimeValues();

                XYSeries clientTimeSeries = new XYSeries("Client time");
                XYSeries queryTimeSeries = new XYSeries("Query time");
                XYSeries runningTimeSeries = new XYSeries("Running time");
                XYSeries serverOverheadTimeSeries = new XYSeries("Server-side overhead");
                for (int i = 0; i < varValues.size(); i++) {
                    Double overhead = queryTimeValues.get(i) - clientTimeValues.get(i);
                    clientTimeSeries.add(varValues.get(i), clientTimeValues.get(i));
                    queryTimeSeries.add(varValues.get(i), queryTimeValues.get(i));
                    runningTimeSeries.add(varValues.get(i), runningTimeValues.get(i));
                    serverOverheadTimeSeries.add(varValues.get(i), overhead);
                }

                XYSeriesCollection clientTimeData = new XYSeriesCollection(clientTimeSeries);
                XYSeriesCollection queryTimeData = new XYSeriesCollection(queryTimeSeries);
                XYSeriesCollection runningTimeData = new XYSeriesCollection(runningTimeSeries);
                XYSeriesCollection overheadTimeData = new XYSeriesCollection(serverOverheadTimeSeries);

                JFreeChart clientTimeChart = ChartFactory.createXYLineChart("Client time",
                        benchmark.getVariableName(), "ms", clientTimeData,
                        PlotOrientation.VERTICAL, true, true, false);
                JFreeChart queryTimeChart = ChartFactory.createXYLineChart("Query time",
                        benchmark.getVariableName(), "ms", queryTimeData,
                        PlotOrientation.VERTICAL, true, true, false);
                JFreeChart runningTimeChart = ChartFactory.createXYLineChart("Total running time",
                        benchmark.getVariableName(), "ms", runningTimeData,
                        PlotOrientation.VERTICAL, true, true, false);
                JFreeChart overheadTimeChart = ChartFactory.createXYLineChart("Server-side overhead (client - query)",
                        benchmark.getVariableName(), "ms", overheadTimeData,
                        PlotOrientation.VERTICAL, true, true, false);

                runningTimePane.setComponentAt(0, new ChartPanel(clientTimeChart));
                runningTimePane.setComponentAt(1, new ChartPanel(queryTimeChart));
                runningTimePane.setComponentAt(2, new ChartPanel(runningTimeChart));
                runningTimePane.setComponentAt(3, new ChartPanel(overheadTimeChart));
            }
        }.execute();
    }

    private void serverTypeComboboxItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            String type = (String) evt.getItem();
            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) serverProcessingType.getModel();
            if (type.equals("TCP")) {
                model.removeAllElements();
                model.addElement("Asynchronous");
                model.addElement("Cached thread pool");
                model.addElement("Raw threads");
                model.addElement("Non-blocking");
                model.addElement("Sequential");
            } else {
                model.removeAllElements();
                model.addElement("Fixed thread pool");
                model.addElement("Raw threads");
            }
        }
    }

    private void arraySizeFromTextfieldFocusGained(java.awt.event.FocusEvent evt) {
        ((JTextField)evt.getComponent()).setText("");
    }

    private void arraySizeToTextfieldFocusGained(java.awt.event.FocusEvent evt) {
        ((JTextField)evt.getComponent()).setText("");
    }

    private void arraySizeStepTextfieldFocusGained(java.awt.event.FocusEvent evt) {
        ((JTextField)evt.getComponent()).setText("");
    }

    private void clientsAmountFromTextfieldFocusGained(java.awt.event.FocusEvent evt) {
        ((JTextField)evt.getComponent()).setText("");
    }

    private void clientsAmountToTextareaFocusGained(java.awt.event.FocusEvent evt) {
        ((JTextField)evt.getComponent()).setText("");
    }

    private void clientsAmountStepTextfieldFocusGained(java.awt.event.FocusEvent evt) {
        ((JTextField)evt.getComponent()).setText("");
    }

    private void deltaFromTextfieldFocusGained(java.awt.event.FocusEvent evt) {
        ((JTextField)evt.getComponent()).setText("");
    }

    private void deltaToTextfieldFocusGained(java.awt.event.FocusEvent evt) {
        ((JTextField)evt.getComponent()).setText("");
    }

    private void delayStepTextfieldFocusGained(java.awt.event.FocusEvent evt) {
        ((JTextField)evt.getComponent()).setText("");
    }

    private void arraySizeVariableRadiobuttonItemStateChanged(java.awt.event.ItemEvent evt) {
        boolean newState;
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            newState = true;
        } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
            newState = false;
        } else return;
        arraySizeToTextfield.setEnabled(newState);
        arraySizeStepTextfield.setEnabled(newState);
    }

    private void clientsAmountVariableRadiobuttonItemStateChanged(java.awt.event.ItemEvent evt) {
        boolean newState;
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            newState = true;
        } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
            newState = false;
        } else return;
        clientsAmountToTextarea.setEnabled(newState);
        clientsAmountStepTextfield.setEnabled(newState);
    }

    private void delayVariableRadiobuttonItemStateChanged(java.awt.event.ItemEvent evt) {
        boolean newState;
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            newState = true;
        } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
            newState = false;
        } else return;
        delayStepTextfield.setEnabled(newState);
        deltaToTextfield.setEnabled(newState);
    }

    public static void main(String args[]) {
        Path root = Paths.get(System.getProperty("user.dir"));
        if (args.length == 2) {
            java.awt.EventQueue.invokeLater(() -> new MainWindow(root, args[0], Integer.parseInt(args[1])).setVisible(true));
        } else {
            java.awt.EventQueue.invokeLater(() -> new MainWindow(root).setVisible(true));
        }
    }

    // Variables declaration - do not modify
    private javax.swing.JTextField arraySizeFromTextfield;
    private javax.swing.JLabel arraySizeLabel;
    private javax.swing.JTextField arraySizeStepTextfield;
    private javax.swing.JTextField arraySizeToTextfield;
    private javax.swing.JRadioButton arraySizeVariableRadiobutton;
    private javax.swing.JComboBox<String> clientTypeCombobox;
    private javax.swing.JLabel clientTypeLabel;
    private javax.swing.JTextField clientsAmountFromTextfield;
    private javax.swing.JLabel clientsAmountLabel;
    private javax.swing.JTextField clientsAmountStepTextfield;
    private javax.swing.JTextField clientsAmountToTextarea;
    private javax.swing.JRadioButton clientsAmountVariableRadiobutton;
    private javax.swing.JTextField delayStepTextfield;
    private javax.swing.JRadioButton delayVariableRadiobutton;
    private javax.swing.JTextField deltaFromTextfield;
    private javax.swing.JLabel deltaLabel;
    private javax.swing.JLabel deltaLabel1;
    private javax.swing.JTextField deltaToTextfield;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea logTextarea;
    private javax.swing.JLabel logLabel;
    private javax.swing.JTextField retriesTextfield;
    private javax.swing.JTabbedPane runningTimePane;
    private javax.swing.JLabel serverConnectionTypeLabel;
    private javax.swing.JLabel serverProcessingLabel;
    private javax.swing.JComboBox<String> serverProcessingType;
    private javax.swing.JComboBox<String> serverTypeCombobox;
    private javax.swing.JButton startButton;
    private javax.swing.JCheckBox verboseLogCheckbox;
    private javax.swing.ButtonGroup variableButtonGroup;
    // End of variables declaration
}
