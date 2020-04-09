import java.util.Random;
import java.util.Scanner;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.SwingConstants;
import javax.swing.text.StyledDocument;


/* Bowl class to keep the tract of Bowl. This class has current number of candy
   and maximum number of candy it has */
class Bowl {
    private int mCandy, maxCandy;

    public Bowl(int maxCandy) {
        this.maxCandy = maxCandy;
        mCandy = maxCandy; // currecnt candy is max candy when the bowl is filled initially
    }

    // synchronized method to accedd the Bowl by only on faculty at a time
    public synchronized boolean getStatus() {
        if (mCandy > 0) {
            mCandy--;
            notifyAll();
            return true;
        } else {
            notifyAll();
            return false;
        }
    }

    // method to get the current number of candy in a bowl
    public int getCandy() {
      return mCandy;
    }

    // method to re-fill the bowl
    public void Fill() {
        mCandy = maxCandy;
    }
}

/* TA class to keep the tract of TA. This class has the bowl to be filled by TA when it is empty */
class TA {
    private Bowl bowl;

    public TA(Bowl bowl) {
        this.bowl = bowl; // initial status of the bowl
    }

    // method to fill the bowl by TA
    public void fillBowl() {
        bowl.Fill();
    }
}


/* Faculty class to keep the tract of activities of Faculties. This class has current number of candy
   in a bowl, TA, faculty names, faculty wait time etc */
class Faculty extends Thread {
    private Bowl bowl;
    private TA ta;
    private String name;
    private Random random = new Random();
    private int mCandy, sleepTime;
    private boolean status;

    static private int maxSleepTime;
    static private StyledDocument doc = CandyBowlProblem.pane.getStyledDocument();
    static private SimpleAttributeSet keyWord = new SimpleAttributeSet();

    public Faculty(Bowl bowl, TA ta, int maxSleepTime, String name) {
        this.bowl = bowl;
        this.ta = ta;
        this.maxSleepTime = maxSleepTime;
        this.name = name;
        mCandy = 0;
        StyleConstants.setForeground(keyWord, Color.WHITE);
        StyleConstants.setBold(keyWord, true);
    }

    public void run() {
        try {
            while (true) {

                status = bowl.getStatus();
                int candy = bowl.getCandy();

                if (status == false) { // Bow is empty so TA needs it to be filled
                    ta.fillBowl();
                    printInfo("\nTA filled the bowl. Now the bowl has " + candy + " candy in it.\n");
                    System.out.println("\nTA filled the bowl. Now the bowl has " + candy + " candy in it.\n");
                } else {
                    mCandy++;
                    do {
                      sleepTime = random.nextInt(maxSleepTime); // generate random time bounded by maxSleepTime
                    } while (sleepTime == 0);

                    printInfo(name + " is thinking and eating a candy for " + sleepTime + " ms. Now Bowl has " + candy + " candy left.");
                    System.out.println(name + " thinking and eating a candy for " + sleepTime + " ms. Now Bowl has " + candy + " candy left.");
                    Thread.sleep(sleepTime); //wait till sleepTime

                }
            }
        } catch (InterruptedException e) {
            // e.printStackTrace();
        } finally {
            System.out.println(name + " ate " + mCandy + " piece(s) of candy.");
            printInfo(name + " ate " + mCandy + " piece(s) of candy.");
        }
    }

    // method to set the sleep time at run time
    public void setSleepTime(int sleeptime) {
        this.maxSleepTime = sleeptime;
    }

    // method to print the information on GUI
    static public void printInfo (String info) {
        int len = CandyBowlProblem.pane.getDocument().getLength();
        CandyBowlProblem.pane.setCaretPosition(len);
        CandyBowlProblem.pane.requestFocusInWindow();
        try {
            doc.insertString(doc.getLength(), "\n" + info, keyWord );
        }
            catch(Exception e) { System.out.println(e);
        }
    }
}

// main class CandyBowlProblem
public class CandyBowlProblem extends JPanel {

    static private int height = 640, width = 1080;
    static private int nProf = 3, mCandy = 5, maxSleepTime = 3000, runTime = 0;

    static private Scanner input = new Scanner(System.in);

    static private Bowl bowl = new Bowl(mCandy);
    static private TA ta = new TA(bowl);
    static private Faculty[] profs = new Faculty[nProf];

    static JTextPane pane = new JTextPane(); // this is global to this package
    static private JButton btnPlay = new JButton("Play");
    static private JButton btnStop = new JButton("Stop");
    static private JButton btnPlusSpeed = new JButton("Speed+");
    static private JButton btnMinusSpeed = new JButton("Speed-");

    static public void playGUI() {

        JFrame frame = new JFrame("Candy Bowl Problem");

        // btnPlay.setBounds(130,60,140,40);
        // btnStop.setBounds(130,140,140,40);
        // btnPlusSpeed.setBounds(130,220,140,40);
        // btnMinusSpeed.setBounds(130,300,140,40);

        btnPlay.setEnabled(true);
        btnStop.setEnabled(false);
        btnPlusSpeed.setEnabled(false);
        btnMinusSpeed.setEnabled(false);

        JLabel lfac = new JLabel("No of Faculties        ");
        JTextField nfac = new JTextField("3",6);
        JLabel lcandy = new JLabel("No of Candies        ");
        JTextField ncandy = new JTextField("5",6);
        JLabel lwaittime = new JLabel("Max wait time (ms)");
        JTextField waittime = new JTextField("3000",6);
        JLabel lmaxtime = new JLabel("Max run time (ms)");
        JTextField maxtime= new JTextField("0",6);

        // lfac.setLocation(200,200);
        // lcandy.setLocation(100,200);
        // lwaittime.setLocation(200,200);
        // lmaxtime.setLocation(200,200);

        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setItalic(attributeSet, true);
        StyleConstants.setForeground(attributeSet, Color.WHITE);
        pane.setCharacterAttributes(attributeSet, false);
        pane.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(pane);
        pane.setPreferredSize(new Dimension(width - 100, 500));
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();

        p1.setPreferredSize(new Dimension(400, 540));

        JScrollPane scrollableTextArea = new JScrollPane(pane);
        scrollableTextArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollableTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        pane.setBackground(Color.BLACK);
        pane.setForeground(Color.WHITE);

        p1.add(scrollableTextArea);
        p2.add(lfac);
        p2.add(nfac);
        p2.add(lcandy);
        p2.add(ncandy);
        p2.add(lwaittime);
        p2.add(waittime);
        p2.add(lmaxtime);
        p2.add(maxtime);
        p2.add(btnPlay);
        p2.add(btnPlusSpeed);
        p2.add(btnMinusSpeed);
        p2.add(btnStop);

        JSplitPane s1 = new JSplitPane(SwingConstants.HORIZONTAL, p1, p2);
        frame.add(s1);

        frame.setSize(width, height);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new FlowLayout());

        btnPlay.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {

                if (nfac.getText().length() > 0)
                    nProf = Integer.parseInt(nfac.getText());

                if (ncandy.getText().length() > 0)
                    mCandy = Integer.parseInt(ncandy.getText());

                if (waittime.getText().length() > 0)
                    maxSleepTime = Integer.parseInt(waittime.getText());

                if (maxtime.getText().length() > 0)
                    runTime = Integer.parseInt(maxtime.getText());

                Bowl bowl = new Bowl(mCandy);

                TA ta = new TA(bowl);

                String name;

                profs = new Faculty[nProf];

                for (int i = 0; i < nProf; i++) {
                    name = "Faculty_" + (i+1);
                    profs[i] = new Faculty(bowl, ta, maxSleepTime, name);
                }

                btnPlay.setEnabled(false);
                btnPlusSpeed.setEnabled(true);
                btnMinusSpeed.setEnabled(true);
                btnStop.setEnabled(true);

                pane.setText("ACTIVITIES:");

                try {
                    Thread.sleep(2 * 1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }

                play(profs, runTime);

            }
        });

        btnStop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {

                System.out.println("Stopped!");
                Faculty.printInfo("Stopped!");

                for (int i = 0; i < profs.length; i++) {
                    profs[i].interrupt();
                }

                btnPlay.setEnabled(true);
                btnStop.setEnabled(false);
                btnPlusSpeed.setEnabled(false);
                btnMinusSpeed.setEnabled(false);

            }
        });

        btnPlusSpeed.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                int sleeptime = 0;

                if (waittime.getText().length() > 0) {
                    sleeptime = Integer.parseInt(waittime.getText());
                    sleeptime = (int)(sleeptime * 0.9); // decrease wait time by 10%
                    waittime.setText(Integer.toString(sleeptime));
                    profs[0].setSleepTime(sleeptime);
                }

            }
        });

        btnMinusSpeed.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                int sleeptime = 0;

                if (waittime.getText().length() > 0) {
                    sleeptime = Integer.parseInt(waittime.getText());
                    sleeptime = (int)(sleeptime * 1.1); // increase wait time by 10%
                    waittime.setText(Integer.toString(sleeptime));
                    profs[0].setSleepTime(sleeptime);
                }

            }
        });

    }

    private static void play(Faculty [] profs, int runTime) {

        for (int i = 0; i < profs.length; i++) {
            profs[i].start();
        }

        if (runTime > 0) {
            //Sleep for this much time then exit the threads
            try {
                Thread.sleep(runTime);
                System.out.println(runTime + " ms time is up!");
                Faculty.printInfo(runTime + " ms time is up!");
                for (int i = 0; i < profs.length; i++) {
                    profs[i].interrupt();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            CandyBowlProblem.btnPlay.setEnabled(true);
            CandyBowlProblem.btnStop.setEnabled(false);
            CandyBowlProblem.btnPlusSpeed.setEnabled(false);
            CandyBowlProblem.btnMinusSpeed.setEnabled(false);
        }

    }

    private static void playCLI() {
        // int nProf = 3, mCandy = 5, maxSleepTime = 3, runTime = 5;
        int nProf, mCandy, maxSleepTime, runTime;

        System.out.print("Enter number of faculty: ");
        nProf = input.nextInt();
        System.out.print("Enter Number of Candy: ");
        mCandy = input.nextInt();

        System.out.print("Enter max sleep time in ms: ");
        maxSleepTime = input.nextInt();

        System.out.print("Enter total run-time is ms: ");
        runTime = input.nextInt();

        System.out.print("\n\n");

        Bowl bowl = new Bowl(mCandy);

        TA ta = new TA(bowl);

        String name;
        Faculty[] profs = new Faculty[nProf];
        for (int i = 0; i < nProf; i++) {
            name = "Faculty_" + (i+1);
            profs[i] = new Faculty(bowl, ta, maxSleepTime, name);
        }

        for (int i = 0; i < nProf; i++) {
            profs[i].start();
        }

       if (runTime > 0) {
            try {
                Thread.sleep(runTime);
                System.out.println(runTime + " ms time is up!");
                for (int i = 0; i < nProf; i++) {
                    profs[i].interrupt();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        playGUI(); // play game in GUI
        // playCLI(); // play game in CLIE
    }

}