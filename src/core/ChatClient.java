package core;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
/**
 * 该版本1）在聊天过程中，若有一个用户先关闭则会抛出SocketException 该版本处理之
 * 
 * @author vonzhou
 *
 */

public class ChatClient extends Frame{
	private TextField tf=new TextField();
	private TextArea ta=new TextArea();
	private Socket socket=null;
	private DataOutputStream dos=null;
	private DataInputStream dis=null;
	private boolean connected=false;

	public static void main(String[] args) {
		new ChatClient().lanuchFrame();
	}
	
	public void lanuchFrame() {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				System.out.println("退出聊天系统");
				try {
					if(dos!=null)dos.close();
					if(socket!=null)socket.close();
					if(dos!=null)dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				System.exit(0);
			}
		});
		
		this.setTitle("来聊吧");
		setBounds(300,300,400,300);
		tf.addActionListener(new TextFieldListener());
		this.add(ta,BorderLayout.NORTH);//Frame默认布局管理器是BorderLayout
		this.add(tf,BorderLayout.SOUTH);
		pack();
		this.setVisible(true);
		connect();
		new Thread(new Receive()).start();
		
	}
	
	public void connect(){

		try{
			socket=new Socket("localhost",8888);
			dos=new DataOutputStream(socket.getOutputStream());
			dis=new DataInputStream(socket.getInputStream());
			System.out.println("已连接服务器！");
			connected=true;
		}
		catch(UnknownHostException e1){e1.printStackTrace();}
		catch(IOException e2){}
		
	}
	
	
	private class TextFieldListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			String s=tf.getText().trim();
			String taString=ta.getText()+"\n"+s;
			//ta.setText(taString);
			tf.setText("");
			
			try {
				dos.writeUTF(s);
				dos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			
		}
   }
	
	private class Receive implements Runnable{
		@Override
		public void run() {
			try{
				while(connected){
				String str=dis.readUTF();
				ta.setText(ta.getText()+str+"\n");
			}
			}
			catch(IOException e){
				//在异常处理层中处理逻辑不合理，等待合理的解决方案
				System.exit(0);
			}
		}
	}

}
