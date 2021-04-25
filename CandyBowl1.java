/*
CLASS LIST
==============================================
Bowl
Professor
TA
main
*/

import java.util.Random;
import java.util.Scanner;

/*
Bowl Class: has a current and max number of candies.
The bowl has two methods associated with it: get and fill
Get gets a piece of candy from the bowl
Fill fills it back up (presumably by a TA)
Bowl also has an ID to differentiate between multiple bowls.
Get and Fill are both synchronized methods because only one person can access it at a time.
*/
class Bowl {
    //Variables
    private int PieceKnt, MaxPieces, id;
    //Constructor: sets bowl size and does initial fill.
    Bowl(int MaxPieces, int id) {
        this.MaxPieces = MaxPieces;
        PieceKnt = MaxPieces;
        this.id = id;
    }
    //Get a candy from the bowl
    synchronized public boolean Get() {
        if (PieceKnt > 0) {
            PieceKnt--;
            System.out.println("Bowl " + id + " has " + PieceKnt + " candy left");
            notifyAll();
            return true;
        } else {
            notifyAll();
            return false;
        }
    }
    //Fills bowl back up (even if it's not empty theoretically)
    synchronized public void Fill() {
        PieceKnt = MaxPieces;
        System.out.println("The bowl is full.");
        notifyAll();
    }
}

/*
Professor class
Alternates between sleeping and eating candy, just like real life.
Unlike Bowl, which is just a shared data structure, Professors are living and breathing.
Therefore, they are extentions of Thread
Professors get candy from the Bowl object 'target'
Professors are also assigned to a TA, so they can offload the demanding task of filling the bowl to a younger body.
*/
class Professor extends Thread {
    //Variables
    private Bowl[] BowlList;
    private TA[] TAList;
    private String name;
    private Random rng = new Random();
    private int KandyKnt, MaxSleep, SleepTime, target, BowlKnt, TAKnt;
    private boolean GetStatus;

    //Contructor method: set local variables
    Professor(Bowl[] BowlList, TA[] TAList, int MaxSleep, String name) {
        this.BowlList = BowlList;
        BowlKnt = BowlList.length;
        this.TAList = TAList;
        TAKnt = TAList.length;
        this.MaxSleep = MaxSleep;
        this.name = name;
        KandyKnt = 0;
    }

    //Method that gets run as a thread
    public void run() {
        try {
            //Infinite loop of sleeping and eating
            while (true) {
                //Select a bowl to Get from and do Get
                target = rng.nextInt(BowlKnt);
                GetStatus = BowlList[target].Get();
                if (GetStatus == false) {
                    //Bowl empty, needs filling. Select a TA and tell them to fill the bowl.
                    TAList[rng.nextInt(TAKnt)].FillBowl(target);
                } else {
                    KandyKnt++;
                    //Proceed to sleep
                    SleepTime = rng.nextInt(MaxSleep);
                    System.out.println(name + " has consumed a candy and sleeps for " + SleepTime + "ms.");
                    Thread.sleep(SleepTime);
                }
            }
        } catch (InterruptedException e) {

        } finally {
            //But before going away, tell everyone how awesome you are.
            System.out.println(name + " ate " + KandyKnt + " pieces of candy.");
        }
    }
}

/*
TA class
A TA simply sits around and does nothing until someone tells them to fill up the candy bowl.
They could probably do other things too, but don't push it.
*/
class TA {
    //Variables
    private Bowl[] BowlList;
    private String name;

    //Constructor: sets target, name and empty fill count
    TA(Bowl[] BowlList, String name) {
        this.BowlList = BowlList;
        this.name = name;
    }

    //This method defines the meaning of existance
    public void FillBowl(int target) {
        BowlList[target].Fill();
        System.out.println(name + " has filled bowl " + target);
    }
}

/*
Main class, start here.
This class handles runtime options and execution
*/
public class CandyBowl1 {
    private static Scanner input = new Scanner(System.in);
    public static void main(String[] args) {
        //Main Start
        //Variables
        int menuoption = 1;
        //User decision input plus case structure
        System.out.println("Runtime options\n===============\n1) Use default parameters (SHSU CS Staff, 1 TA, 1 candy bowl)\n2) Enumerate N objects");
        System.out.print("Enter option: ");
        menuoption = input.nextInt();
        switch (menuoption) {
            case 1:
                DefaultParams();
                break;
            case 2:
                EnumParams();
                break;
            default:
                System.out.println("Bad option entered, using default parameters");
                DefaultParams();
                break;
        }
    }

    //Default Parameter execution
    private static void DefaultParams() {
        //Set up default data
        String[] Names = {
            "Dr. Cooper",
            "Dr. Burris",
            "Dr. Smith",
            "Dr. McGuire",
            "Dr. Cho"
        };
        int SleepTime;
        Bowl[] Bowls = new Bowl[1];
        Bowls[0] = new Bowl(20, 0);
        TA[] TAs = new TA[1];
        TAs[0] = new TA(Bowls, "Steve the TA");
        Professor[] Professors = new Professor[5];
        SleepTime = 800; //CHANGE THIS TO CHANGE DEFAULT SLEEP TIME
        for (int i = 0; i < 5; i++) {
            Professors[i] = new Professor(Bowls, TAs, SleepTime, Names[i]);
        }
        for (int i = 0; i < 5; i++) {
            Professors[i].start();
        }
    }

    //Enumeration Parameter input and execution
    private static void EnumParams() {
        int ProfKnt, TAKnt, BowlKnt, PieceKnt, SleepTime, menuoption, RunTime;
        String Name;
        //Get user input values
        System.out.print("Enter number of professors: ");
        ProfKnt = input.nextInt();
        System.out.print("Enter number of TAs: ");
        TAKnt = input.nextInt();
        System.out.print("Enter number of candy bowls: ");
        BowlKnt = input.nextInt();
        System.out.print("Enter bowl capacity: ");
        PieceKnt = input.nextInt();
        System.out.print("Enter max sleep time (in ms, global): ");
        SleepTime = input.nextInt();
        //Generate data to use for process
        Bowl[] Bowls = new Bowl[BowlKnt];
        for (int i = 0; i < BowlKnt; i++) {
            Bowls[i] = new Bowl(PieceKnt, i);
        }
        TA[] TAs = new TA[TAKnt];
        for (int i = 0; i < TAKnt; i++) {
            Name = "TA" + i;
            TAs[i] = new TA(Bowls, Name);
        }
        Professor[] Professors = new Professor[ProfKnt];
        for (int i = 0; i < ProfKnt; i++) {
            Name = "Professor" + i;
            Professors[i] = new Professor(Bowls, TAs, SleepTime, Name);
        }
        //Get runtime parameters
        System.out.println("Select runtime option\n===============\n1) Run forever\n2) Enter runtime");
        System.out.print("Enter selection: ");
        menuoption = input.nextInt();
        switch (menuoption) {
            case 1: //start threads
                for (int i = 0; i < ProfKnt; i++) {
                    Professors[i].start();
                }
                break;
            case 2: //Get runtime length
                System.out.print("Enter runtime is ms: ");
                RunTime = input.nextInt();
                //Start threads
                for (int i = 0; i < ProfKnt; i++) {
                    Professors[i].start();
                }
                //Sleep for this much time then exit the threads
                try {
                    Thread.sleep(RunTime);
                    System.out.println("Time's up!");
                    for (int i = 0; i < ProfKnt; i++) {
                        Professors[i].interrupt();
                    }
                } catch (InterruptedException e) {

                }
                break;
            default:
                System.out.println("Bad option entered, running forever");
                for (int i = 0; i < ProfKnt; i++) {
                    Professors[i].start();
                }
                break;
        }
    }
}
