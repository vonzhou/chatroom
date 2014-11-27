package core;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
	ServerSocket serverSocket = null;
	private boolean started = false;
	private List<Client> clients = new ArrayList<Client>();

	public static void main(String[] args) {
		new ChatServer().process();
	}

	private void process() {
		try {
			serverSocket = new ServerSocket(8888);
			started = true;
		} catch (BindException e) {
			System.out.println("�˿�����ʹ����......");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			while (started) {
				Socket socket = serverSocket.accept();
				// ֻ�������ӿͻ����󣬾���Ĵ��������������߳���ʵ��
				Client client = new Client(socket);
				System.out.println(" �û�***��ӭ�㣬�����Ƶ�� !");
				new Thread(client).start();
				clients.add(client);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	// //////////////////////////////////
	class Client implements Runnable {
		private Socket socket = null;
		private DataInputStream dis = null;
		private DataOutputStream dos = null;
		private boolean connected = false;

		public Client(Socket socket) {
			this.socket = socket;
			try {
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
				connected = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

//		public void send(String str) {
//			try {
//				dos.writeUTF(str);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		
		public void send(String str){
			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				//�����ָ��쳣˵��������������Ķ����Ѿ��˳�����Socket�رգ�������Ҫ�Ӽ�����ȥ��
				clients.remove(this);
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			Client c=null;
			try {
				while (connected) {
					String str = dis.readUTF();
					System.out.println(str);//
					for (int i = 0; i < clients.size(); i++) {
						c = clients.get(i);
						c.send(str);
					}
					//��ʹ��Iterator���б�������Ϊ����Ҫ������������Ϊ��ǣ�����ı�clients�е����� 
				}

			} catch (EOFException e) {
				System.out.println("�û�***�˳���Ƶ�����ټ���");
			} catch (IOException e1) {
				e1.printStackTrace();
			} 

			finally {// ������Ҫ�رո�����Դ
				try {
					if (dis != null)
						dis.close();
					if (socket != null)
						socket.close();
					if(dos!=null)dos.close();
					if(c!=null)clients.remove(c);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}

}
