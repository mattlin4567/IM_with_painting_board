
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.awt.event.*;
import java.io.*;
import java.net.InetAddress;

public class Server extends javax.swing.JFrame implements Runnable {
    
    class Connection extends Thread {

        Socket socket;
        InputStream input;
        OutputStream output;

        public Connection(Socket s) {
            socket = s;
            
            try {
                System.out.println("open input stream");
                input = socket.getInputStream();
                System.out.println("open output stream");
                output = new PrintStream(socket.getOutputStream());
                System.out.println("open output stream ok");
                new ChatRoom(input,output,socket.getInetAddress().toString());
            } catch (IOException x) {
                x.printStackTrace();
            }
        }
     

        public void close() {
            try {
                System.out.println("close connection...");
                input.close();
                output.close();
                socket.close();
                removeConnection(this);
            } catch (IOException x) {
                x.printStackTrace();
            }
        }
    }
    
    int port;
    int maxConnections;
    String ip;
    ServerSocket serverSocket;
    List<Connection> connections;
    public Server(int p, int c) throws UnknownHostException {
        ip = InetAddress.getLocalHost().getHostAddress().toString();
        initComponents();
        port = p;
        maxConnections = c;
        connections =  Collections.synchronizedList(new LinkedList());
        try {
            serverSocket = new ServerSocket(port, maxConnections);            
        } catch (IOException x) {
            x.printStackTrace();
        }
        
         jButton1.addActionListener(new ActionListener(){
             public void actionPerformed(ActionEvent event){
                 String ip=jTextField1.getText();
                 new Client(ip, 1234);
             }
    });
         
    }
    
    void removeConnection(Connection connection) {
        connections.remove(connection);
    }
    @Override
    public void run() {
        while (true) {
            try {
                // connection Socket
                System.out.println("wait for connections...");
                Socket connSocket = serverSocket.accept();
                System.out.println("create connection socket");
                Connection connection = new Connection(connSocket);
                System.out.println("maxConnections=" + maxConnections);
                if(connections.size() < maxConnections) {
                    connections.add(connection);                    
                }
               
            } catch (IOException x) {
                x.printStackTrace();
            }
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("JChat");

        jButton1.setText("connect");

        jLabel1.setText("請輸入IP");

        jLabel2.setText("本機IP "+ip);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1)
                    .addComponent(jTextField1)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE))
                .addContainerGap(245, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 194, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    public static void main(String args[]) throws UnknownHostException {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        Server server = new Server(1234, 50);
        server.setVisible(true);        
        server.run();
        
        /* Create and display the form 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {    
                server.setVisible(true);
            }
        });*/
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
class Client{
    String serverName;
    int port;
    Socket socket;
    InputStream input;
    OutputStream output;

    public Client(String name, int p) {
        serverName = name;
        port = p;
        
        try {
            socket = new Socket(InetAddress.getByName(serverName), port);
            System.out.println("create client socket ok");
            output = new PrintStream(socket.getOutputStream());
            System.out.println("create output stream ok");
            output.flush();
            input = socket.getInputStream();
            System.out.println("create input stream ok");
            new ChatRoom(input,output,serverName);
        } catch (UnknownHostException x) {
            x.printStackTrace();
        } catch (IOException x) {
            x.printStackTrace();
        }
    }
   
}