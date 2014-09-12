package com.example.mdptool;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

public class MazeThread extends Thread{

	boolean running = false;
	Maze maze;
	SurfaceHolder surfaceHolder;
	
	public MazeThread(SurfaceHolder surfaceHolder, Maze maze){
		this.maze=maze;
		this.surfaceHolder = surfaceHolder;
	}
	public void setRunning(boolean r){
		running = r;
	}
	@Override
	public void run(){
		while(running){
                Canvas c = null;
                try {
                       c = surfaceHolder.lockCanvas();
                       if(c != null){
	                       synchronized (surfaceHolder) {
	                              maze.onDraw(c);
	                       }
                       }
                } finally {
                       if (c != null) {
                              surfaceHolder.unlockCanvasAndPost(c);
                       }

                }

         }				
	}
}
