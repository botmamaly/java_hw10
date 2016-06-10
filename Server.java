package myjava.homework;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;

public class Server{	
	private ServerSocket myServerSocket;
	private ObjectOutputStream out;
	private Connection con;
	private Socket server;
	private BufferedReader br;
	private BufferedWriter bw;
	private String user;
	private String passwd;
	public Server(){
		try {
        	myServerSocket = new ServerSocket(6666);//build a TCP server
        } catch (java.io.IOException e) {
        	 e.printStackTrace();
        }
	}
	private void connect(){
		System.out.println("Ready to accept connection...");
		try {
			server = myServerSocket.accept();
			System.out.println("Client has Connected");
			br=new BufferedReader(new InputStreamReader(server.getInputStream()));
			user=br.readLine();
			passwd=br.readLine();
		} catch (UnknownHostException e) {
			System.out.println("Connect to Client fail!!"+e);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver" );
			con = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:9001/java_db",user,passwd);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private void sendList(ArrayList<String> list){
		 try {
			out.writeObject(list);
			out.flush();
		} catch (IOException e) {
			System.out.println(e);
		}   
	}
	private void run() throws IOException{
		String query=null;
		Statement statement=null;
		System.out.println("Ready to catch SQL query...");
		bw = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
		out= new ObjectOutputStream(server.getOutputStream());
		try {
			statement=con.createStatement();
		} catch (SQLException e) {
			System.out.println("Server Statement SQLException"+e);
		}
		while(true){
			query=br.readLine();//Requested query
			System.out.println("[SQL query] :"+query);
			if(query.equals("end"))
				break;
			try {
				ResultSet rs=statement.executeQuery(query);
				ResultSetMetaData meta = rs.getMetaData();
				int columnNum=meta.getColumnCount();
				bw.write(Integer.toString(columnNum));//write column's number
				bw.newLine();
				bw.flush();//send to client
				ArrayList<String> list = new ArrayList<String>();
				for(int i=1;i<=columnNum;i++)
					list.add(meta.getColumnName(i));
				while(rs.next()){
					for(int i=1;i<=columnNum;i++)
						list.add(rs.getString(i));
				}
				sendList(list);//send the list to client
			} catch(SQLSyntaxErrorException e){
				System.out.println("SQLSyntaxErrorException Please Enter the correct query.");
				bw.write(Integer.toString(-1));//write Error
				bw.newLine();
				bw.flush();//send to client
			}catch (SQLException e) {
				System.out.println("Server SQLException");
			}
		}
		server.close();
		myServerSocket.close();
		br.close();
		out.close();
		bw.close();
	}
    public static void main(String[] args) {
		Server server=new Server();
		server.connect();
		try {
			server.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}