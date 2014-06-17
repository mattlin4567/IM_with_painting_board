

import java.awt.event.*;
import java.awt.*;
import java.io.*;
import javax.swing.text.*;




public class ChatRoom extends javax.swing.JFrame {
    DataInputStream reader;
    DataOutputStream writer;
    DataOutputStream piped_out;
    Graphics gs,gx;
    boolean onDrag = false;
    int x,y,xs,ys,fontSize;  
    Color selected=Color.BLACK;
    private StyledDocument doc = null;
    private SimpleAttributeSet attrSet = null;
    String text;
    //FontAttrib att;
    public ChatRoom(final InputStream input,
        final OutputStream output,String title) {
        initComponents(); 
        this.setTitle(title);
        this.setVisible(true);        
        jTextPane1.setEditable(false); 
        gs = canvas1.getGraphics();
        doc = jTextPane1.getStyledDocument();
        reader = new DataInputStream(input);  
        writer = new DataOutputStream(output);
        piped_out = new DataOutputStream(output);
        CheckBoxHandler handler = new CheckBoxHandler();
        Thread readerThread = new Thread(new InputReader());  
        readerThread.start();
        jButton1.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                DataBag data = null;
                String fontItem = jComboBox1.getItemAt(jComboBox1.getSelectedIndex()).toString();
                fontSize = Integer.parseInt(fontItem);
                try{
                data = new DataBag(jTextField1.getText(),false,false,fontSize,0,0,0);
                writer.writeUTF(data.message);
                writer.writeInt(fontSize);
                writer.writeInt(valItalic);
                writer.writeInt(valBold);
                //jTextPane1.setFont(new Font( "Serif", valBold + valItalic, fontSize ));
                //jTextPane1.append("<font color=#ffffdd>"+"me:"+jTextField1.getText()+"</font>"+'\n');
                String mine = "me:" + data.message + "\n";
                doc.insertString(doc.getLength(), mine,  getAttrSet());
                }catch(Exception e){                    
                    System.out.println("送出資料失敗");
                }
                jTextField1.setText("");
             }
        });
        jButton2.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                try{
                    clearBoard_Canvas();
                    writer.writeUTF("**clear");
                }catch(Exception e){                    
                }
             }
        });
        jCheckBox1.addItemListener( handler );
        jCheckBox2.addItemListener( handler );
    }
    
      private int valBold = Font.PLAIN;
      private int valItalic = Font.PLAIN; 
    private class CheckBoxHandler implements ItemListener 
   {
      public void itemStateChanged( ItemEvent event )
      {
         if ( event.getSource() == jCheckBox1 ) {
            valBold = jCheckBox1.isSelected() ? Font.BOLD : Font.PLAIN;
             System.out.println("bold check box event:  valBold=" + valBold);             
         }
               
         if ( event.getSource() == jCheckBox2 ) {
            valItalic = 
               jCheckBox2.isSelected() ? Font.ITALIC : Font.PLAIN;
             System.out.println("italic check box event:  valItalic=" + valItalic);             
             
         }
      } 
   }
    
    public SimpleAttributeSet getAttrSet() {
    attrSet = new SimpleAttributeSet();
   if (valItalic == 0 && valBold == 0) {
    StyleConstants.setBold(attrSet, false);
    StyleConstants.setItalic(attrSet, false);
   } else if (valItalic == 0 && valBold == 1) {
    StyleConstants.setBold(attrSet, true);
    StyleConstants.setItalic(attrSet, false);
   } else if (valItalic == 2 && valBold == 0) {
    StyleConstants.setBold(attrSet, false);
    StyleConstants.setItalic(attrSet, true);
   } else if (valItalic == 2 && valBold == 1) {
    StyleConstants.setBold(attrSet, true);
    StyleConstants.setItalic(attrSet, true);
   }
   StyleConstants.setFontSize(attrSet, fontSize);
   return attrSet;
  }
    
    synchronized public void drawBoard_Canvas(DataBag data) {
          gs.setColor(selected);
          gs.drawLine(data.x0,data.y0,data.x1,data.y1);
    }
    
    public void clearBoard_Canvas() {
           gs.setColor(Color.white);
           gs.fillRect(canvas1.getX(), canvas1.getY(), canvas1.getWidth(), canvas1.getHeight()); 
    }
    
    class InputReader implements Runnable{
        public void run(){
            DataBag data = null;
            int x0 = 0, y0 = 0, x1 = 0, y1 = 0;
            String message;
            try{
                //while ((message = reader.readUTF()) != null && (message = reader.readUTF()).equals("draw") != true){
                while (true){
                    message = reader.readUTF();
                    if(message.equals("draw")){
                        x0=reader.readInt();
                        y0=reader.readInt();
                        x1=reader.readInt();
                        y1=reader.readInt();
                        data = new DataBag("none",false,false,x0,y0,x1,y1);
                        drawBoard_Canvas(data);
                    }else if(message.equals("**clear")){
                        clearBoard_Canvas();
                    }else if(message.equals("**color")){
                        int ci,cj;
                        ci = reader.readInt();
                        cj = reader.readInt();
                        setColor(c[ci][cj]);
                    }else{
                    fontSize = reader.readInt();
                    valItalic = reader.readInt();
                    valBold = reader.readInt();
                    data = new DataBag(message,false,false,fontSize,0,0,0);
//                    jTextPane1.setFont(new Font( "Serif", a+b , data.x0 ));
//                    jTextPane1.append("you:"+message+'\n');
                    String mine = "you:" + data.message + "\n";
                    doc.insertString(doc.getLength(), mine,  getAttrSet());
                    }
                }                
            }catch(Exception ex ){
                ex.printStackTrace();
                System.out.println("接收失敗");
            }
        }
    }
    private int preferW,preferH;
    private int width,height; // 每一小塊顏色大小
    private Color c[][]={ {Color.red,Color.yellow,Color.green,Color.cyan,
                                        Color.blue,Color.magenta,Color.white},
                        {Color.black,Color.darkGray,Color.gray,Color.lightGray,
                                        Color.orange,Color.pink,Color.white}};
     void setColor(Color ci){ // 設定 顏色
                selected=ci;
            }
     
    class canvas2 extends Canvas{ // 繼承 基本畫圖元件 功能
        public void paint(Graphics gx) {
            preferW=canvas2.getWidth();  
            preferH=canvas2.getHeight();
            width = preferW/2;
            height = preferH/7;
            int x,y;
            for(int i=0;i<2;i++){
                x=i*width;
                y=0;
                for(int j=0;j<7;j++){// 畫出可選擇各顏色
                    gx.setColor(c[i][j]);
                    gx.fillRect(x,y,width,height);
                    y+=height;
                }
            }
            //changeColor(gx,selected);
            } // 設定 最小範圍
            public Dimension minimumSize() {
            return new Dimension(preferW,preferH);
            }
            // 設定 理想範圍
            public Dimension preferredSize() {
            return minimumSize();
            }
    }
    
    class DataBag {
         String message=null;
         boolean fontB,fontI;
         int x0=0,y0=0,x1=0,y1=0;
         DataBag(String message,boolean fontB,boolean fontI,
                 int x0,int y0,int x1,int y1) {
          this.message = message;
          this.fontB = fontB;
          this.fontI = fontI;
          this.x0=x0;
          this.y0=y0;
          this.x1=x1;
          this.y1=y1;
         }
}
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jButton2 = new javax.swing.JButton();
        scrollPane1 = new java.awt.ScrollPane();
        canvas1 = new java.awt.Canvas();
        canvas2 = new canvas2();
        jComboBox1 = new javax.swing.JComboBox();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jButton1.setText("send");

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(jTextPane1);

        jButton2.setText("clear");

        canvas1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                canvas1MouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                canvas1MouseMoved(evt);
            }
        });
        scrollPane1.add(canvas1);

        canvas2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                canvas2MousePressed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "12", "14", "16", "18", "20" }));

        jCheckBox1.setText("Bold");

        jCheckBox2.setText("Italic");

        jLabel1.setText("size:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextField1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(58, 58, 58)
                                .addComponent(jCheckBox1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(canvas2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(scrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(canvas2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox1)
                    .addComponent(jCheckBox2)
                    .addComponent(jLabel1))
                .addGap(1, 1, 1)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-416)/2, (screenSize.height-358)/2, 416, 358);
    }// </editor-fold>//GEN-END:initComponents

    private void canvas2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_canvas2MousePressed
        int x2 = evt.getX();
        int y2 = evt.getY();
        int j=y2/height;
        int i=0;
            if(j>-1 && j<7 && x2<preferW){//是否在 調色區域內
                i = x2 / width;
                setColor(c[i][j]);
                System.out.println(selected);
            }
          try{
                piped_out.writeUTF("**color");
                piped_out.writeInt(i);
                piped_out.writeInt(j);
            }catch(IOException e){
                System.out.println("failed");
            }
    
    }//GEN-LAST:event_canvas2MousePressed

    private void canvas1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_canvas1MouseDragged
              onDrag=true;
              if(onDrag){
                  x = evt.getX();
                  y = evt.getY();
                  DataBag data = new DataBag("none",false,false,xs,ys,x,y);
                  drawBoard_Canvas(data);
                  try{
                      piped_out.writeUTF("draw");
                      piped_out.writeInt(xs);
                      piped_out.writeInt(ys);
                      piped_out.writeInt(x);
                      piped_out.writeInt(y);
                  }catch(Exception e){
                      System.out.println("傳送失敗");   
                  }
              }
              xs = x;
              ys = y;
              onDrag=false;
    }//GEN-LAST:event_canvas1MouseDragged

    private void canvas1MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_canvas1MouseMoved
              xs = evt.getX();
              ys = evt.getY();  
    }//GEN-LAST:event_canvas1MouseMoved

   
    public static void main(String args[]) {
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
            java.util.logging.Logger.getLogger(ChatRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChatRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChatRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChatRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Canvas canvas1;
    private java.awt.Canvas canvas2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextPane jTextPane1;
    private java.awt.ScrollPane scrollPane1;
    // End of variables declaration//GEN-END:variables
}
