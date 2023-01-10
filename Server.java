import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

	private ArrayList<ConnectionHandler> connections;
	private ServerSocket server;
	private Boolean done;
	private ExecutorService pool;
	private Scanner sendPM;
	private ConnectionHandler handler;
	
	private ByteBuffer byteBuffer;
	private String utf8Emoji; 

	private String emoji;
	

	private DataOutputStream dataOutputStream = null;
	private DataInputStream dataInputStream = null;

//test variables

	public Server() {
		// 20:21
		// https://www.youtube.com/watch?v=hIc_9Wbn704&t=1221s
		/*
		 * this to add send direct message hint -> look at the ArrayList connections.
		 * add color add
		 * 
		 */
		connections = new ArrayList<ConnectionHandler>();
		sendPM = new Scanner(System.in);

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			server = new ServerSocket(5999);
			pool = Executors.newCachedThreadPool();

			done = false;
			while (!done) {

				Socket client = server.accept();

				dataInputStream = new DataInputStream(client.getInputStream());
				dataOutputStream = new DataOutputStream(client.getOutputStream());

				handler = new ConnectionHandler(client);

				connections.add(handler);

				pool.execute(handler);

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block

		}

	}

	public void shutdown() throws IOException {

		done = true;
		if (!server.isClosed()) {
			server.close();
		}

	}

	class ConnectionHandler implements Runnable {

		private Socket client;
		private BufferedReader in; // use to get the stream from the socket when client send it
		private PrintWriter out; // when we want to write somethign to the client out
		private String nickname;
		private File file = new File("WELCOME.TXT");
		
		private File MenuFile = new File("Menu.txt");
		
		private boolean doesNicknameExist = false;
		
		
		public ArrayList ar = new ArrayList();

		// test variables

		private BufferedReader inPM;

		public ConnectionHandler(Socket client) {
			this.client = client;

		}

		@Override
		public void run() {
			try {
				out = new PrintWriter(client.getOutputStream(), true); // what server sends.
				in = new BufferedReader(new InputStreamReader(client.getInputStream())); // what client types

				out.println("hello client");
				out.println("please enter a nickanme : ");

				nickname = in.readLine();

				// jsut for server
				
	
				
				System.out.println(nickname + " has connected");

				broadcast(nickname + " has connected");

				broadcast("Type //help to show commands");

				String message;
				while ((message = in.readLine()) != null) {

					switch (message) {

					case "/quit":
						broadcast(nickname + " has left the chat");
						shutdown();
						System.out.println(nickname + " has left the chat");
						break;

					case "/showbanner":
						showBanner();
						break;

					case "/help":
						showHelp();
						break;

					case "/showusers":
						showUsers();
						break;

					case "/sendmessage":

						inPM = new BufferedReader(new InputStreamReader(client.getInputStream()));

						out.println("enter username : ");

						String username = inPM.readLine();

						out.println("enter message : ");

						String mes = inPM.readLine();

						sendPM(username, mes);

						System.out.println(" username : " + username + " Message :  " + mes);

						//were gonna need to fix this later. get rid of inPM. 

						break;

				
					
						
					case "/watchmovie":
						watchmovie();						
						break;
						
					case "/readstory":
						
						
						
						break;
						
					case "/username":
						for (int i = 0; i < connections.size(); i++) {

							if (connections.get(i).getNickName().equals(nickname)) {

								connections.get(i).out.println(nickname);
							}

						}
						break;

					case "/connect":
						broadcast(nickname + " has connected to the FTP server");
						System.out.println(nickname + "has connected to the FTP Server");

						break;

					default:
						broadcast(nickname + " -> " + message); // broadcast the message from user nickname with the																// message
						break;
					}

				}

			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		public void shutdown() throws IOException {
			for (int i = 0; i < connections.size(); i++) {

				if (connections.get(i).getNickName().equals(nickname)) {

					connections.remove(i);
				}

			}
			in.close();
			out.close();
			if (!client.isClosed()) {
				client.close();
				
			
				
				
				System.exit(1);
			}

			/*
			 * for(ConnectionHandler ch : connections) { ch.shutdown(); }
			 */
		}

		public void broadcast(String message) {
			for (ConnectionHandler ch : connections) {
				if (ch != null) {

					ch.sendMessage(message);
				}
			}
		}

		public void sendMessage(String message) {
		
			out.println(message);

		}

		public void showUsers() {

			for (ConnectionHandler ch : connections) {
				out.println("\n");
				out.println(ch.nickname);
			}

		}

		public String getNickName() {
			return nickname;
		}


		
		
		public void TestMethod() {
			
			for(int i = 0; i < connections.size(); i++) {
				
			}
		}
		
		
		public void sendPM(String Nickname, String msg) {
			try {

				for (int i = 0; i < connections.size(); i++) {

					if (connections.get(i).getNickName().equals(Nickname)) {

						connections.get(i).out.println(Nickname + " < - > " + msg);
						
					}

				}

			} catch (Exception e) {
				// TODO: handle exception
			}

		}

		public void showNicknames() {

			for (ConnectionHandler ch : connections) {

				System.out.println("nick name is : " + ch.nickname);

			}

		}

		
		public void readstory() {
			
			
		}
		
		
		public void watchmovie() {
			Thread t1 = new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						System.out.println("RUnning");
						Runtime.getRuntime().exec(new String[] {"cmd", "/K", "start", "telnet","towel.blinkenlights.nl"});
						
						
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			t1.start();
		}
		
		
		public void showHelp() {
				Scanner nsc;
				
			try {
				
				nsc = new Scanner(MenuFile);
				
				while(nsc.hasNext()) {
					
					String line = nsc.nextLine();
					Thread.sleep(1);
					
					//nickname

					for (int i = 0; i < connections.size(); i++) {

						if (connections.get(i).getNickName().equals(nickname)) {

							connections.get(i).out.println(line);
							Thread.sleep(4);
						}

					}
				}
				nsc.close();
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

		
		
		


		public void showBanner() {
			Scanner reader;
			try {
				reader = new Scanner(file);
				while (reader.hasNext()) {
					String line = reader.nextLine();
					broadcast(line);
					Thread.sleep(2);
					
				}

				reader.close();
			} catch (FileNotFoundException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/*
		 * send files!
		 * 
		 */

		public void recieveFile(String File) {

			try {

				int bytes = 0;
				FileOutputStream fileOutputStream = new FileOutputStream(File);

				long size = dataInputStream.readLong();
				byte buffer[] = new byte[4 * 1024];

				while (size > 0
						&& (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {

					// here we write the file using write command
					fileOutputStream.write(buffer, 0, bytes);
					size -= bytes; // read upto the bytes
				} // end of while

				fileOutputStream.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

}
