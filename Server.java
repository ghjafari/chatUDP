/**
 * @author: 	Gino Jafari
 * @course:  	CS4349
 * @program: 	Reliable Data Transport, UDP data message 
 * 				Programming Assignment 2
 */
package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server {
	static int sequenceNum = 0;
	static int serverPort;
	static int maxSequence;
	static int lossProbability;
	static int expectedAck = 0;
	
	
	
	//Server <port number> <max sequences> <loss probability (e.g. 0, 0.5)>
	public static void main(String[] args) throws IOException{
		try {
			byte[] recData = new byte[1024];
			byte[] sendData = new byte[1024];			
			String port_num = args[0];
			String max_seq = args[1];
			String loss_prob = args[2];
			
			serverPort = Integer.parseInt(port_num);
			maxSequence = Integer.parseInt(max_seq);
			lossProbability = Integer.parseInt(loss_prob);
			
			//Creation of UDP socket for the server
			DatagramSocket sSocket = new DatagramSocket(serverPort);
			
			while(true){	
				recData = new byte[1024];
				DatagramPacket recPacket = new DatagramPacket(recData, recData.length);
				if(expectedAck == maxSequence)
					expectedAck = 0;
				
				//Wait for the packet to be received 
				sSocket.receive(recPacket);
				 
				//Splits string to grab the sequence number and the data sent.
				String message = new String(recPacket.getData());
				String[] tokens = message.split(" ");
				sequenceNum = Integer.parseInt(tokens[1]);
				
				
				InetAddress IP = recPacket.getAddress();
				int port = recPacket.getPort();
				
				//Checks to see if it's a DATA message, and checks for the expected ACK
				if(!message.contains("DATA") || sequenceNum != expectedAck){
					String dupAck = "ACK " + sequenceNum + "\n";
					sendData = dupAck.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IP, port);
					sSocket.send(sendPacket);
				}
				
				
				//ACK is sent with the correct sequence number to the client console
				System.out.println("recv: " + message + "\n");
				System.out.println("Send out to screen: " + tokens[1]);
					message = "ACK " + sequenceNum + "\n";
				System.out.println("send: " + message + "\n");
				
				
				
				//ACK message, sent to the client
				sendData = message.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IP, port);
				sSocket.send(sendPacket);
				
				expectedAck++;
			}
			
		} catch (SocketException e) {
			System.out.println("UDP PORT already occupied.");
			System.exit(1);
		}
	}
}
