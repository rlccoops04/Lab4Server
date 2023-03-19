package Server;

import java.io.*;
import java.net.*;

public class UDP_Server {
    private DatagramSocket serverSocket;
    private InetAddress clientAddress;
    private int clientPort;
    private int[][] intArray;
    private float[][] floatArray;
    private String[][] stringArray;
    private File file;
    public UDP_Server(String HOST, int port, String pathToLog) {
        InitArrays();

        try {
            serverSocket = new DatagramSocket(port);
        } catch (SocketException e) { e.getMessage(); }

        file = new File(pathToLog);
    }
    private void SendData(String data) {
        try {
            byte[] sendingData = data.getBytes();
            DatagramPacket sendingPacket = new DatagramPacket(sendingData,sendingData.length, clientAddress, clientPort);
            serverSocket.send(sendingPacket);
            System.out.println("Server send: " + (new String(sendingPacket.getData())).trim());
        } catch(IOException e) { e.getMessage(); }
    }
    private String ReceiveData() {
        String receivedString = "";
        try {
            byte[] receivedData = new byte[1024];
            DatagramPacket receivingPacket = new DatagramPacket(receivedData, receivedData.length);
            serverSocket.receive(receivingPacket);
            receivedString = (new String(receivingPacket.getData())).trim();
        } catch(IOException e) { e.getMessage(); }
        System.out.println("Server received: " + receivedString);
        WriteToFile(receivedString);
        return receivedString;
    }
    private boolean getConnection()
    {
        System.out.println("Waiting for client...");
        String receivedString = "";
        try {
            byte[] receivedData = new byte[1024];
            DatagramPacket receivingPacket = new DatagramPacket(receivedData, receivedData.length);
            serverSocket.receive(receivingPacket);
            receivedString = (new String(receivingPacket.getData())).trim();
            System.out.println(receivedString);
            WriteToFile(receivedString);

            clientAddress = receivingPacket.getAddress();
            clientPort = receivingPacket.getPort();

            String data = "Server is connected";
            byte[] sendingData = data.getBytes();
            DatagramPacket sendingPacket = new DatagramPacket(sendingData,sendingData.length, clientAddress, clientPort);
            serverSocket.send(sendingPacket);
        } catch(IOException e) { e.getMessage(); }
        return true;
    }
    private void WriteToFile(String data)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file.getAbsoluteFile()));
            try
            {
                String s;
                while((s = br.readLine())!=null)
                {//построчное чтение
                    sb.append(s);
                    sb.append("\n");
                }
            }
            finally
            {
                br.close();
            }
        }
        catch(IOException e)
        {
            throw new RuntimeException();
        }
        try
        {
            PrintWriter pw = new PrintWriter(file.getAbsoluteFile());
            try
            {
                pw.println(sb.toString() + data);
            }
            finally
            {
                pw.close();
            }
        }catch(IOException e){throw new RuntimeException();}
    }
    public void run() {
        if(getConnection()) {
            System.out.println("Connection is successful");
        }
        String choiceMenu, choiceArray, choiceCells;
        boolean menu = true;
        while(menu) {
            choiceMenu = ReceiveData();
            switch (choiceMenu) {
                case "0":
                    menu = false;
                    break;
                case "1":
                    choiceArray = ReceiveData();
                    String array;
                    switch (choiceArray) {
                        case "1":
                            array = showArray(intArray);
                            break;
                        case "2":
                            array = showArray(floatArray);
                            break;
                        case "3":
                            array = showArray(stringArray);
                            break;
                        default:
                            array = "Ошибка";
                            break;
                    }
                    SendData(array);
                    break;
                case "2":
                    choiceArray = ReceiveData();
                    choiceCells = ReceiveData();
                    ChangeCells(choiceArray, choiceCells);
                    break;
            }
        }
    }
    private void ChangeCells(String arrays, String cells)
    {
        if(arrays.contains("1")) {
            if(!cells.contains(",")) {
                String[] splCells = cells.split(" ");
                intArray[Integer.parseInt(splCells[0])][Integer.parseInt(splCells[1])] = Integer.parseInt(splCells[2]);
            }
            else {
                String[] spl = cells.split(",");
                String[][] splCells = new String[spl.length][2];
                for (int i = 0; i < spl.length; i++) {
                    splCells[i] = spl[i].split(" ");
                    intArray[Integer.parseInt(splCells[i][0])][Integer.parseInt(splCells[i][1])] = 100;
                }
            }
            SendData(showArray(intArray));
        }
        if(arrays.contains("2")) {
            if(!cells.contains(",")) {
                String[] splCells = cells.split(" ");
                floatArray[Integer.parseInt(splCells[0])][Integer.parseInt(splCells[1])] = Integer.parseInt(splCells[2])*1.0F;
            }
            else {
                String[] spl = cells.split(",");
                String[][] splCells = new String[spl.length][2];
                for (int i = 0; i < spl.length; i++) {
                    splCells[i] = spl[i].split(" ");
                    floatArray[Integer.parseInt(splCells[i][0])][Integer.parseInt(splCells[i][1])] = -89.0F;
                }
            }
            SendData(showArray(floatArray));
        }
        if(arrays.contains("3")) {
            if(!cells.contains(",")) {
                String[] splCells = cells.split(" ");
                stringArray[Integer.parseInt(splCells[0])][Integer.parseInt(splCells[1])] = splCells[2];
            }
            else {
                String[] spl = cells.split(",");
                String[][] splCells = new String[spl.length][2];
                for (int i = 0; i < spl.length; i++) {
                    splCells[i] = spl[i].split(" ");
                    stringArray[Integer.parseInt(splCells[i][0])][Integer.parseInt(splCells[i][1])] = "ChangedString";
                    System.out.println(splCells[i][0]);
                    System.out.println(splCells[i][1]);
                }
            }
            SendData(showArray(stringArray));
        }
    }
    private void InitArrays()
    {
        intArray = new int[5][5];
        floatArray = new float[5][5];
        stringArray = new String[5][5];
        for(int i = 0; i < 5; i++)
        {
            for(int j = 0; j < 5; j++)
            {
                intArray[i][j] = j;
                floatArray[i][j] = j * 1.1F;
                stringArray[i][j] = "MyString" + j;
            }
        }
    }
    private String showArray(int[][] array) {
        String str = "Размерность массива: " + intArray.length + "\n";
        for(int i = 0; i < 5; i++)
        {
            str += ( i + ": ");
            for(int j = 0; j < 5; j++)
            {
                str += (array[i][j] + " ");
            }
            str += "\n";
        }
        return str;
    }
    private String showArray(float[][] array) {
        String str = "Размерность массива: " + floatArray.length + "\n";
        for(int i = 0; i < 5; i++)
        {
            str += ( i + ": ");
            for(int j = 0; j < 5; j++)
            {
                str += (array[i][j] + " ");
            }
            str += "\n";
        }
        return str;
    }
    private String showArray(String[][] array) {
        String str = "Размерность массива: " + stringArray.length + "\n";
        for(int i = 0; i < 5; i++)
        {
            str += ( i + ": ");
            for(int j = 0; j < 5; j++)
            {
                str += (array[i][j] + " ");
            }
            str += "\n";
        }
        return str;
    }

}
