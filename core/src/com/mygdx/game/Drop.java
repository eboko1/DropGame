package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

import javax.swing.JLabel;

public class Drop extends ApplicationAdapter {
	OrthographicCamera camera;
	SpriteBatch batch;
	Texture dropImage;
	Texture bucketImage;
	Sound dropSound;
	Music rainMusic;
	Rectangle bucket;
	Vector3 touchPos;
	Array<Rectangle> rainDrops;
	long lastDropTime;
	int countRainDropInBucket;
	//JLabel label;

	private void spawnRainDrop(){
		Rectangle rainDrop = new Rectangle();
		rainDrop.x = MathUtils.random(0, 800 - bucket.width);
		rainDrop.y = 480;
		rainDrop.width = 64;
		rainDrop.height = 64;
		rainDrops.add(rainDrop);
		lastDropTime = TimeUtils.nanoTime();

	}
	
	@Override
	public void create () {

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		//label = new JLabel("Create drop game Frantsuh "+countRainDropInBucket);


		batch = new SpriteBatch();
		touchPos = new Vector3();

		dropImage = new Texture("drop.png");
		bucketImage = new Texture("bucket.png");

		dropSound = Gdx.audio.newSound(Gdx.files.internal("waterDroplet.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("waterDrop.mp3"));

		rainMusic.setLooping(true);
		rainMusic.play();

		bucket = new Rectangle();
		bucket.x =800/2-64;
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;

		rainDrops = new Array<Rectangle>();//many drops
		spawnRainDrop();//one drop
		System.out.println(countRainDropInBucket);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(bucketImage, bucket.x, bucket.y);
			for(Rectangle raindrop : rainDrops){
				batch.draw(dropImage, raindrop.x, raindrop.y);
			}
		batch.end();

		if (Gdx.input.isTouched()){

			touchPos.set(Gdx.input.getX(), Gdx.input.getY(),0);
			camera.unproject(touchPos);
			bucket.x = (int)(touchPos.x - 64/2);

		}
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x = bucket.x - 200 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x = bucket.x + 200 * Gdx.graphics.getDeltaTime();

		if (bucket.x < 0) bucket.x = 0;
		if(bucket.x > 800 - bucket.width) bucket.x = 800 - bucket.width;

		if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRainDrop();

		Iterator<Rectangle> iter = rainDrops.iterator();
		while (iter.hasNext()){
			Rectangle rainDrop = iter.next();
			rainDrop.y -=  200 * Gdx.graphics.getDeltaTime();
			if (rainDrop.y + 64 < 0) iter.remove();

			if(rainDrop.overlaps(bucket)){
				dropSound.play();
				iter.remove();
				countRainDropInBucket++;
			}
		}



	}
	
	@Override
	public void dispose () {
		super.dispose();

		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		batch.dispose();

	}
}
