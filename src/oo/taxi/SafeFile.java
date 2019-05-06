package oo.taxi;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

public class SafeFile 
/**
 * @OVERVIEW :
 * keep thread-safe when writing files 
 */
{
	private File file;
	
	public SafeFile(String filepath){
		file = new File(filepath);
		if (!file.exists()) {
			try{file.createNewFile();}catch(Exception e){}
		}
		else{
			file.delete();
			try{file.createNewFile();}catch(Exception e){}
		}
	}
	
	public boolean repOK(){
		if (!file.exists()) return false;
		return true;
	}
	
	/**
	 * @MODIFIES : file;
	 * @EFFECTS : write file;
	 */
	public synchronized void writeFile(String toWrite){
		try{
			toWrite += "----------------------------------------------------------\r\n";
			Writer filewriter = new FileWriter(file, true);
			filewriter.write(toWrite);
			filewriter.close();
		}catch(Exception e){}
	}
}
