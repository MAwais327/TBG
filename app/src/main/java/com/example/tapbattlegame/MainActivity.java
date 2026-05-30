package com.example.tapbattlegame;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView tvScore, tvTimer, tvPlayerName, tvHighScore;
    private Button btnTap;
    private RelativeLayout gameLayout;

    private int score = 0;
    private int highScore = 0;
    private int totalTaps = 0;

    private boolean gameActive = false;
    private String playerName = "Player";

    private CountDownTimer countDownTimer;
    private final Random random = new Random();

    private static final String PREFS = "TapBattlePrefs";
    private static final String KEY_NAME = "player_name";
    private static final String KEY_HIGH = "high_score";

    private static final int GAME_DURATION_MS = 30000;
    private static final int BUTTON_MARGIN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvScore = findViewById(R.id.tvScore);
        tvTimer = findViewById(R.id.tvTimer);
        tvPlayerName = findViewById(R.id.tvPlayerName);
        tvHighScore = findViewById(R.id.tvHighScore);
        btnTap = findViewById(R.id.btnTap);
        gameLayout = findViewById(R.id.gameLayout);

        loadData();
        updatePlayerNameDisplay();
        updateHighScoreDisplay();

        btnTap.setOnClickListener(v -> onTapButton());
    }

    private void loadData() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        playerName = prefs.getString(KEY_NAME, "Player");
        highScore = prefs.getInt(KEY_HIGH, 0);
    }

    private void saveData() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        prefs.edit().putString(KEY_NAME, playerName).putInt(KEY_HIGH, highScore).apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_set_name) {
            showSetNameDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSetNameDialog() {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        new AlertDialog.Builder(this)
                .setTitle("Set Player Name")
                .setView(input)
                .setPositiveButton("Save", (d,w) -> {
                    playerName = input.getText().toString().trim();
                    if(!playerName.isEmpty()){
                        saveData();
                        updatePlayerNameDisplay();
                    }
                })
                .show();
    }

    private void onTapButton() {
        if (!gameActive) {
            startGame();
            return;
        }

        score++;
        totalTaps++;
        updateScoreDisplay();
        moveButtonToRandomPosition();
    }

    private void startGame() {
        score = 0;
        totalTaps = 0;
        gameActive = true;
        btnTap.setText("TAP!");
        updateScoreDisplay();
        startTimer();
    }

    private void endGame() {

        gameActive = false;

        boolean newRecord = false;

        if(score > highScore){
            highScore = score;
            saveData();
            newRecord = true;
        }

        updateHighScoreDisplay();

        String msg =
                "Player: " + playerName +
                "\\nScore: " + score +
                "\\nHigh Score: " + highScore +
                "\\nTotal Taps: " + totalTaps;

        if(newRecord){
            msg += "\\n\\n🏆 New High Score!";
        }

        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Play Again",(d,w)->startGame())
                .setNegativeButton("Exit",(d,w)->finish())
                .show();

        btnTap.setText("Play Again");
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(GAME_DURATION_MS,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("Time: " + millisUntilFinished/1000 + "s");
            }

            @Override
            public void onFinish() {
                endGame();
            }
        }.start();
    }

    private void updateScoreDisplay() {
        tvScore.setText("Score: " + score);
    }

    private void updatePlayerNameDisplay() {
        tvPlayerName.setText("Player: " + playerName);
    }

    private void updateHighScoreDisplay() {
        tvHighScore.setText("High Score: " + highScore);
    }

    private void moveButtonToRandomPosition() {}
}
