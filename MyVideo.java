import java.util.ArrayList;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class MyVideo extends MultimediaFile{
    private String length = "5";
	private String framerate = "30";
	private String frameWidth = "1920";
	private String frameHeight = "1080";

    MyVideo(File multimediaFileName, String profileName){
        super(multimediaFileName, profileName);

    }

    public String getLength(){
		return length;
	}
	
	public String getFramerate(){
		return framerate;
	}
	
	public String getFrameWidth(){
		return frameWidth;
	}
	
	public String getFrameHeight(){
		return frameHeight;
	}

    public void setLengtg(String length){
		this.length = length; 
	}
	
	public void setFramerate(String framerate){
		this.framerate = framerate; 
	}
	
	public void setFrameWidth(String frameWidth){
		this.frameWidth = frameWidth; 
	}
	
	public void setFrameHeight(String frameHeight){
		this.frameHeight = frameHeight; 
	}
}
