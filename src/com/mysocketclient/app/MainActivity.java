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
	
	private EditText mIp; //��������IP��ַ
	private EditText mEditText; //��������Ҫ���͵�����
	private TextView mText; //���ڽ�����������
	private Button mConnect; //���Ӱ�ť
	private Button mSend; //���Ͱ�ť
	
	

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
				//����
				connect();
			}
		});
		
		mSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//����
				send();
			}
		});
		
		
		
	}//onCreate()��������
	
	
	/*
	 * -----------------------------�ָ���-------------------------------------------
	 */
	
	Socket socket = null;
	BufferedWriter writer = null;
	BufferedReader reader = null;
	
	
	public void connect() {
		/*
		 * AsyncTask���첽ͨ�ţ������߳��д�������
		 */
		//(4)���AsyncTask���ڴ������ȡ���ݣ�ע���Ǵ�д��Void
		AsyncTask<Void , String , Void> read = new AsyncTask<Void, String, Void>(){
			@Override
			protected Void doInBackground(Void... params) {
				try {
					//(1)��һ�������Ƿ�������IP��ַ���ڶ��������Ƿ������Ķ˿ں�
					socket = new Socket(mIp.getText().toString() , 12345);
					
					//(2)��socket�е�OutputStream�𲽷�װ��BufferedWriter
					writer = new BufferedWriter( new OutputStreamWriter(socket.getOutputStream()) ) ;
					
					//(3)��socket�е�InputStream�𲽷�װ��BufferedReader
					reader = new BufferedReader( new InputStreamReader(socket.getInputStream()) );
					
					publishProgress("@success");
				}
				catch (UnknownHostException e) {
					Toast.makeText(MainActivity.this , "�޷���������" , Toast.LENGTH_SHORT).show();
				} 
				catch (IOException e) {
					Toast.makeText(MainActivity.this , "�޷���������" , Toast.LENGTH_SHORT).show();
				}
				
				/*
				 * -----------�������Ϳ���ʹ���߳̿��Ƶ�ǰ���ݵĶ�д��------------
				 */
				try {
					String line;
					while( (line = reader.readLine()) != null){ //������ܶ�������
						publishProgress(line); //ʵ�����ǰ�������ݴ��뵽onProgressUpdate()�����н��д���
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
					Toast.makeText(MainActivity.this , "���ӳɹ�" , Toast.LENGTH_SHORT).show();
				}
				//���յ���������line�е��ַ����󣬱�����values����ĵ�һ���±�����
				mText.append("����˵:" + values[0] + "\n");
				
				super.onProgressUpdate(values);
			}
		};
		
		//(5)��AsyncTask��Ķ�����execute()������ʼִ��
		read.execute();

	}//connect()��������



	public void send() {
		try {
			//���������Android�ͻ����п����Լ����Ķ������ͼ�����һ��
			mText.append("��˵:" + mEditText.getText().toString() + "\n");
			
			//���ڰ������������������ע�⣬�����Ǻ����׳�����ĵ㣬Ҫ���� \n
			writer.write(mEditText.getText().toString() + "\n");
			//��Ҫ����flush()����������ǿ�����
			writer.flush();
			
			mEditText.setText("");
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}//send()��������



	


}
