import serialio.Serial;

import java.io.IOException;
import java.util.Arrays;

public class DruckerXONXOFF {

    private byte[] puffer;
    private int groessePufferspeicher;

    private static final int XON = 0x11;
    private static final int XOFF = 0x13;
    private static final int ETB = 0x17;

    private Serial con;

    public DruckerXONXOFF(int portnr, int n) throws IOException {

        con = new Serial("COM" + portnr, 9600, 8, 1, 0);

        puffer = new byte[n];

        Arrays.fill(puffer, (byte) -1);

    }

    boolean oeffnePort() throws IOException {

        if (con.open()){
            System.out.println("Port offen!");
        }else {
            System.out.println("Port nicht gefunden! \n[Systemabbruch]");
            System.exit(0);
            return false;
        }

        con.write(XON);
        System.out.println("XON Signal gesendet!");
        return true;
    }

    void empfangeTextUndDrucke() throws IOException, InterruptedException {

        int i = 0;

        while (true) {

            if (istPufferVoll()){

                con.write(XOFF);

                druckePufferLeer();

                i = 0;

                //wait(100);

                con.write(XON);

            }

            int zeichen = con.read();

            if (zeichen == ETB){

                druckePufferLeer();

                i = 0;

                con.write(XOFF);

                break;

            }

            puffer[i] = (byte) zeichen;

            i++;

        }
    }

    private boolean istPufferVoll(){

        if (puffer[puffer.length - 1] == -1){
            return false;
        }
        else return true;
    }

    private void druckePufferLeer() throws InterruptedException {

        for (byte z : puffer){

            if (z != -1) {
                if (z == 32) {
                    System.out.println(" ");
                }else if (z == 47){
                    System.out.println("\n");
                }else System.out.print((char) z);
            }

            Thread.sleep(500);

        }

        Arrays.fill(puffer, (byte) -1);

    }

    void schliessePort() throws IOException {

        con.close();
        System.out.println("\nVerbindung geschlossen \n[System beendet]");

    }

    static void main(String[] args) throws IOException, InterruptedException {

        DruckerXONXOFF druck = new DruckerXONXOFF(2, 8);

        druck.oeffnePort();

        druck.empfangeTextUndDrucke();

        druck.schliessePort();

    }

}
