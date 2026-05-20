package com.example.tapbattlegame;

import com.example.tapbattlegame.R;

import android.app.AlertDialog;
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

    private TextView tvScore, tvTimer, tvPlayerName;
    private Button btnTap;
    private RelativeLayout gameLayout;

    private int score = 0;
    private boolean gameActive = false;
    private String playerName = "Player";
    private CountDownTimer countDownTimer;
    private final Random random = new Random();

    private static final int GAME_DURATION_MS = 30000;
    private static final int BUTTON_MARGIN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);

        tvScore      = findViewById(R.id.tvScore);
        tvTimer      = findViewById(R.id.tvTimer);
        tvPlayerName = findViewById(R.id.tvPlayerName);
        btnTap       = findViewById(R.id.btnTap);
        gameLayout   = findViewById(R.id.gameLayout);

        updatePlayerNameDisplay();
        btnTap.setOnClickListener(v -> onTapButton());
    }

    // ── Menu ─────────────────────────────────────────────────────────────────
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
        input.setHint("Enter your name");
        input.setText(playerName);
        input.setTextColor(0xFF000000);
        int pad = (int)(16 * getResources().getDisplayMetrics().density);
        input.setPadding(pad, pad, pad, pad);

        new AlertDialog.Builder(this)
                .setTitle("Set Player Name")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        playerName = name;
                        updatePlayerNameDisplay();
                        Toast.makeText(this, "Welcome, " + playerName + "!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ── Tap logic ─────────────────────────────────────────────────────────────
    private void onTapButton() {
        if (!gameActive) {
            startGame();
            return;
        }
        score++;
        updateScoreDisplay();
        moveButtonToRandomPosition();
    }

    // ── Game lifecycle ────────────────────────────────────────────────────────
    private void startGame() {
        score = 0;
        gameActive = true;
        btnTap.setText("TAP!");
        updateScoreDisplay();
        moveButtonToRandomPosition();
        startTimer();
    }

    private void endGame() {
        gameActive = false;
        if (countDownTimer != null) countDownTimer.cancel();

        btnTap.setText("Play Again");

        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) btnTap.getLayoutParams();
        params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.removeRule(RelativeLayout.ALIGN_PARENT_START);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        btnTap.setLayoutParams(params);

        tvScore.setText(playerName + "'s Score: " + score);
        tvTimer.setText("Time: 0s");
    }

    // ── Timer ─────────────────────────────────────────────────────────────────
    private void startTimer() {
        countDownTimer = new CountDownTimer(GAME_DURATION_MS, 1000) {
            @Override public void onTick(long ms) {
                tvTimer.setText("Time: " + ms / 1000 + "s");
            }
            @Override public void onFinish() {
                tvTimer.setText("Time: 0s");
                endGame();
            }
        }.start();
    }

    // ── UI helpers ────────────────────────────────────────────────────────────
    private void updateScoreDisplay() {
        tvScore.setText("Score: " + score);
    }

    private void updatePlayerNameDisplay() {
        tvPlayerName.setText("Player: " + playerName);
    }

    private void moveButtonToRandomPosition() {
        gameLayout.post(() -> {
            int layoutW = gameLayout.getWidth();
            int layoutH = gameLayout.getHeight();
            int btnW    = btnTap.getWidth();
            int btnH    = btnTap.getHeight();

            if (layoutW == 0 || btnW == 0) return;

            int maxX = layoutW - btnW - BUTTON_MARGIN;
            int maxY = layoutH - btnH - BUTTON_MARGIN;

            int newX = BUTTON_MARGIN + random.nextInt(Math.max(1, maxX - BUTTON_MARGIN));
            int newY = BUTTON_MARGIN + random.nextInt(Math.max(1, maxY - BUTTON_MARGIN));

            RelativeLayout.LayoutParams params =
                    (RelativeLayout.LayoutParams) btnTap.getLayoutParams();
            params.removeRule(RelativeLayout.CENTER_IN_PARENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            params.leftMargin = newX;
            params.topMargin  = newY;
            btnTap.setLayoutParams(params);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
