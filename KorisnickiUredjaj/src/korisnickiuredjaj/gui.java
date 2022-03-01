/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package korisnickiuredjaj;

/**
 *
 * @author Janko
 */
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.MaskFormatter;

class gui {

    private static JFrame homeFrame;
    private static JPanel panel = new JPanel();
    private static JLabel label;
    private static JTextField periodicAlarm;
    private static JFormattedTextField formatText;
    private static JComboBox timesCombo;
    private static JRadioButton commonTime;
    private static JRadioButton specificTime;
    private static JTextArea myTasks;
    private static JTextArea mySongs;
    private static JTextField time;
    private static JTextField timeOne;
    private static String username = "janko";
    private static String password = "janko";
    private static JCheckBox checkBox;

    public static void main(String args[]) {

        //Creating the Frame
        homeFrame = new JFrame("Smart House");
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homeFrame.setSize(500, 500);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        homeFrame.setLocation(dim.width / 2 - homeFrame.getSize().width / 2, dim.height / 2 - homeFrame.getSize().height / 2);
        makeMenu(homeFrame);
        //Creating the MenuBar and adding components
        label = new JLabel("Welcome", 0);
        Font f = new Font("TimesRoman", Font.BOLD, 35);
        label.setFont(f);
        panel.add(label); // Components Added using Flow Layout
        homeFrame.getContentPane().add(BorderLayout.CENTER, panel);
        homeFrame.setVisible(true);
        panel.setLayout(new GridLayout(0, 1));

    }

    private static MaskFormatter createFormatter(String s) {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(s);
        } catch (java.text.ParseException exc) {
            System.err.println("formatter is bad: " + exc.getMessage());
            System.exit(-1);
        }
        return formatter;
    }

    public static void makeNewAlarmFrame() {
        panel.removeAll();
        JButton ringtone = new JButton("Save Changes");
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        JPanel panel4 = new JPanel();
        JLabel ringtoneLabel = new JLabel("Change your Alarm Ringtone");
        JTextField ringtoneText = new JTextField("Enter new ringtone");
        JButton ringtoneButton = new JButton("Add new Alarm");

        panel4.add(BorderLayout.WEST, ringtoneLabel);
        panel4.add(BorderLayout.CENTER, ringtoneText);
        panel4.add(BorderLayout.EAST, ringtone);

        specificTime = new JRadioButton("Specific Time");
        specificTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JRadioButton check = (JRadioButton) e.getSource();
                if (check.isSelected()) {
                    gui.formatText.setEnabled(true);
                    gui.commonTime.setSelected(false);
                    gui.timesCombo.setEnabled(false);
                } else {
                    gui.formatText.setEnabled(false);
                }
            }
        });
        ringtone.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ringtoneText.getText().equals("Enter new ringtone")) {
                    Error error = new Error("Unesite zeljeno zvono");
                    return;
                }
                gui.changeRingtone(ringtoneText.getText());
            }
        });
        commonTime = new JRadioButton("Common Time");
        commonTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JRadioButton check = (JRadioButton) e.getSource();
                if (check.isSelected()) {
                    gui.timesCombo.setEnabled(true);
                    gui.specificTime.setSelected(false);
                    gui.formatText.setEnabled(false);
                } else {
                    gui.timesCombo.setEnabled(false);

                }
            }
        });
        String[] times = {"08:00", "10:00", "12:00", "14:00", "16:00", "18:00", "20:00"};
        timesCombo = new JComboBox(times);

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd-HH:mm");
        String dateString = formatter.format(date);
        formatText = new JFormattedTextField(createFormatter("####.##.##-##:##"));
        formatText.setColumns(20);
        formatText.setText(dateString);
        panel1.add(specificTime, BorderLayout.WEST);
        panel1.add(new JLabel("Enter Date and Time"), BorderLayout.CENTER);
        panel1.add(formatText, BorderLayout.EAST);
        gui.timesCombo.setEnabled(false);
        gui.formatText.setEnabled(false);
        panel.add(panel1);
        checkBox = new JCheckBox("Periodic?");
        periodicAlarm = new JTextField("Period in minutes");
        periodicAlarm.setEnabled(false);
        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox check = (JCheckBox) e.getSource();
                if (check.isSelected()) {
                    gui.periodicAlarm.setEnabled(true);
                } else {
                    gui.periodicAlarm.setEnabled(false);
                }
            }
        });
        ringtoneButton.setSize(70, 30);
        ringtoneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String date = "";
                int period;
                SimpleDateFormat formatter = new SimpleDateFormat("YYYY.mm.dd");
                Date currentDate = new Date();
                System.out.println(formatter.format(currentDate));
                if (commonTime.isSelected()) {
                    date = formatter.format(currentDate) + "-";
                    date += timesCombo.getSelectedItem().toString();
                    System.out.println(date);
                } else {
                    date = formatText.getText();
                }
                if (checkBox.isSelected()) {
                    try {
                        period = Integer.parseInt(periodicAlarm.getText());
                    } catch (Exception ex) {
                        Error error = new Error("Unesite frekvenciju Vaseg periodicnog alarma!");
                        return;
                    }
                } else {
                    period = 0;
                }
                gui.newAlarm(date, period, "n");
            }
        });
        panel2.add(BorderLayout.WEST, commonTime);
        panel2.add(BorderLayout.EAST, timesCombo);
        panel3.add(BorderLayout.WEST, checkBox);
        panel3.add(BorderLayout.EAST, periodicAlarm);
        JPanel panel5 = new JPanel();
        panel5.add(ringtoneButton, BorderLayout.CENTER);
        panel.add(panel2);
        panel.add(panel3);
        panel.add(panel4);
        panel.add(panel5);

        homeFrame.add(BorderLayout.CENTER, panel);
        homeFrame.validate();
    }

    public static void makeMenu(JFrame frame) {
        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("User");
        JMenu m2 = new JMenu("Alarm");
        JMenu m3 = new JMenu("Planner");
        JMenu m4 = new JMenu("Reproducter");
        mb.add(m1);
        mb.add(m2);
        mb.add(m3);
        mb.add(m4);

        JMenuItem m11 = new JMenuItem("Log In");
        JMenuItem m12 = new JMenuItem("Register");
        m11.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.login();
            }
        });
        m12.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.register();
            }
        });
        m1.add(m11);
        m1.add(m12);
        JMenuItem m21 = new JMenuItem("New Alarm");

        m21.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.makeNewAlarmFrame();
            }
        });
        m2.add(m21);

        JMenuItem m31 = new JMenuItem("Add new Task");
        m31.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.makenewPlaner();

            }
        });
        JMenuItem m32 = new JMenuItem("Change Task");
        m32.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.changePlaner();

            }
        });
        JMenuItem m33 = new JMenuItem("Delete Task");
        m33.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.deleteTask();

            }
        });
        JMenuItem m34 = new JMenuItem("Get my Tasks");
        m34.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.getMyTasks();

            }
        });
        JMenuItem m35 = new JMenuItem("Set Alarm for Task");
        m35.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.setAlarmTask();
            }
        });
        JMenuItem m36 = new JMenuItem("Time from A to B");
        m36.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.calculate();

            }
        });
        JMenuItem m37 = new JMenuItem("Time from current location to B");
        m37.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.calculateOneLocation();
            }
        });

        m3.add(m31);
        m3.add(m32);
        m3.add(m33);
        m3.add(m34);
        m3.add(m35);
        m3.add(m36);
        m3.add(m37);

        JMenuItem m41 = new JMenuItem("Play song");
        m41.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.playSong();

            }
        });
        JMenuItem m42 = new JMenuItem("Get my Songs");
        m42.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.getMySongs();

            }
        });
        m4.add(m41);
        m4.add(m42);
        frame.getContentPane().add(BorderLayout.NORTH, mb);
    }

    public static void makenewPlaner() {
        homeFrame.remove(panel);
        homeFrame.revalidate();
        panel.removeAll();

        JLabel title = new JLabel("Add New Task", 0);
        Font f = new Font("TimesRoman", Font.BOLD, 25);
        title.setFont(f);
        panel.add(title);
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        JPanel panel5 = new JPanel();
        JLabel nameLabel = new JLabel("Name of the Task");
        JTextField nameText = new JTextField();
        nameText.setColumns(20);
        panel5.add(BorderLayout.WEST, nameLabel);
        panel5.add(BorderLayout.EAST, nameText);
        JTextField destination = new JTextField("");
        destination.setColumns(20);
        JLabel startLabel = new JLabel("Enter start time: ");
        JLabel reminderLabel = new JLabel("Set a Reminder?");
        JLabel destinationLabel = new JLabel("Destination: ");
        JCheckBox reminder = new JCheckBox();
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd-HH:mm");
        String dateString = formatter.format(date);
        JFormattedTextField formatText1 = new JFormattedTextField(createFormatter("####.##.##-##:##"));
        formatText1.setColumns(20);
        formatText1.setText(dateString);
        JLabel duration = new JLabel("Enter Duration in minutes");
        JTextField durationText = new JTextField();
        durationText.setColumns(20);
        panel1.add(BorderLayout.WEST, startLabel);
        panel1.add(BorderLayout.EAST, formatText1);
        panel2.add(BorderLayout.WEST, duration);
        panel2.add(BorderLayout.EAST, durationText);
        panel3.add(BorderLayout.WEST, destinationLabel);
        panel3.add(BorderLayout.EAST, destination);

        JButton add = new JButton("Add");
        JPanel panel4 = new JPanel();
        panel4.add(add, BorderLayout.CENTER);
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nameText.getText().equals("")) {
                    Error error = new Error("Name of the Task is required!");
                    return;

                }
                if (durationText.getText().equals("")) {
                    Error error = new Error("Duration field is required!");
                    return;
                }

                gui.newPlanner(nameText.getText(), formatText1.getText(), durationText.getText(), destination.getText());
            }
        });
        panel.add(panel5);
        panel.add(panel1);
        panel.add(panel2);
        panel.add(panel3);
        panel.add(panel4);

        homeFrame.getContentPane().add(panel, BorderLayout.CENTER);
        homeFrame.validate();

    }

    public static void changePlaner() {
        homeFrame.remove(panel);
        homeFrame.revalidate();
        panel.removeAll();

        JLabel title = new JLabel("Change your Task", 0);
        Font f = new Font("TimesRoman", Font.BOLD, 25);
        title.setFont(f);
        panel.add(title);
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();

        JPanel panel3 = new JPanel();
        JPanel panel5 = new JPanel();
        JPanel panel6 = new JPanel();
        JLabel nameChangeLabel = new JLabel("Name of the Task you want to Change");
        JTextField nameChangeText = new JTextField();
        nameChangeText.setColumns(20);
        panel5.add(BorderLayout.WEST, nameChangeLabel);
        panel5.add(BorderLayout.EAST, nameChangeText);
        JLabel nameLabel = new JLabel("Name of the Task");
        JTextField nameText = new JTextField();
        nameText.setColumns(20);
        panel5.add(BorderLayout.WEST, nameLabel);
        panel5.add(BorderLayout.EAST, nameText);
        JTextField destination = new JTextField("");
        destination.setColumns(20);
        JLabel startLabel = new JLabel("Enter start time in yyyy.mm.dd-hh:mm format: ");
        JLabel reminderLabel = new JLabel("Set a Reminder?");
        JLabel destinationLabel = new JLabel("Destination: ");
        JCheckBox reminder = new JCheckBox();
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd-HH:mm");
        String dateString = formatter.format(date);
        JFormattedTextField formatText1 = new JFormattedTextField();
        formatText1.setColumns(20);
        JLabel duration = new JLabel("Enter Duration in minutes");
        JTextField durationText = new JTextField();
        durationText.setColumns(20);
        panel1.add(BorderLayout.WEST, startLabel);
        panel1.add(BorderLayout.EAST, formatText1);
        panel2.add(BorderLayout.WEST, duration);
        panel2.add(BorderLayout.EAST, durationText);
        panel3.add(BorderLayout.WEST, destinationLabel);
        panel3.add(BorderLayout.EAST, destination);

        JButton add = new JButton("Change");
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nameChangeText.getText().equals("")) {
                    Error error = new Error("Name of the Task you want to change is Required!");
                    return;
                }
                gui.changePlanner(nameChangeText.getText(), nameText.getText(), formatText1.getText(), durationText.getText(), destination.getText());
            }
        });
        panel.add(panel6);
        panel.add(panel5);
        panel.add(panel1);
        panel.add(panel2);
        panel.add(panel3);
        JPanel panel4 = new JPanel();
        panel4.add(add, BorderLayout.CENTER);
        panel.add(panel4);

        homeFrame.getContentPane().add(panel, BorderLayout.CENTER);
        homeFrame.validate();
    }

    public static void deleteTask() {
        homeFrame.remove(panel);
        homeFrame.revalidate();
        panel.removeAll();

        JLabel title = new JLabel("Delete Task", 0);
        Font f = new Font("TimesRoman", Font.BOLD, 25);
        title.setFont(f);
        panel.add(title);
        JPanel panel1 = new JPanel();
        JLabel nameLabel = new JLabel("Enter Name of the Task: ");
        JTextField nameText = new JTextField();
        nameText.setColumns(20);
        panel1.add(BorderLayout.WEST, nameLabel);
        panel1.add(BorderLayout.EAST, nameText);
        JButton add = new JButton("Delete");
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nameText.getText().equals("")) {
                    Error error = new Error("Enter name of the Task");
                    return;
                }
                gui.deletePlanner(nameText.getText());
            }
        });
        panel.add(panel1);
        JPanel panel2 = new JPanel();
        panel2.add(add, BorderLayout.CENTER);
        panel.add(panel2);
        homeFrame.getContentPane().add(panel, BorderLayout.CENTER);
        homeFrame.validate();

    }

    public static void setAlarmTask() {
        homeFrame.remove(panel);
        homeFrame.revalidate();
        panel.removeAll();

        JLabel title = new JLabel("Set an Alarm for Task", 0);
        Font f = new Font("TimesRoman", Font.BOLD, 25);
        title.setFont(f);
        panel.add(title);
        JPanel panel1 = new JPanel();
        JLabel nameLabel = new JLabel("Enter Name of the Task: ");
        JTextField nameText = new JTextField();
        nameText.setColumns(20);
        panel1.add(BorderLayout.WEST, nameLabel);
        panel1.add(BorderLayout.EAST, nameText);
        JButton add = new JButton("Set an Alarm");
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.setAlarm(nameText.getText());
            }
        });
        panel.add(panel1);
        JPanel panel2 = new JPanel();
        panel2.add(add, BorderLayout.CENTER);
        panel.add(panel2);
        homeFrame.getContentPane().add(panel, BorderLayout.CENTER);
        homeFrame.validate();

    }

    public static void getMyTasks() {
        homeFrame.remove(panel);
        homeFrame.revalidate();
        panel.removeAll();
        JLabel title = new JLabel("Get my Tasks", 0);
        Font f = new Font("TimesRoman", Font.BOLD, 25);
        title.setFont(f);
        panel.add(title);
        gui.myTasks = new JTextArea(20, 50);
        panel.add(myTasks);
        JButton getTasks = new JButton("Get my Tasks");
        getTasks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.getPlanner();
            }
        });
        JPanel panel2 = new JPanel();
        panel2.add(getTasks, BorderLayout.CENTER);
        panel.add(panel2);
        homeFrame.getContentPane().add(panel, BorderLayout.CENTER);
        homeFrame.validate();
    }

    public static void calculate() {
        homeFrame.remove(panel);
        homeFrame.revalidate();
        panel.removeAll();

//        panel.setLayout(new FlowLayout());
        JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayout(0, 2));

        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(2, 0));
        JLabel title = new JLabel("Calculate time from A to B", 0);
        Font f = new Font("TimesRoman", Font.BOLD, 25);
        title.setFont(f);
        panel.add(title);
        JLabel labelA = new JLabel("Location A");
        JLabel labelB = new JLabel("Location B");
        JTextField aText = new JTextField();
        aText.setColumns(10);
        JTextField bText = new JTextField();
        bText.setColumns(10);
        panel.add(labelA, BorderLayout.WEST);
        panel.add(aText, BorderLayout.EAST);
        panel.add(labelB, BorderLayout.WEST);
        panel.add(bText, BorderLayout.EAST);
        gui.time = new JTextField();
        gui.time.setColumns(20);
        panel.add(new JLabel("Duration:"), BorderLayout.WEST);
        panel.add(gui.time, BorderLayout.EAST);
//        panel.add(panel1);
//        panel.add(panel2);
        JButton calculate = new JButton("Calculate");
        calculate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (aText.getText().equals("") || bText.getText().equals("")) {
                    Error error = new Error("Both locations are required!");
                    return;
                }
                gui.calculate(aText.getText(), bText.getText());
            }
        });
        JPanel panel3 = new JPanel();
        panel3.add(calculate, BorderLayout.CENTER);
        panel.add(panel3);
        homeFrame.getContentPane().add(panel, BorderLayout.CENTER);
        homeFrame.validate();
    }

    public static void calculateOneLocation() {
        homeFrame.remove(panel);
        homeFrame.revalidate();
        panel.removeAll();

//        panel.setLayout(new FlowLayout());
        JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayout(0, 2));

        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(2, 0));
        JLabel title = new JLabel("Calculate time from Current Location to Destination", 0);
        Font f = new Font("TimesRoman", Font.BOLD, 25);
        title.setFont(f);
        panel.add(title);
        JLabel labelA = new JLabel("Destination: ");

        JTextField aText = new JTextField();
        aText.setColumns(10);

        panel.add(labelA, BorderLayout.WEST);
        panel.add(aText, BorderLayout.EAST);

        gui.timeOne = new JTextField();
        gui.timeOne.setColumns(20);
        panel.add(new JLabel("Duration:"), BorderLayout.WEST);
        panel.add(gui.timeOne, BorderLayout.EAST);
//        panel.add(panel1);
//        panel.add(panel2);
        JButton calculate = new JButton("Calculate");
        calculate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (aText.getText().equals("")) {
                    Error error = new Error("Both locations are required!");
                    return;
                }
                gui.calculateOne(aText.getText());
            }
        });
        JPanel panel3 = new JPanel();
        panel3.add(calculate, BorderLayout.CENTER);
        panel.add(panel3);
        homeFrame.getContentPane().add(panel, BorderLayout.CENTER);
        homeFrame.validate();
    }

    public static void getMySongs() {
        homeFrame.remove(panel);
        homeFrame.revalidate();
        panel.removeAll();
        JLabel title = new JLabel("Get my Songs", 0);
        Font f = new Font("TimesRoman", Font.BOLD, 25);
        title.setFont(f);
        panel.add(title);
        mySongs = new JTextArea(20, 50);
        panel.add(mySongs);
        JButton getTasks = new JButton("Get my Songs");
        getTasks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.getSongs();
            }
        });
        JPanel panel2 = new JPanel();
        panel2.add(getTasks, BorderLayout.CENTER);
        panel.add(panel2);
        homeFrame.getContentPane().add(panel, BorderLayout.CENTER);
        homeFrame.validate();
    }

    public static void playSong() {
        homeFrame.remove(panel);
        homeFrame.revalidate();
        panel.removeAll();
        panel.setLayout(new GridLayout(0, 1));
        JLabel title = new JLabel("Youtube Player", 0);
        Font f = new Font("TimesRoman", Font.BOLD, 25);
        title.setFont(f);
        panel.add(title);
        JPanel panel1 = new JPanel();
        JLabel nameLabel = new JLabel("Enter Name of the Song: ");
        JTextField nameText = new JTextField();
        nameText.setColumns(20);
        panel1.add(BorderLayout.WEST, nameLabel);
        panel1.add(BorderLayout.EAST, nameText);
        JButton add = new JButton("Play");
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.playSong(nameText.getText());
            }
        });
        panel.add(panel1);
        JPanel panel2 = new JPanel();
        panel2.add(add, BorderLayout.CENTER);
        panel.add(panel2);
        homeFrame.getContentPane().add(panel, BorderLayout.CENTER);
        homeFrame.validate();

    }

    private static void changeRingtone(String ringtone) {

        try {
            String stringUrl = "http://localhost:8080/UserService/resources/alarm/" + ringtone;
            URL url = new URL(stringUrl);
            System.out.println(ringtone);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            String authorization = gui.username + ":" + gui.password;
            byte[] authorizationBytes = authorization.getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(authorizationBytes));
            System.out.println(Base64.getEncoder().encodeToString(authorizationBytes));

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            System.out.println(response.toString());
        } catch (ProtocolException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void newAlarm(String date, int period, String name) {
        try {
            if (!commonTime.isSelected() && !specificTime.isSelected()) {
                Error error = new Error("Unesite datum i vreme Vaseg alarma!");
                return;
            }
            if (checkBox.isSelected() && periodicAlarm.getText().equals("Period in minutes")) {
                Error error = new Error("Unesite frekvenciju Vaseg periodicnog alarma!");
                return;
            }
            String stringUrl = "http://localhost:8080/UserService/resources/alarm/newAlarm/" + date + "/" + period + "/" + name;
            URL url = new URL(stringUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            System.out.println(gui.username);
            String authorization = gui.username + ":" + gui.password;
            byte[] authorizationBytes = authorization.getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(authorizationBytes));
            System.out.println(Base64.getEncoder().encodeToString(authorizationBytes));

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            System.out.println(response.toString());

        } catch (ProtocolException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void newAlarmTask(String name) {
        try {
            if (name.equals("")) {
                Error error = new Error("Name of the Task is required!");
                return;
            }
            String stringUrl = "http://localhost:8080/UserService/resources/alarm/newAlarmTask/" + name;
            URL url = new URL(stringUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            System.out.println(gui.username);
            String authorization = gui.username + ":" + gui.password;
            byte[] authorizationBytes = authorization.getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(authorizationBytes));
            System.out.println(Base64.getEncoder().encodeToString(authorizationBytes));

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            System.out.println(response.toString());

        } catch (ProtocolException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void newPlanner(String name, String date, String duration, String location) {
        try {
            if (location.equals("")) {
                location = "n";
            }

            String stringUrl = "http://localhost:8080/UserService/resources/planner/newPlanner/" + name + "/" + date + "/" + duration + "/" + location;

            System.out.println(stringUrl);
            URL url = new URL(stringUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            String authorization = gui.username + ":" + gui.password;
            byte[] authorizationBytes = authorization.getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(authorizationBytes));
            System.out.println(Base64.getEncoder().encodeToString(authorizationBytes));

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            System.out.println(response.toString());
        } catch (ProtocolException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void changePlanner(String nameChange, String name, String date, String duration, String location) {
        try {
            if (duration.equals("")) {
                duration = "n";
            }
            if (location.equals("")) {
                location = "n";
            }
            if (date.equals("")) {
                date = "n";
            }
            if (name.equals("")) {
                name = "n";
            }
            String stringUrl = "http://localhost:8080/UserService/resources/planner/changePlanner/" + name + "/" + nameChange + "/" + date + "/" + duration + "/" + location;

            System.out.println(stringUrl);
            URL url = new URL(stringUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            String authorization = gui.username + ":" + gui.password;
            byte[] authorizationBytes = authorization.getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(authorizationBytes));
            System.out.println(Base64.getEncoder().encodeToString(authorizationBytes));

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            System.out.println(response.toString());
        } catch (ProtocolException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void deletePlanner(String name) {
        try {

            String stringUrl = "http://localhost:8080/UserService/resources/planner/deletePlanner/" + name;

            System.out.println(stringUrl);
            URL url = new URL(stringUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            String authorization = gui.username + ":" + gui.password;
            byte[] authorizationBytes = authorization.getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(authorizationBytes));
            System.out.println(Base64.getEncoder().encodeToString(authorizationBytes));

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            System.out.println(response.toString());
        } catch (ProtocolException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void getPlanner() {
        try {

            String stringUrl = "http://localhost:8080/UserService/resources/planner";

            System.out.println(stringUrl);
            URL url = new URL(stringUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            String authorization = gui.username + ":" + gui.password;
            byte[] authorizationBytes = authorization.getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(authorizationBytes));
            System.out.println(Base64.getEncoder().encodeToString(authorizationBytes));
            if (connection.getResponseCode() == 200) {
                System.out.println("Uspesan zahtev");
                BufferedReader buffreader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuffer buffer = new StringBuffer();
                while ((line = buffreader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                myTasks.setText(buffer.toString());
                System.out.println(buffer.toString());
            }

        } catch (ProtocolException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void setAlarm(String text) {
        gui.newAlarmTask(text);
    }

    private static void getSongs() {
        try {

            String stringUrl = "http://localhost:8080/UserService/resources/reproduct";

            System.out.println(stringUrl);
            URL url = new URL(stringUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            String authorization = gui.username + ":" + gui.password;
            byte[] authorizationBytes = authorization.getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(authorizationBytes));
            System.out.println(Base64.getEncoder().encodeToString(authorizationBytes));
            if (connection.getResponseCode() == 200) {
                System.out.println("Uspesan zahtev");
                BufferedReader buffreader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuffer buffer = new StringBuffer();
                while ((line = buffreader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                mySongs.setText(buffer.toString());
                System.out.println(buffer.toString());
            }

        } catch (ProtocolException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void playSong(String name) {
        try {
            if (name.equals("")) {
                Error error = new Error("Name of the Song is required!");
                return;
            }
            String stringUrl = "http://localhost:8080/UserService/resources/reproduct/" + name;

            System.out.println(stringUrl);
            URL url = new URL(stringUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            String authorization = gui.username + ":" + gui.password;
            byte[] authorizationBytes = authorization.getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(authorizationBytes));
            System.out.println(Base64.getEncoder().encodeToString(authorizationBytes));

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            System.out.println(response.toString());
        } catch (ProtocolException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void calculate(String locA, String locB) {

        try {
            String key = "AIzaSyDxhFqG90rQHN_QGdubeSkyDHtivpgaZJg";
            GeoApiContext gc = new GeoApiContext.Builder().apiKey(key).build();
            DistanceMatrixApiRequest request = DistanceMatrixApi.newRequest(gc);

            DistanceMatrix result = request.origins(locA)
                    .destinations(locB)
                    .mode(TravelMode.DRIVING)
                    .language("en-US")
                    .await();

//            String stringUrl = "http://localhost:8080/UserService/resources/planner/calculate/" + locA + "/" + locB;
//
//            System.out.println(stringUrl);
//            URL url = new URL(stringUrl);
//
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//
//            String authorization = "janko:janko";
//            byte[] authorizationBytes = authorization.getBytes(StandardCharsets.UTF_8);
//            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(authorizationBytes));
//            System.out.println(Base64.getEncoder().encodeToString(authorizationBytes));
//            if (connection.getResponseCode() == 200) {
//                System.out.println("Uspesan zahtev");
//                BufferedReader buffreader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                String line;
//                StringBuffer buffer = new StringBuffer();
//                while ((line = buffreader.readLine()) != null) {
//                    buffer.append(line).append("\n");
//                }
            Long val = result.rows[0].elements[0].duration.inSeconds;
            String text = "" + val / 3600 + "h ";
            val -= (val / 3600) * 3600;
            text += "" + val / 60 + "m ";
            val -= (val / 60) * 60;
            text += "" + val + "s";
            time.setText(text);

        } catch (ApiException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void calculateOne(String destination) {

        try {

            String stringUrl = "http://localhost:8080/UserService/resources/planner/calculate";

            System.out.println(stringUrl);
            URL url = new URL(stringUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            String authorization = gui.username + ":" + gui.password;
            byte[] authorizationBytes = authorization.getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(authorizationBytes));
            System.out.println(Base64.getEncoder().encodeToString(authorizationBytes));
            if (connection.getResponseCode() == 200) {
                System.out.println("Uspesan zahtev");
                BufferedReader buffreader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuffer buffer = new StringBuffer();
                while ((line = buffreader.readLine()) != null) {
                    buffer.append(line);
                }
                if (!buffer.toString().equals("All tasks are later!")) {
                    String key = "AIzaSyDxhFqG90rQHN_QGdubeSkyDHtivpgaZJg";
                    GeoApiContext gc = new GeoApiContext.Builder().apiKey(key).build();
                    DistanceMatrixApiRequest request = DistanceMatrixApi.newRequest(gc);

                    DistanceMatrix result = request.origins(buffer.toString())
                            .destinations(destination)
                            .mode(TravelMode.DRIVING)
                            .language("en-US")
                            .await();
                    Long val = result.rows[0].elements[0].duration.inSeconds;
                    String text = "" + val / 3600 + "h ";
                    val -= (val / 3600) * 3600;
                    text += "" + val / 60 + "m ";
                    val -= (val / 60) * 60;
                    text += "" + val + "s";
                    timeOne.setText(text);
                } else {
                    Error error = new Error("All tasks are later!");
                }
            }

        } catch (ApiException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void login(String userName, String password) {
        try {
            if (userName.equals("")) {
                Error error = new Error("Unesite korisnicko ime!");
                return;
            }
            if (password.equals("")) {
                Error error = new Error("Unesite lozinku");
                return;
            }
            String stringUrl = "http://localhost:8080/UserService/resources/user/login/" + userName + "/" + password;

            System.out.println(stringUrl);
            URL url = new URL(stringUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            String authorization = gui.username + ":" + gui.password;
            byte[] authorizationBytes = authorization.getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(authorizationBytes));
            System.out.println(Base64.getEncoder().encodeToString(authorizationBytes));

//             if (connection.getResponseCode() == 200) {
            System.out.println("Uspesan zahtev");
            BufferedReader buffreader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = buffreader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            System.out.println(buffer.toString());
            if (buffer.toString().equals("Login successful\n")) {

                System.out.println("ulogvan");
                gui.username = userName;
                gui.password = password;
            }

//            }
            Error e = new Error(buffer.toString());

        } catch (ProtocolException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void register(String userName, String password, String address) {
        try {
            if (userName.equals("")) {
                Error error = new Error("Username is required!");
                return;
            }
            if (password.equals("")) {
                Error error = new Error("Password is required!");
                return;
            }
            if (address.equals("")) {
                Error error = new Error("Address is required!");
                return;
            }
            String stringUrl = "http://localhost:8080/UserService/resources/user/register/" + userName + "/" + password + "/" + address;

            System.out.println(stringUrl);
            URL url = new URL(stringUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            String authorization = gui.username + ":" + gui.password;
            byte[] authorizationBytes = authorization.getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(authorizationBytes));
            System.out.println(Base64.getEncoder().encodeToString(authorizationBytes));

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            System.out.println(response.toString());
            Error error = new Error(response.toString());
        } catch (ProtocolException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void register() {

        homeFrame.remove(panel);
        homeFrame.revalidate();
        panel.removeAll();

        JLabel title = new JLabel("Register", 0);
        Font f = new Font("TimesRoman", Font.BOLD, 25);
        title.setFont(f);
        panel.add(title);
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();

        JPanel panel3 = new JPanel();

        JLabel usernameLabel = new JLabel("Username: ");
        JTextField usernameText = new JTextField();
        usernameText.setColumns(20);

        JTextField passwordText = new JTextField("");
        passwordText.setColumns(20);
        JLabel password = new JLabel("Password: ");
        JLabel addressLabel = new JLabel("Address");

        JTextField addressText = new JTextField();
        addressText.setColumns(20);
        panel1.add(BorderLayout.WEST, usernameLabel);
        panel1.add(BorderLayout.EAST, usernameText);
        panel2.add(BorderLayout.WEST, password);
        panel2.add(BorderLayout.EAST, passwordText);
        panel3.add(BorderLayout.WEST, addressLabel);
        panel3.add(BorderLayout.EAST, addressText);
        JPanel panel4=new JPanel();
        JButton add = new JButton("Register");
        panel4.add(add, BorderLayout.CENTER);
        
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.register(usernameText.getText(), passwordText.getText(), addressText.getText());
            }
        });

        panel.add(panel1);
        panel.add(panel2);
        panel.add(panel3);

        panel.add(panel4);

        homeFrame.getContentPane().add(panel, BorderLayout.CENTER);
        homeFrame.validate();

    }

    private static void login() {
        homeFrame.remove(panel);
        homeFrame.revalidate();
        panel.removeAll();

        JLabel title = new JLabel("Log In", 0);
        Font f = new Font("TimesRoman", Font.BOLD, 25);
        title.setFont(f);
        panel.add(title);
        JPanel panel1 = new JPanel();
        JPanel panel5 = new JPanel();
        JLabel nameLabel = new JLabel("Username: ");
        JTextField usernameText = new JTextField();
        usernameText.setColumns(20);
        panel5.add(BorderLayout.WEST, nameLabel);
        panel5.add(BorderLayout.EAST, usernameText);
        JTextField passwordText = new JTextField("");
        passwordText.setColumns(20);
        JLabel passwordLabel = new JLabel("Password: ");

        panel1.add(BorderLayout.WEST, passwordLabel);
        panel1.add(BorderLayout.EAST, passwordText);

        JButton add = new JButton("Log In");
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.login(usernameText.getText(), passwordText.getText());
            }
        });
        JPanel panel2=new JPanel();
        panel2.add(add,BorderLayout.CENTER);
        panel.add(panel5);
        panel.add(panel1);

        panel.add(panel2);

        homeFrame.getContentPane().add(panel, BorderLayout.CENTER);
        homeFrame.validate();

    }

}
