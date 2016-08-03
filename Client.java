/**
 * @author: 	Gino Jafari
 * @course:  	CS4349
 * @program: 	Reliable Data Transport, UDP data message 
 * 				Programming Assignment 2
 */
package UDP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;

public class Client {
	static int sequenceNum = 0, ackChecker = 0;
	static int clientPort;
	static int maxSequence;
	static int lossProbability;	
	static boolean flag = true;
	
	public static void main(String[] args) throws IOException{
		byte[] sendData = new byte[1024];
		byte[] recData = new byte[1024];
		String host = new String ("127.0.0.1");
		//User inputs
		String hostname = args[0];
		String port_num = args[1];
		String max_seq = args[2];
		String loss_prob = args[3];
		String message;
		
		double lossProb = Double.parseDouble(loss_prob);
		
		clientPort = Integer.parseInt(port_num);
		maxSequence = Integer.parseInt(max_seq);
		lossProbability = Integer.parseInt(loss_prob);
		
		//Creation of UDP Socket for the client, input stream, and IP address for host
		DatagramSocket cSocket = new DatagramSocket();
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		InetAddress IP = InetAddress.getByName(host); 
		
		do{
			try{
				sequenceNum = 0;
				
				//Reads one char at a time from user input
				String line = input.readLine();
				String[] tokens = line.split("");	
					
				//If user types 'exit', client closes 
					if(line.contains("exit")){
						cSocket.close();
						System.exit(0);
					}
				
				//Sends the characters one by one to the server 	 
				for(int i = 0; i < line.length(); i++){
					
					if(sequenceNum == maxSequence)
						sequenceNum = 0;
					
					message = "DATA " + String.valueOf(sequenceNum);
					message = message + " " + tokens[i];
					
					//DATA message passed contains a single character
					sendData = message.getBytes();
					
					//Create DATA message and sent to the server; server IP is bound here.
					System.out.println("send: " + message + "\n");
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IP, clientPort);
					cSocket.send(sendPacket);
					
					DatagramPacket recPacket = new DatagramPacket(recData, recData.length);
	
					//Set timeout
					boolean flag = true;
					cSocket.setSoTimeout(5000);
					
					while(flag == true){
						//Receives ACK back from the server
						try{
		
							cSocket.receive(recPacket);
							String modifiedSentence = new String(recPacket.getData());
							InetAddress returnIP = recPacket.getAddress();
							int port = recPacket.getPort();
							
							//Parses the ACK number
							ackChecker = Integer.parseInt(modifiedSentence.substring(4, 5));
							
							//Check to see if ACK is correct, if not wait reset timeout 5 more seconds.
							if(sequenceNum != ackChecker)
								cSocket.setSoTimeout(5000);
							
							System.out.println("recv: " + modifiedSentence + "\n");
						
							sequenceNum++;
							ackChecker++;
							flag = false;
							
						}catch(SocketTimeoutException e){
							System.out.println("TO-resend: " + tokens[i]);
							cSocket.send(sendPacket);
						}
					
						System.out.println();
					}
				}
		
			}catch(Exception e){e.printStackTrace();}
		}while(true);	
	}
}
