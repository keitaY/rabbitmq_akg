package akg.rabbitmq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class Top extends Activity{
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.top);

	//	requestWindowFeature(Window.FEATURE_NO_TITLE);

       Button button1 = (Button) findViewById(R.id.button1);
       Button button2 = (Button) findViewById(R.id.button2);
       button1.setOnClickListener(new View.OnClickListener() {
       @Override
    public void onClick(View v) {
            Intent i = new Intent (Top.this, MainActivity.class);
            startActivity(i);
       }
       });

       button2.setOnClickListener(new View.OnClickListener() {
       @Override
    public void onClick(View v) {
            Intent j = new Intent (Top.this, TutorialActivity.class);
            startActivity(j);
       }
       });
    }
}
