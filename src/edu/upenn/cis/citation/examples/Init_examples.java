package edu.upenn.cis.citation.examples;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Init_examples {
	
	Properties configProp = new Properties();
	
	String config_file = "./Example.config";
	
	private InputStream in;
	
	public void load_properties()
	{
		this.in = this.getClass().getResourceAsStream(config_file);
        try {
            configProp.load(this.in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
				this.in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}

}
