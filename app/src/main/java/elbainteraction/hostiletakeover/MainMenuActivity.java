package elbainteraction.hostiletakeover;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainMenuActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button button = (Button)findViewById(R.id.new_game);
        button.setBackgroundColor(Color.RED);
        button.setTextColor(Color.WHITE);
        button.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);

        button = (Button)findViewById(R.id.continue_game);
        button.setBackgroundColor(Color.BLUE);
        button.setTextColor(Color.WHITE);
        button.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
        //This is a comment

        button = (Button)findViewById(R.id.options);
        button.setBackgroundColor(Color.parseColor("#FF009911"));
        button.setTextColor(Color.WHITE);
        button.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);



    }
    public void goToNewGame(View view){
        startActivity(new Intent(view.getContext(), CreateGameActivity.class));
        overridePendingTransition(0, 0);

    }
    public void goToContinueGame(View view){
        startActivity(new Intent(view.getContext(), ContinueGameActivity.class));
        overridePendingTransition(0, 0);

    }
    public void goToOptions(View view){
        startActivity(new Intent(view.getContext(), OptionsActivity.class));
        overridePendingTransition(0, 0);
    }



}
