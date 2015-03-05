package com.mysocketclient.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	private EditText mIp; //用于输入IP地址
	private EditText mEditText; //用于输入要发送的内容
	private TextView mText; //用于接收聊天内容
	private Button mConnect; //连接按钮
	private Button mSend; //发送按钮
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mIp = (EditText) findViewById(R.id.ip);
		mEditText = (EditText) findViewById(R.id.edit);
		mText = (TextView) findViewById(R.id.text);
		mConnect = (Button) findViewById(R.id.connect);
		mSend = (Button) findViewById(R.id.send);
		
		mConnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//连接
				connect();
			}
		});
		
		mSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//发送
				send();
			}
		});
		
		
		
	}//onCreate()方法结束
	
	
	/*
	 * -----------------------------分割线-------------------------------------------
	 */
	
	Socket socket = null;
	BufferedWriter writer = null;
	BufferedReader reader = null;
	
	
	public void connect() {
		/*
		 * AsyncTask是异步通信，在子线程中处理任务
		 */
		//(4)这个AsyncTask用于从网络读取数据，注意是大写的Void
		AsyncTask<Void , String , Void> read = new AsyncTask<Void, String, Void>(){
			@Override
			protected Void doInBackground(Void... params) {
				try {
					//(1)第一个参数是服务器的IP地址；第二个参数是服务器的端口号
					socket = new Socket(mIp.getText().toString() , 12345);
					
					//(2)将socket中的OutputStream逐步封装成BufferedWriter
					writer = new BufferedWriter( new OutputStreamWriter(socket.getOutputStream()) ) ;
					
					//(3)将socket中的InputStream逐步封装成BufferedReader
					reader = new BufferedReader( new InputStreamReader(socket.getInputStream()) );
					
					publishProgress("@success");
				}
				catch (UnknownHostException e) {
					Toast.makeText(MainActivity.this , "无法建立连接" , Toast.LENGTH_SHORT).show();
				} 
				catch (IOException e) {
					Toast.makeText(MainActivity.this , "无法建立连接" , Toast.LENGTH_SHORT).show();
				}
				
				/*
				 * -----------接下来就可以使用线程控制当前数据的读写了------------
				 */
				try {
					String line;
					while( (line = reader.readLine()) != null){ //如果还能读出数据
						publishProgress(line); //实际上是把这个数据传入到onProgressUpdate()方法中进行处理
					}
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onProgressUpdate(String... values) {
				if(values[0].equals("@success")){
					Toast.makeText(MainActivity.this , "连接成功" , Toast.LENGTH_SHORT).show();
				}
				//接收到传进来的line中的字符串后，保存在values数组的第一个下标里面
				mText.append("别人说:" + values[0] + "\n");
				
				super.onProgressUpdate(values);
			}
		};
		
		//(5)将AsyncTask类的对象，用execute()方法开始执行
		read.execute();

	}//connect()方法结束



	public void send() {
		try {
			//如果是想在Android客户端中看到自己发的东西，就加上这一句
			mText.append("我说:" + mEditText.getText().toString() + "\n");
			
			//现在把数据输出到服务器。注意，这里是很容易出问题的点，要加上 \n
			writer.write(mEditText.getText().toString() + "\n");
			//还要调用flush()方法将数据强制输出
			writer.flush();
			
			mEditText.setText("");
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}//send()方法结束



	


}
