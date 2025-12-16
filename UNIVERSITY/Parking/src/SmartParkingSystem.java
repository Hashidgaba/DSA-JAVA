import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SmartParkingSystem extends JFrame {

    public static final Color COLOR_BG_START = new Color(0, 198, 255); // Cyan
    public static final Color COLOR_BG_END = new Color(0, 114, 255);   // Dark Blue
    public static final Color COLOR_TEXT = COLOR_BG_END;               // Consistent Text Color
    public static final Color COLOR_WHITE = Color.WHITE;
    public static final Font FONT_MAIN = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);

    // --- 2. GLOBAL DATA STORAGE ---
    static ArrayList<Driver> drivers = new ArrayList<>();
    static ArrayList<ParkingArea> parkingAreas = new ArrayList<>();
    static Driver currentLoggedInDriver = null;


    CardLayout cardLayout = new CardLayout();
    JPanel mainPanel = new JPanel();
    Image bgImage, profileIcon;

    // --- 4. CUSTOM UI CLASSES (Optimized & Animated) ---


    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            if (bgImage != null) {
                g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                GradientPaint gp = new GradientPaint(0, 0, COLOR_BG_START, getWidth(), getHeight(), COLOR_BG_END);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }


    class RoundPanel extends JPanel {
        public RoundPanel() { setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40); // Soft Edges
            super.paintComponent(g);
        }
    }


    class ModernButton extends JButton {
        private boolean isHovered = false;

        public ModernButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(FONT_MAIN);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Animation Logic: Hover par color change
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Hover Effect: Thoda Dark/Light karna
            Color c1 = isHovered ? COLOR_BG_END : COLOR_BG_START;
            Color c2 = isHovered ? COLOR_BG_START : COLOR_BG_END;

            GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), 0, c2);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            super.paintComponent(g);
        }
    }

    // Modern Text Field (Consistent UI)
    class ModernTextField extends JTextField {
        public ModernTextField(String placeholder) {
            setOpaque(false);
            setBorder(new EmptyBorder(10, 15, 10, 15));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setText(placeholder);
            setForeground(Color.GRAY);

            addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    if (getText().equals(placeholder)) { setText(""); setForeground(COLOR_TEXT); }
                }
                public void focusLost(FocusEvent e) {
                    if (getText().isEmpty()) { setText(placeholder); setForeground(Color.GRAY); }
                }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 30, 30);
            g2.setColor(COLOR_BG_END); // Border color consistent
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 30, 30);
            super.paintComponent(g);
        }
    }



    // A. Message Dialog (OK Button)
    public void showCustomDialog(String title, String message, int type) {
        JDialog dialog = new JDialog(this, title, true);
        setupDialog(dialog, message);

        ModernButton btnOk = new ModernButton("OK");
        btnOk.setPreferredSize(new Dimension(150, 45));
        btnOk.addActionListener(e -> dialog.dispose());

        addDialogComponents(dialog, btnOk);
        dialog.setVisible(true);
    }

    // B. Input Dialog (Car Number)
    public String showCustomInput(String message) {
        JDialog dialog = new JDialog(this, "Input", true);
        JLabel lblMsg = setupDialog(dialog, message);

        final String[] result = {null};
        ModernTextField txtInput = new ModernTextField("Type here...");
        txtInput.setMaximumSize(new Dimension(300, 45));
        txtInput.setAlignmentX(Component.CENTER_ALIGNMENT);

        ModernButton btnSubmit = new ModernButton("SUBMIT");
        btnSubmit.setPreferredSize(new Dimension(150, 45));
        btnSubmit.addActionListener(e -> {
            if(!txtInput.getText().trim().isEmpty() && !txtInput.getText().equals("Type here...")){
                result[0] = txtInput.getText();
                dialog.dispose();
            }
        });

        // Add to Panel
        JPanel content = (JPanel) dialog.getContentPane();
        content.add(Box.createVerticalStrut(20));
        content.add(txtInput);
        content.add(Box.createVerticalStrut(20));
        content.add(btnSubmit);
        content.add(Box.createVerticalGlue());

        dialog.setVisible(true);
        return result[0];
    }

    // C. Confirm Dialog (Yes/No)
    public int showCustomConfirm(String message) {
        JDialog dialog = new JDialog(this, "Confirm", true);
        setupDialog(dialog, message);

        final int[] result = {JOptionPane.NO_OPTION};
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setBackground(Color.WHITE);

        ModernButton btnYes = new ModernButton("YES");
        btnYes.setPreferredSize(new Dimension(100, 40));
        btnYes.addActionListener(e -> { result[0] = JOptionPane.YES_OPTION; dialog.dispose(); });

        JButton btnNo = new JButton("NO");
        btnNo.setPreferredSize(new Dimension(100, 40));
        btnNo.setBackground(Color.LIGHT_GRAY);
        btnNo.setFocusPainted(false);
        btnNo.addActionListener(e -> dialog.dispose());

        btnPanel.add(btnYes);
        btnPanel.add(btnNo);

        addDialogComponents(dialog, btnPanel);
        dialog.setVisible(true);
        return result[0];
    }

    // Helper to avoid duplicate code in Popups
    private JLabel setupDialog(JDialog dialog, String msg) {
        dialog.setSize(400, 300);
        dialog.setUndecorated(true);
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createLineBorder(COLOR_BG_END, 4));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        dialog.setContentPane(content);

        JLabel lblMsg = new JLabel("<html><center><div style='width:250px'>" + msg + "</div></center></html>", SwingConstants.CENTER);
        lblMsg.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblMsg.setForeground(COLOR_BG_END);
        lblMsg.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(Box.createVerticalGlue());
        content.add(lblMsg);
        content.add(Box.createVerticalStrut(20));
        return lblMsg;
    }

    private void addDialogComponents(JDialog d, Component comp) {
        JPanel c = (JPanel) d.getContentPane();
        comp.setMaximumSize(new Dimension(150, 45));
        if(comp instanceof JPanel) comp.setMaximumSize(new Dimension(300, 50));
        c.add(comp);
        c.add(Box.createVerticalGlue());
    }

// PART 1


    // --- 6. DATA CLASSES (Optimized for Billing & Status) ---

    // A. Slot Class
    static class ParkingSlot implements Serializable {
        int id;
        boolean isBooked;
        boolean isParked;
        String bookedByCNIC;
        String carNumber;
        LocalDateTime reservationTime; // For 1 Minute Timeout
        LocalDateTime parkTime;        // For Smart Billing

        public ParkingSlot(int id) {
            this.id = id;
            this.isBooked = false;
            this.isParked = false;
        }

        // Updated Status Logic for Admin (Occupied Priority)
        public String getStatus() {
            if (isParked) return "OCCUPIED"; // Admin ko Occupied dikhega
            if (isBooked) return "RESERVED";
            return "FREE";
        }
    }

    // B. Parking Area Class
    static class ParkingArea implements Serializable {
        String name;
        ArrayList<ParkingSlot> slots;
        int totalSlots;

        public ParkingArea(String name, int totalSlots) {
            this.name = name;
            this.totalSlots = totalSlots;
            this.slots = new ArrayList<>();
            for (int i = 1; i <= totalSlots; i++) {
                slots.add(new ParkingSlot(i));
            }
        }
    }

    // C. History Record
    static class HistoryRecord implements Serializable {
        String areaName;
        int slotId;
        String carNumber;
        String startTime, endTime;
        double cost;

        public HistoryRecord(String areaName, int slotId, String carNumber, String startTime, String endTime, double cost) {
            this.areaName = areaName;
            this.slotId = slotId;
            this.carNumber = carNumber;
            this.startTime = startTime;
            this.endTime = endTime;
            this.cost = cost;
        }
    }

    // D. Driver Class
    static class Driver implements Serializable {
        String cnic, password;
        ArrayList<HistoryRecord> history;

        public Driver(String cnic, String password) {
            this.cnic = cnic;
            this.password = password;
            this.history = new ArrayList<>();
        }
    }

    // --- 7. FILE HANDLING & SETUP ---

    public static void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("parking_data.dat"))) {
            drivers = (ArrayList<Driver>) ois.readObject();
            parkingAreas = (ArrayList<ParkingArea>) ois.readObject();
        } catch (Exception e) {
            initializeDummyData(); // File na mile to naya data banao
        }
    }

    public static void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("parking_data.dat"))) {
            oos.writeObject(drivers);
            oos.writeObject(parkingAreas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void initializeDummyData() {
        parkingAreas.clear();
        parkingAreas.add(new ParkingArea("Saddar Parking Plaza", 50));
        parkingAreas.add(new ParkingArea("Gulshan Civic Center", 30));
        parkingAreas.add(new ParkingArea("Dolmen Mall Clifton", 40));
        // New Areas Added:
        parkingAreas.add(new ParkingArea("North Nazimabad", 45));
        parkingAreas.add(new ParkingArea("FB Area Block 6", 35));

        saveData();
    }


    public SmartParkingSystem() {
        loadData(); // 1. Data Load


        try {
            bgImage = Toolkit.getDefaultToolkit().getImage("parking_bg.jpg");
            profileIcon = Toolkit.getDefaultToolkit().getImage("profile_icon.png");
        } catch (Exception e) {
            System.out.println("Images missing! Using gradient only.");
        }

        // 3. Window Settings
        setTitle("Smart Parking Allocation System - Karachi");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel.setLayout(cardLayout);

        // 4. Screens Init
        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createSignupPanel(), "SIGNUP");
        mainPanel.add(createAdminPanel(), "ADMIN");
        // Driver Dashboard dynamic add hoga

        add(mainPanel);

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                boolean changed = false;
                LocalDateTime now = LocalDateTime.now();
                for (ParkingArea area : parkingAreas) {
                    for (ParkingSlot slot : area.slots) {
                        // Agar Booked hai par Parked nahi, aur time > 1 min
                        if (slot.isBooked && !slot.isParked && slot.reservationTime != null) {
                            if (Duration.between(slot.reservationTime, now).toMinutes() >= 1) {
                                slot.isBooked = false;
                                slot.bookedByCNIC = null;
                                slot.carNumber = null;
                                slot.reservationTime = null;
                                changed = true;
                            }
                        }
                    }
                }
                if (changed) saveData();
            }
        }, 0, 5000); // Check every 5 seconds
    }



    private JPanel createLoginPanel() {
        GradientPanel panel = new GradientPanel();
        panel.setLayout(new GridBagLayout());

        RoundPanel card = new RoundPanel();
        card.setBackground(COLOR_WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Title with Theme Color
        JLabel lblTitle = new JLabel("Welcome Back");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(COLOR_BG_END); // Dark Blue Text
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Login to continue");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        ModernTextField txtCnic = new ModernTextField("Enter CNIC / Admin ID");
        txtCnic.setMaximumSize(new Dimension(300, 45)); // Fixed Size

        JPasswordField txtPass = new JPasswordField();
        txtPass.setBorder(BorderFactory.createTitledBorder("Password"));
        txtPass.setMaximumSize(new Dimension(300, 45));

        // Login Button (Large Width 300px as requested)
        ModernButton btnLogin = new ModernButton("LOGIN");
        btnLogin.setMaximumSize(new Dimension(300, 45));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnLink = new JButton("New User? Register Here");
        btnLink.setBorderPainted(false);
        btnLink.setContentAreaFilled(false);
        btnLink.setForeground(COLOR_BG_END);
        btnLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLink.setAlignmentX(Component.CENTER_ALIGNMENT);

        // -- LOGIN LOGIC --
        btnLogin.addActionListener(e -> {
            String id = txtCnic.getText();
            String pass = new String(txtPass.getPassword());

            // 1. Admin Login
            if (id.equalsIgnoreCase("admin") && pass.equals("admin123")) {
                refreshAdminPanel();
                cardLayout.show(mainPanel, "ADMIN");
            }
            // 2. Driver Login
            else {
                Driver foundDriver = null;
                for (Driver d : drivers) {
                    if (d.cnic.equals(id) && d.password.equals(pass)) {
                        foundDriver = d;
                        break;
                    }
                }
                if (foundDriver != null) {
                    currentLoggedInDriver = foundDriver;
                    // Dashboard ko naye sire se banayenge taake updated data dikhe
                    mainPanel.add(createDriverDashboard(), "DRIVER_DASH");
                    cardLayout.show(mainPanel, "DRIVER_DASH");
                } else {
                    showCustomDialog("Login Failed", "Invalid Credentials!", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnLink.addActionListener(e -> cardLayout.show(mainPanel, "SIGNUP"));

        card.add(lblTitle);
        card.add(lblSub);
        card.add(Box.createVerticalStrut(30));
        card.add(txtCnic);
        card.add(Box.createVerticalStrut(15));
        card.add(txtPass);
        card.add(Box.createVerticalStrut(20));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(10));
        card.add(btnLink);

        panel.add(card);
        return panel;
    }

    private JPanel createSignupPanel() {
        GradientPanel panel = new GradientPanel();
        panel.setLayout(new GridBagLayout());

        RoundPanel card = new RoundPanel();
        card.setBackground(COLOR_WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel lblTitle = new JLabel("Create Account");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(COLOR_BG_END);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        ModernTextField txtCnic = new ModernTextField("Enter 13-Digit CNIC");
        txtCnic.setMaximumSize(new Dimension(300, 45));

        JPasswordField txtPass = new JPasswordField();
        txtPass.setBorder(BorderFactory.createTitledBorder("Set Password"));
        txtPass.setMaximumSize(new Dimension(300, 45));

        // Register Button (Large Width 300px)
        ModernButton btnRegister = new ModernButton("REGISTER");
        btnRegister.setMaximumSize(new Dimension(300, 45));
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnBack = new JButton("Back to Login");
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setForeground(Color.GRAY);
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);

        // -- REGISTRATION LOGIC --
        btnRegister.addActionListener(e -> {
            String cnic = txtCnic.getText();
            String pass = new String(txtPass.getPassword());

            if (cnic.isEmpty() || pass.isEmpty()) {
                showCustomDialog("Error", "Please fill all fields", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Strict CNIC check
            if (!cnic.matches("\\d{13}")) {
                showCustomDialog("Invalid CNIC", "CNIC must be 13 digits (Numbers only)", JOptionPane.ERROR_MESSAGE);
                return;
            }
            for (Driver d : drivers) {
                if (d.cnic.equals(cnic)) {
                    showCustomDialog("Error", "Account already exists!", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            drivers.add(new Driver(cnic, pass));
            saveData();
            showCustomDialog("Success", "Account Created Successfully!", JOptionPane.INFORMATION_MESSAGE);
            cardLayout.show(mainPanel, "LOGIN");
        });

        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(30));
        card.add(txtCnic);
        card.add(Box.createVerticalStrut(15));
        card.add(txtPass);
        card.add(Box.createVerticalStrut(20));
        card.add(btnRegister);
        card.add(Box.createVerticalStrut(10));
        card.add(btnBack);

        panel.add(card);
        return panel;
    }



    // --- 11. SCREEN 3: ADMIN PANEL ---
    JPanel adminContentPanel;
    JLabel lblTotalDrivers, lblTotalAreas, lblGrandTotal;

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // A. Admin Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE); // Cleaner look
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_BG_END)); // Bottom Border

        JLabel title = new JLabel("ADMIN DASHBOARD");
        title.setFont(FONT_TITLE);
        title.setForeground(COLOR_BG_END); // Dark Blue

        // Modern Logout Button (Small Size)
        ModernButton btnLogout = new ModernButton("Logout");
        btnLogout.setPreferredSize(new Dimension(100, 35));
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));

        header.add(title, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);

        // B. Stats Bar
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        statsPanel.setBackground(new Color(240, 248, 255)); // Light Blue
        lblTotalDrivers = new JLabel("Drivers: 0");
        lblTotalAreas = new JLabel("Areas: 0");
        lblGrandTotal = new JLabel("Total Revenue: Rs 0");

        // Styling Stats
        Font statFont = new Font("Segoe UI", Font.BOLD, 14);
        lblTotalDrivers.setFont(statFont); lblTotalDrivers.setForeground(Color.DARK_GRAY);
        lblTotalAreas.setFont(statFont); lblTotalAreas.setForeground(Color.DARK_GRAY);
        lblGrandTotal.setFont(statFont); lblGrandTotal.setForeground(new Color(0, 100, 0)); // Green for Money

        statsPanel.add(lblTotalDrivers);
        statsPanel.add(new JLabel("|"));
        statsPanel.add(lblTotalAreas);
        statsPanel.add(new JLabel("|"));
        statsPanel.add(lblGrandTotal);

        panel.add(header, BorderLayout.NORTH);

        // C. Scrollable Content
        adminContentPanel = new JPanel();
        adminContentPanel.setLayout(new BoxLayout(adminContentPanel, BoxLayout.Y_AXIS));
        adminContentPanel.setBackground(Color.WHITE);
        adminContentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel container = new JPanel(new BorderLayout());
        container.add(statsPanel, BorderLayout.NORTH);
        container.add(new JScrollPane(adminContentPanel), BorderLayout.CENTER);

        panel.add(container, BorderLayout.CENTER);
        return panel;
    }

    // --- REFRESH LOGIC (Smart Calculation) ---
    private void refreshAdminPanel() {
        lblTotalDrivers.setText("Drivers Registered: " + drivers.size());
        lblTotalAreas.setText("Parking Areas: " + parkingAreas.size());
        adminContentPanel.removeAll();

        // 1. AREA STATUS SECTION
        JLabel sec1 = new JLabel("Live Area Status");
        sec1.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sec1.setForeground(COLOR_BG_END);
        adminContentPanel.add(sec1);
        adminContentPanel.add(Box.createVerticalStrut(10));

        for (ParkingArea area : parkingAreas) {
            int booked = 0, parked = 0;
            // Logic Fix: Count distinct states
            for (ParkingSlot s : area.slots) {
                if (s.isParked) parked++;
                else if (s.isBooked) booked++;
            }
            int free = area.totalSlots - booked - parked;

            JPanel areaCard = new JPanel(new BorderLayout());
            areaCard.setBorder(BorderFactory.createCompoundBorder(
                    new EmptyBorder(5, 5, 5, 5),
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)
            ));
            areaCard.setBackground(Color.WHITE);
            areaCard.setMaximumSize(new Dimension(900, 60));

            JLabel nameLbl = new JLabel("  " + area.name);
            nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
            nameLbl.setForeground(Color.DARK_GRAY);

            JLabel statLbl = new JLabel(String.format(
                    "<html>Total: %d | <font color='red'>Occupied: %d</font> | <font color='orange'>Reserved: %d</font> | <font color='green'>Free: %d</font>  </html>",
                    area.totalSlots, parked, booked, free));
            statLbl.setBorder(new EmptyBorder(0, 0, 0, 10));
            statLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            areaCard.add(nameLbl, BorderLayout.WEST);
            areaCard.add(statLbl, BorderLayout.EAST);
            adminContentPanel.add(areaCard);
            adminContentPanel.add(Box.createVerticalStrut(5));
        }

        adminContentPanel.add(Box.createVerticalStrut(30));

        // 2. FINANCIAL REPORT SECTION (New Feature)
        JLabel sec2 = new JLabel("Revenue Report (By Driver)");
        sec2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sec2.setForeground(COLOR_BG_END);
        adminContentPanel.add(sec2);
        adminContentPanel.add(Box.createVerticalStrut(10));

        // Table Header
        JPanel tableHead = new JPanel(new GridLayout(1, 3));
        tableHead.setBackground(COLOR_BG_END);
        tableHead.setMaximumSize(new Dimension(900, 30));
        JLabel h1 = new JLabel(" Driver CNIC", SwingConstants.CENTER); h1.setForeground(Color.WHITE);
        JLabel h2 = new JLabel(" Total Visits", SwingConstants.CENTER); h2.setForeground(Color.WHITE);
        JLabel h3 = new JLabel(" Total Paid (Rs)", SwingConstants.CENTER); h3.setForeground(Color.WHITE);
        tableHead.add(h1); tableHead.add(h2); tableHead.add(h3);
        adminContentPanel.add(tableHead);

        double grandTotal = 0;

        for (Driver d : drivers) {
            double driverTotal = 0;
            for (HistoryRecord h : d.history) {
                driverTotal += h.cost;
            }
            grandTotal += driverTotal;

            if (driverTotal > 0) { // Sirf unko dikhao jinhone kuch pay kiya
                JPanel row = new JPanel(new GridLayout(1, 3));
                row.setBackground(Color.WHITE);
                row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
                row.setMaximumSize(new Dimension(900, 30));

                row.add(new JLabel(d.cnic, SwingConstants.CENTER));
                row.add(new JLabel(String.valueOf(d.history.size()), SwingConstants.CENTER));
                JLabel costLbl = new JLabel(String.format("%.2f", driverTotal), SwingConstants.CENTER);
                costLbl.setForeground(new Color(0, 100, 0));
                row.add(costLbl);

                adminContentPanel.add(row);
            }
        }

        lblGrandTotal.setText("Total Revenue: Rs " + String.format("%.2f", grandTotal));

        adminContentPanel.revalidate();
        adminContentPanel.repaint();
    }

// PART 4

    // --- 12. SCREEN 4: DRIVER DASHBOARD (Updated Layout & Sidebar) ---
    private JPanel createDriverDashboard() {
        JPanel mainDash = new JPanel(new BorderLayout());

        // A. Top Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(10, 20, 10, 20));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        // Profile Button (Toggles Sidebar)
        JButton btnProfile = new JButton();
        if (profileIcon != null) {
            btnProfile.setIcon(new ImageIcon(profileIcon.getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        } else {
            btnProfile.setText("MENU");
            btnProfile.setForeground(COLOR_BG_END);
            btnProfile.setFont(new Font("Segoe UI", Font.BOLD, 12));
        }
        btnProfile.setBorderPainted(false);
        btnProfile.setContentAreaFilled(false);
        btnProfile.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblTitle = new JLabel("  Smart Parking Allocation", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(COLOR_BG_END);

        // Modern Logout Button (Small Size)
        ModernButton btnLogout = new ModernButton("Logout");
        btnLogout.setPreferredSize(new Dimension(100, 35));
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.addActionListener(e -> {
            currentLoggedInDriver = null;
            cardLayout.show(mainPanel, "LOGIN");
        });

        JPanel leftContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftContainer.setBackground(Color.WHITE);
        leftContainer.add(btnProfile);
        leftContainer.add(lblTitle);

        header.add(leftContainer, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);
        mainDash.add(header, BorderLayout.NORTH);

        // B. Sidebar (Hidden by default)
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(245, 245, 245)); // Light Grey
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
        sidebar.setVisible(false); // Initially Hidden

        // Sidebar Content
        JLabel lblDriverName = new JLabel("Driver Account");
        lblDriverName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblDriverName.setForeground(COLOR_BG_END);
        lblDriverName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblCnic = new JLabel();
        lblCnic.setForeground(Color.GRAY);
        lblCnic.setAlignmentX(Component.CENTER_ALIGNMENT);

        // History Button (Large Size for Sidebar)
        ModernButton btnHistory = new ModernButton("View Parking History");
        btnHistory.setMaximumSize(new Dimension(220, 50));
        btnHistory.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(Box.createVerticalStrut(40));
        sidebar.add(lblDriverName);
        sidebar.add(lblCnic);
        sidebar.add(Box.createVerticalStrut(40));
        sidebar.add(btnHistory);

        mainDash.add(sidebar, BorderLayout.WEST);

        JPanel areaListPanel = new JPanel();
        // Grid: 3 Columns looks best for 5 items on a wide screen
        areaListPanel.setLayout(new GridLayout(0, 3, 20, 20));
        areaListPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        areaListPanel.setBackground(Color.WHITE);

        for (ParkingArea area : parkingAreas) {
            // Creating Card
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(new Color(240, 248, 255)); // Very Light Blue
            card.setBorder(BorderFactory.createLineBorder(COLOR_BG_START, 1));

            JLabel nameLabel = new JLabel(area.name, SwingConstants.CENTER);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            nameLabel.setForeground(COLOR_BG_END); // Dark Blue Text
            nameLabel.setBorder(new EmptyBorder(15, 0, 5, 0));

            JLabel slotsLabel = new JLabel("Total Slots: " + area.totalSlots, SwingConstants.CENTER);
            slotsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            slotsLabel.setForeground(Color.GRAY);

            ModernButton btnView = new ModernButton("VIEW SLOTS");
            btnView.setPreferredSize(new Dimension(100, 40));
            btnView.addActionListener(e -> showAreaSlots(area));

            card.add(nameLabel, BorderLayout.NORTH);
            card.add(slotsLabel, BorderLayout.CENTER);

            JPanel btnContainer = new JPanel();
            btnContainer.setOpaque(false);
            btnContainer.setBorder(new EmptyBorder(10, 10, 15, 10));
            btnContainer.add(btnView);
            card.add(btnContainer, BorderLayout.SOUTH);

            areaListPanel.add(card);
        }

        mainDash.add(new JScrollPane(areaListPanel), BorderLayout.CENTER);

        // D. Events
        btnProfile.addActionListener(e -> {
            boolean state = sidebar.isVisible();
            sidebar.setVisible(!state); // Toggle Animation effect
            if (!state && currentLoggedInDriver != null) {
                lblCnic.setText("ID: " + currentLoggedInDriver.cnic);
            }
            mainDash.revalidate();
        });

        btnHistory.addActionListener(e -> showHistory());

        return mainDash;
    }

    // --- 13. SCREEN 5: SLOT VIEW FRAME ---
    private void showAreaSlots(ParkingArea area) {
        JFrame slotFrame = new JFrame(area.name + " - Live Status");
        slotFrame.setSize(900, 650);
        slotFrame.setLocationRelativeTo(null);

        JPanel gridPanel = new JPanel(new GridLayout(0, 5, 15, 15)); // 5 Columns
        gridPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        gridPanel.setBackground(Color.WHITE);

        for (ParkingSlot slot : area.slots) {
            JButton btnSlot = new JButton();
            btnSlot.setPreferredSize(new Dimension(100, 80));
            btnSlot.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnSlot.setFocusPainted(false);

            updateSlotColor(btnSlot, slot); // Initial Color

            btnSlot.addActionListener(e -> handleSlotClick(slot, area, btnSlot, slotFrame));
            gridPanel.add(btnSlot);
        }

        slotFrame.add(new JScrollPane(gridPanel));
        slotFrame.setVisible(true);
    }

    // --- 14. SLOT COLOR & STATE HELPER ---
    private void updateSlotColor(JButton btn, ParkingSlot slot) {
        String status = slot.getStatus(); // Occupied, Reserved, Free

        if (slot.isParked) {
            // OCCUPIED (Red)
            btn.setBackground(new Color(220, 20, 60));
            btn.setForeground(Color.WHITE);
            String timeStr = slot.parkTime.format(DateTimeFormatter.ofPattern("hh:mm a"));

            // Logic: Agar meri car hai to "My Car" dikhao, warna "Occupied"
            if (currentLoggedInDriver != null && slot.bookedByCNIC != null && !slot.bookedByCNIC.equals(currentLoggedInDriver.cnic)) {
                btn.setEnabled(false); // Doosre ki car
                btn.setText("<html><center>OCCUPIED<br>" + timeStr + "</center></html>");
            } else {
                btn.setEnabled(true); // Meri car (Exit ke liye)
                btn.setText("<html><center>MY CAR<br>" + timeStr + "</center></html>");
                btn.setBackground(new Color(139, 0, 0)); // Dark Red
            }

        } else if (slot.isBooked) {
            // RESERVED (Orange)
            btn.setBackground(Color.ORANGE);
            btn.setForeground(Color.BLACK);

            if (currentLoggedInDriver != null && slot.bookedByCNIC != null && slot.bookedByCNIC.equals(currentLoggedInDriver.cnic)) {
                btn.setEnabled(true); // Meri Booking (Arrival ke liye)
                btn.setText("<html><center>PARK HERE<br>Click Now</center></html>");
            } else {
                btn.setEnabled(false); // Doosre ki booking
                btn.setText("<html><center>RESERVED<br>(1 Min Left)</center></html>");
            }

        } else {
            // FREE (Green)
            btn.setBackground(new Color(34, 139, 34));
            btn.setForeground(Color.WHITE);
            btn.setText("Slot " + slot.id);
            btn.setEnabled(true);
        }
    }

// PART 5

    // --- 15. CORE LOGIC: BOOKING -> PARKING -> BILLING ---
    private void handleSlotClick(ParkingSlot slot, ParkingArea area, JButton btn, JFrame frame) {
        // Global Exception Handling for Safety
        try {
            // A. FREE SLOT -> BOOKING
            if (!slot.isBooked && !slot.isParked) {
                // Custom Input Popup (Blue/White)
                String carNum = showCustomInput("Enter Car Number (e.g. ABC-123):");

                // Validation: Agar user ne khali chora ya cancel kiya to error
                if (carNum != null && !carNum.trim().isEmpty()) {
                    slot.isBooked = true;
                    slot.bookedByCNIC = currentLoggedInDriver.cnic;
                    slot.carNumber = carNum.toUpperCase();
                    slot.reservationTime = LocalDateTime.now(); // Timer Starts

                    saveData();
                    updateSlotColor(btn, slot);
                    showCustomDialog("Reserved", "Slot Reserved!<br>You have 1 Minute to Park.", JOptionPane.INFORMATION_MESSAGE);
                } else if (carNum != null) {
                    // Agar null nahi hai par empty hai
                    showCustomDialog("Error", "Car Number is required!", JOptionPane.ERROR_MESSAGE);
                }
            }

            // B. RESERVED -> PARKING (Arrival Confirmation)
            else if (slot.isBooked && !slot.isParked && slot.bookedByCNIC.equals(currentLoggedInDriver.cnic)) {
                int choice = showCustomConfirm("Arrived at location?<br>Park Car Now?");

                if (choice == JOptionPane.YES_OPTION) {
                    slot.isParked = true;
                    slot.parkTime = LocalDateTime.now(); // Billing Timer Starts
                    saveData();
                    updateSlotColor(btn, slot);
                    showCustomDialog("Parked", "Car Parked Successfully.<br>Billing Started!", JOptionPane.INFORMATION_MESSAGE);
                }
            }

            // C. PARKED -> EXIT & BILLING (Smart Calculation)
            else if (slot.isParked && slot.bookedByCNIC.equals(currentLoggedInDriver.cnic)) {
                LocalDateTime now = LocalDateTime.now();
                Duration duration = Duration.between(slot.parkTime, now);

                long totalMinutes = duration.toMinutes();
                if (totalMinutes == 0) totalMinutes = 1; // Kam se kam 1 min ka charge hoga

                // Logic: 100 Rs Per Hour -> Divided by minutes
                // Example: 30 mins = 50 Rs, 15 mins = 25 Rs.
                double ratePerMinute = 100.0 / 60.0;
                double totalBill = totalMinutes * ratePerMinute;

                // Formatting Time
                long hours = totalMinutes / 60;
                long minutes = totalMinutes % 60;
                String timeString = String.format("%dh %02dm", hours, minutes);

                // Receipt Logic
                String message = String.format(
                        "<b>Total Time:</b> %s<br>" +
                                "<b>Rate:</b> Rs. 100/hr (Pro-rata)<br>" +
                                "<b>Total Bill:</b> <font color='red'>Rs. %.2f</font><br><br>" +
                                "Pay and Exit?", timeString, totalBill);

                int pay = showCustomConfirm(message);

                if (pay == JOptionPane.YES_OPTION) {
                    // 1. Save to History (Admin Panel me yehi show hoga)
                    HistoryRecord rec = new HistoryRecord(area.name, slot.id, slot.carNumber,
                            slot.parkTime.format(DateTimeFormatter.ofPattern("dd-MM HH:mm")),
                            now.format(DateTimeFormatter.ofPattern("HH:mm")), totalBill);
                    currentLoggedInDriver.history.add(rec);

                    // 2. Reset Slot
                    slot.isBooked = false;
                    slot.isParked = false;
                    slot.bookedByCNIC = null;
                    slot.carNumber = null;
                    slot.reservationTime = null;
                    slot.parkTime = null;

                    saveData();
                    updateSlotColor(btn, slot);
                    showCustomDialog("Success", "Payment Received.<br>Thank You!", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose(); // Close window
                }
            }
        } catch (Exception ex) {
            showCustomDialog("Error", "System Error: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // --- 16. HISTORY TABLE ---
    private void showHistory() {
        JFrame hFrame = new JFrame("My Parking History");
        hFrame.setSize(700, 400);
        hFrame.setLocationRelativeTo(null);

        String[] columns = {"Area Name", "Slot", "Car Plate", "Arrival", "Departure", "Paid (Rs)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        if(currentLoggedInDriver != null) {
            // Loop backwards taake latest history upar aaye
            for (int i = currentLoggedInDriver.history.size() - 1; i >= 0; i--) {
                HistoryRecord h = currentLoggedInDriver.history.get(i);
                model.addRow(new Object[]{h.areaName, h.slotId, h.carNumber, h.startTime, h.endTime, String.format("%.2f", h.cost)});
            }
        }

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Header Styling
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(COLOR_BG_END);
        table.getTableHeader().setForeground(Color.WHITE);

        hFrame.add(new JScrollPane(table));
        hFrame.setVisible(true);
    }

    // --- 17. MAIN METHOD ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // System UI Look for better buttons
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {}

            new SmartParkingSystem().setVisible(true);
        });
    }

}

