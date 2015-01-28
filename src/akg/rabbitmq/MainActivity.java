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



public class MainActivity extends Activity {
	private MessageConsumer mConsumer;
	public int state = 1; /* 現在何番まで行っているかを保存する変数 */
	public long time, endTime, startTime;
	private TextView mOutput, send, textState;
	int people = 0, phaseOfGame = 0, point = 0, countInPhase = 0 ,subcount = 0,flag = 0,flag2 = 0,flag3 = 0, temp, num, mypoint, oppoint, res = 0, gotExcellent = 0;  /*接続人数、　モード、　得点*/
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
			15, 16, 17, 18, 19, 20, 21, 22, 23, 24 };//mogura hole labeling int
	final int Rid[] = { R.id.button1, R.id.button2, R.id.button3, R.id.button4,
			R.id.button5, R.id.button6, R.id.button7, R.id.button8,
			R.id.button9, R.id.button10, R.id.button11, R.id.button12,
			R.id.button13, R.id.button14, R.id.button15, R.id.button16,
			R.id.button17, R.id.button18, R.id.button19, R.id.button20,
			R.id.button21, R.id.button22, R.id.button23, R.id.button24,
			R.id.button25 };
	String result ="";
    Dialog dlg;
    int flagA=0,flagB=0,flagC=0,flagD=0,flagE=0;
    
    private ProgressDialog pd;
    int mpf=0;
    int p=0;
    int maxpointbuff=-5000;
    char maxpointIam='.';

    MediaPlayer mp1,mp2,mp3;
    
    //-----------------------------------------
    Channel mChannel;
    Connection mConnection;
    
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//---------------------------------------------------リソースの用意
		setContentView(R.layout.activity_main);
		setSounds();
		setHandlerAndThread();
		setTextOutput();
		setHoleLabel();
		//---------------------------------------------------コネクション張る
		mConsumer = new MessageConsumer();
		mConsumer.connectToRabbitMQ();
		//---------------------------------------------------送受信のハンドラ
		setReceiverHandler();
		setSenderHandler();
		//---------------------------------------------------制限時間用のハンドラ
		setTimerHandler();
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
    	new sendMessage().execute("EXITEXIT");
		}
		Constants.EXCHANGE="logs0";
		Constants.QUEUE="TestQueue0";
		Iam = 'Z';
		mConsumer.connectToRabbitMQ();
		timer.cancel();
		MainActivity.this.finish();
		
		
		new disconnect().execute();
		// mConsumer.dispose();
	}
	
	
	private class sendMessage extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... Message) {
			try {
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
	
	void setSounds(){//sound buffer
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		mp1 = MediaPlayer.create(MainActivity.this, R.raw.kick1);
		mp2 = MediaPlayer.create(MainActivity.this, R.raw.kick1);
		mp3 = MediaPlayer.create(MainActivity.this, R.raw.kick1);
	}
	
	void setHandlerAndThread(){//Handler initialization
		handler = new Handler();
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.permitAll().build());
	}
	void setTextOutput(){//set textview
		mOutput = (TextView) findViewById(R.id.output);
		send = (TextView) findViewById(R.id.TextViewSend);
		textState = (TextView) findViewById(R.id.textViewState);
	}
	
	void setHoleLabel(){//randomize hole label and initialize
		shuffle(label);
		for (int i = 0; i < 25; i++) {
			button[i] = (Button) findViewById(Rid[label[i]]);
			button[i].setEnabled(false);
		}
	    for (int i = 0; i < 25; i++){
	    	dam[i] = 1;
	    	rest[i] = 5;
	    }
	}
	
	
	private void setTimerHandler(){

	    timer.scheduleAtFixedRate(new TimerTask(){
		      @Override
		      public void run() {
		    	  time = System.currentTimeMillis();
		    	  handler.post(new Runnable(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if ( flag == 1){
							subcount++;
							send.setText(" "+(int)((time-startTime)/1000)+"秒 / "+(point)+"point");
				    		  num = 0;
				    		  for(int i = 0; i < 25; i++){
				    			  if(button[i].isEnabled()){
				    				  //----------------------------------
				    				  if ( phaseOfGame > 3 ){ //phase4~では2倍速
				    					  rest[i] = rest[i]-1;
				    				  }else{	//通常速度
				    					  if(subcount%2 == 0){
				    						  rest[i] = rest[i]-1;
				    					  }
				    				  }
				    				  //---------------------------------
				    				  if(rest[i]==0){
			 		  					  button[i].setEnabled(false);
										  rest[i]=5;
										  dam[i]=1;
										  button[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.rapidselector));
				    				  }
				    				  //---------------
				    				  if(dam[i]==1){
				    					  num++;
				    				  }
				    				  //--------------------
				    			  }
				    		  }
				    		  //---------------------------------
					    		  if ( max > num){
					    			  for(int i = 0; i < max - num; i++){
					    				  state++;
					    				  if (state > 24)state = 0;
			 		  					  button[state].setEnabled(true);
										  rest[state]=5;
										  dam[state]=1;
					    			  }
					    		  }else if ( max < num){
					    			  for(int i = 0; i < num - max; i++){
					    				  int j = 0;
					    				  for ( j = 0; !button[j].isEnabled(); j++);
			 		  					  button[j].setEnabled(false);
										  rest[j]=5;
										  dam[j]=1;
					    			  }
					    		  }
					    		//--------------------------------
					    		  if(gotExcellent > 0){//EXELLENT　高得点
				    				  state++;
				    				  if (state > 24)state = 0;
		 		  					  button[state].setEnabled(true);
									  rest[state]=5;
									  dam[state]=5;
									  button[state].setBackgroundDrawable(getResources().getDrawable(R.drawable.selector5));
									  gotExcellent=0;
					    		  }
					    		  //----------------------------------
						}
					}
		    	  });
		    	  if(flag == 0){
		    		  countInPhase = 0;
		    	  }
		    	  if(countInPhase==30){
		    		  phaseOfGame++;
		    		  countInPhase=0;
		    	  }
		    if(phaseOfGame==5){
		      handler.post(new Runnable(){//集計処理　countは集計処理が始まってからのタイミング
		    			  @Override
		    			  public void run(){
		    				  int sendflag=0;
		    					if (phaseOfGame==5&&(countInPhase>=4&&countInPhase<=15)) {
		    						if(countInPhase==4){//集計中を示すダイアログ
		    						flag = 2;
		    						showPleasewaitDialog();
		    						}
	    							if(sendflag==0){//各者得点を各々のタイミングでメッセージキューに流す。もし自分がAさんならカウント6のとき、Bさんならカウント8のとき...
	    								if((Iam=='A'&&countInPhase==6)||(Iam=='B'&&countInPhase==8)||(Iam=='C'&&countInPhase==10)||(Iam=='D'&&countInPhase==12)||(Iam=='E'&&countInPhase==14)){
			    						new sendMessage().execute("_POINT"+Iam+""+point);	
			    						sendflag=1;//I already sended my score
	    								}
		    						}
	    							setButtonDisable();
		    					}
		    					if (phaseOfGame==5&&countInPhase==16){
		    						showResultDialog();
		    					}
		    			  }
		    		  });
		    	  }
		    
		    	  countInPhase++;
		      }
		    }, 0, INTERVAL_PERIOD);
	    
	}
	
	private void setReceiverHandler(){

		mConsumer.setOnReceiveMessageHandler(new OnReceiveMessageHandler() {
			public void onReceiveMessage(byte[] message) {
				String text = "";
				try {
					text = new String(message, "UTF8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
				if ( flag == 0){//-----------------ゲームスタート前
					 if(flag3!=1&&text.substring(1).equals(""+random)){
						 showReadyDialog(text);
					}
					 if(text.length()>=6){if(  flag2 != 1 && Iam!='Z'&&text.substring(0,5).equals("GROUP")){
						 setExchangeAndQueue(text);
						new sendMessage().execute("ACKACK");
						flag2 = 1;
					}}
					 if(text.equals("HITOGAOOI")&&Iam=='Z'){
						 Toast.makeText( MainActivity.this, "待機人数が多すぎるので戻るボタンで戻ってください。", Toast.LENGTH_SHORT ).show();
					}
					 if(text.length()>=6){if(   text.substring(0,6).equals("PEOPLE")){
						people = (int)(text.charAt(6)-'0');//接続人数管理
					}}
					 if(text.equals("START")){//STARTが来たらゲーム開始
						 gameStart();
					 }
				}else if(text.length()<5 && flag==1 && text.charAt(0)!=Iam){ // 相手からの攻撃の処理 
					temp = Integer.valueOf(text.substring(1));
					if(temp == 25) temp=0;
					nekoDasu();//おじゃま猫をだす
				}
				else if( text.length()> 6){if(  text.substring(0,6).equals("_POINT")){
					//終了処理
					setResult(text);
				}}
				
			}
		});	
	}
	private void showReadyDialog(String text){
		Iam=text.charAt(0);
		flag3=1;
		people = (int)(Iam - 'A' + 1);
		dlg = new AlertDialog.Builder(MainActivity.this)
		.setMessage(Iam+" で登録できました。\n一緒に対戦したい人の準備ができたら、どなたかがスタートを押してください。")
		.setPositiveButton("START",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialog, int id) {
						new sendMessage().execute("READY");
					//	mOutput.append("READYだした");
						dialog.cancel();
					}
				}).show();
	}
	private void setExchangeAndQueue(String text){
		Constants.EXCHANGE="logs"+text.charAt(5);
		Constants.QUEUE="TestQUEUE"+text.charAt(5);
		mConsumer.connectToRabbitMQ();
	}
	
	private void gameStart(){
		if(dlg!=null && dlg.isShowing()){
			dlg.dismiss();
		}
		flag=1;
		for(int i = 0; i < MAX_0; i++){
			button[i].setEnabled(true);
		}
		max = MAX_0;
		startTime =System.currentTimeMillis();
		state = MAX_0;
	}
	
	private void nekoDasu(){
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
			rest[state] = 5;
		}
		dam[label[temp]] = -1;
		button[label[temp]].setBackgroundDrawable(getResources().getDrawable(R.drawable.selector2));
		button[label[temp]].setEnabled(true);
		rest[label[temp]] = 5;
	}
	
	private void setResult(String text){
		res++;
		if(flagA==0&&text.charAt(6)=='A'||flagB==0&&text.charAt(6)=='B'||flagC==0&&text.charAt(6)=='C'||flagD==0&&text.charAt(6)=='D'||flagE==0&&text.charAt(6)=='E'){

		result=result+text.charAt(6)+" : "+text.substring(7)+"point";
		if(Integer.parseInt(text.substring(7))>maxpointbuff){
			
			maxpointbuff=Integer.parseInt(text.substring(7));
			maxpointIam=text.charAt(6);
		}
		if(text.charAt(6)==Iam){
			result=result+" <- あなた\n";
		}
		else{
			result=result+"\n";
		}
		if(text.charAt(6)=='A'){flagA=1;}if(text.charAt(6)=='B'){flagB=1;}if(text.charAt(6)=='C'){flagC=1;}if(text.charAt(6)=='D'){flagD=1;}if(text.charAt(6)=='E'){flagE=1;}
		}
		mOutput.append("text:"+text+"\n");
		mOutput.append("res:"+res+" people"+people+"\n");
	}
	
	private void setSenderHandler(){
		for (int i = 0; i < 25; i++) {//各ボタンについて
			final int n = i;
			button[n].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//-------------sound resource (当時は同時再生についてこんな方法しか思いつかなかった）
					if(mpf==0){
						mp1.start();
						mpf=1;
					}else if(mpf==1){
						mp2.start();
						mpf=2;
					}else if(mpf==2){
						mp3.start();
						mpf=0;
					}
					//-------------
					state++;
					if (state>24) state = 0;
					point=point+100*dam[n]-10*(5-rest[n]);
					switch(rest[n]){
						case 5:
							textState.setText("EXCELLENT!");
							gotExcellent++;
							break;
						case 4:textState.setText("GREAT!");break;
						case 3:textState.setText("GOOD!");break;
						case 2:textState.setText("GOOD");break;
						case 1:textState.setText("OK");break;
					}
					new sendMessage().execute(Iam + Integer.toString(n + 1));
					button[n].setEnabled(false);
					dam[n]=1;

					button[state].setEnabled(true);
					dam[state]=1;
					rest[state]=5;
					button[n].setBackgroundDrawable(getResources().getDrawable(R.drawable.selector));
				}
			});
		}
		
	}
	private void setButtonDisable(){
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
	
	private void showPleasewaitDialog(){
		pd = new ProgressDialog(MainActivity.this);
		pd.setCancelable(false);
		pd.setMessage("集計中...");
		pd.show();
	}
	
	private void showResultDialog(){
		result=result+"\n"+maxpointIam+"さんの勝ちです";
		pd.dismiss();
			try{
		new AlertDialog.Builder(MainActivity.this)
		.setMessage(result)
		.setCancelable(false)
		.setPositiveButton("もう一度",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialog, int id) {
						Intent intent = new Intent(MainActivity.this,MainActivity.class);
						startActivity(intent);
						MainActivity.this.finish();
				}
				})
		.setNegativeButton("さようなら",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialog, int id) {
						MainActivity.this.finish();
					}

				}).show();
			}
			catch(Exception e){
				e.getStackTrace();
				MainActivity.this.finish();
		}	
	}

	private static void shuffle(int[] arr) {
		for (int i = arr.length - 1; i > 0; i--) {
			int t = (int) (Math.random() * i); // 0～i-1の中から適当に選ぶ

			// 選ばれた値と交換する
			int tmp = arr[i];
			arr[i] = arr[t];
			arr[t] = tmp;
		}
	}
	
		
	
}
