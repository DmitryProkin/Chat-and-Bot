/**
 * Created by Dmitry on 25.04.2018.
 */

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import bot.*;

public class SimpleChat extends JFrame implements ActionListener {
    private AboutDialog dialog;
    public String yourName;
    public Boolean flag=false;
    JTextArea incoming;
    JTextField outgoing;
    PrintWriter writer;
    BufferedReader reader;
    Socket sock;
    JCheckBox ai;
    SimpleBot sbot;     // chat-bot class (in bot package)


    final String CHB_AI = "ИИ(Бот)";
    final String TITLE_OF_PROGRAM = "Чат";
    public SimpleChat(){
        setUpNetWorking();
        if(dialog == null) // в первый раз
            dialog = new AboutDialog(SimpleChat.this);

        dialog.setVisible(true); // отобразить диалог

        JFrame frames = new JFrame(TITLE_OF_PROGRAM);
        frames.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel mainPanel = new JPanel();

        // mainPanel.setSize(200,200);
        incoming = new JTextArea(15,50);
        incoming.setLineWrap(true);
        incoming.setWrapStyleWord(true);
        incoming.setEditable(false);

        JScrollPane qScroller = new JScrollPane(incoming);
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        ai = new JCheckBox(CHB_AI);
        outgoing = new JTextField(20);
        outgoing.addActionListener(this::actionPerformed);
        JButton sendButton = new JButton("Отправить");

       sendButton.addActionListener(this::actionPerformed);
        frames.getRootPane().setDefaultButton(sendButton);

        // mainPanel.add(incoming);
        mainPanel.add(qScroller);
        mainPanel.add(ai);
        mainPanel.add(outgoing);
        mainPanel.add(sendButton);
        frames.getContentPane().add(BorderLayout.CENTER,mainPanel);
        //setUpNetWorking();

        Thread readerTread = new Thread(new IncomingReader());
        readerTread.start();

        frames.setSize(580,400);
        frames.setResizable(false);
        frames.setVisible(true);

        sbot = new SimpleBot();
    }

    public void setUpNetWorking(){
        try {
            //Создаем сокет и PrintWriter
            //Присваеваем PrintWriter переменной writer
            sock = new Socket("localhost", 5000);//"192.168.1.43"
//            InputStreamReader это мост соединяющий низкоуровневый поток байтов(получаемый из сокета)и высокоуровневый
//            символьный поток(например представляемый объектом  BufferedReader, который находится на вершине цепочки).
            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());

            //Создаем Bufferedreader и считываем данные
            reader = new BufferedReader(streamReader);

//         PrintWriter ведет себя как мост между символьными и байтовыми данными, которые получает из низкоуровненного
//          потока представляемого сокетом, подключив PrintWriter мы можем записывать строки в сокет.
            writer = new PrintWriter(sock.getOutputStream());
            System.out.println("Соединение установлено");
            //writer.println("Server : " + dialog.nm  + " присоединился к беседе");
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

  //  public class SendButtonListener implements ActionListener{
  @Override
        public void actionPerformed(ActionEvent event) {
            try{
                //Получаем текст из текстого поля и отправляем
                //Его на сервер с помощью переменнной writer(PrintWriter)
                yourName = dialog.nm;
                if(outgoing.getText().trim().length() > 0) {
                    writer.println(yourName + ": " + outgoing.getText());
                    if (ai.isSelected()) {
                        writer.println("Chatter: " +
                                sbot.sayInReturn(outgoing.getText(), ai.isSelected()) );
                    }
                    writer.flush();
                }
            } catch (Exception ex){
                ex.printStackTrace();
            }
            outgoing.setText("");
            outgoing.requestFocus();

        }
   // }

    public class IncomingReader implements  Runnable{

        public void run(){
            String message  ;


            try{
                while ((message= reader.readLine())!= null){
                    yourName = dialog.nm;
                    System.out.println("read "+yourName+": "+ message);
                    incoming.append(message+ "\n");

                }
            } catch (Exception ex){ex.printStackTrace();}
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable(){
            public void run() {
                SimpleChat frame = new SimpleChat();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                // frame.setVisible(true);
            }
        });

        // new SimpleChat().go();
    }
    class AboutDialog extends JDialog
    {
        public  String nm;
        public AboutDialog(JFrame owner)
        {
            super(owner, "Идентификация пользователя", true);


            JLabel NameLabel = new JLabel("Введите имя:");
            NameLabel.setHorizontalAlignment(JLabel.CENTER);
            add(NameLabel);
            // При активизации кнопки ОК диалогове окно закрывается.
            JTextField nameTxt = new JTextField(10);

            JButton ok = new JButton("Ok");

            //warning!!!!

            ok.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent event) {
                    if(nameTxt.getText().trim().length()>0){
                        nm = nameTxt.getText();
                        writer.println("Server : " + dialog.nm  + " присоединился к беседе");
                        writer.flush();
                        setVisible(false);
                    }

                }

            });

            nameTxt.addKeyListener(new KeyAdapter() {

                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER){
                        ok.doClick();
                    }

                }

            });



            // Кнопка ОК помещается в нижнюю часть окна.

            JPanel panel = new JPanel();
            panel.add(nameTxt);
            panel.add(ok);
            add(panel, BorderLayout.SOUTH);
            setSize(260, 160);


        }



    }

}




