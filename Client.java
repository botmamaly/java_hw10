package myjava.homework;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import com.opencsv.CSVWriter;

public class Client{
	private String addr;
	private String port;
	private String account;
	private String passwd;
	private Socket client;
	private BufferedWriter bw;
	private BufferedReader br;
	private BufferedReader socketBr;
    private ObjectInputStream in;
	public void getInfo(){
		br = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println("Address:");
			addr=br.readLine();
			System.out.println("Port");
			port=br.readLine();
			System.out.println("HSQL Data account:");
			account=br.readLine();
			System.out.println("HSQL Data password:");
			passwd=br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void run() throws IOException{
		String query=null;
		int columnNum;
		in=new ObjectInputStream(client.getInputStream());
		socketBr=new BufferedReader(new InputStreamReader(client.getInputStream()));
		while(true)
	{
			System.out.println("Enter you SQL query:");
			query=br.readLine();
			bw.write(query);//query
			bw.newLine();
			bw.flush();//send query
			if(query.equals("end"))
				break;
		try {
			columnNum=Integer.parseInt(socketBr.readLine());
			if(columnNum!=-1)
			{
				ArrayList<String> list=(ArrayList<String>)in.readObject();
				CSVWriter writer = new CSVWriter(new FileWriter("Result.csv"));
			    for(int time=0;time<(list.size())/columnNum;time++)
			    {
			    	String[] string=new String[columnNum];
			    	for(int index=0;index<columnNum;index++){
			    		string[index]=list.get(index+time*columnNum); 
			    	}
			    	writer.writeNext(string);
			    }
			    writer.close();
			}
			else{
				System.out.println("SQLSyntaxErrorException: Please Enter the correct query.");
			}
		} catch (ClassNotFoundException e) {
				e.printStackTrace();
		}
	}
		br.close();
		bw.close();
		client.close();
		socketBr.close();
	}
	public void connect(){
		try {
			client=new Socket(addr,Integer.parseInt(port));
			System.out.println("Connect to Server success.");
			bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			bw.write(account);//account
			bw.newLine();
			bw.flush();//send account
			
			bw.write(passwd);//password
			bw.newLine();
			bw.flush();//send password
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		Client client=new Client();
		client.getInfo();
		client.connect();
		try {
			client.run();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}