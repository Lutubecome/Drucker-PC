import serialio.*;

import java.io.IOException;
import java.io.PrintStream;

public class SenderXONXOFF {

    private static final int XON = 0x11;
    private static final int XOFF = 0x13;
    private static final int ETB = 0x17;

    private Serial con;

    public SenderXONXOFF(int portnr) throws IOException {

        con = new Serial("COM" + portnr, 9600, 8, 1, 0);

    }

    public boolean oeffnePort() throws IOException {

        if (con.open()){
            System.out.println("Port offen!");
        }else {
            System.out.println("Port nicht gefunden! \n[Systemabbruch]");
            System.exit(0);
            return false;
        }

        while(con.read() != XON);

        System.out.println("XON empfangen! Beginne mit senden...");
//        if (con.dataAvailable()!= XON){
//            System.out.println("Data Available funktioniert!");
//        }
        return true;
    }

    public int sendeText(String text) throws IOException, InterruptedException {

        char [] textInBytes = text.toCharArray();

        boolean sendenFrei = true;

        for (int i = 0; i < textInBytes.length; i++){

            if (con.dataAvailable() > 0 && con.read() == XOFF){
                while (con.read() != XON) Thread.sleep(50);
                Thread.sleep(500);
            }

            con.write(textInBytes[i]);

            //Thread.sleep(100);


        }

        con.write(ETB);

        while(con.read() != XOFF);

        return textInBytes.length;
    }

    public void schliessePort() throws IOException {
        System.out.println("Schnittstelle wird geschlossen...");
        con.close();
        System.out.println("Schnittstelle geschlossen! \n[System beendet]");
        System.exit(0);
    }

    static void main(String[] args) throws IOException, InterruptedException {

        final String text = "Das war eigendlich nicht so schwer./Habe unnoetig lang gebraucht!";

        SenderXONXOFF sender = new SenderXONXOFF(1);

        sender.oeffnePort();
        System.out.println("Gesendete Zeichen : " + sender.sendeText(text));
        sender.schliessePort();

    }

}
