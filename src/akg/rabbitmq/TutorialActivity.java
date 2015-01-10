package akg.rabbitmq;

import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import akg.rabbitmq.MessageConsumer.OnReceiveMessageHandler;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;



public class TutorialActivity extends Activity {
	private MessageConsumer mConsumer;
	public int state = 1; /* 現在何番まで行っているかを保存する変数 */
	public long time, endTime, startTime;
	private TextView mOutput, send, textState;
	private EditText mInput;
	int people = 0, mode = 0, point = 0, count = 0 ,count2 = 0,flag = 0,flag2 = 0,flag3 = 0, temp, num, mypoint, oppoint, res = 0, exe = 0;  /*接続人数、　モード、　得点*/
    char Iam='Z';
    Timer timer = new Timer();
    final int INTERVAL_PERIOD = 200;
    private Handler handler;
    final int MAX_0 = 3;
    int max, random;
    int[] dam = new int[25];	/* -1:ダメージ 1:通常  2:2倍 3:3倍   */
    int[] rest = new int[25];   /* 残り表示時間 */
	private Button[] button = new Button[25];
	private int[] label = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
			15, 16, 17, 18, 19, 20, 21, 22, 23, 24 };
	String result ="";
    Dialog dlg;
    
    private MediaPlayer mp;
    private String path;
    private ProgressDialog pd;
    int mpf=0;
    int p=0;

    Channel mChannel;
    Connection mConnection;
    
//Animation mogani = AnimationUtils.loadAnimation(this, R.drawable.mogura);

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		
		setContentView(R.layout.tutorial_main);
	    Button owaributton = (Button) findViewById(R.id.owaributton);
	       owaributton.setOnClickListener(new View.OnClickListener() {
	       @Override
	    public void onClick(View v) {

				Constants.EXCHANGE="logs0";
				Constants.QUEUE="QUEUE0";
				Iam = 'Z';
				mConsumer.connectToRabbitMQ();
				timer.cancel();
				TutorialActivity.this.finish();
	       }
	       });
		


	//	requestWindowFeature(Window.FEATURE_NO_TITLE);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);	//---------------------------------------------1
		final MediaPlayer mp1 = MediaPlayer.create(TutorialActivity.this, R.raw.kick1);
		final MediaPlayer mp2 = MediaPlayer.create(TutorialActivity.this, R.raw.kick1);
		final MediaPlayer mp3 = MediaPlayer.create(TutorialActivity.this, R.raw.kick1);

		//Handler initialization
		handler = new Handler();
		
		// 譛ｬ蠖薙�濶ｯ縺上↑縺�￠縺ｩ窶ｦ
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.permitAll().build());

		mOutput = (TextView) findViewById(R.id.output);
		send = (TextView) findViewById(R.id.TextViewSend);
//		mInput = (EditText) findViewById(R.id.input);
		textState = (TextView) findViewById(R.id.textViewState);
		
		int Rid[] = { R.id.button1, R.id.button2, R.id.button3, R.id.button4,
				R.id.button5, R.id.button6, R.id.button7, R.id.button8,
				R.id.button9, R.id.button10, R.id.button11, R.id.button12,
				R.id.button13, R.id.button14, R.id.button15, R.id.button16,
				R.id.button17, R.id.button18, R.id.button19, R.id.button20,
				R.id.button21, R.id.button22, R.id.button23, R.id.button24,
				R.id.button25 };
		shuffle(label);
		for (int i = 0; i < 25; i++) {
			button[i] = (Button) findViewById(Rid[label[i]]);
			button[i].setEnabled(false);
		}
	    for (int i = 0; i < 25; i++){
	    	dam[i] = 1;
	    	rest[i] = 5;
	    }
	    
	    /*

		Intent intent = new Intent(MainActivity.this, Top.class);
        startActivity(intent);
		
		*/
		
		//mo
		
		//-------------------------entry button
	    /*
		new AlertDialog.Builder(MainActivity.this)
		.setMessage("PLEASE CLICK ENTRY BUTTON")
		.setCancelable(false)
		.setPositiveButton("ENTRY",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialog, int id) {
						dialog.cancel();
					}
				}).show();
		
		*/
		//---------------start
		

		//---------------------------------------------------受信系処理
		
		
		mConsumer = new MessageConsumer();
		mConsumer.connectToRabbitMQ();
		mConsumer.setOnReceiveMessageHandler(new OnReceiveMessageHandler() {
			public void onReceiveMessage(byte[] message) {
				String text = "";
				try {
					text = new String(message, "UTF8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
/*			if (p==0){
					Constants.EXCHANGE="logs2";
					Constants.QUEUE="QUEUE2";
					mConsumer.connectToRabbitMQ();
					p=1;
				}
				*/
				
				if ( flag == 0){
					if(text.substring(0,5).equals("ENTRY"));
					else if(flag3!=1&&text.substring(1).equals(""+random)){
						Iam=text.charAt(0);
						flag3=1;
						people = (int)(Iam - 'A' + 1);
						dlg = new AlertDialog.Builder(TutorialActivity.this)
						.setMessage("PLEASE CLICK START BUTTON")
						.setPositiveButton("START",
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialog, int id) {
										new sendMessage().execute("READY");
									//	mOutput.append("READYだした");
										dialog.cancel();
									}
								}).show();
					}else if(flag2 != 1 && Iam!='Z'&&text.substring(0,5).equals("GROUP")){//-----------------------------
						Constants.EXCHANGE="logs10";
						Constants.QUEUE="TestQUEUE10";
						mConsumer.connectToRabbitMQ();
						new sendMessage().execute("START");
						//
						flag2 = 1;
				//		mOutput.append("ACKだした");
					}else if(text.length()>=6 && text.substring(0,6).equals("PEOPLE")){//-----------------------------
						people = (int)(text.charAt(6)-'0');
					}
					else if(text.equals("START")){
						//---------
						if(dlg!=null && dlg.isShowing()){
							dlg.dismiss();
						}
						flag=1;
						for(int i = 0; i < MAX_0; i++){

						//	button[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.selector));
							button[i].setEnabled(true);

					//		button[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.selector));
						}
						max = MAX_0;
						//----------
						startTime =System.currentTimeMillis();
						state = MAX_0;
						 // これによってアプリの起動時間のカウントが開始される 
					}
				}
				
				else if(text.length()<5 && flag==1 && text.charAt(0)!=Iam){ // 相手からの攻撃の処理 
					temp = Integer.valueOf(text.substring(1));
					if(temp == 25) temp=0;
					if(button[label[temp]].isEnabled() && dam[label[temp]]!=-1){
						while(!button[state].isEnabled()){
							if(dam[state]!=-1){
								state++;
								if(state>25){
									state=0;
								}
							}
						}
						dam[state] = 1;
						button[state].setEnabled(true);
				//		button[state].setBackgroundDrawable(getResources().getDrawable(R.drawable.selector));
				//		button[state].startAnimation(mogani);
						rest[state] = 5;
					}
					dam[label[temp]] = -1;
					button[label[temp]].setBackgroundDrawable(getResources().getDrawable(R.drawable.selector2));
					button[label[temp]].setEnabled(true);
					rest[label[temp]] = 5;
					
				}
				else if( text.length()> 6 && text.substring(0,6).equals("_POINT")){
					//終了処理
					res++;
					result=result+text.charAt(6)+" : "+text.substring(7)+"point";
					if(text.charAt(6)==Iam){
						result=result+" <- YOU!!\n";
					}
					else{
						result=result+"\n";
					}
					mOutput.append("text:"+text+"\n");
					mOutput.append("res:"+res+" people"+people+"\n");
					
				}else if(text.equals("FINISH")){

				}
				
			}
		});
		
		//----------------------------------------------------クリック系処理
		
		
		for (int i = 0; i < 25; i++) {
			final int n = i;
			button[n].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
//					音源の読み込み
					if(mpf==0){//----------------------------------------------------------------------1
						mp1.start();
						mpf=1;
					}else if(mpf==1){
						mp2.start();
						mpf=2;
					}else if(mpf==2){
						mp3.start();
						mpf=0;
					}
//					state = next_ (state,button,label);
					state++;
					if (state>24) state = 0;
					point=point+100*dam[n]-10*(5-rest[n]);
					switch(rest[n]){
						case 5:
							textState.setText("EXCELLENT!");
							exe++;
							break;
						case 4:textState.setText("GREAT!");break;
						case 3:textState.setText("GOOD!");break;
						case 2:textState.setText("GOOD");break;
						case 1:textState.setText("OK");break;
					}
					new sendMessage().execute(Iam + Integer.toString(n + 1));
					button[n].setEnabled(false);
					dam[n]=1;

				//	button[state].setBackgroundDrawable(getResources().getDrawable(R.drawable.selector));
					button[state].setEnabled(true);
					dam[state]=1;
					rest[state]=5;
					button[n].setBackgroundDrawable(getResources().getDrawable(R.drawable.selector));
			//		button[state].setBackgroundDrawable(getResources().getDrawable(R.drawable.selector));
		//			button[state].startAnimation(mogani);
				}
			});
		}
	    timer.scheduleAtFixedRate(new TimerTask(){
		      @Override
		      public void run() {
		    	  time = System.currentTimeMillis();
		    	  handler.post(new Runnable(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if ( flag == 1){
							count2++;
							send.setText(" "+(int)((time-startTime)/1000)+"秒 / "+(point)+"point");
				    		  num = 0;
				    		  for(int i = 0; i < 25; i++){
				    			  if(button[i].isEnabled()){
				    				  if ( mode > 3 ){ //倍速
				    					  rest[i] = rest[i]-1;
				    				  }
				    				  else{	//通常速度
				    					  if(count2%2 == 0){
				    						  rest[i] = rest[i]-1;
				    					  }
				    				  }
				    				  if(rest[i]==0){
			 		  					  button[i].setEnabled(false);
										  rest[i]=5;
										  dam[i]=1;
										  button[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.selector));
										  
				    				  }
				    				  if(dam[i]==1){
				    					  num++;
				    				  }
				    			  }
				    		  }
//								mOutput.append(" num="+num+"\n"+" max="+max+"\n");
					    		  if ( max > num){
					    			  for(int i = 0; i < max - num; i++){
					    				  state++;
					    				  if (state > 24)state = 0;

									//		button[state].setBackgroundDrawable(getResources().getDrawable(R.drawable.selector));
			 		  					  button[state].setEnabled(true);
										  rest[state]=5;
										  dam[state]=1;
									//	  button[state].setBackgroundDrawable(getResources().getDrawable(R.drawable.selector));
					//					  button[state].startAnimation(mogani);
					    			  }
					    		  }
					    		  if ( max < num){
					    			  for(int i = 0; i < num - max; i++){
					    				  int j = 0;
					    				  for ( j = 0; !button[j].isEnabled(); j++);
			 		  					  button[j].setEnabled(false);
										  rest[j]=5;
										  dam[j]=1;
								//		  button[j].setBackgroundDrawable(getResources().getDrawable(R.drawable.selector));
					    			  }
					    		  }
					    		  //特殊効果実装
					    		  if(exe > 0){//EXELLENT　高得点
				    				  state++;
				    				  if (state > 24)state = 0;
				    				  
		 		  					  button[state].setEnabled(true);
									  rest[state]=5;
									  dam[state]=5;
									  button[state].setBackgroundDrawable(getResources().getDrawable(R.drawable.selector5));
									  exe=0;
					    		  }
						}
					}
		    	  });
		    	  if(flag == 0){
		    		  count = 0;
		    	  }
		    	  if(count==30){
		    		  mode++;
		    		  count=0;
		    		  handler.post(new Runnable(){
		    			  @Override
		    			  public void run(){
		    					if (mode == 5000) {

		    						flag = 2;
//		    						send.setText(()(endTime-startTime));
		    						pd = new ProgressDialog(TutorialActivity.this);
		    						pd.setCancelable(false);
		    						pd.setMessage("集計中...");
		    						pd.show();
		    						new sendMessage().execute("_POINT"+Iam+""+point);
		    						for (int i = 0; i < 25; i++) {
		    							final int n = i;
		    							button[n].setOnClickListener(new OnClickListener() {
		    								@Override
		    								public void onClick(View v) {
		    									button[n].setEnabled(false);
		    								}
		    							});
		    						}
		    					}
		    					if (mode == 6000){
			    					pd.dismiss();
			    					Constants.EXCHANGE="logs0";
			    					Constants.QUEUE="QUEUE0";
			    					Iam = 'Z';
			    					mConsumer.connectToRabbitMQ();
			    						try{
			    							
			    					new AlertDialog.Builder(TutorialActivity.this)
			    					.setMessage(result)
			    					.setCancelable(false)
			    					.setPositiveButton("もう一度",
			    							new DialogInterface.OnClickListener() {
			    								public void onClick(
			    										DialogInterface dialog, int id) {
			    									Intent intent = new Intent(TutorialActivity.this,TutorialActivity.class);
			    									startActivity(intent);
			    									TutorialActivity.this.finish();
			    							}
			    							})
			    					.setNegativeButton("さようなら",
			    							new DialogInterface.OnClickListener() {
			    								public void onClick(
			    										DialogInterface dialog, int id) {
			    									TutorialActivity.this.finish();
			    								}
	
			    							}).show();
			    						}
			    						catch(Exception e){
			    							e.getStackTrace();
			    							TutorialActivity.this.finish();
			    						
			    					}
		    					}

		    			  }
		    		  });
		    	  }
		    	  count++;
		      }
		    }, 0, INTERVAL_PERIOD);
	    
	}

	@Override
	protected void onResume() {
		super.onResume();
		new connect().execute();
		// mConsumer.connectToRabbitMQ();
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		if(Constants.EXCHANGE=="logs0"){
    	new sendMessage().execute("EXITEXIT");}
		
		Constants.EXCHANGE="logs0";
		Constants.QUEUE="TestQueue0";
		Iam = 'Z';
		mConsumer.connectToRabbitMQ();
		timer.cancel();
		TutorialActivity.this.finish();
		
		new disconnect().execute();
		// mConsumer.dispose();
	}
	
	
	private class sendMessage extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... Message) {
			try {
				// mChannel.exchangeDeclare(Constants.EXCHANGE,
				// Constants.EX_TYPE, true);
				mChannel.basicPublish(Constants.EXCHANGE, Constants.QUEUE,
						null, Message[0].getBytes());
				Log.v("debug",Message[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	private class connect extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... Params) {
			try {
				//サーバーとの接続を開始
			    ConnectionFactory connectionFactory = new ConnectionFactory();
				connectionFactory.setHost(Constants.SERVER_DOMAIN);
				connectionFactory.setUsername(Constants.SERVER_ID);
				connectionFactory.setPassword(Constants.SERVER_PASSWORD);
				
				//コネクションを確立
				mConnection = connectionFactory.newConnection();
				mChannel = mConnection.createChannel();
				
				Random rnd = new Random();
				random = rnd.nextInt(90000);
				random = random + 10000;
				new sendMessage().execute("ENTRY"+random);


			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private class disconnect extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... Params) {
			//通信終了
			try{
				mChannel.close();
				mConnection.close();							
			}
			catch(Exception e){
				e.getStackTrace();
			}									
			return null;
		}
	}	
	



	public static void shuffle(int[] arr) {
		for (int i = arr.length - 1; i > 0; i--) {
			int t = (int) (Math.random() * i); // 0～i-1の中から適当に選ぶ

			// 選ばれた値と交換する
			int tmp = arr[i];
			arr[i] = arr[t];
			arr[t] = tmp;
		}
	}
	public int next_(int n, Button[] button, int[] label){
		int t = 0;
		if(n < 24 && button[n+1].isEnabled() && label[n+1]==1){
				send.append(" ho "+n);
				t = next_(n+1,button,label);
		}
		else if( n > 24 ){
			t = next_(0,button,label);
		}
		else{
			if ( t == 0 )
				t = n;
		}
		return t;
	}
	
}
