package pl.edu.agh.inz.reactive.games.fit;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import pl.edu.agh.inz.reactive.R;
import pl.edu.agh.inz.reactive.games.AbstractGame;
import pl.edu.agh.inz.reactive.games.GameActivity;
import pl.edu.agh.inz.reactive.games.finish.criteria.DefaultFinishListener;

import static pl.edu.agh.inz.reactive.R.drawable.*;

/**
 * Created by Jacek on 2015-07-19.
 */
public class FitActivity extends GameActivity {

    private FitGame logic;
    private FitGame.Level level;
    private RelativeLayout layout;
    private Random random = new Random();
    private ScheduledThreadPoolExecutor timer;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void startLevel(int levelId) {
        setContentView(R.layout.activity_fit);

        logic.startLevel(levelId);

        level = logic.getLevelDescription(levelId);

        layout = (RelativeLayout)findViewById(R.id.fitLayout);

        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        ImageView background = new BackgroundLines(this, level.getRows(), level.getColumns(), 2, Color.BLACK, Color.WHITE, screenSize);

        layout.setBackground(new BitmapDrawable(background.getDrawingCache()));

        timer = new ScheduledThreadPoolExecutor(1);

        logic.setFinishListener(new DefaultFinishListener(logic, level, this));

        splitImage(morze, 3, 4);
    }

    @Override
    public AbstractGame getLogic() {
        return logic;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.shutdownNow();
        }
    }

    @Override
    public void createGameLogic() {
        logic = new FitGame(this, factory);
    }

    public void splitImage(int imgResource, int rows, int cols) {

        Bitmap bMap = BitmapFactory.decodeResource(getResources(), imgResource);
        int chunkWidth = bMap.getWidth() / cols;
        int chunkHeight = bMap.getHeight() / rows;
        Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, bMap.getWidth(), bMap.getHeight(), true);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int cornerX = j * chunkWidth;
                int cornerY = i * chunkHeight;

                Bitmap bitmap = Bitmap.createBitmap(bMapScaled, cornerX, cornerY, chunkWidth, chunkHeight);

                ImageView imageView = new ImageView(this);
                imageView.setImageBitmap(bitmap);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(chunkWidth, chunkHeight);
                params.topMargin = random.nextInt(bMap.getHeight() - chunkHeight);
                params.leftMargin = random.nextInt(bMap.getWidth() - chunkWidth);
                imageView.setRotation(random.nextInt(90));
                layout.addView(imageView, params);
            }
        }
    }
}
