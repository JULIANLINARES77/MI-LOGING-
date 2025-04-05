import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SistemaLogin {
   
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";
    
    private Connection conn;
    private Statement stmt;
    private JFrame frame;
    private JPanel panel;
    private JTextField usuarioField;
    private JPasswordField contrasenaField;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new SistemaLogin().iniciar();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error al iniciar la aplicación: " + e.getMessage());
            }
        });
    }
    
    public void iniciar() throws SQLException {
        connectToMySQL(); 
        
        
        crearBaseDatosYTabla();
        
        
        configurarGUI();
    }
    
    public void connectToMySQL() {
        try {
            
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            
            stmt = conn.createStatement();
           
            JOptionPane.showMessageDialog(null, "BIENVENIDOS OPRIMA OK PARA CONTINUAR.");
        } catch (SQLException e) {
            
            JOptionPane.showMessageDialog(null, "No se pudo conectar a MySQL: " + e.getMessage());
            System.exit(1); 
        }
    }
    
    private void crearBaseDatosYTabla() throws SQLException {
       
        stmt.execute("CREATE DATABASE IF NOT EXISTS sistema_login");
        stmt.execute("USE sistema_login");
        
        
        String sql = "CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "usuario VARCHAR(50) NOT NULL UNIQUE, " +
                    "contrasena VARCHAR(100) NOT NULL)";
        
        stmt.execute(sql);
    }
    
    private void configurarGUI() {
        frame = new JFrame("Sistema de Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        
        panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        
        JLabel tituloLabel = new JLabel("Sistema de Login");
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(tituloLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridy++;
        panel.add(new JLabel("Usuario:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        usuarioField = new JTextField(15);
        panel.add(usuarioField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Contraseña:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        contrasenaField = new JPasswordField(15);
        panel.add(contrasenaField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JButton loginButton = new JButton("Iniciar Sesión");
        loginButton.addActionListener(e -> iniciarSesion());
        panel.add(loginButton, gbc);
        
        gbc.gridy++;
        JButton registroButton = new JButton("Registrar Nueva Cuenta");
        registroButton.addActionListener(e -> registrarCuenta());
        panel.add(registroButton, gbc);
        
        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private void iniciarSesion() {
        String usuario = usuarioField.getText().trim();
        String contrasena = new String(contrasenaField.getPassword());
        
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Por favor ingrese usuario y contraseña");
            return;
        }
        
        try {
            
            stmt.execute("USE sistema_login");
            
            String sql = "SELECT * FROM usuarios WHERE usuario = ? AND contrasena = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, usuario);
                pstmt.setString(2, contrasena);
                
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    JOptionPane.showMessageDialog(frame, 
                        "¡HAS INICIADO SESIÓN CON ÉXITO!\nBienvenido, " + usuario,
                        "Éxito", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, 
                        "Usuario o contraseña incorrectos", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, 
                "Error al verificar credenciales: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void registrarCuenta() {
        String usuario = usuarioField.getText().trim();
        String contrasena = new String(contrasenaField.getPassword());
        
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Por favor ingrese usuario y contraseña");
            return;
        }
        
        try {
            
            stmt.execute("USE sistema_login");
            
           
            String checkSql = "SELECT * FROM usuarios WHERE usuario = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, usuario);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    JOptionPane.showMessageDialog(frame, 
                        "El nombre de usuario ya está en uso", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            
            String insertSql = "INSERT INTO usuarios (usuario, contrasena) VALUES (?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, usuario);
                insertStmt.setString(2, contrasena);
                
                int filasAfectadas = insertStmt.executeUpdate();
                
                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(frame, 
                        "CUENTA CREADA CON ÉXITO!\nAhora puedes iniciar sesión.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, 
                "Error al registrar usuario: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}